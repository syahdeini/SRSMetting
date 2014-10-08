package id.net.iconpln.meetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
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
import java.util.HashMap;
import srsmeeting.iconpln.net.id.srsmeeting.R;

public class editRapat extends ActionBarActivity {
    SessionManager session;
    ProgressDialog pDialog;
    String url;
    String text;
    HashMap<String, String> map, user,dictAplikasi,dictRuangan;
    final ArrayList<HashMap<String, String>> MyArrListAplikasi = new ArrayList<HashMap<String, String>>();
    final ArrayList<HashMap<String, String>> MyArrListRuangan = new ArrayList<HashMap<String, String>>();
    ArrayList<String> nama_ruanganList=new ArrayList<String>();
    ArrayList<String> nama_aplikasiList=new ArrayList<String>();
    private ArrayAdapter<String> RuanganArrayAdapter,ApliasiArrayAdapter;
    HashMap<String,String> listPeserta;
    private String ID_USER;
    //GUI Object
    Spinner ruanganSpinner;
    Spinner aplikasiSpinner;
    String str_IDRapat,str_idRuangan,str_TanggalMulai,str_TanggalSelesai,str_jamMulai,str_jamSelesai,str_perihal
            ,str_penanggungJawab,str_resumeHasil,str_tanggalBuatRapat,str_pembuatJadwalIdUser,str_statusRapat,str_applikasiRapat;

    String IDRAPATEDIT="";
    int ACTIVITY_CODE=655;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rapat);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUserDetails();
        ID_USER= user.get(SessionManager.KEY_ID_USER);
        IDRAPATEDIT=globalVar.idRapatEdited;
        new getData().execute();

        Button submitRapat=(Button)findViewById(R.id.submitRapat);
        submitRapat.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Handler_submitRapat(v);

            };
        });
        final Button managePesertaBtn = (Button)findViewById(R.id.managePesertaBtn);


        ((Button)findViewById(R.id.buttonBatal)).setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                finish();
            }
        });

        managePesertaBtn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent managePesertaRapatActivity = new Intent(editRapat.this,managePesertaRapat.class);
                startActivityForResult(managePesertaRapatActivity,ACTIVITY_CODE);
