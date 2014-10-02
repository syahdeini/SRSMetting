package id.net.iconpln.meetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Register extends ActionBarActivity {

    private Button submitButton,cancelButton;
    private EditText fullNameTV,usernameTV,emailTV,passwordTV,divisionTV,repasswordTV;
    private String fullNameStr,usernameStr,emailStr,passwordStr,divisionStr,repasswordStr;
    String success;
    String url;
    String text;
    SessionManager session;
    ProgressDialog pDialog;
    HashMap<String,String> hashDivisi;
    HashMap<String, String> map;
    // final ArrayList<HashMap<String, String>> MyArrListDivisi  = new ArrayList<HashMap<String, String>>();
    ArrayList<String> namaDivisi=new ArrayList<String>();
    private ArrayAdapter<String> divisiAdapter;
    Spinner divisiSpin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeView();
        new getDataDivisi().execute();
        // attaching Event Handler for Button
        submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                fullNameStr=fullNameTV.getText().toString();
                usernameStr=usernameTV.getText().toString();
                emailStr=emailTV.getText().toString();
                passwordStr=passwordTV.getText().toString();
                repasswordStr=repasswordTV.getText().toString();
                if(fullNameStr.length()==0 || usernameStr.length()==0 || emailStr.length()==0
                        || passwordStr.length()==0 ||
                        repasswordStr.length()==0)
                {
                    // System.out.print(fullNameStr + usernameStr + emailStr + passwordStr + divisionStr + repasswordStr);
                    Toast.makeText(getApplicationContext(), "Tolong isi semua field dahulu", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!passwordStr.equals(repasswordStr))
                {
                    Toast.makeText(getApplicationContext(), "Password yang anda isikan berbeda", Toast.LENGTH_LONG).show();
                    return;
                }

                // allfield is okay
                new registerToSystem().execute();

                //finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeView() {
        submitButton = (Button) findViewById(R.id.SubmitButton);
        cancelButton = (Button) findViewById(R.id.CancelButton);
        fullNameTV = (EditText) findViewById(R.id.fullNameEditText);
        usernameTV = (EditText) findViewById(R.id.userNameEditText);
        emailTV = (EditText) findViewById(R.id.emailEditText);
        passwordTV = (EditText) findViewById(R.id.passwordEditText);
        repasswordTV = (EditText) findViewById(R.id.repasswordEditText);
    }

    public static String md5(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(),0,s.length());
            return new BigInteger(1, digest.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    class registerToSystem extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Menunggu");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/register.php";

            JSONObject json = null;
            try {
                postData(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                // getting result
                success = json.getString("success");


                if (success.equals("1")) {

                    Log.e("debug", "register success");
                    //  Toast.makeText(getApplicationContext(), "registrasi berhasil", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Log.e("error", "php tidak mengembalikan succes 1 ");
                }
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("error", "tidak dapat mengambil kembalian json");
                Log.d("DEBUG COY","MESSAGE APA",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            //    if(success==null)
            //        return;
            //    if (success.equals("1")) {
            // back to login
            Toast.makeText(getApplicationContext(), "registrasi berhasil", Toast.LENGTH_LONG).show();
            Intent backToLogin = new Intent(Register.this,Login.class);
            startActivity(backToLogin);
            success = "0";
            finish();
            //   } else {
            //       Toast.makeText(getApplicationContext(), "NIPG/Kata sandi tidak sesuai", Toast.LENGTH_LONG).show();
            //    }
        }

        public void postData(String url) throws JSONException {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            JSONObject json = new JSONObject();

            try {
                // JSON data:

                json.put("fullName", fullNameTV.getText());
                String keySelected= divisiSpin.getSelectedItem().toString();
                String divisiSelected=hashDivisi.get(divisiSpin.getSelectedItem().toString());
                json.put("divisi",divisiSelected);
                json.put("email",emailTV.getText());
                json.put("username",usernameTV.getText());
                json.put("password",md5(passwordTV.getText().toString().trim()));

                JSONArray postjson=new JSONArray();
                postjson.put(json);

                // Post the data:
                httppost.setHeader("json",json.toString());
                httppost.getParams().setParameter("jsonpost",postjson);

                // Execute HTTP Post Request
                // System.out.print(json);
                HttpResponse response = httpclient.execute(httppost);

                // for JSON:
                if(response != null)
                {
                    InputStream is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    text = sb.toString();
                }
                else {
                    text="";
                }
            }catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
    }

    // class for getting data from divisi
    // class for handling another thread
    public void postData2(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:

            // putting JSON as parameter to php

            //  json.put("keyword", edittext_search.getText());
            JSONArray postjson=new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost",postjson);

            // Execute HTTP Post Request
            System.out.print(json);
            HttpResponse response = httpclient.execute(httppost);

            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public class getDataDivisi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Mengambil data");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/getDataDivisi.php";

            JSONObject json = null;
            try {
                postData2(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray divisi = json.getJSONArray("divisi");
                hashDivisi= new HashMap<String, String>();

                for(int i=0; i<divisi.length(); i++)
                {
                    JSONObject c = divisi.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id_divisi", c.getString("id_divisi"));
                    map.put("divisi", c.getString("divisi"));
                    namaDivisi.add(c.getString("divisi"));
                    hashDivisi.put(c.getString("divisi"),c.getString("id_divisi"));
                    //  MyArrListDivisi.add(map);
                }


            } catch (Exception e) {
                Log.e("error", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateView();
        }
    }
    // Populate Method
    private void populateView() {
        divisiSpin = (Spinner)findViewById(R.id.spinnerDivisi);
        String[] divisiStr=namaDivisi.toArray(new String[namaDivisi.size()]);
        divisiAdapter= new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,divisiStr);
        divisiSpin.setAdapter(divisiAdapter);
    }

}