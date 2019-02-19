package mygpstracker.android.mygpstracker.RecyclerViewTest;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RatingBar;
import android.widget.Toast;


import mygpstracker.android.mygpstracker.R;
import mygpstracker.android.mygpstracker.Settings;


/**
 * Created by doroy on 11-Oct-18.
 */

public class RecyclerActivity extends AppCompatActivity implements ICLickListener {

    private static final int NUM_LIST_ITEMS = 100;

    private RecyclerAdapter mAdapter;
    private RecyclerView mRecyclerList;

    private FloatingActionButton floatingActionButton;


    private Toast mToast;


    private ContentResolver myContentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerList = (RecyclerView) findViewById(R.id.rv_locations);
                /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you dont specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we dont need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerList.setLayoutManager(layoutManager);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        initialFloatingActionButton();

        myContentProvider = this.getContentResolver();
        RecyclerLocationLog.getInstance().setResolver(myContentProvider);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new RecyclerAdapter(this,NUM_LIST_ITEMS, RecyclerLocationCollection.getLocations());
        mRecyclerList.setAdapter(mAdapter);




    }

    /**
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        // COMPLETED (11) In the beginning of the method, cancel the Toast if it isnt null
        /*
         * Even if a Toast isnt showing, its okay to cancel it. Doing so
         * ensures that our new Toast will show immediately, rather than
         * being delayed while other pending Toasts are shown.
         *
         * Comment out these three lines, run the app, and click on a bunch of
         * different items if youre not sure what Im talking about.
         */
        if (mToast != null) {
            mToast.cancel();
        }

        // COMPLETED (12) Show a Toast when an item is clicked, displaying that item number that was clicked
        /*
         * Create a Toast and store it in our Toast field.
         * The Toast that shows up will have a message similar to the following:
         *
         *                     Item #42 clicked.
         */
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();
    }

    private void initialFloatingActionButton(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addLocation = new Intent(v.getContext(), AddLocationActivity.class);
                startActivity(addLocation);
            }
        });
    }

}
