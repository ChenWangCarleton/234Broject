package com.a234broject.grocerygo.grocerygo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrontUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_ui);
        ListView listView=(ListView)findViewById(R.id.productList);
        String[] items = {"Dairy", "Fruits", "Veggies"};
        final ArrayList<String> listItems = new ArrayList<String>();
        listItems.add("Dairy");
        listItems.add("Fruits");
        listItems.add("Veggies");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, listItems );
        listView.setAdapter( adapter );
    }
}
