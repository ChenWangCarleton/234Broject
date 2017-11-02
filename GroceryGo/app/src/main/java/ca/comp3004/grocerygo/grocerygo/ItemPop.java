package ca.comp3004.grocerygo.grocerygo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Abdullrhman on 10/7/2017.
 */

public class ItemPop extends Activity {

    MyDBHandler dbHandler;
    ListView myViewList;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> tempAL = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_item);

        //Setting popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.5));
        dbHandler = new MyDBHandler(this, null, null, 2);

        myViewList = (ListView) findViewById(R.id.Prices);

        //Filling up the window
        Bundle data = getIntent().getExtras();
        final String product;
        final String productCat;
        final int productID;

        TextView productName = (TextView) findViewById(R.id.pName);
        if (data != null) {
            product = data.getString("Product");
            productID = data.getInt("ID");

            TFTPClient itemReq = new TFTPClient();

            mainItem requestedItem = null;

            try {
                requestedItem = itemReq.getItem(""+productID);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            int counter = 0;

            if(requestedItem != null){
                for(String e: requestedItem.price){
                    if(e != null) {
                        switch(counter) {
                            case 0:
                                tempAL.add("Loblaws: " + e);
                                break;
                            case 1:
                                tempAL.add("Independent: " + e);
                                break;
                            case 2:
                                tempAL.add("Walmart: " + e);
                                break;
                        }
                    } else {
                        switch(counter){
                            case 0:
                                tempAL.add("Loblaws: N/A");
                                break;
                            case 1:
                                tempAL.add("Independent: N/A");
                                break;
                            case 2:
                                tempAL.add("Walmart: N/A");
                                break;
                        }
                    }
                    counter++;
                }
                listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tempAL);
                myViewList.setAdapter(listAdapter);
            }



            productName.setText("Product: " + product);
            //Individual request

            Button button = (Button) findViewById(R.id.addItem);
            final String finalProduct = data.getString("Product");
            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    dbHandler.addProduct(new Product(product, productID));
                    Toast.makeText(ItemPop.this,"Added", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            productName.setText("ERROR");
        }
        dbHandler.close();
    }
}
