package com.oregonstate.squinn.key_store;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.JsonWriter;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class ViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        try {
            getAllPubkeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllPubkeys() throws Exception {
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
            String userName = ((UserInfo) this.getApplication()).getPersonName();
            String res = response.toString().substring(5);
            String[] ind = res.split("</pre><pre>");
            TextView output = (TextView)findViewById(R.id.viewAllKeys);
            JSONObject tempj;
            for (String temp:ind) {
                temp.replaceAll("</pre>", "");
                temp.replaceAll(Pattern.quote("<pre>"), "");
                System.out.println(temp.toString());
                tempj = new JSONObject(temp);
                //System.out.println(tempj.getString("google") + " : " + userID);
                if ( tempj.getString("google").equals(userID)) {
                    output.append("Google ID: " + userID + "\n");
                    output.append("Google Name: " + userName + "\n");
                    output.append("Fullname: " + tempj.getString("fullname") + "\n");
                    output.append("\tEmail: " + tempj.getString("email") + "\n");
                    output.append("\tComment: " + tempj.getString("comment") + "\n");
                    output.append("\tExpiration: " + tempj.getString("exp") + "\n");
                    output.append("\t\tDate: " + tempj.getString("expDate") + "\n");
                    output.append("\t\tTime: " + tempj.getString("expTime") + "\n");
                    output.append("\tPublic Key: " + tempj.getString("pubkey") + "\n");
                    output.append("\tAPI Key: " + tempj.getString("key") + "\n\n");
                }
            }



        } else { // Not Connected to the network
            System.out.println("Not connected to internet cannot get data.");
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
