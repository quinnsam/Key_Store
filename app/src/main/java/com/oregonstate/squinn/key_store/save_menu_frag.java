package com.oregonstate.squinn.key_store;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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
        rootview = inflater.inflate(R.layout.save_layout, container, false);
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
            //android.widget.Toast.makeText(getActivity(), "Saved Pubkey", android.widget.Toast.LENGTH_SHORT).show();
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

        // Get Values from layout.
        EditText edit_name = (EditText) getActivity().findViewById(R.id.name);
        String print_name = edit_name.getText().toString();
        if (print_name.equals("Name") || print_name.isEmpty()) {
            android.widget.Toast.makeText(getActivity(), "Error Name is required", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        EditText edit_email = (EditText) getActivity().findViewById(R.id.email);
        String print_email = edit_email.getText().toString();
        if (print_email.equals("Email") || print_email.isEmpty()) {
            android.widget.Toast.makeText(getActivity(), "Error Email is required", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        EditText edit_comment = (EditText) getActivity().findViewById(R.id.comment);
        String print_comment = edit_comment.getText().toString();
        //RadioButton edit_rsa = (RadioButton) getActivity().findViewById(R.id.rsa);
        RadioButton edit_dsa = (RadioButton) getActivity().findViewById(R.id.dsa);
        String print_encType = "";
        if (edit_dsa.isChecked()) {
            print_encType = "DSA";
        } else {
            print_encType = "RSA";
        }
        CheckBox edit_exp = (CheckBox) getActivity().findViewById(R.id.exp);
        Boolean print_exp = edit_exp.isChecked();
        String print_expDate = "";
        String print_expTime = "";
        if (print_exp) {
            print_expDate = "N/A";
            print_expTime = "N/A";
            print_exp = Boolean.FALSE;
        } else {
            EditText edit_expDate = (EditText) getActivity().findViewById(R.id.date);
            EditText edit_expTime = (EditText) getActivity().findViewById(R.id.time);
            print_expDate = edit_expDate.getText().toString();
            print_expTime = edit_expTime.getText().toString();
            if (print_expDate.equals("Date") || print_expDate.isEmpty()) {
                android.widget.Toast.makeText(getActivity(), "Error Expire Date is required", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if (print_expTime.equals("Time") || print_expTime.isEmpty()) {
                android.widget.Toast.makeText(getActivity(), "Error Expire Time is required", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            print_exp = Boolean.TRUE;
        }
        EditText edit_pubkey = (EditText) getActivity().findViewById(R.id.pubkey);
        String print_pubkey = edit_pubkey.getText().toString();
        if (print_pubkey.equals("Public Encryption Key here.") || print_pubkey.isEmpty()) {
            android.widget.Toast.makeText(getActivity(), "Error Public Key is required", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }


        //String urlstring = "http://cs496-hw03-api.appspot.com/pubkey";
        //URL url = new URL(urlstring);
        //HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //// Add request Headers
        //conn.setRequestMethod("POST");
        //conn.setRequestProperty("Accept", "application/json");
        //conn.setUseCaches(false);

        Uri.Builder builder = new Uri.Builder()
                .scheme("http")
                .authority("cs496-hw03-api.appspot.com")
                .path("pubkey")
                .appendQueryParameter("full-name", print_name)
                .appendQueryParameter("email", print_email)
                .appendQueryParameter("comment", print_comment)
                .appendQueryParameter("Encryption-type", print_encType)
                .appendQueryParameter("bit-strenght", print_comment)
                .appendQueryParameter("exp-date", print_expDate)
                .appendQueryParameter("exp-time", print_expTime)
                .appendQueryParameter("expiration", print_exp.toString())
                .appendQueryParameter("pubkey", print_pubkey);

        String query = builder.build().getEncodedQuery();

        System.out.println("Network: " + isNetworkAvailable());
        //System.out.println(builder.build());
        //EditText print_name = (EditText) getActivity().findViewById(R.id.comment);

        if (isNetworkAvailable()) {
            System.out.println("Connected to saving to API");
            // Configure Connection
            String urlstring = "http://cs496-hw03-api.appspot.com/pubkey";
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Add request Headers
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setUseCaches(false);

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
        } else { // Not Connected to the network
            System.out.println("Not connected to internet saving to file");
            String filename = ((EditText) getActivity().findViewById(R.id.name)).getText()
                    .toString().replaceAll("\\s+", "");
            FileOutputStream saveFile;

            try {
                saveFile = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                saveFile.write(query.getBytes());
                saveFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        android.widget.Toast.makeText(getActivity(), "Key Saved", android.widget.Toast.LENGTH_SHORT).show();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


