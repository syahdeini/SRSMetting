package id.net.iconpln.meetings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Main extends Activity {

    Button button_login, button_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_login = (Button) findViewById(R.id.button_login);
        button_register = (Button) findViewById(R.id.button_register);

        button_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                Intent a = new Intent(Main.this, Login.class);
                startActivity(a);
            }
        });
        button_register.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent registerIntent= new Intent(Main.this,Register.class);
                startActivity(registerIntent);
            }
        });
    }
}
