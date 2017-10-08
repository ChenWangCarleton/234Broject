package ca.comp3004.grocerygo.grocerygo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ListView myViewList;
    Button cart;
    Map<String, String[]> myList = new HashMap<String, String[]>();
    ArrayAdapter<String> listAdapter;
    ArrayList<String> tempAL = new ArrayList<String>(); //Temporary Array List
    MyDBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(this, null, null, 2);

        //Populating the map with products and categories from the database
        //Implementing retrevial from server
        myList.put("Fruits & Veggies",new String[] {"Apple","Orange","Grapes","Cucumber","ETC"});
        myList.put("Proteins",new String[]{"Beef","Chicken","Fish","Turkey","ETC"});
        myList.put("Dairy",new String[]{"Milk","Yogurt","Cream","ETC"});
        myList.put("Grains",new String[]{"Flour","Flat Bread","Toast","ETC"});
        myList.put("Oils",new String[]{"Olive Oil","Sunflower Oil","Sesame oil","ETC"});

        //Creating a list of all categories
        Set<String> myListKeys = myList.keySet();
        for(String val : myListKeys){
            tempAL.add(val);
        }

        //Populating list
        myViewList = (ListView) findViewById(R.id.groceryList);
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tempAL);
        myViewList.setAdapter(listAdapter);
        myViewList.setOnItemClickListener(this);

        //Creating cart
        cart = (Button) findViewById(R.id.myList);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // View cart
                Intent cartIntent = new Intent(MainActivity.this, CartPop.class);
                startActivity(cartIntent);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Selected item
        TextView selected = (TextView) view;
        tempAL.clear();

        //Return to categories
        if(selected.getText().toString().equals("Go Back")){
            Set<String> myListKeys = myList.keySet();
            for(String val : myListKeys){
                tempAL.add(val);
            }
        } else if(myList.get(selected.getText().toString()) != null) { //Selected category

            tempAL.clear();
            tempAL.add("Go Back");
            for (String val : myList.get(selected.getText().toString())) {
                tempAL.add(val);
            }
        } else {
            Intent popIntent = new Intent(MainActivity.this, ItemPop.class);
            popIntent.putExtra("Product", selected.getText().toString());
            startActivity(popIntent);

            //Adding item into the CART


            //After leaving
            Set<String> myListKeys = myList.keySet();
            for(String val : myListKeys){
                tempAL.add(val);
            }
        }

        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,tempAL);
        myViewList.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
}
