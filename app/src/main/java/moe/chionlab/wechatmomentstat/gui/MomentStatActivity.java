package moe.chionlab.wechatmomentstat.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import moe.chionlab.wechatmomentstat.Model.UserSnsInfo;
import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.SnsStat;
import moe.chionlab.wechatmomentstat.common.Share;


public class MomentStatActivity extends AppCompatActivity {

    TextView mainTextView;
    SnsStat snsStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_stat);
        snsStat = Share.snsData;
        mainTextView = (TextView)findViewById(R.id.moment_stat_main_textview);
        showMomentStat();
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

        mainTextView.setText(showText);
    }
}
