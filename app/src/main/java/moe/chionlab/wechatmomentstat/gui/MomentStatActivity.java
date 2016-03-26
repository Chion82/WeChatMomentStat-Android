package moe.chionlab.wechatmomentstat.gui;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import moe.chionlab.wechatmomentstat.Config;
import moe.chionlab.wechatmomentstat.Model.UserSnsInfo;
import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.SnsStat;
import moe.chionlab.wechatmomentstat.common.Share;


public class MomentStatActivity extends AppCompatActivity {

    TextView mainTextView;
    SnsStat snsStat;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.share_menu_btn:
                shareToTimeLine();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.moment_stat_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_stat);

        snsStat = Share.snsData;
        mainTextView = (TextView)findViewById(R.id.moment_stat_main_textview);

        showMomentStat();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Button)findViewById(R.id.export_moment_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomentStatActivity.this, MomentListActivity.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.share_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToTimeLine();
            }
        });
    }

    protected void showMomentStat() {
        UserSnsInfo mySnsInfo = snsStat.getUserSnsInfo(snsStat.currentUserId);
        setTitle(String.format(getString(R.string.stat_title), mySnsInfo.userName));
        String showText = String.format(getString(R.string.from_date), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(snsStat.earliestTimestamp * 1000))) + "\n";
        showText += String.format(getString(R.string.my_sent_moments), mySnsInfo.snsList.size()) + "\n";
        showText += String.format(getString(R.string.my_sent_photos), mySnsInfo.photoNumbers) + "\n";
        showText += String.format(getString(R.string.my_like_numbers), mySnsInfo.likeCount) + "\n";
        showText += String.format(getString(R.string.my_liked_numbers), mySnsInfo.likedCount) + "\n";
        showText += String.format(getString(R.string.my_sent_comments), mySnsInfo.sentCommentCount) + "\n";
        showText += String.format(getString(R.string.my_received_comments), mySnsInfo.receivedCommentCount) + "\n";
        showText += String.format(getString(R.string.my_replied_comments), mySnsInfo.repliedCommentCount) + "\n";

        showText += "\n";
        showText += getString(R.string.sent_moment_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.momentRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.snsList.size() + ")\n";
            } catch (Exception e) {

            }
        }
        showText += "\n";
        showText += getString(R.string.sent_photo_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.photoRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.photoNumbers + ")\n";
            } catch (Exception e) {

            }
        }

        showText += "\n";
        showText += getString(R.string.like_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.likeRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.likeCount + ")\n";
            } catch (Exception e) {

            }
        }

        showText += "\n";
        showText += getString(R.string.liked_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.likedRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.likedCount + ")\n";
            } catch (Exception e) {

            }
        }

        showText += "\n";
        showText += getString(R.string.sent_comment_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.sentCommentRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.sentCommentCount + ")\n";
            } catch (Exception e) {

            }
        }

        showText += "\n";
        showText += getString(R.string.received_comment_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.receivedCommentRank.get(i);
                showText += userSnsInfo.userName;
                showText += "(" + userSnsInfo.receivedCommentCount + ")\n";
            } catch (Exception e) {

            }
        }

        showText += "\n";
        showText += getString(R.string.cold_rank) + "\n";
        for (int i=0;i<5;i++) {
            try {
                UserSnsInfo userSnsInfo = snsStat.coldRank.get(i);
                showText += userSnsInfo.userName;
                showText += String.format(getString(R.string.comment_details), userSnsInfo.sentCommentCount, userSnsInfo.repliedCommentCount) + "\n";
            } catch (Exception e) {

            }
        }

        mainTextView.setText(showText);
    }

    public void shareToTimeLine() {
        generateShareImage();
        File file = new File(Config.EXT_DIR + "/share_image.jpg");
        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.SEND");
            intent.setType("image/*");
            //intent.setFlags(0x3000001);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(intent);
        } catch (Exception e) {
            Log.e("wechatmomentstat", "exception", e);
        }
    }

    protected void generateShareImage() {
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.empty_background);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        String text = mainTextView.getText().toString();

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(50);
        tPaint.setColor(Color.BLUE);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(src, 0f, 0f, null);
        float y_coor = 60;
        String[] lineArray = text.split("\n");
        for (int i=0;i<lineArray.length;i++) {
            cs.drawText(lineArray[i],30, y_coor, tPaint);
            y_coor += 60;
        }
        cs.drawText(getString(R.string.share_suffix), 60, y_coor + 60, tPaint);
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(new File(Config.EXT_DIR + "/share_image.jpg")));
        } catch (FileNotFoundException e) {

        }

    }

}
