package mygpstracker.android.mygpstracker.Review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.junit.rules.Timeout;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.AppExecutors;
import mygpstracker.android.mygpstracker.DB.ReviewInfoWrapper;
import mygpstracker.android.mygpstracker.DB.SqliteHelper;
import mygpstracker.android.mygpstracker.Places.MyPlaces;
import mygpstracker.android.mygpstracker.Places.PlaceArrayAdapter;
import mygpstracker.android.mygpstracker.R;

public class ReviewActivity extends Activity {

    private final String TAG = "ReviewActivity";

    RadioGroup radioGroup;
    Spinner places_spinner;
    AutoCompleteTextView autoCompleteTextViewPlace_review;
    Button next_btn;

    MyPlaces myPlaces;
    Place[] placesList;
    String chosenPlaceID;


    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            chosenPlaceID = placeId;
            Log.i(TAG, "Selected: " + item.description);
            AppExecutors.getInstance().networkIO().execute(() -> {
                List<Place> placeListFromID = myPlaces.getPlaceByID(placeId);
                placesList = (Place[]) placeListFromID.toArray();
            });
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            resetAutoCompleteText();

        }


    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeWidgets();
        if (myPlaces == null){
            myPlaces = new MyPlaces(this);
            if (mPlaceArrayAdapter != null){
                myPlaces.setPlaceArrayAdapter(mPlaceArrayAdapter);
            }
            setNewPlaces();
        }




    }

    private void initializeWidgets() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        autoCompleteTextViewPlace_review = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewPlace_review);
        autoCompleteTextViewPlace_review.setEnabled(false);
        autoCompleteTextViewPlace_review.setThreshold(3);
        autoCompleteTextViewPlace_review.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        autoCompleteTextViewPlace_review.setAdapter(mPlaceArrayAdapter);
        inPlace = true;
        places_spinner = (Spinner) findViewById(R.id.places_spinner);
        places_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("Place picked: " + placesList[position].getName());
                if (placesList != null) {
                    System.out.println("Place picked: " + position);
                    chosenPlaceID = placesList[position].getId();
                    next_btn.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Nothing was clicked");
            }


        });
//        if (places_spinner != null)
//            setNewPlaces();
        next_btn = (Button) findViewById(R.id.next_btn);



    }

    private void setNewPlacesInSpinner(String[] newList) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, newList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(()->places_spinner.setAdapter(dataAdapter));

    }

/*    private void setNewPlaces(){
        Map<Place, Float> tempMap = myPlaces.guessCurrentPlace();
        placesList = new Place[tempMap.size()];
        String[] placesListNames = new String[tempMap.size()];
        int i = 0;
        for(Map.Entry<Place,Float> entry : tempMap.entrySet()){
            placesList[i] = entry.getKey().freeze();
            placesListNames[i] = String.valueOf(entry.getKey().freeze().getName());
            i++;
        }
        setNewPlacesInSpinner(placesListNames);
    }*/

    private void setNewPlaces() {
        AppExecutors.getInstance().networkIO().execute(()-> {
            Map<Place, Float>  tempMap = myPlaces.guessCurrentPlace();

            String[] places = {"Amsterdam", "London", "Rio"};
            if (tempMap != null)
                places = new String[tempMap.size()];
            int i = 0;
            for (Map.Entry<Place, Float> pair : tempMap.entrySet()) {
                if (pair.getKey().getName() != null && String.valueOf(pair.getKey().getName()) != null){
                    places[i] = String.valueOf(pair.getKey().getName());
                    i++;
                }
            }
            if (places_spinner != null)
                setNewPlacesInSpinner(places);
        });
    }


    private void resetAutoCompleteText() {
        autoCompleteTextViewPlace_review.setText("");
    }

    public void radioButtonClicked(View v) {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(radioButtonID);

        Toast.makeText(getBaseContext(), rb.getText(), Toast.LENGTH_LONG).show();
        System.out.println("Click: " + rb.getText() + "ID: " + radioButtonID );

        switch (rb.getId()){
            case R.id.first:
                places_spinner.setEnabled(true);
                inPlace = true;
                autoCompleteTextViewPlace_review.setEnabled(false);
                break;
            case R.id.second:
                places_spinner.setEnabled(false);
                inPlace = false;
                autoCompleteTextViewPlace_review.setEnabled(true);
                break;
        }
    }

    public void nextButtonClicked(View v){
        setContentView(R.layout.activity_review_form);
        startForm();

    }



    /**     ######## FORM ######      **/

    public SimpleRatingBar ratingBar;
    public EditText EditTextFeedbackBody;
    public CheckBox checkBox_review;
    public EditText review_date;
    public EditText review_time;
    public Spinner company_spinner;
    public Spinner freq_spinner;
    public Spinner purpose_spinner;
    public Button submit_btn;

    private float ratings = 0;
    private boolean inPlace;


    private void startForm() {
        ratingBar = (SimpleRatingBar) this.findViewById(R.id.review_ratingBar);
        review_date = (EditText) this.findViewById(R.id.review_date);
        review_time = (EditText) this.findViewById(R.id.review_time);
        EditTextFeedbackBody = (EditText) this.findViewById(R.id.EditTextFeedbackBody);
        company_spinner = (Spinner) this.findViewById(R.id.company_spinner);
        purpose_spinner = (Spinner) this.findViewById(R.id.purpose_spinner);
        freq_spinner = (Spinner) this.findViewById(R.id.freq_spinner);
        submit_btn = (Button) this.findViewById(R.id.submit_btn);
        ratingBar.setStepSize(1);
        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                if(fromUser) {
                    ratings = rating;
                }
            }
        });
        checkBox_review = (CheckBox) findViewById(R.id.checkBox_review);
        checkBox_review.setChecked(inPlace);
        checkBox_review.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setInPlace(isChecked);
            }
        });
        setInPlace(inPlace);

        EditTextFeedbackBody.clearFocus();


    }

    private void setInPlace(boolean inPlace){
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterTime = new SimpleDateFormat(" HH:mm");
        Calendar.getInstance().getTime();
        Date currentTime = Calendar.getInstance().getTime();
        review_date.setText(formatterDate.format(currentTime.getTime()));
        review_time.setText(formatterTime.format(currentTime.getTime()));
        if (inPlace){
            review_time.setEnabled(false);
            review_date.setEnabled(false);
        }
        else{
            review_time.setEnabled(true);
            review_date.setEnabled(true);
        }
    }

    public void onSubmitButtonClicked(View view){
        System.out.println("Ratings: " + ratings);
        System.out.println("Review: " + EditTextFeedbackBody.getText());
        System.out.println("Time: " + review_time.getText());
        System.out.println("Date: " + review_date.getText());
        System.out.println("Companion: " + company_spinner.getSelectedItem());
        System.out.println("Frequency: " + freq_spinner.getSelectedItem());
        System.out.println("Purpose: " + purpose_spinner.getSelectedItem());

        ReviewInfoWrapper reviewInfoWrapper = new ReviewInfoWrapper(chosenPlaceID, ""+EditTextFeedbackBody.getText(),review_date.getText() + " " + review_time.getText(), ""+company_spinner.getSelectedItem(),""+freq_spinner.getSelectedItem(),""+ purpose_spinner.getSelectedItem(), String.valueOf(ratings));
        SqliteHelper sqliteHelper = new SqliteHelper(getApplicationContext());
        sqliteHelper.createReviewRecord(reviewInfoWrapper);
    }

}
