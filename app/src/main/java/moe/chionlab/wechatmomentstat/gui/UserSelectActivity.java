package moe.chionlab.wechatmomentstat.gui;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.Model.UserSnsInfo;
import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.common.Share;

public class UserSelectActivity extends AppCompatActivity {

    protected boolean isSelectedAll = true;
    protected ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_select_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.toggle_select_all_menu_btn:
                for (int i=0;i<checkBoxList.size();i++) {
                    checkBoxList.get(i).setChecked(!isSelectedAll);
                }
                isSelectedAll = !isSelectedAll;
                return true;
            case R.id.user_select_done_menu_btn:
                doneSelectingUsers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUserList();
    }

    protected void loadUserList() {
        LinearLayout userListContainer = (LinearLayout)findViewById(R.id.user_list_container);
        ArrayList<UserSnsInfo> userSnsList = Share.snsData.userSnsList;
        checkBoxList.clear();
        userListContainer.removeAllViews();
        for (int i=0;i<userSnsList.size();i++) {
            CheckBox userCheckBox = new CheckBox(this);
            userCheckBox.setText(userSnsList.get(i).userName + "(" + userSnsList.get(i).userId + ")" + "(" + String.format(getString(R.string.user_moment_count), userSnsList.get(i).snsList.size()) + ")");
            userListContainer.addView(userCheckBox);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)userCheckBox.getLayoutParams();
            layoutParams.setMargins(5, 5, 5, 5);
            userCheckBox.setLayoutParams(layoutParams);
            userCheckBox.setChecked(true);
            userCheckBox.setTag(userSnsList.get(i).userId);
            checkBoxList.add(userCheckBox);
        }
    }

    protected void doneSelectingUsers() {
        ArrayList<SnsInfo> snsList = Share.snsData.snsList;
        for (int i=0;i<snsList.size();i++) {
            snsList.get(i).selected = getUserCheckBox(snsList.get(i).authorId).isChecked();
        }
        finish();
    }

    protected CheckBox getUserCheckBox(String userId) {
        for (int i=0;i<checkBoxList.size();i++) {
            if (checkBoxList.get(i).getTag().equals(userId)) {
                return checkBoxList.get(i);
            }
        }
        return checkBoxList.get(0);
    }
}
