package id.net.iconpln.meetings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class LihatRapat extends Activity {
    SessionManager session;
    HashMap<String, String> map, user;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ProgressDialog pDialog;

    String url;
    String text;

    Dialog dialog_rapat_view;

    TextView textview_perihal, textview_tanggal_mulai, textview_jam_mulai, textview_tanggal_selesai,
            textview_jam_selesai, textview_nama_ruangan, textview_nama_pembuat_jadwal,
            textview_penanggung_jawab, textview_resume_hasil;
    Button button_batal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rapat);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUserDetails();
        new AmbilDaftarRapat().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public class AmbilDaftarRapat extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LihatRapat.this);
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
                    map.put("nama_ruangan", c.getString("nama_ruangan"));
                    map.put("nama_pembuat_jadwal", c.getString("nama_pembuat_jadwal"));
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
            Toast.makeText(getApplicationContext(), "Klik untuk melihat rincian", Toast.LENGTH_LONG).show();
        }
    }

    private void populateView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();
        for (final HashMap<String, String> aMyArrList : MyArrList) {
            String perihal = aMyArrList.get("perihal");
            String penanggungjawab = aMyArrList.get("penanggungjawab");
            String tanggal = aMyArrList.get("tanggal_mulai");
            String jam = aMyArrList.get("jam_mulai").replaceAll(".000000", "").substring(10);
            String ruangan = aMyArrList.get("nama_ruangan");

            Button but = new Button(this);
            String[] id = aMyArrList.get("id_rapat").split("-");
            but.setId(Integer.parseInt(id[1]));
            but.setHint(aMyArrList.get("id_rapat"));
            but.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            but.setText(Html.fromHtml("<big>" + perihal + "</big><br/><font color=\"blue\"><small>" + tanggal + ", " + jam + "-" + ruangan + "</small></font><br/>" +
                    penanggungjawab));
            but.setGravity(Gravity.START);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_rapat_view = new Dialog(LihatRapat.this);
                    dialog_rapat_view.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_rapat_view.setContentView(R.layout.dialog_rapat_view);
                    dialog_rapat_view.setCancelable(true);

                    textview_perihal = (TextView) dialog_rapat_view.findViewById(R.id.textview_perihal);
                    textview_tanggal_mulai = (TextView) dialog_rapat_view.findViewById(R.id.textview_tanggal_mulai);
                    textview_jam_mulai = (TextView) dialog_rapat_view.findViewById(R.id.textview_jam_mulai);
                    textview_tanggal_selesai = (TextView) dialog_rapat_view.findViewById(R.id.textview_tanggal_selesai);
                    textview_jam_selesai = (TextView) dialog_rapat_view.findViewById(R.id.textview_jam_selesai);
                    textview_nama_ruangan = (TextView) dialog_rapat_view.findViewById(R.id.textview_nama_ruangan);
                    textview_nama_pembuat_jadwal = (TextView) dialog_rapat_view.findViewById(R.id.textview_nama_pembuat_jadwal);
                    textview_penanggung_jawab = (TextView) dialog_rapat_view.findViewById(R.id.textview_penanggung_jawab);
                    textview_resume_hasil = (TextView) dialog_rapat_view.findViewById(R.id.textview_resume_hasil);
                    button_batal = (Button) dialog_rapat_view.findViewById(R.id.button_batal);

                    textview_perihal.setText(aMyArrList.get("perihal"));
                    textview_tanggal_mulai.setText(aMyArrList.get("tanggal_mulai"));
                    String jam_temp = aMyArrList.get("jam_mulai").replaceAll(".000000", "");
                    if(jam_temp.length() > 9)
                        jam_temp = jam_temp.substring(10);
                    textview_jam_mulai.setText(jam_temp);
                    textview_tanggal_selesai.setText(aMyArrList.get("tanggal_selesai"));
                    jam_temp = aMyArrList.get("jam_selesai").replaceAll(".000000", "");
                    if(jam_temp.length() > 9)
                        jam_temp = jam_temp.substring(10);
                    textview_jam_selesai.setText(jam_temp);
                    textview_nama_ruangan.setText(aMyArrList.get("nama_ruangan"));
                    textview_nama_pembuat_jadwal.setText(aMyArrList.get("nama_pembuat_jadwal"));
                    textview_penanggung_jawab.setText(aMyArrList.get("penanggungjawab"));
                    textview_resume_hasil.setText(aMyArrList.get("resume_hasil"));

                    button_batal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_rapat_view.dismiss();
                        }
                    });

                    dialog_rapat_view.show();
                }
            });
            layout.addView(but);
        }
    }
}