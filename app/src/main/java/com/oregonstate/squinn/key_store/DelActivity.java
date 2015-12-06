package com.oregonstate.squinn.key_store;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DelActivity extends Activity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);

        // Get ListView object from layout
        listView = (ListView) findViewById(R.id.delListView);

        // Get array values to show in ListView
        ArrayList<String> pubkeys = null;
        try {
            pubkeys = new ArrayList<>(getAllPubkeys());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Pubsss" + pubkeys);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, android.R.id.text1, pubkeys);

        // Assign adapter to LIstView
        listView.setAdapter(adapter);

        // ListView item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ListView Clicked item index
                int itemPosition = i;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(i);
                // Show Alert
                String p = itemValue.substring(itemValue.length() - 16, itemValue.length());

                // Send to delete
                try {
                    DeleteItem(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Deleted:" + p, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void DeleteItem (String key) throws Exception {
        if (isNetworkAvailable()) {
            System.out.println("Connected to saving to API");
            // Configure Connection
            String urlstring = "http://cs496-hw03-api.appspot.com/pubkey/del/" + key;
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Add request Header
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setUseCaches(false);
            //conn.setDoInput(true);
            //conn.setDoOutput(true);

            // Send GET request
            //DataOutputStream wr = new DataOutputStream(
            //        conn.getOutputStream());
            //wr.write("");
            //wr.flush();
            //wr.close();

            // Get response
            System.out.println("Response Code: " + conn.getResponseCode());

        } else { // Not Connected to the network
            System.out.println("Not connected to internet cannot get data.");
        }
    }

    public ArrayList<String> getAllPubkeys() throws Exception {
        ArrayList<String> output = new ArrayList<>();
        android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);
        //Uri.Builder builder = new Uri.Builder()
        //        .scheme("http")
        //        .authority("cs496-hw03-api.appspot.com")
        //        .path("pubkey");

        //String query = builder.build().getEncodedQuery();

        System.out.println("Network: " + isNetworkAvailable());
        //System.out.println(builder.build());

        if (isNetworkAvailable()) {
            System.out.println("Connected to saving to API");
            // Configure Connection
            String urlstring = "http://cs496-hw03-api.appspot.com/pubkey";
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Add request Header
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setUseCaches(false);
            //conn.setDoInput(true);
            //conn.setDoOutput(true);

            // Send GET request
            //DataOutputStream wr = new DataOutputStream(
            //        conn.getOutputStream());
            //wr.write("");
            //wr.flush();
            //wr.close();

            // Get response
            System.out.println("Response Code: " + conn.getResponseCode());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print Result
            String userID = ((UserInfo) this.getApplication()).getPersonId();
            String res = response.toString().substring(5);
            String[] ind = res.split("</pre><pre>");
            //TextView output = (TextView)findViewById(R.id.viewAllKeys);
            String temp_out = "";
            JSONObject tempj;
            for (String temp:ind) {
                temp.replaceAll("</pre>", "");
                temp.replaceAll(Pattern.quote("<pre>"), "");
                System.out.println(temp.toString());
                tempj = new JSONObject(temp);
                //System.out.println(tempj.getString("google") + " : " + userID);
                if ( tempj.getString("google").equals(userID)) {
                    temp_out = "Fullname: " + tempj.getString("fullname") + "\n" + "API Key: " + tempj.getString("key");
                }
                if (! temp_out.isEmpty())
                    output.add(temp_out);
                System.out.println("TEMMPPP:" + temp_out);
            }



        } else { // Not Connected to the network
            System.out.println("Not connected to internet cannot get data.");
        }
        System.out.println("OUTTTT:" + output + "OUT");
        return output;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

