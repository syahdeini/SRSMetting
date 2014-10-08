package id.net.iconpln.meetings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Manage_rapat extends ActionBarActivity {
    SessionManager session;
    HashMap<String, String> map, user;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ProgressDialog pDialog;

    String url;
    String text;
    String id_rapat;

    File upload;

    int serverResponseCode = 0;
    String upLoadServerUri;
    ProgressDialog dialog = null;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                upload = new File(data.getData().getPath());
                dialog = ProgressDialog.show(Manage_rapat.this, "", "Uploading file...", true);
                new uploadDokumen().execute();
            }
        }
    }

    private String en(String plain) throws UnsupportedEncodingException {
        return URLEncoder.encode(plain, "UTF-8");
    }

    public class uploadDokumen extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                upLoadServerUri = "http://" + globalVar.serverIPaddress + "/meetings/uploadDokumen.php" +
                        "?id_rapat=" + id_rapat +
                        "&uploader_id=" + user.get(SessionManager.KEY_ID_USER) +
                        "&status_dokumen=" + "1" +
                        "&nama_dokumen=" + en(upload.getName());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            pDialog = new ProgressDialog(Manage_rapat.this);
            pDialog.setMessage("Sedang menyimpan");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String sourceFileUri = upload.getAbsolutePath();
            String fileName = sourceFileUri;

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                dialog.dismiss();

                Log.e("unggahBerkas", sourceFile.getPath());

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Berkas tidak ditemukan\nTidak bisa upload foto/video", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                    }

                    Toast.makeText(getApplicationContext(), "Berhasil upload dokumen", Toast.LENGTH_SHORT).show();

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    dialog.dismiss();

                } catch (MalformedURLException ex) {
                    dialog.dismiss();
                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Manage_rapat.this, "MalformedURLException",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Unggah berkas ke server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //messageText.setText("Got Exception : see logcat ");
                            Toast.makeText(Manage_rapat.this, "Got Exception : see logcat ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Unggah berkas ke server Exception", "Exception : "
                            + e.getMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }
    }

    public class AmbilDaftarRapat extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Manage_rapat.this);
            pDialog.setMessage("Mengambil data rapat");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            JSONParser jParser = new JSONParser();
            url = "http://" + globalVar.serverIPaddress + "/meetings/data_rapat.php";
            JSONObject json = jParser.getJSONFromUrl(url);
            MyArrList.clear();

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
            Toast.makeText(getApplicationContext(), "Klik untuk upload dokumen", Toast.LENGTH_LONG).show();
        }
    }

    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("id_rapat", id_rapat);

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

    public class DeleteRapat extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/deleteRapat.php";

            try {
                postData(url);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new AmbilDaftarRapat().execute();
        }
    }

    private void kelolaRapat() {
        final CharSequence[] options = { "Edit Rapat", "Upload Dokumen", "Hapus Rapat", "Batal" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Manage_rapat.this);
        builder.setTitle("Kelola Rapat");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Edit Rapat")) {
                        // edit rapat clicked
                        globalVar.idRapatEdited=id_rapat;
                        Intent editRapatIntent= new Intent(Manage_rapat.this,editRapat.class);
                        startActivity(editRapatIntent);
                        finish();
                }
                else if (options[item].equals("Upload Dokumen")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Hapus Rapat")) {
                    new AlertDialog.Builder(Manage_rapat.this)
                            .setTitle("Hapus Rapat?")
                            .setMessage("Yakin ingin hapus rapat ini?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Berhasil hapus rapat", Toast.LENGTH_SHORT).show();
                                    new DeleteRapat().execute();
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
                else if (options[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
                    id_rapat = aMyArrList.get("id_rapat");
                    kelolaRapat();
                }
            });
            layout.addView(but);
        }
    }
}