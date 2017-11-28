package ca.carleton.comp3004.grocerygov2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ShowProduct extends AppCompatActivity {
    DataBase userList;
    ListView storePrices;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> listPrices = new ArrayList<String>();
    TextView productName;
    TextView description;
    Button addProduct;
    Product selectedProduct = null;
    ImageView proImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        //Server request******************
        Bundle data = getIntent().getExtras();
        int proID = data.getInt("ID");
        ServerRequset request = null;

        storePrices = (ListView) findViewById(R.id.storePrices);
        productName = (TextView) findViewById(R.id.productName);
        description = (TextView) findViewById(R.id.description);
        addProduct = (Button) findViewById(R.id.addProduct);
        proImg = (ImageView) findViewById(R.id.proImg);
        //storePrices.setBackgroundColor(Color.WHITE);
        //storePrices.getBackground().setAlpha(175);
        userList = new DataBase(this, null, null, 1);

        try {
            request = new ServerRequset();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Log.e("Testing: ", "Request initilized");

        if(request != null){
            //Log.e("Testing: ", "Sending request");
            try {
                String rawData = request.initialize(""+proID);
                Log.e("Testing: ", rawData);
                selectedProduct = request.readSearchID(rawData);
                //Log.e("Testing: ", "Request recieved");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Error: ", "Server error");
        }

        //Log.e("Testing: ", "Request sent");

        productName.setText(selectedProduct.name);
        description.setText(selectedProduct.description);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(selectedProduct.image).getContent());
            proImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }


        int counter = 0;
        if(selectedProduct != null){
            for(String e: selectedProduct.price){
                if(e != null) {
                    switch(counter) {
                        case 0:
                            listPrices.add("Loblaws: " + e);
                            break;
                        case 1:
                            listPrices.add("Independent: " + e);
                            break;
                        case 2:
                            listPrices.add("Walmart: " + e);
                            break;
                    }
                } else {
                    switch(counter){
                        case 0:
                            listPrices.add("Loblaws: N/A");
                            break;
                        case 1:
                            listPrices.add("Independent: N/A");
                            break;
                        case 2:
                            listPrices.add("Walmart: N/A");
                            break;
                    }
                }
                counter++;
            }
            listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listPrices);
            storePrices.setAdapter(listAdapter);
        }

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userList.addProduct(selectedProduct);
                Toast.makeText(ShowProduct.this,"Added",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
