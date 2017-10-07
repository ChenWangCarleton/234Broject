package ca.comp3004.grocerygo.grocerygo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Abdullrhman on 10/7/2017.
 */

public class ItemPop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_item);

        //Setting popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.6));

        //Filling up the window
        Bundle data = getIntent().getExtras();
        String product;

        TextView productName = (TextView) findViewById(R.id.pName);
        if(data != null){
            product = data.getString("Product");
            productName.setText("Product: "+product);
            //Individual request

        } else {
            productName.setText("ERROR");
        }

    }
}
