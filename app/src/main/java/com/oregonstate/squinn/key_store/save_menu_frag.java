package com.oregonstate.squinn.key_store;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by root on 11/3/15.
 */
public class save_menu_frag extends Fragment {
    View rootview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.save_layout,container,false);
        setHasOptionsMenu(true);
        return rootview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            android.widget.Toast.makeText(getActivity(), "Saved Pubkey", android.widget.Toast.LENGTH_SHORT).show();
            // Store key to data base
            try {
                postPubkey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void postPubkey() throws Exception {

        android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);

        String urlstring = "http://cs496-hw03-api.appspot.com/pubkey";
        URL url = new URL(urlstring);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Add request Headers
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches(false);

        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("cs496-hw03-api.appspot.com")
                .path("pubkey")
                .appendQueryParameter("full-name", "Android Tester")
                .appendQueryParameter("email", "AndroidTester@Androidemail.com")
                .appendQueryParameter("comment", "Android Tester comment")
                .appendQueryParameter("expiration", "False")
                .appendQueryParameter("pubkey", "Android Public key test.. Weeewwwww");

        String query = builder.build().getEncodedQuery();

        System.out.println(query);
        System.out.println(builder.build());

        // Send POST request
        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(query);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + query);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print Result
        System.out.println(response.toString());

    }
}


