package id.net.iconpln.meetings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Main extends Activity {

    Button button_login, button_register;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        button_login = (Button) findViewById(R.id.button_login);
        button_register = (Button) findViewById(R.id.button_register);

        button_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent a = new Intent(Main.this, Login.class);
                startActivity(a);
                finish();
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

    private boolean isLastActivity() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1024);

        final String ourAppPackageName = getPackageName();
        ActivityManager.RunningTaskInfo taskInfo;
        final int size = tasksInfo.size();
        for (int i = 0; i < size; i++) {
            taskInfo = tasksInfo.get(i);
            if (ourAppPackageName.equals(taskInfo.baseActivity.getPackageName())) {
                return taskInfo.numActivities == 1;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isLastActivity()) {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar?")
                    .setMessage("Keluar dari aplikasi?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // kosong
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            super.onBackPressed(); // this will actually finish the Activity
        }
    }
}
