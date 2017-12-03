package ca.carleton.comp3004.grocerygov2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Process extends AppCompatActivity {
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    //TODO: PROCESS THE USER LIST
    ServerRequset request = null;
    DataBase userList;
    Product curProduct = null;
    ListView walList;
    TextView walTxt;
    Button walBut;
    ListView labList;
    TextView labTxt;
    Button labBut;
    ListView indList;
    TextView indTxt;
    Button indBut;
    ArrayList<String> walAL = new ArrayList<String>();
    ArrayList<String> indAL = new ArrayList<String>();
    ArrayList<String> labAL = new ArrayList<String>();


    private LocationManager locMan;
    private double userLong;
    private double userLit;
    private double walLat;
    private double walLong;
    private double labLat;
    private double labLong;
    private double indLat;
    private double indLong;
    private double walDis = 0;
    private double labDis = 0;
    private double indDis = 0;
    private double walTotal = 0;
    private double indTotal = 0;
    private double labTotal = 0;

    private String walURL;
    private String walRaw = null;
    private String labURL;
    private String labRaw = null;
    private String indURL;
    private String indRaw = null;

    ProgressDialog pd = null;


    //*****************
    private boolean fin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        pd = ProgressDialog.show(this, "Processing...", "Finding best store.", true, false);

        requestPermission();

        walList = (ListView) findViewById(R.id.walList);
        //walList.setVisibility(View.INVISIBLE);
        labList = (ListView) findViewById(R.id.lobList);
        //labList.setVisibility(View.INVISIBLE);
        indList = (ListView) findViewById(R.id.indList);
        //indList.setVisibility(View.INVISIBLE);
        /*walList.setBackgroundColor(Color.WHITE);
        labList.setBackgroundColor(Color.WHITE);
        indList.setBackgroundColor(Color.WHITE);
        walList.getBackground().setAlpha(175);
        labList.getBackground().setAlpha(175);
        indList.getBackground().setAlpha(175);*/

        walTxt = (TextView) findViewById(R.id.walmart);
        labTxt = (TextView) findViewById(R.id.loblaws);
        indTxt = (TextView) findViewById(R.id.indep);

        walBut = (Button) findViewById(R.id.walBut);
        labBut = (Button) findViewById(R.id.lobBut);
        indBut = (Button) findViewById(R.id.indBut);

        userList = new DataBase(this, null, null, 1);
        try {
            request = new ServerRequset();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Log.e("Test", ""+location.getLatitude());
        //Log.e("Test", ""+location.getLongitude());

        userLit =location.getLatitude();
        userLong=location.getLongitude();

        /*userLit = 45.3833400;
        userLong = -75.6988720;*/


        walURL = getUrl(userLit,userLong,"Walmart");
        labURL = getUrl(userLit,userLong,"Loblaws");
        indURL = getUrl(userLit,userLong,"Independent");

        Log.e("User- ", ""+userLit + " " + userLong);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    walRaw = readUrl(walURL);
                    labRaw = readUrl(labURL);
                    indRaw = readUrl(indURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                JSONObject walJSON = null;
                JSONArray walArray = null;
                JSONObject labJSON = null;
                JSONArray labArray = null;
                JSONObject indJSON = null;
                JSONArray indArray = null;

                if(walRaw != null){
                    try {
                        walJSON = new JSONObject(walRaw);
                        labJSON = new JSONObject(labRaw);
                        indJSON = new JSONObject(indRaw);

                        walArray = walJSON.getJSONArray("results");
                        labArray = labJSON.getJSONArray("results");
                        indArray = indJSON.getJSONArray("results");

                        walLat = walArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        walLong = walArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        indLat = indArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        indLong = indArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        labLat = labArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        labLong = labArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                        //Eval distance ----****----
                        walDis = getNewUrl(userLit, userLong, walLat, walLong);

                        indDis = getNewUrl(userLit,userLong,indLat,indLong);

                        labDis = getNewUrl(userLit,userLong,labLat,labLong);



                        Log.e("Walmart -", "Lat: "+walLat+", Long: "+walLong +", Distance: "+walDis);
                        Log.e("Loblaws -","Lat: "+labLat+", Long: "+labLong + ", Distance: "+labDis);
                        Log.e("Independent -","Lat: "+indLat+", Long: "+indLong + ", Distance: " + indDis);

                        ArrayList<Integer> proID = userList.userProductsID();
                        ArrayList<Product> pros = new ArrayList<Product>();
                        String rawData = "";

                        for(int p: proID){
                            rawData = rawData + p +",";
                        }
                        if(!rawData.equals("")) {
                            pros = request.readProcess(request.initialize(rawData));
                        }


                        for(Product e: pros){
                            int quant = userList.getQuant(e.productID);
                            //rawData = request.initialize(""+e);

                            //Log.e("Raw data: ", ""+rawData);

                            //curProduct = request.readSearchID(rawData);

                            //curProduct = request.getItem(e);

                            double[] prices = e.getPrices();

                            if(prices[0] != -1){
                                labTotal += (prices[0] * quant);
                                labAL.add(quant + "x - " +e.name);
                            }
                            if(prices[1] != -1){
                                indTotal += (prices[1] * quant);
                                indAL.add(quant + "x - " +e.name);
                            }
                            if(prices[2] != -1){
                                walTotal += (prices[2] * quant);
                                walAL.add(quant + "x - " +e.name);
                            }
                        }

                        //walTxt.setHeight(convertDpToPixels(90, this));

                        //walTxt.setText("Walmart: \n" + "Total Grocery: $" + round(walTotal,2) + "\nGas price: $" + round((walDis * 0.53),2));
                        //labTxt.setText("Loblaws: \n" + "Total Grocery: $" + round(labTotal,2) + "\nGas price: $" + round((labDis * 0.53),2));
                        //indTxt.setText("Independent: \n" + "Total Grocery: $" + round(indTotal,2) + "\nGas price: $" + round((indTotal * 0.53),2));








                        userList.removeAll();
                        userList.close();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        walTxt.setText("Walmart: $" + round(walTotal,2) + " + $" + round((walDis * 0.53),2));
                        labTxt.setText("Loblaws: $" + round(labTotal,2) + " + $" + round((labDis * 0.53), 2));
                        indTxt.setText("Independent: $" + round(indTotal,2) + " + $" + round((indDis * 0.53), 2));
                        ArrayAdapter<String> labAdapter = new ArrayAdapter<String>(Process.this, android.R.layout.simple_list_item_1, labAL);
                        labList.setAdapter(labAdapter);
                        ArrayAdapter<String> indAdapter = new ArrayAdapter<String>(Process.this, android.R.layout.simple_list_item_1, indAL);
                        indList.setAdapter(indAdapter);
                        ArrayAdapter<String> walAdapter = new ArrayAdapter<String>(Process.this, android.R.layout.simple_list_item_1, walAL);
                        walList.setAdapter(walAdapter);
                        walBut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //saddr="+userLit+","+userLong +"& *****
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr="+walLat+","+walLong));
                                startActivity(intent);
                            }
                        });
                        labBut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //saddr="+userLit+","+userLong +"&
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr="+labLat+","+labLong));
                                startActivity(intent);
                            }
                        });
                        indBut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //saddr="+userLit+","+userLong +"&
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr="+indLat +","+indLong));
                                startActivity(intent);
                            }
                        });
                        if(pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                });

            }
        });
        thread.start();




    }


    private void requestPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    private String getUrl(double latitude , double longitude , String store)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&keyword="+store);
        googlePlaceUrl.append("&rankby=distance");
        googlePlaceUrl.append("&key="+"AIzaSyBkR0zn08UItzr_QuMuQNEbLDtx2H09C9g");
        if(store.equals("Independent") || store.equals("Loblaws")){
            googlePlaceUrl.append("&types=supermarket");
        }

        //Log.e("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(myUrl);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        //Log.e("DownloadURL","Returning data= "+data);

        return data;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case MY_PERMISSION_FINE_LOCATION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "This is required to evaluate the best store!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private double getNewUrl(double oLat,double oLng,double dLat , double dLng) throws IOException, JSONException {
        String rawData = null;

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googlePlaceUrl.append("origin="+oLat+","+oLng);
        googlePlaceUrl.append("&destination="+dLat+","+dLng);
        googlePlaceUrl.append("&key="+"AIzaSyD09FF4BA77MlGbze-GsauZ6DLXlcmRF2A");

        //Log.e("MapsActivity", "url = "+googlePlaceUrl.toString());

        rawData = readUrl(googlePlaceUrl.toString());

        final JSONObject json = new JSONObject(rawData);
        JSONArray routeArray = json.getJSONArray("routes");
        JSONObject routes = routeArray.getJSONObject(0);

        JSONArray newTempARr = routes.getJSONArray("legs");
        JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

        JSONObject distOb = newDisTimeOb.getJSONObject("distance");

        //Log.d("Diatance :", distOb.getString("text"));

        String dis = distOb.getString("text");

        return Double.parseDouble(dis.substring(0, (dis.indexOf('k')-1)));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



}
