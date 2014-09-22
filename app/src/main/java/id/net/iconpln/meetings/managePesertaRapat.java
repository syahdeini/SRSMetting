package id.net.iconpln.meetings;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class managePesertaRapat extends ActionBarActivity {

    Button addPesertaBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_peserta_rapat);

        setupListViewAdapter();
        addPesertaBtn= (Button)findViewById(R.id.addPesertaButton);
        addPesertaBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                EditText pesertaEditText = (EditText)findViewById(R.id.pesertaEditText);
                adapter.insert(new AtomPayment(pesertaEditText.getText().toString(), 0), 0);
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

    private AtomPayListAdapter adapter;
    public void removeAtomPayOnClickHandler(View v) {
        AtomPayment itemToRemove = (AtomPayment)v.getTag();
        adapter.remove(itemToRemove);
    }

    private void setupListViewAdapter() {
        adapter = new AtomPayListAdapter(managePesertaRapat.this, R.layout.atom_pay_list_item, new ArrayList<AtomPayment>());
        ListView atomPaysListView = (ListView)findViewById(R.id.EnterPays_atomPaysList);
        atomPaysListView.setAdapter(adapter);
    }



// Buat thread yang mengecek apakah nama ada di User table, kalo ada ambil ID user tersebut

}
