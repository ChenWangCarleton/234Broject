package ca.comp3004.grocerygo.grocerygo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Abdullrhman on 10/7/2017.
 */

public class ItemPop extends Activity {

    MyDBHandler dbHandler;

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
        dbHandler = new MyDBHandler(this, null, null, 2);

        //Filling up the window
        Bundle data = getIntent().getExtras();
        final String product;

        TextView productName = (TextView) findViewById(R.id.pName);
        if (data != null) {
            product = data.getString("Product");
            productName.setText("Product: " + product);
            //Individual request

            Button button = (Button) findViewById(R.id.addItem);
            final String finalProduct = data.getString("Product");
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    dbHandler.addProduct(new Product(product));
                    Toast.makeText(ItemPop.this, "Added", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            productName.setText("ERROR");
        }





    }
}
