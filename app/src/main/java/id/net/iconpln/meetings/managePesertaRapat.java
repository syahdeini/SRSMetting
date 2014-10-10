
// Kelas ini hanya akan mengecek apakah user yang dicantumkan masuk ke golongan,
// user/peserta dan menyimpannya pada globalVar
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
import java.lang.reflect.Array;
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
    private int personTYPE;   // 0 = user, 1 = peserta
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_peserta_rapat);

        setupListViewAdapter();
        addPesertaBtn = (Button) findViewById(R.id.addPesertaButton);

        // populating list of peserta
        for(String key :globalVar.id_user_saver.keySet())
        {
            adapter.add(new AtomPayment(key));
        }
        for(String key :globalVar.id_peserta_saver.keySet())
        {
            adapter.add(new AtomPayment(key));
        }
        for(String key: globalVar.tambahan_peserta)
        {
            adapter.add(new AtomPayment(key));
        }

        // Adding peserta button
        addPesertaBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pesertaEditText = (EditText)findViewById(R.id.namaPesertaEditText);
                Name_userNow=pesertaEditText.getText().toString();
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
           //     resultData.putExtra("joko","widodo");

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
        if(globalVar.id_user_saver.containsKey(itemToRemove.getName()))
        {
            globalVar.id_user_saver.remove(itemToRemove.getName());
        }
        else if(globalVar.id_peserta_saver.containsKey(itemToRemove.getName())){
            globalVar.id_peserta_saver.remove(itemToRemove.getName());
        } else if(globalVar.tambahan_peserta.contains(itemToRemove.getName()))
        {
            globalVar.tambahan_peserta.remove(itemToRemove.getName());
        }

        adapter.remove(itemToRemove);
    }

    private void setupListViewAdapter() {
        adapter = new AtomPayListAdapter(managePesertaRapat.this, R.layout.atom_pay_list_item, new ArrayList<AtomPayment>());
        ListView atomPaysListView = (ListView)findViewById(R.id.EnterPays_atomPaysList);
        atomPaysListView.setAdapter(adapter);
    }

    ////////////////////////////////////////////////////////////////////////////
/// Buat thread yang mengecek apakah nama ada di User table, kalo ada ambil ID user tersebut

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
                    // user ditemukan
                    personTYPE=0;

                    ID_userNow=json.getString("ID_USER");
                    Log.e("debug", "nama ditemukan success");
                } else {
                    // check di table peserta
                    url = "http://" + globalVar.serverIPaddress + "/meetings/checkPeserta.php";
                    try {
                        postData(url);
                        json = new JSONObject(text);
                        success = json.getString("success");
                        if(success.equals("1"))
                        {
                            ID_userNow=json.getString("ID_USER");
                            // orang termasuk kategori peserta
                            personTYPE=1;
                        }
                        else    // peserta di tambahkan
                        {
                            personTYPE=-1;

                        }

                    }catch (Exception e)
                    {
                        Log.e("ERROR MANAGE PESERTA","ERROR CHECK PESERTA");
                    }
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
            if (success == null)
                return;
            // add orang ke variable static globalVar
            if (personTYPE == 0) {      // Add ke tabel user
                adapter.add(new AtomPayment(Name_userNow));
                globalVar.id_user_saver.put(Name_userNow, ID_userNow);
                ID_pesertaS.add(ID_userNow);
            }
            else if(personTYPE==1){ // add ke tabel perserta
                adapter.add(new AtomPayment(Name_userNow));
                globalVar.id_peserta_saver.put(Name_userNow,ID_userNow);

            }
            else if(personTYPE==-1) { // add ke tabel tambah
                adapter.add(new AtomPayment(Name_userNow));
                globalVar.tambahan_peserta.add(Name_userNow);
            }
            Toast.makeText(getApplicationContext(), "User ditambahkan", Toast.LENGTH_LONG).show();

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
