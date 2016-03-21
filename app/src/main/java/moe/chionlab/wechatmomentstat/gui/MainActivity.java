package moe.chionlab.wechatmomentstat.gui;


import android.os.AsyncTask;
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
                ((Button) findViewById(R.id.launch_button)).setText(R.string.exporting_sns);
                ((Button) findViewById(R.id.launch_button)).setEnabled(false);
                new RunningTask().execute();
            }
        });

    }

    class RunningTask extends AsyncTask<Void, Integer, Long> {

        Throwable error = null;

        @Override
        protected Long doInBackground(Void... params) {
            try {
                task.copySnsDB();
                task.initSnsReader();
                task.snsReader.run();
            } catch (Throwable e) {
                this.error = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            ((Button)findViewById(R.id.launch_button)).setText(R.string.launch);
            ((Button) findViewById(R.id.launch_button)).setEnabled(true);
            if (this.error != null) {
                Toast.makeText(MainActivity.this, R.string.not_rooted, Toast.LENGTH_LONG).show();
                Log.e("wechatmomentstat", "exception", this.error);
            }
        }
    }


}
