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
import java.util.logging.Logger;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;

import srsmeeting.iconpln.net.id.srsmeeting.R;


public class Login extends ActionBarActivity {

    EditText edittext_username, edittext_password;
    Button button_login;
    TextView textview_lupapassword;

    String url;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edittext_username = (EditText) findViewById(R.id.edittext_username);
        edittext_password = (EditText) findViewById(R.id.edittext_password);
        button_login = (Button) findViewById(R.id.button_login);
        textview_lupapassword = (TextView) findViewById(R.id.textview_lupapassword);

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

    public void postData() throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.1.1/test/post.php");
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("name", "Fahmi Rahman");
            json.put("position", "sysdev");

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

            //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    private void setteks() {
        textview_lupapassword.setText(text);
    }

    public class Masuk extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

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
            try {
                postData();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                success = json.getString("success");
                JSONArray hasil = json.getJSONArray("login");

                if (success.equals("1")) {
                    for (int i = 0; i < hasil.length(); i++) {
                        JSONObject c = hasil.getJSONObject(i);

                        String nipg = c.getString("nipg").trim();
                        String nama_pekerja = c.getString("nama_pekerja").trim();
                        String hak_akses = c.getString("hak_akses").trim();
                        String last_login = c.getString("last_login").trim();
                        String sum_login = c.getString("sum_login").trim();
                        String foto = c.getString("foto").trim();
                        String id_pic = c.getString("id_pic").trim();
                        session.createLoginSession(nipg, nama_pekerja, hak_akses, last_login, sum_login, foto,id_pic);
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
            setteks();
            /*if(success==null)
                return;
            if (success.equals("1")) {
                a = new Intent(MainActivity.this, MyAccount.class);
                startActivity(a);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "NIPG/Kata sandi tidak sesuai", Toast.LENGTH_LONG).show();
            }*/
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
