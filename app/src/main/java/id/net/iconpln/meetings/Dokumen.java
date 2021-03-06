package id.net.iconpln.meetings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import srsmeeting.iconpln.net.id.srsmeeting.R;

public class Dokumen extends Activity {
    SessionManager session;
    HashMap<String, String> map, user;
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ProgressDialog pDialog;

    EditText edittext_search;
    Button button_search;

    String url;
    String text;

    String id_dokumen;
    String nama_dokumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokumen);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        edittext_search = (EditText) findViewById(R.id.edittext_search);
        button_search = (Button) findViewById(R.id.button_search);

        new SearchDokumen().execute();

        button_search.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyArrList.clear();
                new SearchDokumen().execute();
            }
        });

        Toast.makeText(getApplicationContext(), "Klik untuk download dokumen", Toast.LENGTH_LONG).show();
    }

    public void postData(String url) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject json = new JSONObject();

        try {
            // JSON data:
            json.put("keyword", edittext_search.getText());

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

    public class SearchDokumen extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Dokumen.this);
            pDialog.setMessage("Mengambil data");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            url = "http://" + globalVar.serverIPaddress + "/meetings/dokumen.php";

            JSONObject json = null;
            try {
                postData(url);
                json = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray dokumen = json.getJSONArray("dokumen");

                for(int i=0; i<dokumen.length(); i++)
                {
                    JSONObject c = dokumen.getJSONObject(i);

                    map = new HashMap<String, String>();
                    map.put("id_dokumen", c.getString("id_dokumen"));
                    map.put("nama", c.getString("nama"));
                    map.put("waktu_upload", c.getString("waktu_upload"));
                    map.put("status_dokumen", c.getString("status_dokumen"));
                    map.put("nama_dokumen", c.getString("nama_dokumen"));
                    map.put("tipe_file",c.getString("tipe_file"));
                    map.put("perihal",c.getString("perihal"));
                    map.put("tanggal_mulai",c.getString("tanggal_mulai"));
                    map.put("jam_mulai",c.getString("jam_mulai"));
                    map.put("nama_ruangan",c.getString("nama_ruangan"));
                    MyArrList.add(map);
                }

            } catch (Exception e) {
                Log.e("erro", e.toString());
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

    private void populateView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_buttons);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();
        for(int i=0; i<MyArrList.size(); i++)
        {
            final int finalI = i;
            nama_dokumen = MyArrList.get(i).get("nama_dokumen");
            if(nama_dokumen.length()>34)
                nama_dokumen = nama_dokumen.substring(0, 30) + " ...";
            String nama = MyArrList.get(i).get("nama");
            String waktu_upload = MyArrList.get(i).get("waktu_upload").replaceAll(".000000", "");
            String tanggal = MyArrList.get(i).get("tanggal_mulai");
            String jam = MyArrList.get(i).get("jam_mulai").replaceAll(".000000", "").substring(9);
            String ruangan = MyArrList.get(i).get("nama_ruangan");

            Button but = new Button(this);
            String[] id = MyArrList.get(i).get("id_dokumen").split("-");
            but.setId(Integer.parseInt(id[1]));
            but.setHint(MyArrList.get(i).get("id_dokumen"));
            but.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            but.setText(Html.fromHtml("<big>" + MyArrList.get(i).get("perihal") + "</big><br/><font color=\"blue\"><small>" + tanggal + ", " + jam + "-" + ruangan + "</small><br/></font></big>" + nama_dokumen + "<br/><font color=\"blue\"><small>oleh: " + nama + "</small></font>"));
            but.setGravity(Gravity.START);

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id_dokumen = MyArrList.get(finalI).get("id_dokumen");
                    nama_dokumen = MyArrList.get(finalI).get("nama_dokumen");
                    new unduh_dokumen().execute();
                }
            });
            layout.addView(but);
        }
    }

    public class unduh_dokumen extends AsyncTask<String, String, String> {
        File dokumen;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Dokumen.this);
            pDialog.setMessage("Mengunduh dokumen");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                File saveDir = new File(android.os.Environment.getExternalStorageDirectory().toString() + "/ICON+Meetings/Dokumen");

                if(!saveDir.exists()){
                    saveDir.mkdirs();
                }

                URL url = new URL("http://" + globalVar.serverIPaddress + "/meetings/unduh_dokumen.php?id_dokumen=" + id_dokumen);
                dokumen = new File(saveDir,nama_dokumen);

                URLConnection uconn = url.openConnection();
                uconn.setReadTimeout(15000);
                uconn.setConnectTimeout(15000);

                InputStream is = uconn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                int current;
                while((current = bis.read()) != -1){
                    baf.append((byte) current);
                }

                FileOutputStream fos = new FileOutputStream( dokumen);
                fos.write(baf.toByteArray());
                fos.flush();
                fos.close();
            }
            catch(IOException e) {
                Log.e("DownloadManager", "Error:" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            File myFile = dokumen;
            try {
                openFile(getApplicationContext(), myFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file=url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
