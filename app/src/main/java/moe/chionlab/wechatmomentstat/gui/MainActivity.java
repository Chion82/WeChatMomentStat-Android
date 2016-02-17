package moe.chionlab.wechatmomentstat.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import moe.chionlab.wechatmomentstat.R;

public class MainActivity extends AppCompatActivity {

    Task task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = new Task(this.getApplicationContext());
        
        setContentView(R.layout.activity_main);

        task.testRoot();

        ((Button)findViewById(R.id.launch_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                task.copySnsDB();
                task.restartWeChat();
            } catch (Throwable throwable) {
                Log.e("wechatmomentstat", throwable.getMessage());
            }
            }
        });
    }





}
