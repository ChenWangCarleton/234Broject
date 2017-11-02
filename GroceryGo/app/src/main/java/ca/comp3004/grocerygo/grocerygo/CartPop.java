package ca.comp3004.grocerygo.grocerygo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by AyeJay on 10/8/2017.
 */

public class CartPop extends Activity{
    MyDBHandler dbHandler;
    ArrayList<String> tempAL;
    Button process;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        Button button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_cart);
        dbHandler = new MyDBHandler(this, null, null, 2);

        //Setting popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.6));
        //Toast.makeText(CartPop.this,"Click an item to remove from cart",Toast.LENGTH_SHORT).show();



        ListView myViewList = (ListView) findViewById(R.id.groceryList);
        tempAL = dbHandler.dbToAS();
        if(tempAL != null) {
            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempAL);
            myViewList.setAdapter(listAdapter);
            myViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView selected = (TextView) view;

                    String deletedPro = selected.getText().toString();

                    Log.e("Client", deletedPro.substring(deletedPro.indexOf("-")+1));

                    dbHandler.deleteProduct(deletedPro.substring(deletedPro.lastIndexOf("-")+2));
                    onCreate(savedInstanceState);
                }
            });
        }

        process = (Button) findViewById(R.id.request);
        process.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Random r = new Random();
                                           int chance = r.nextInt(4 - 1) + 1;
                                           if(chance < 1) Toast.makeText(CartPop.this,"The best store to buy from is Walmart",Toast.LENGTH_LONG).show();
                                           else if(chance < 2) Toast.makeText(CartPop.this,"The best store to buy from is Loblaws",Toast.LENGTH_LONG).show();
                                           else Toast.makeText(CartPop.this,"The best store to buy from is Independent",Toast.LENGTH_LONG).show();

                                           dbHandler.removeAll();
                                           onCreate(savedInstanceState);
                                       }
                                   });

                dbHandler.close();
    }

}
