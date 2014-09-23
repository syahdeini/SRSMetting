package id.net.iconpln.meetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Manage_rapat extends ActionBarActivity {
    SessionManager session;
    HashMap<String, String> map, user;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ProgressDialog pDialog;

    String url;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rapat);

        new AmbilDaftarRapat().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_rapat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.e("data",data.getData().getPath());
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
            json.put("keyword", Base64.encodeBase64("test".getBytes()));

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

    public class AmbilDaftarRapat extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Manage_rapat.this);
            pDialog.setMessage("Mengambil data rapat");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();
            url = "http://" + globalVar.serverIPaddress + "/meetings/data_rapat.php";
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                JSONArray rapat = json.getJSONArray("rapat");

                for(int i=0; i<rapat.length(); i++)
                {
                    JSONObject c = rapat.getJSONObject(i);

                    map = new HashMap<String, String>();
                    map.put("id_rapat", c.getString("id_rapat"));
                    map.put("id_ruangan", c.getString("id_ruangan"));
                    map.put("tanggal_mulai", c.getString("tanggal_mulai"));
                    map.put("tanggal_selesai", c.getString("tanggal_selesai"));
                    map.put("jam_mulai", c.getString("jam_mulai"));
                    map.put("jam_selesai",c.getString("jam_selesai"));
                    map.put("perihal" , c.getString("perihal"));
                    map.put("penanggungjawab" , c.getString("penanggungjawab"));
                    map.put("resume_hasil" , c.getString("resume_hasil"));
                    map.put("tanggal_buat_rapat", c.getString("tanggal_buat_rapat"));
                    map.put("pembuat_jadwal_id_user", c.getString("pembuat_jadwal_id_user"));
                    map.put("status_rapat", c.getString("status_rapat"));
                    MyArrList.add(map);
                }

            } catch (Exception e) {
                Log.e("erro", "JSON rapat");
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

    private void populateView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();
        for (HashMap<String, String> aMyArrList : MyArrList) {
            String perihal = aMyArrList.get("perihal");
            String penanggungjawab = aMyArrList.get("penanggungjawab");
            String tanggal = aMyArrList.get("tanggal_mulai");
            String jam = aMyArrList.get("jam_mulai").replaceAll(".000000", "").substring(9);
            String ruangan = aMyArrList.get("id_ruangan");

            Button but = new Button(this);
            String[] id = aMyArrList.get("id_rapat").split("-");
            but.setId(Integer.parseInt(id[1]));
            but.setHint(aMyArrList.get("id_rapat"));
            but.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            but.setText(Html.fromHtml("<big>Rapat (" + perihal + ")</big><br/><font color=\"blue\"><small>" + tanggal + ", " + jam + "</small></font><br/>" +
                    penanggungjawab));
            but.setGravity(Gravity.START);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, 1);
                }
            });
            layout.addView(but);
        }
    }
}
