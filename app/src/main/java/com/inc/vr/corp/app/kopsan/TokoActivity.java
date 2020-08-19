package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inc.vr.corp.app.kopsan.DB.koneksi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TokoActivity extends AppCompatActivity {
    TextView namaToko, txtsaldo;
    String saldo="";
    SharedPreferences sharedPreferences;
    Button transaksi, logout;
    LinearLayout bgFade;
    koneksi koneksiClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko);
        koneksiClass = new koneksi();
        namaToko = findViewById(R.id.nama_toko);
        transaksi = findViewById(R.id.btnutama);
        logout = findViewById(R.id.btnlogout);
        txtsaldo = findViewById(R.id.saldo);
        bgFade = findViewById(R.id.bg_fade);
        sharedPreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        namaToko.setText(sharedPreferences.getString("nama","Kopsan"));

        transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TokoActivity.this, MenuActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("kopsan", MODE_PRIVATE).edit();
                editor.putString("nama", "");
                editor.putString("noid", "");
                editor.putString("no_rek", "");
                editor.putBoolean("session_status", false);
                editor.apply();
                startActivity(new Intent(TokoActivity.this, LoginActivity.class));
            }
        });
        if(cekKoneksi()){
            GetData fillList = new GetData();
            fillList.execute("");
        }else{
            Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        }
    }
    public boolean cekKoneksi(){
        //---------- CEK KONEKSI ------------
        ConnectivityManager conMgr;
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

    }
    public class GetData extends AsyncTask<String, String, String> {
        String z = "";
        @Override
        protected void onPreExecute() {
            bgFade.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            bgFade.setVisibility(View.GONE);
            txtsaldo.setText(KurensiIndonesia(Double.parseDouble(saldo.split("-")[0])));
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
                        Log.d("TAG", "doInBackground: "+"table ada");
                    }
                    else {
                        // Table does not exist
                        Log.d("TAG", "doInBackground: "+"table ga ada");
                    }
                    String qsaldo = "select * from tbl_tabungan where no_rek='"+sharedPreferences.getString("no_rek","Kopsan").trim()+"' order by id desc";
                    PreparedStatement psaldo = con.prepareStatement(qsaldo);
                    ResultSet rsaldo = psaldo.executeQuery();
                    Log.d("TAG", "doInBackground: "+qsaldo);
                    Integer a=0;
                    while (rsaldo.next()) {
                        if(rsaldo.wasNull()){
                            saldo="0";
                        }else{
                            if(a<1){
                                saldo=rsaldo.getString("saldo");
                            }

                        }
                        a++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return z;
        }
    }
    public String KurensiIndonesia(double harga){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        System.out.printf("Hargaah: %s %n", kursIndonesia.format(harga));
        return kursIndonesia.format(harga);
    };
}