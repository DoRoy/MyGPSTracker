package mygpstracker.android.mygpstracker.Places;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Map;

import mygpstracker.android.mygpstracker.AppExecutors;
import mygpstracker.android.mygpstracker.*;

public class ActivityPlaces extends AppCompatActivity {

    private final String TAG = "ActivityPlaces";

    public ImageView imageView_place;
    public Button btn_next_place;
    public Button btn_prev_place;
    public Button btn_next_photo;
    public Button btn_prev_photo;
    public TextView tv_photo_number;
    public TextView tv_place_info;
    private AutoCompleteTextView mAutocompleteTextView;

    private int placeIndx = 0;
    private int photoIndx = 0;
    private int placeSize = 0;
    private int photoSize = 0;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            AppExecutors.getInstance().networkIO().execute(() -> {
                List<Place> placeListFromID = myPlaces.getPlaceByID(placeId);
                setPlaceFromPlaceID(placeListFromID);
            });
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            resetAutoCompleteText();

        }


    };
    private void resetAutoCompleteText() {
        mAutocompleteTextView.setText("");
    }



    MyPlaces myPlaces;
    Place[] placesList;
    float[] likelihood;
    Place currentPlace;
    List<PlacePhotoMetadata> photosDataList;
    Bitmap currentPhoto;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCraete");
        setContentView(R.layout.activity_place);
        initialWidgetsAndListeners();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (myPlaces == null){
            myPlaces = new MyPlaces(this);
            if (mPlaceArrayAdapter != null){
                myPlaces.setPlaceArrayAdapter(mPlaceArrayAdapter);
            }
            setNewLocation();
        }
        super.onCreate(savedInstanceState);

    }

    private void initialWidgetsAndListeners() {
        imageView_place = (ImageView) findViewById(R.id.imageView_place);

        btn_next_place = (Button) findViewById((R.id.btn_next_place));
        btn_next_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeSize > 1) {
                    placeIndx = (placeIndx + 1) % placeSize;
                    setNewPlace();
                }
            }
        });

        btn_prev_place = (Button) findViewById((R.id.btn_prev_place));
        btn_prev_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeSize > 1) {
                    if (placeIndx == 0) {
                        placeIndx = placeSize - 1;
                    } else {
                        placeIndx = placeIndx - 1;

                    }
                    setNewPlace();
                }
            }
        });

        btn_next_photo = (Button) findViewById((R.id.btn_next_photo));
        btn_next_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoSize > 1) {
                    photoIndx = (photoIndx + 1) % photoSize;
                    setNewPhoto();
                }
            }
        });

        btn_prev_photo = (Button) findViewById((R.id.btn_prev_photo));
        btn_prev_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoSize > 1) {
                    if (photoIndx == 0) {
                        photoIndx = photoSize - 1;
                    } else {
                        photoIndx = photoIndx - 1;
                    }
                    setNewPhoto();
                }
            }
        });

        tv_photo_number = (TextView) findViewById(R.id.tv_photo_number);

        tv_place_info = (TextView) findViewById(R.id.tv_place_info);

        mAutocompleteTextView = (AutoCompleteTextView) findViewById((R.id.autoCompleteTextViewPlace));
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }

    // initialize the menu button
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.places_menu, menu);
        return true;
    }

    // listener for the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.places_reset:
                resetAutoCompleteText();
                setNewLocation();
                break;

        }
        return true;
    }

    private void setNewLocation() {

        AppExecutors.getInstance().networkIO().execute(()-> {
            Map<Place, Float>  tempMap = myPlaces.guessCurrentPlace();

            placesList = new Place[tempMap.size()];
            likelihood = new float[tempMap.size()];
            Log.d(TAG, "setNewLocation - " + placesList.length + " Places");
            int i = 0;
            for(Map.Entry<Place,Float> entry : tempMap.entrySet()){
                placesList[i] = entry.getKey().freeze();
                likelihood[i] = entry.getValue();
                i++;
            }
            if(placesList != null){
                placeSize = placesList.length;
                if(placeSize > 0) {
                    setNewPlace();
                }
            }
        });
    }

    private void setPlaceFromPlaceID(List<Place> placeListFromID) {
        placesList = new Place[placeListFromID.size()];
        likelihood = null;
        Log.d(TAG, "setPlaceFromPlaceID - " + placesList.length + " Places");
        int i = 0;
        for(Place place : placeListFromID){
            placesList[i] = place;
            i++;
        }
        if(placesList != null){
            placeSize = placesList.length;
            placeIndx = 0;
            if(placeSize > 0) {
                setNewPlace();
            }
        }
    }



    private void setNewPlace(){
        Log.d(TAG, "setNewPlace");
        currentPlace = placesList[placeIndx];
        AppExecutors.getInstance().networkIO().execute(()-> {
            photosDataList = myPlaces.getPhotoMetadata(currentPlace.getId());
            photoIndx = 0;
            photoSize = photosDataList.size();
            Log.d(TAG, "setNewPlace - " + photoSize + " Photos exist");
            String placeInfo = "Place:  " + (placeIndx + 1) + "/" + placeSize +"\n";
            placeInfo += currentPlace.getName() + "\n";
            if(likelihood != null && likelihood.length < placeIndx) {
                placeInfo += "\tLikelihood: " + (int) (likelihood[placeIndx] * 100) + "%\n";
            }
            placeInfo += "\tID: "  + currentPlace.getId() + "\n";
            placeInfo += "\tLatLon: "  + currentPlace.getLatLng().latitude + ", " + currentPlace.getLatLng().longitude + "\n";
            placeInfo += "\tAddress: " + currentPlace.getAddress() + "\n";
            placeInfo += "\tAttribution: " + currentPlace.getAttributions() + "\n";
            placeInfo += "\tRating: " + currentPlace.getRating() + "\n";
            placeInfo += "\tPhone: " + currentPlace.getPhoneNumber() + "\n";
            placeInfo += "\tPrice Level: " + currentPlace.getPriceLevel() + "\n";
            placeInfo += "\tWebsite Uri: " + currentPlace.getWebsiteUri() + "\n";
            List<Integer> typeList = currentPlace.getPlaceTypes();
            placeInfo += "\tTypes: ";
            for(Integer intType:typeList){
                placeInfo += intType + " ";
            }




            String finalPlaceInfo = placeInfo;

            AppExecutors.getInstance().mainThread().execute(() -> {
                tv_place_info.setText(finalPlaceInfo);
            });

            setNewPhoto();
        });
    }

    private void setNewPhoto(){
        Log.d(TAG, "setNewPhoto");
        AppExecutors.getInstance().networkIO().execute(() -> {
            if(photoSize > 0) {
                currentPhoto = myPlaces.getPhotoBitmap(photosDataList.get(photoIndx));
                Log.d(TAG, "setNewPhoto - currentPhoto changed");
                AppExecutors.getInstance().mainThread().execute(() -> {
                    String photoInfo = "Photo " + (photoIndx + 1) + "/" + photoSize;
                    tv_photo_number.setText(photoInfo );
                    imageView_place.invalidate();
                    imageView_place.setImageBitmap(currentPhoto);
                    imageView_place.setVisibility(View.VISIBLE);
                    btn_prev_photo.setEnabled(true);
                    btn_next_photo.setEnabled(true);
                    Log.d(TAG, "setNewPhoto - new bitmap has been set");
                });
            }
            else{
                Log.d(TAG, "setNewPhoto - no photos");
                String photoInfo = "This place has no photos available";
                AppExecutors.getInstance().mainThread().execute(() -> {
                    tv_photo_number.setText(photoInfo);
                    btn_prev_photo.setEnabled(false);
                    btn_next_photo.setEnabled(false);
                    imageView_place.setVisibility(View.INVISIBLE);
                });

            }
        });
    }

}
