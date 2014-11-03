package id.net.iconpln.meetings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Beranda extends ActionBarActivity {

    Button buatJadwalBaruBtn, button_lihatJadwalRapat, button_kelolaJadwalRapat, button_cariDokumenRapat,
            button_ubahAkun, button_logout, button_adminpage;
    TextView textview_notifikasi;
    SessionManager session;
    HashMap<String, String> map, user;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    String url;
    String peran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUserDetails();
        peran = user.get(SessionManager.KEY_PERAN);

        buatJadwalBaruBtn = (Button) findViewById(R.id.jadwalBaruBtn);
        button_lihatJadwalRapat = (Button) findViewById(R.id.button_lihatJadwalRapat);
        button_kelolaJadwalRapat = (Button) findViewById(R.id.button_kelolaJadwalRapat);
        button_cariDokumenRapat = (Button) findViewById(R.id.button_cariDokumenRapat);
        button_ubahAkun = (Button) findViewById(R.id.button_ubahAkun);
        button_logout = (Button) findViewById(R.id.button_logout);
        textview_notifikasi = (TextView) findViewById(R.id.textview_notifikasi);

        new AmbilNotifikasi().execute();

        if(peran.equals("USR")) {
            buatJadwalBaruBtn.setVisibility(View.GONE);
            button_kelolaJadwalRapat.setVisibility(View.GONE);
        }

        buatJadwalBaruBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Beranda.this, daftar_rapat.class);
                startActivity(a);
            }
        });
        button_lihatJadwalRapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Beranda.this, LihatRapat.class);
                startActivity(a);
            }
        });
        button_kelolaJadwalRapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Beranda.this, Manage_rapat.class);
                startActivity(a);
            }
        });
        button_cariDokumenRapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Beranda.this, Dokumen.class);
                startActivity(a);
            }
        });
        button_ubahAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Beranda.this, EditUserAccount.class);
                startActivity(a);
            }
        });
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inginLogout();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(peran.equals("ADM")) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.button_admin:
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inginLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Apakah Anda ingin logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        session.logoutUser();
                        finish();
                        Toast.makeText(getApplicationContext(), "Berhasil logout", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // kosong
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void populateNotifikasi() {
        StringBuilder notifikasi = new StringBuilder("RAPAT HARI INI\n\n");
        String temp, jam_selesai;
        for (final HashMap<String, String> aMyArrList : MyArrList) {
            jam_selesai = aMyArrList.get("jam_selesai");
            if(jam_selesai.length() > 9)
                jam_selesai = jam_selesai.replaceAll(".000000", "").substring(10);
            else
                jam_selesai = "-";
            temp = "<big><b>" + aMyArrList.get("perihal") + "</b></big><br/>" +
                    aMyArrList.get("jam_mulai").replaceAll(".000000", "").substring(10) + " - " +
                    jam_selesai + ", " +
                    aMyArrList.get("nama_ruangan") + "<br/><br/>";
            notifikasi.append(Html.fromHtml(temp));
        }
        if (MyArrList.size() == 0)
            notifikasi.append("(tidak ada rapat)");
        textview_notifikasi.setText(notifikasi);
    }

    public class AmbilNotifikasi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textview_notifikasi.setText("Sedang mengambil notifikasi");
        }

        @Override
        protected String doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();
            url = "http://" + globalVar.serverIPaddress + "/meetings/getNotifikasi.php";
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                JSONArray rapat = json.getJSONArray("notifikasi");

                for(int i=0; i<rapat.length(); i++)
                {
                    JSONObject c = rapat.getJSONObject(i);

                    map = new HashMap<String, String>();
                    map.put("jam_mulai", c.getString("jam_mulai"));
                    map.put("jam_selesai", c.getString("jam_selesai"));
                    map.put("perihal", c.getString("perihal"));
                    map.put("nama_ruangan", c.getString("nama_ruangan"));
                    MyArrList.add(map);
                }

            } catch (Exception e) {
                Log.e("erro", "JSON notifikasi");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            populateNotifikasi();
        }
    }

    @Override
    public void onBackPressed() {
        inginLogout();
    }
}
