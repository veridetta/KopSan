package com.inc.vr.corp.app.kopsan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.inc.vr.corp.app.kopsan.DB.koneksi;
import com.inc.vr.corp.app.kopsan.app.AppController;
import com.inc.vr.corp.app.kopsan.server.LokUrl;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    TextInputEditText edit_email, edit_password;
    TextView appversion;
    ProgressDialog pDialog;
    private String url = LokUrl.URL + "login.php";
    int success;
    ConnectivityManager conMgr;
    private static final String TAG = LoginActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;
    Boolean session = false, login=false;
    String string_email, string_id, no_token,db, nama, noid, no_rek, pedagang_code;
    boolean doubleBackToExitPressedOnce = false;
    koneksi koneksiClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        edit_email = findViewById(R.id.email_input);
        edit_password = findViewById(R.id.password_input);
        appversion = findViewById(R.id.appversion);
        koneksiClass = new koneksi();
        Bundle cek_data = getIntent().getExtras();
        String version = "1.0";
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memeriksa data");
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appversion.setText("App Version " + version);

        //---------- CEK KONEKSI ------------
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        //-------------------------------------
        //-------Cek session login jika TRUE
        // maka langsung buka MainActivity --------------------
        sharedpreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean("session_status", false);
        nama = sharedpreferences.getString("nama", null);
        noid = sharedpreferences.getString("noid", null);
        no_rek= sharedpreferences.getString("no_rek", null);
        if (session) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("noid", string_id);
            intent.putExtra("nama", string_email);
            finish();
            startActivity(intent);
        }
        // ------------------------------
        // ------ BUTTON MASUK KLIK ------------
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ----------- CEK YANG MASIH KOSONG ------
                if (edit_email.getText().toString().trim().length() > 0 && edit_password.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        cekLogin fillList = new cekLogin();
                        fillList.execute("");
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    // ------ FUNCTION CEK LOGIN ---------------
    public class cekLogin extends AsyncTask<String, String, String> {
        String z = "";
        @Override
        protected void onPreExecute() {
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            pDialog.hide();
            if(login){
                SharedPreferences.Editor editor = getSharedPreferences("kopsan", MODE_PRIVATE).edit();
                editor.putString("nama", nama);
                editor.putString("noid", noid);
                editor.putString("no_rek", no_rek);
                editor.putString("pedagang_code", pedagang_code);
                editor.putBoolean("session_status", true);
                editor.apply();
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            }else{
                Toast.makeText(getApplicationContext(),"Silahkan Coba Lagi",Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(ScanResultActivity.this, z, Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = koneksiClass.CONN();
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    DatabaseMetaData dbm = con.getMetaData();
                    // check if "employee" table is there
                    ResultSet tables = dbm.getTables(null, null, "nasabah_table", null);
                    if (tables.next()) {
                        // Table exists
                        Log.d(TAG, "doInBackground: "+"table ada");
                    }
                    else {
                        // Table does not exist
                        Log.d(TAG, "doInBackground: "+"table ga ada");
                    }
                    String query = "select u.no_anggota, u.pwd, u.noid, ms_pedagang.noid, ms_pedagang.no_rek, ms_pedagang.pedagang_code,nasabah_table.noid," +
                            "nasabah_table.nama_lengkap from tbl_users u inner join ms_pedagang " +
                            "on ms_pedagang.noid=u.noid inner join nasabah_table on nasabah_table.noid=ms_pedagang.noid" +
                            " where " +
                            "u.pwd='225022234'";
                    Log.d(TAG, "doInBackground: "+edit_password.getText().toString()+" "+edit_email.getText().toString());
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if(rs.wasNull()){
                            login=false;
                        }else{
                            login=true;
                            nama=rs.getString("nama_lengkap");
                            noid=rs.getString("noid");
                            no_rek=rs.getString("no_rek");
                            pedagang_code=rs.getString("pedagang_code");
                        }
                    }
                    Log.d(TAG, "doInBackground: "+rs.getCursorName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return z;
        }
    }
    /// -----------------
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