//                startActivity(managePesertaRapatActivity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the results is coming from BROWSER_ACTIVATION_REQUEST
        if (requestCode == ACTIVITY_CODE) {

            // check the result code set by the activity
            if (resultCode == RESULT_OK) {
                try {
                    listPeserta=globalVar.id_user_saver;
                    // Log.e("CACA","CACACA");
                } catch (Exception e)
                {
                    Log.e("RRR..... ERROR",e.toString());
                }
            }
        }
    }
    // Handler saat rapat disubmit
    private void Handler_submitRapat(View v)
    {
        // DatePicker waktuMulai_dateP = (DatePicker)findViewById(R.id.WaktuMulai_datePicker);
        //DatePicker waktuSelesai_dateP = (DatePicker)findViewById(R.id.WaktuSelesai_datePicker);
        //TimePicker waktuMulai_timeP = (TimePicker)findViewById(R.id.WaktuMulai_timePicker);
        //TimePicker waktuSelesai_timeP = (TimePicker)findViewById(R.id.WaktuSelesai_timePicker);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        //Date date = new Date();
        //  System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        //   Toast.makeText(getApplicationContext(), dateFormat.format(date) , Toast.LENGTH_LONG).show();
        new submitData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }



    // POPULATE PICKER WITH DATA
    /****************************************************************/
    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:

            // putting JSON as parameter to php
            //  json.put("keyword", edittext_search.getText());
            json.put("rapat_id",IDRAPATEDIT);
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



    // class for handling another thread
    public class getData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editRapat.this);
            pDialog.setMessage("Mengambil data");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/getDataRapatByID.php";

            JSONObject json = null;
            try {
                postData(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray aplikasi = json.getJSONArray("aplikasi");
                JSONArray ruangan = json.getJSONArray("ruangan");
                JSONArray rapat = json.getJSONArray("rapat");
                JSONArray aplikasiRapat = json.getJSONArray("aplikasi_rapat");
                JSONArray pesertaRapat = json.getJSONArray("pesertaRapat");
                dictAplikasi= new HashMap<String, String>();
                dictRuangan= new HashMap<String, String>();
                for(int i=0; i<aplikasi.length(); i++)
                {
                    JSONObject c = aplikasi.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id_aplikasi", c.getString("id_aplikasi"));
                    map.put("nama_aplikasi", c.getString("nama_aplikasi"));
                    nama_aplikasiList.add(c.getString("nama_aplikasi"));
                    dictAplikasi.put(c.getString("nama_aplikasi"),c.getString("id_aplikasi"));
                    MyArrListAplikasi.add(map);
                }
                for(int i=0; i<ruangan.length(); i++)
                {
                    JSONObject c = ruangan.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id_ruangan", c.getString("id_ruangan"));
                    map.put("nama_ruangan", c.getString("nama_ruangan"));
                    dictRuangan.put(c.getString("nama_ruangan"),c.getString("id_ruangan"));
                    nama_ruanganList.add(c.getString("nama_ruangan"));
                    MyArrListAplikasi.add(map);
                }
                // Getting rapat data based on rapat ID
                JSONObject c = rapat.getJSONObject(0);
                str_TanggalMulai=c.getString("TANGGAL_MULAI");
                str_idRuangan=c.getString("ID_RUANGAN");
                str_TanggalSelesai=c.getString("TANGGAL_SELESAI");
                str_jamMulai = c.getString("JAM_MULAI");
                str_jamSelesai=c.getString("JAM_SELESAI");
                str_perihal=c.getString("PERIHAL");
                str_penanggungJawab=c.getString("PENANGGUNG_JAWAB");
                str_resumeHasil=c.getString("RESUME_HASIL");
                str_tanggalBuatRapat=c.getString("TANGGAL_BUAT_RAPAT");
                str_pembuatJadwalIdUser=c.getString("PEMBUAT_JADWAL_ID_USER");
                str_statusRapat=c.getString("STATUS_RAPAT");

                // getting aplikasi rapat
                c = aplikasiRapat.getJSONObject(0);
                str_applikasiRapat=c.getString("ID_APLIKASI");

                // Getting all peserta rapat
                for(int i=0; i<pesertaRapat.length(); i++) {
                     c = pesertaRapat.getJSONObject(i);
                    globalVar.id_user_saver.put(c.getString("NAMA_PESERTA"),c.getString("ID_PESERTA"));
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
        ruanganSpinner = (Spinner)findViewById(R.id.ruanganSpinner);
        aplikasiSpinner=(Spinner)findViewById(R.id.aplikasiSpinner);
        String[] dummyStr=nama_ruanganList.toArray(new String[nama_ruanganList.size()]);
        RuanganArrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dummyStr);//nama_ruanganList.toArray(new String[nama_ruanganList.size()]));
        ruanganSpinner.setAdapter(RuanganArrayAdapter);
        dummyStr=nama_aplikasiList.toArray(new String[nama_aplikasiList.size()]);
        ApliasiArrayAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dummyStr);
        aplikasiSpinner.setAdapter(ApliasiArrayAdapter);


        //setting value of datepicker
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat sdfFullTime=new SimpleDateFormat("dd-MMM-yy K.mm.ss.SSS a");
        Calendar calendar= Calendar.getInstance();
//        SimpleDateFormat
        try {
            Date dateTmp = sdf.parse(str_TanggalMulai);


            DatePicker dp_tanggalMulai =(DatePicker)findViewById(R.id.WaktuMulai_datePicker);
            DatePicker dp_tanggalSelesai = (DatePicker)findViewById(R.id.WaktuSelesai_datePicker);
            TimePicker tm_waktuMulai = (TimePicker)findViewById(R.id.WaktuMulai_timePicker);
            TimePicker tm_waktuSelesai=(TimePicker)findViewById(R.id.WaktuSelesai_timePicker);
            EditText ed_perihal =(EditText)findViewById(R.id.perihalEditText);
            EditText ed_penanggungJawab = (EditText)findViewById(R.id.penanggungJawabEditText);

            calendar.setTime(dateTmp);
            dp_tanggalMulai.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

            dateTmp=sdf.parse(str_TanggalSelesai);
            calendar.setTime(dateTmp);
            dp_tanggalSelesai.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

            dateTmp= sdfFullTime.parse(str_jamMulai);
            calendar.setTime(dateTmp);
            tm_waktuMulai.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            tm_waktuMulai.setCurrentMinute(calendar.get(Calendar.MINUTE));

            dateTmp=sdfFullTime.parse(str_jamSelesai);
            calendar.setTime(dateTmp);
            tm_waktuSelesai.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            tm_waktuSelesai.setCurrentMinute(calendar.get(Calendar.MINUTE));


            ed_perihal.setText(str_perihal);
            ed_penanggungJawab.setText(str_penanggungJawab);
            // SPINNER ///
            // mengganti spinner aplikasi
            if(str_applikasiRapat!=null) {
                ArrayAdapter myAdap = (ArrayAdapter) aplikasiSpinner.getAdapter();
                int position = myAdap.getPosition(str_applikasiRapat);
                aplikasiSpinner.setSelection(position);
            }
            // Mengganti spinner ruangan

            if(str_idRuangan!=null) {
                ArrayAdapter myAdap = (ArrayAdapter) ruanganSpinner.getAdapter();
                int pos = myAdap.getPosition(str_idRuangan);
                ruanganSpinner.setSelection(pos);
            }
        }catch(Exception e)
        {
            Log.e("ERROR in date populate",e.toString());
        }
           // str_TanggalMulai.replace('-','/');

        //dp_tanggalMulai.


    }


    /****************************************************************************
     * FOR SUBMITTING DATA
     */
    public class submitData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(editRapat.this);
            pDialog.setMessage("Submitting data");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/editRapatByID.php";

            JSONObject json = null;
            try {
                postDataSubmit(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*try {
                JSONArray aplikasi = json.getJSONArray("aplikasi");
                JSONArray ruangan = json.getJSONArray("ruangan");
                dictAplikasi= new HashMap<String, String>();
                dictRuangan= new HashMap<String, String>();
                for(int i=0; i<aplikasi.length(); i++)
                {
                    JSONObject c = aplikasi.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id_aplikasi", c.getString("id_aplikasi"));
                    map.put("nama_aplikasi", c.getString("nama_aplikasi"));
                    nama_aplikasiList.add(c.getString("nama_aplikasi"));
                    dictAplikasi.put(c.getString("nama_aplikasi"),c.getString("id_aplikasi"));
                    MyArrListAplikasi.add(map);
                }
                for(int i=0; i<ruangan.length(); i++)
                {
                    JSONObject c = ruangan.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("id_ruangan", c.getString("id_ruangan"));
                    map.put("nama_ruangan", c.getString("nama_ruangan"));
                    dictRuangan.put(c.getString("nama_ruangan"),c.getString("id_ruangan"));
                    nama_ruanganList.add(c.getString("nama_ruangan"));
                    MyArrListAplikasi.add(map);
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
*/
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            finish();
            //populateView();
        }
    }
    public void postDataSubmit(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            DatePicker waktuMulai_dateP = (DatePicker)findViewById(R.id.WaktuMulai_datePicker);
            DatePicker waktuSelesai_dateP = (DatePicker)findViewById(R.id.WaktuSelesai_datePicker);
            TimePicker waktuMulai_timeP = (TimePicker)findViewById(R.id.WaktuMulai_timePicker);
            TimePicker waktuSelesai_timeP = (TimePicker)findViewById(R.id.WaktuSelesai_timePicker);

            //ambil ID ruangan berdasarkan nama ruangan
            String id_ruangan=dictRuangan.get(ruanganSpinner.getSelectedItem().toString());

            //ambil ID aplikasi berdasarkan nama aplikasi
            String id_aplikasi=dictAplikasi.get(aplikasiSpinner.getSelectedItem().toString());

            //  EditText ad=((EditText)findViewById(R.id.passwordEditText));
            //String PenanngungJawab = ad.getText().toString();
            String dateMulai= waktuMulai_dateP.getYear()+"/"+waktuMulai_dateP.getMonth()+"/"+waktuMulai_dateP.getDayOfMonth();
            String dateSelesai = waktuSelesai_dateP.getYear()+"/"+waktuSelesai_dateP.getMonth()+"/"+waktuSelesai_dateP.getDayOfMonth();
            String timeStampMulai = dateMulai+" "+waktuMulai_timeP.getCurrentHour()+":"+waktuSelesai_timeP.getCurrentMinute();
            String timeStampSelesai = dateSelesai+" "+ waktuSelesai_timeP.getCurrentHour()+":"+waktuSelesai_timeP.getCurrentMinute();


            //Still zonk value
            String perihal = ((EditText)findViewById(R.id.perihalEditText)).getText().toString();
            String penanggungJawab=((EditText)findViewById(R.id.penanggungJawabEditText)).getText().toString();
            String resumeHasil="Belum ada Resume hasil";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = new Date();
            String tanggalBuat = dateFormat.format(date).toString(); //2014/08/06 15:59:48
            String pembuatJadwalID=ID_USER;
            String statusRapat="0";

            String cor="";
            // get list of anggota rapat
            int flag=0;
            for(String key :listPeserta.keySet())
            {
                if(flag>0)cor+=",";
                flag++;
                cor+=listPeserta.get(key);
            }

            // cor+="}";


            //putting key to json
            json.put("idrapat",IDRAPATEDIT);
            json.put("ruangan",id_ruangan);
            json.put("aplikasi",id_aplikasi);
            json.put("dateMulai",dateMulai);
            json.put("dateSelesai",dateSelesai);
            json.put("timeStampMulai",timeStampMulai);
            json.put("timeStampSelesai",timeStampSelesai);
            json.put("perihal",perihal);
            json.put("penanggungJawab",penanggungJawab);
            json.put("resumeHasil",resumeHasil);
            json.put("tanggalBuatRapat",tanggalBuat);
            json.put("pembuatJadwal",pembuatJadwalID);
            json.put("statusRapat",statusRapat);
            // add array of String ID peserta rapat
            json.put("listPeserta",cor);


            //  json.put("keyword", edittext_search.getText());
            JSONArray postjson=new JSONArray();
            postjson.put(json);

            // Post the data:
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost",postjson);

            // Execute HTTP Post Request
            //  System.out.print(json);
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
        //Toast.makeText(getApplicationContext(), "data sudah di submit" , Toast.LENGTH_LONG).show();
    }
}
