package mygpstracker.android.mygpstracker;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Settings extends AppCompatActivity {

    EditText editText_TimesToTakeIntervals;
    EditText editText_Intervals;
    Button btn_OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editText_TimesToTakeIntervals = (EditText) findViewById(R.id.editText_TimesToTakeLocation);
        editText_TimesToTakeIntervals.setText(Integer.toString(MainActivity.timesToTakeLocation));

        editText_Intervals = (EditText) findViewById(R.id.editText_Intervals);
        editText_Intervals.setText(Double.toString(MainActivity.intervals));

        btn_OK = (Button) findViewById(R.id.btn_OK);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.valueOf(editText_Intervals.getText().toString()) > 0 && Double.valueOf(editText_Intervals.getText().toString()) != MainActivity.intervals ) {
                    System.out.println(Double.valueOf(editText_Intervals.getText().toString()));
                    MainActivity.intervals = Double.valueOf(editText_Intervals.getText().toString());
                    MainActivity.didChanged = true;
                }
                if(Integer.valueOf(editText_TimesToTakeIntervals.getText().toString()) > 0 && Integer.valueOf(editText_TimesToTakeIntervals.getText().toString()) != MainActivity.timesToTakeLocation) {
                    System.out.println(Integer.valueOf(editText_TimesToTakeIntervals.getText().toString()));
                    MainActivity.timesToTakeLocation = Integer.valueOf(editText_TimesToTakeIntervals.getText().toString());
                    MainActivity.didChanged = true;
                }
                closeWindow();
            }

        });


    }

    private void closeWindow() {
        this.finish();
    }


}
