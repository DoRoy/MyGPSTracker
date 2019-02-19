package mygpstracker.android.mygpstracker.RecyclerViewTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.Calendar;
import java.util.Date;

import mygpstracker.android.mygpstracker.R;

/**
 * Created by doroy on 14-Oct-18.
 */

public class AddLocationActivity extends Activity {


    EditText editText;
    Button button;
    SimpleRatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_add_location);


        editText = (EditText) findViewById(R.id.frame_editText_name);

        ratingBar = (SimpleRatingBar) findViewById(R.id.frame_ratingBar);
        ratingBar.setStepSize(1);

        button = (Button) findViewById(R.id.frame_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerLocationLog.getInstance().writeWithResolver(editText.getText().toString(), "0", "" + ratingBar.getRating());
                dismissDialog(v.getId());
            }
        });
    }

}
