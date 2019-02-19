package mygpstracker.android.mygpstracker.RecyclerViewTest;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doroy on 14-Oct-18.
 */

public class RecyclerLocationCollection {

    public static ArrayList<RecyclerLocation> getLocations(){
        ArrayList<RecyclerLocation> locations = new ArrayList<>();

        List<String[]> list = RecyclerLocationLog.getInstance().readWithResolver();

        for(String[] stringArr: list){
            RecyclerLocation rl = new RecyclerLocation();
            rl.setName(stringArr[0]);
            rl.setRating(Integer.valueOf(stringArr[2]));
            locations.add(rl);
        }
/*        RecyclerLocation rl = new RecyclerLocation();
        rl.setName("Aroma");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Snitzale");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Manga");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Benji");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Thina");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Blender");
        rl.setRating(3);
        locations.add(rl);

        rl = new RecyclerLocation();
        rl.setName("Lee Office");
        rl.setRating(3);
        locations.add(rl);*/

        return locations;
    }
}
