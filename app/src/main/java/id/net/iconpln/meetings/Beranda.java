package id.net.iconpln.meetings;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Beranda extends ActionBarActivity {

    Button buatJadwalBaruBtn, button_lihatJadwalRapat, button_kelolaJadwalRapat, button_cariDokumenRapat,
            button_ubahAkun, button_logout;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        session = new SessionManager(getApplicationContext());

        buatJadwalBaruBtn = (Button) findViewById(R.id.jadwalBaruBtn);
        button_lihatJadwalRapat = (Button) findViewById(R.id.button_lihatJadwalRapat);
        button_kelolaJadwalRapat = (Button) findViewById(R.id.button_kelolaJadwalRapat);
        button_cariDokumenRapat = (Button) findViewById(R.id.button_cariDokumenRapat);
        button_ubahAkun = (Button) findViewById(R.id.button_ubahAkun);
        button_logout = (Button) findViewById(R.id.button_logout);

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
                //Intent a = new Intent(Beranda.this, EditUserAccount.class);
                //startActivity(a);
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
                session.logoutUser();
                finish();
                Toast.makeText(getApplicationContext(), "Berhasil logout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
