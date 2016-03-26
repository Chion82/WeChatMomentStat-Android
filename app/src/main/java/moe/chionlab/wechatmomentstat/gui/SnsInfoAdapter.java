package moe.chionlab.wechatmomentstat.gui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.R;



/**
 * Created by chiontang on 3/26/16.
 */
public class SnsInfoAdapter extends ArrayAdapter<SnsInfo> {

    protected ArrayList<SnsInfo> snsList = null;

    public SnsInfoAdapter(Context context, int resource, ArrayList<SnsInfo> snsList) {
        super(context, resource, snsList);
        this.snsList = snsList;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;

        SnsInfo snsInfo = snsList.get(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sns_item, null);

            final CheckBox selectedCheckBox = (CheckBox) view.findViewById(R.id.sns_item_username);
            TextView snsContentTextView = (TextView) view.findViewById(R.id.sns_item_text_content);
            TextView snsTimeTextView = (TextView) view.findViewById(R.id.sns_item_time);
            LinearLayout photoContainer = (LinearLayout) view.findViewById(R.id.sns_item_photo_layout);

            viewHolder = new ViewHolder();
            viewHolder.selectedCheckBox = selectedCheckBox;
            viewHolder.snsContentTextView = snsContentTextView;
            viewHolder.snsTimeTextView = snsTimeTextView;
            viewHolder.photoContainer = photoContainer;

            for (int i=0;i<10;i++) {
                ImageView snsImageView = new ImageView(getContext());
                viewHolder.imageViewList.add(snsImageView);
            }

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.selectedCheckBox.setText(snsInfo.authorName);
        viewHolder.selectedCheckBox.setChecked(snsInfo.selected);
        viewHolder.snsContentTextView.setText(snsInfo.content);
        viewHolder.snsTimeTextView.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date(snsInfo.timestamp * 1000)));
        viewHolder.selectedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnsInfo snsInfo = snsList.get(position);
                snsInfo.selected = viewHolder.selectedCheckBox.isChecked();
            }
        });

        viewHolder.photoContainer.removeAllViews();
        for (int i=0;i<snsInfo.mediaList.size();i++) {
            ImageView snsImageView = null;
            try {
                snsImageView = viewHolder.imageViewList.get(i);
            } catch (IndexOutOfBoundsException exception) {
                snsImageView = new ImageView(getContext());
                viewHolder.imageViewList.add(snsImageView);
            }

            try {
                String imageUrl = snsInfo.mediaList.get(i);
                if (imageUrl.startsWith("http://mmsns.qpic.cn/mmsns")) {

                    viewHolder.photoContainer.addView(snsImageView);

                    snsImageView.setImageBitmap(null);
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(imageUrl, snsImageView);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) snsImageView.getLayoutParams();
                    layoutParams.setMargins(0, 0, 10, 0);
                    layoutParams.height = 200;
                    layoutParams.width = 200;
                    snsImageView.setLayoutParams(layoutParams);
                }
            } catch (Exception e) {
                Log.e("wechatmomentstat", "exception", e);
            }

        }

        return view;
    }

    static protected class ViewHolder {
        CheckBox selectedCheckBox;
        TextView snsContentTextView;
        TextView snsTimeTextView;
        LinearLayout photoContainer;
        ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
    }
}
