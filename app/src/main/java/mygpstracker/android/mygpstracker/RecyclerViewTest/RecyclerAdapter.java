package mygpstracker.android.mygpstracker.RecyclerViewTest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import mygpstracker.android.mygpstracker.R;

/**
 * Created by doroy on 11-Oct-18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.LocationViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    final protected ICLickListener mOnClickListener;

    private static int viewHolderCount;

    private int mNumberItems;

    ArrayList<RecyclerLocation> locations;

    public RecyclerAdapter(ICLickListener mOnClickListener, int mNumberItems, ArrayList<RecyclerLocation> locations) {
        this.mOnClickListener = mOnClickListener;
        /*this.mNumberItems = mNumberItems;*/
        this.mNumberItems = locations.size();
        viewHolderCount = 0;
        this.locations = locations;
    }


    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesnt) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.number_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        LocationViewHolder viewHolder = new LocationViewHolder(view);

        viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);




        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapters data set.
     */
    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        RecyclerLocation recyclerLocation = locations.get(position);
        holder.bind(position);
        holder.ratingBar.setRating(recyclerLocation.getRating());
        holder.viewHolderIndex.setText(recyclerLocation.getName());
    }


    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return mNumberItems;
    }



    /**
     * The interface that receives onClick messages.
     */

    class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView listItemNumberView;
        // Will display which ViewHolder is displaying this data
        TextView viewHolderIndex;

        SimpleRatingBar ratingBar;


        public LocationViewHolder(View itemView) {
            super(itemView);

            //TODO - make the ratingBar clickable with indication & animation
            //TODO - make a list of locations and make the recyclerView fit them, and try to save the ranks in DB and question the DB about it. make the data consistent

            listItemNumberView = (TextView) itemView.findViewById(R.id.tv_item_number);
            viewHolderIndex = (TextView) itemView.findViewById(R.id.tv_view_holder_instance);
            ratingBar = (SimpleRatingBar) itemView.findViewById(R.id.ratingBar);
            ratingBar.setStepSize(1);
            ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                    if(fromUser) {
                        int clickedPosition = getAdapterPosition();
                        locations.get(clickedPosition).setRating((int)rating);
                    }
                }
            });
            ratingBar.setOnClickListener(this);

        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(listIndex));
        }


        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            System.out.println("onClick: " + v);
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }


    }
}
