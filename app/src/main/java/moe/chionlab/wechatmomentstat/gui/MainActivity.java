package moe.chionlab.wechatmomentstat.gui;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import moe.chionlab.wechatmomentstat.R;
import moe.chionlab.wechatmomentstat.SnsStat;
import moe.chionlab.wechatmomentstat.Task;
import moe.chionlab.wechatmomentstat.common.Share;


public class MainActivity extends AppCompatActivity {

    Task task = null;
    SnsStat snsStat = null;

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

        TextView descriptionHtmlTextView = (TextView)findViewById(R.id.description_html_textview);
        descriptionHtmlTextView.setMovementMethod(LinkMovementMethod.getInstance());
        descriptionHtmlTextView.setText(Html.fromHtml(getResources().getString(R.string.description_html)));

    }

    class RunningTask extends AsyncTask<Void, Void, Void> {

        Throwable error = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                task.copySnsDB();
                task.initSnsReader();
                task.snsReader.run();
                snsStat = new SnsStat(task.snsReader.getSnsList());
            } catch (Throwable e) {
                this.error = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voidParam) {
            super.onPostExecute(voidParam);
            ((Button)findViewById(R.id.launch_button)).setText(R.string.launch);
            ((Button) findViewById(R.id.launch_button)).setEnabled(true);
            if (this.error != null) {
                Toast.makeText(MainActivity.this, R.string.not_rooted, Toast.LENGTH_LONG).show();
                Log.e("wechatmomentstat", "exception", this.error);
                return;
            }
            Share.snsData = snsStat;
            Intent intent = new Intent(MainActivity.this, MomentStatActivity.class);
            startActivity(intent);
        }
    }


}
