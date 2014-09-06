package id.net.iconpln.meetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;

import srsmeeting.iconpln.net.id.srsmeeting.R;


public class Login extends ActionBarActivity {
    SessionManager session;
    HashMap<String, String> user;
    ProgressDialog pDialog;

    EditText edittext_username, edittext_password;
    Button button_login;
    TextView textview_lupapassword;

    String success;
    String url;
    String text;

    Intent a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edittext_username = (EditText) findViewById(R.id.edittext_username);
        edittext_password = (EditText) findViewById(R.id.edittext_password);
        button_login = (Button) findViewById(R.id.button_login);
        textview_lupapassword = (TextView) findViewById(R.id.textview_lupapassword);

        session = new SessionManager(getApplicationContext());

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_username.getText().toString().trim().length() > 0 && edittext_password.getText().toString().trim().length() > 0) {
                    url = "";
                    new Masuk().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "kolom username/password masih kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("user", edittext_username.getText());
            json.put("pass", edittext_password.getText());

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

    public class Masuk extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Menunggu");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/login.php";

            JSONObject json = null;
            try {
                postData(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                success = json.getString("success");
                JSONArray hasil = json.getJSONArray("login");

                if (success.equals("1")) {
                    for (int i = 0; i < hasil.length(); i++) {
                        JSONObject c = hasil.getJSONObject(i);

                        String id_user = c.getString("id_user").trim();
                        String id_divisi = c.getString("id_divisi").trim();
                        String nama = c.getString("nama").trim();
                        String email = c.getString("email").trim();
                        session.createLoginSession(id_user, id_divisi, nama, email);
                    }
                } else {
                    Log.e("erro", "tidak bisa ambil data 0");
                }
            } catch (Exception e) {
                Log.e("erro", "tidak bisa ambil data 1");
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
                user = session.getUserDetails();
                String test = user.get(SessionManager.KEY_NAMA);
                Toast.makeText(getApplicationContext(), "Selamat datang " + test, Toast.LENGTH_LONG).show();
                success = "0";

                a = new Intent(Login.this, Dokumen.class);
                startActivity(a);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Username/Password tidak sesuai", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
