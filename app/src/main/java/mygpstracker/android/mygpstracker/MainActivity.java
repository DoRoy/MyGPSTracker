package mygpstracker.android.mygpstracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TextView textView_mainActivity;
    public Button refresh_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        textView_mainActivity = (TextView) findViewById(R.id.textView_mainActivity);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        textView_mainActivity.setText("Welcome");
        /*setContentView(R.layout.activity_main);

        BackgroundService.timesToTakeLocation = 6;
        BackgroundService.intervalsInMinutes = 30;
        BackgroundService.gpsLocation = new GPSLocation(this);

        startService(new Intent(this, BackgroundService.class));
        refresh(null);*/
    }

    public void refresh(){
        String content = Log.getInstance().read();
        textView_mainActivity.setText(content);
    }
}
