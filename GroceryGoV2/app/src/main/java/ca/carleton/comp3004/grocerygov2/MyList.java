package ca.carleton.comp3004.grocerygov2;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyList extends AppCompatActivity {
    ListView list;
    DataBase userList;
    ArrayList<String> listValues;
    ArrayAdapter<String> listAdapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        //Adding back button top left
        userList = new DataBase(this, null, null, 1);
        list = findViewById(R.id.userList);
        //list.setBackgroundColor(Color.WHITE);
        //list.getBackground().setAlpha(175);
        //listValues = userList.userProductsList();

        update();
    }
    private void update(){
        listValues = userList.userProductsList();
        if(listValues != null){
            Log.e("Error: ", "HERE");
            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listValues);
            list.setAdapter(listAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView selected = (TextView) view;

                    String deleted = selected.getText().toString();

                    userList.deleteProduct(deleted.substring(deleted.lastIndexOf("-")+2));

                    update();
                }
            });
        }

    }
}
