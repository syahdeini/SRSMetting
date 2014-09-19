package id.net.iconpln.meetings;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Beranda extends ActionBarActivity {

    Button buatJadwalBaruBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buatJadwalBaruBtn=(Button)findViewById(R.id.jadwalBaruBtn);
        buatJadwalBaruBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daftarRapatActv = new Intent(Beranda.this, daftar_rapat.class);
                startActivity(daftarRapatActv);
              //  finish();
            }
        });
    }


}
