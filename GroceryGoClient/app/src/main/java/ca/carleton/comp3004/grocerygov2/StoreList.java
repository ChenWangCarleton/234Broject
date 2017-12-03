package ca.carleton.comp3004.grocerygov2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class StoreList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String rawData = null;
    ArrayList<Product> data;
    ArrayList<String> listInfo = new ArrayList<String>();
    ArrayList<String> cats = new ArrayList<String>();
    ListView store;
    ArrayAdapter<String> storeAdapter;
    ServerRequset request = null;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        pd = ProgressDialog.show(this, "Processing...", "Retrieving store products.", true, false);



        try {
            request = new ServerRequset();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //********************************** Retrieve data
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    rawData = request.initialize("all");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(rawData != null) {
                            try {
                                data = request.readGetAll(rawData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if(pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                });

            }
        });
        thread.start();



        //********************************** Translate raw data

        //********************************** Adding Categories
        cats.add("Fruits & Vegetables");
        cats.add("Deli & Ready Meals");
        cats.add("Bakery");
        cats.add("Meat & Seafood");
        cats.add("Dairy and Eggs");
        cats.add("Drinks");
        cats.add("Frozen");
        cats.add("Pantry");

        for(String e: cats){
            listInfo.add(e);
        }


        //********************************** Populate list
        store = (ListView) findViewById(R.id.productList);

        //store.setBackgroundColor(Color.WHITE);
        //store.getBackground().setAlpha(175);

        storeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listInfo);

        store.setAdapter(storeAdapter);
        store.setOnItemClickListener(this);

        //********************************** Search bar functionality
        EditText search = findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                listInfo.clear();
                String searchWord = editable.toString();

                if(!searchWord.equals("")){
                    //Return all possible items with the word of choice.
                    for (Product e: data) {
                        if(e.na.toLowerCase().indexOf(searchWord.toLowerCase()) != -1)
                            listInfo.add(e.na);
                    }
                } else if(listInfo.isEmpty()){
                    for(String e: cats){
                        listInfo.add(e);
                    }
                } else {
                    for(String e: cats){
                        listInfo.add(e);
                    }
                }
                store.setAdapter(storeAdapter);
                storeAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView selected = (TextView) view;
        listInfo.clear();
        if(selected.getText().toString().equals("Go Back")){
            for(String e: cats){
                listInfo.add(e);
            }
        } else if(cats.contains(selected.getText().toString())) { //Selected category

            listInfo.clear();
            listInfo.add("Go Back");
            for (Product e: data) {
                if(e.ca.equals(selected.getText().toString()))
                    listInfo.add(e.na);
            }
        } else {
            Intent popIntent = new Intent(StoreList.this, ShowProduct.class);
            popIntent.putExtra("Product", selected.getText().toString());
            for(Product e: data){
                if(e.na.equals(selected.getText().toString()))
                    popIntent.putExtra("ID", e.id);
            }
            startActivity(popIntent);

            for(String e: cats){
                listInfo.add(e);
            }
        }
        
        store.setAdapter(storeAdapter);
        storeAdapter.notifyDataSetChanged();
    }
}
