package ca.comp3004.grocerygo.grocerygo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dbHandler = new MyDBHandler(this, null, null, 2);
        TFTPClient itemReq = new TFTPClient();

        mainItem item = null;

        ListView labList = (ListView) findViewById(R.id.labList);
        ArrayList<String> labAL = new ArrayList<String>();
        ListView indList = (ListView) findViewById(R.id.indList);
        ArrayList<String> indAL = new ArrayList<String>();
        ListView walList = (ListView) findViewById(R.id.walList);
        ArrayList<String> walAL = new ArrayList<String>();
        TextView labText = (TextView) findViewById(R.id.lab);
        double labTotal = 0;
        TextView indText = (TextView) findViewById(R.id.ind);
        double indTotal = 0;
        TextView walText = (TextView) findViewById(R.id.wal);
        double walTotal = 0;

        ArrayList<String> cartList = dbHandler.dbToASProductName();
        ArrayList<Integer> cartID = new ArrayList<Integer>();

        for(String e: cartList){
            cartID.add(dbHandler.getProductID(e));
            //Log.e("Client", ""+dbHandler.getProductID(e)); //Testing
        }

        for(int e: cartID){
            //Log.e("Client",""+dbHandler.getQuant(e)); //Testing
            try {
                item = itemReq.getItem(""+e);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
            double[] prices = item.getPrices();
            if(item != null){
                if(prices[0] != -1){
                    labTotal += (prices[0] * dbHandler.getQuant(e));
                    labAL.add(item.name);
                }
                if(prices[1] != -1){
                    indTotal += (prices[1] * dbHandler.getQuant(e));
                    indAL.add(item.name);
                }
                if(prices[2] != -1){
                    walTotal += (prices[2] * dbHandler.getQuant(e));
                    walAL.add(item.name);
                }
                //Log.e("Price",""+labTotal); //Testing
                //Log.e("Price",""+indTotal); //Testing
                //Log.e("Price",""+walTotal); //Testing
            }
        }
        labText.setText("  Loblaws total price: $"+labTotal);
        indText.setText("  Walmart total price: $"+walTotal);
        walText.setText("  Independent total price: $"+indTotal);


        ArrayAdapter<String> labAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labAL);
        labList.setAdapter(labAdapter);
        ArrayAdapter<String> indAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, indAL);
        indList.setAdapter(indAdapter);
        ArrayAdapter<String> walAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, walAL);
        walList.setAdapter(walAdapter);

        dbHandler.removeAll();
        dbHandler.close();
    }
}
