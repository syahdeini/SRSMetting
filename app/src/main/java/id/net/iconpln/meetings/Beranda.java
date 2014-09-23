package id.net.iconpln.meetings;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Beranda extends ActionBarActivity {

    Button buatJadwalBaruBtn, button_lihatJadwalRapat, button_kelolaJadwalRapat, button_cariDokumenRapat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buatJadwalBaruBtn = (Button) findViewById(R.id.jadwalBaruBtn);
        button_lihatJadwalRapat = (Button) findViewById(R.id.button_lihatJadwalRapat);
        button_kelolaJadwalRapat = (Button) findViewById(R.id.button_kelolaJadwalRapat);
        button_cariDokumenRapat = (Button) findViewById(R.id.button_cariDokumenRapat);

        buatJadwalBaruBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daftarRapatActv = new Intent(Beranda.this, daftar_rapat.class);
                startActivity(daftarRapatActv);
            }
        });
        button_lihatJadwalRapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }
}
