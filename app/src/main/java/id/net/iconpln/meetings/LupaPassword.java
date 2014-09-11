package id.net.iconpln.meetings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

/**
 * Created by syahdieni on 07/09/14.
 */
public class LupaPassword extends Activity {
    Button resetPasswordButton;
    String success;
    String url;
    String text;
    SessionManager session;
    HashMap<String, String> user;

    String to,subject,message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_password);
        resetPasswordButton = (Button)findViewById(R.id.LupaPasswordbutton);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to = ((EditText)findViewById(R.id.lupaPasswordEditText)).getText().toString();
                new Masuk().execute();

            }
        });
    }

    public class Masuk extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LupaPassword.this);
            pDialog.setMessage("Menunggu");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        JSONObject json = new JSONObject();
        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/lupaPassword.php";


            try {
                postData(url);
                Log.e("ERROR HUY",text);
                json = new JSONObject(text);
            } catch (JSONException e) {
                Log.e("ERROR AFTER POST DATA",e.toString());
                e.printStackTrace();
            }

            try {
                success = json.getString("success");
               // JSONArray hasil = json.getJSONArray ("login");

                if (success.equals("1")) {
                    //for (int i = 0; i < hasil.length(); i++) {
                     //   JSONObject c = hasil.getJSONObject(i);
                        Log.e("debug", "masuk lupa password");
                        // sending email to email
                        String getPassword= json.getString("password");
                        String userName = json.getString("username");
                        message="Here is your username : "+userName+" and passowrd : "+getPassword;
                        Intent sendPass=new Intent(Intent.ACTION_SEND);
                        sendPass.putExtra(Intent.EXTRA_EMAIL,new String[]{"syahdeini@gmail.com"});
                        sendPass.putExtra(Intent.EXTRA_SUBJECT,"subject");
                        sendPass.putExtra(Intent.EXTRA_TEXT,message);
                        sendPass.setType("meassge/rfc822");
                        startActivity(Intent.createChooser(sendPass,"choose:"));
               //        Toast.makeText(getApplicationContext(), "Password dan username sudah di kirim ke email anda ", Toast.LENGTH_LONG).show();
                   //     finish();

                 //   }
                } else if(success.equals("0")) {
                  //  Toast.makeText(getApplicationContext(), " Data tidak ditemukan, tolong periksa email anda ", Toast.LENGTH_LONG).show();
                    Log.e("error", "data untuk password tidak dapat di temukan");
                }
                else {
                 //   Toast.makeText(getApplicationContext(), " koneksi database bermasalah "+success.toString(), Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                Log.e("error", "tidak bisa ambil data Lupa password "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if(success==null)
                return;
            if (success.equals("1")) {
                String getPassword= null;
                try {
                    getPassword = json.getString("password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String userName = null;
                try {
                    userName = json.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              /*()  message="Here is your username : "+userName+" and passowrd : "+getPassword;
                Intent sendPass=new Intent(Intent.ACTION_SEND);
                sendPass.putExtra(Intent.EXTRA_EMAIL,new String[]{"syahdeini@gmail.com"});
                sendPass.putExtra(Intent.EXTRA_SUBJECT,"subject");
                sendPass.putExtra(Intent.EXTRA_TEXT,message);
                sendPass.setType("meassge/rfc822");
                startActivity(Intent.createChooser(sendPass,"choose:"));
*/
                Toast.makeText(getApplicationContext(), "Tolong periksa email anda", Toast.LENGTH_LONG).show();
                success = "0";
                Intent loginAgain = new Intent(LupaPassword.this, Login.class);
                startActivity(loginAgain);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah "+success.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("email", ((EditText)findViewById(R.id.lupaPasswordEditText)).getText().toString());
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
}
