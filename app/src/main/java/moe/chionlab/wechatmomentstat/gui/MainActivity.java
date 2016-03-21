package moe.chionlab.wechatmomentstat.gui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.Task;


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
                    ((Button)findViewById(R.id.launch_button)).setText(R.string.exporting_sns);
                    task.copySnsDB();
                    task.initSnsReader();
                    task.snsReader.run();
                    ((Button)findViewById(R.id.launch_button)).setText(R.string.launch);
                } catch (Throwable e) {
                    Toast.makeText(MainActivity.this, R.string.not_rooted, Toast.LENGTH_LONG).show();
                    Log.e("wechatmomentstat", "exception", e);
                }
            }
        });


    }


}
