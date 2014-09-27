package id.net.iconpln.meetings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class managePesertaRapat extends ActionBarActivity {

    Button addPesertaBtn;
    String url,text,success;
    int dummy=0,FLAG=-1;
    String ID_userNow,Name_userNow;
    EditText pesertaEditText;
    ArrayList<String> ID_pesertaS= new ArrayList<String>();
    private AtomPayListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_peserta_rapat);

        setupListViewAdapter();
        addPesertaBtn= (Button)findViewById(R.id.addPesertaButton);

        // Adding peserta button
        addPesertaBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pesertaEditText = (EditText)findViewById(R.id.namaPesertaEditText);
                Name_userNow=pesertaEditText.getText().toString();
                FLAG=-1;
                pesertaEditText.setText("");
                new checkUser().execute();
            }
        });

        ((Button)findViewById(R.id.submitPersertaRapatBtn)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent resultData= new Intent(managePesertaRapat.this, daftar_rapat.class);
                Intent resultData=new Intent(v.getContext(),daftar_rapat.class);
                ArrayList<AtomPayment> ata = new ArrayList<AtomPayment>();
            /*    for(int i=0;i<adapter.getCount();i++)
                {
                    ata.add(adapter.getItem(i));
                }
             */ //  resultData.putParcelableArrayListExtra("anggotaRapat",ata);
                resultData.putExtra("joko","widodo");

                if(getParent()==null)
                    setResult(RESULT_OK, resultData);
                else
                    getParent().setResult(RESULT_OK,resultData);
                finish();
            }
        });

        // setupAddPaymentButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_peserta_rapat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void removeAtomPayOnClickHandler(View v) {
        AtomPayment itemToRemove = (AtomPayment)v.getTag();
        globalVar.id_user_saver.remove(itemToRemove.getName());
        adapter.remove(itemToRemove);
    }

    private void setupListViewAdapter() {
        adapter = new AtomPayListAdapter(managePesertaRapat.this, R.layout.atom_pay_list_item, new ArrayList<AtomPayment>());
        ListView atomPaysListView = (ListView)findViewById(R.id.EnterPays_atomPaysList);
        atomPaysListView.setAdapter(adapter);
    }



// Buat thread yang mengecek apakah nama ada di User table, kalo ada ambil ID user tersebut

    class checkUser extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(managePesertaRapat.this);
            pDialog.setMessage("Menunggu");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/checkUser.php";

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
                    ID_userNow=json.getString("ID_USER");
                    FLAG=1;
                    Log.e("debug", "nama ditemukan success");
//                    Toast.makeText(getApplicationContext(), "registrasi berhasil", Toast.LENGTH_LONG).show();
                    //finish();
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
            if(success==null)
                return;
            // back to login
            if(FLAG==1) {
                adapter.add(new AtomPayment(Name_userNow));
                globalVar.id_user_saver.put(Name_userNow,ID_userNow);
                ID_pesertaS.add(ID_userNow);
            }
            else
                Toast.makeText(getApplicationContext(),"nama user tidak ditemukan", Toast.LENGTH_LONG).show();

            //  Toast.makeText(getApplicationContext(), "NIPG/Kata sandi tidak sesuai", Toast.LENGTH_LONG).show();

        }


        public void postData(String url) throws JSONException {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            JSONObject json = new JSONObject();

            try {
                // JSON data:
                json.put("nama",Name_userNow);

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
}
