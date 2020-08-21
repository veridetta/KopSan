package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.inc.vr.corp.app.kopsan.DB.koneksi;
import com.inc.vr.corp.app.kopsan.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaldoActivity extends AppCompatActivity {
    String saldo, idSiswa, url = "login.php";
    int success;
    TextView txtKelas, txtAlamat, textViewNama;
    TextView txtSaldo;
    koneksi koneksiClass;
    LinearLayout hasil, bgFade, lyKonfirm;
    Button btnLain;
    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog pDialog;
    String nama="default", alamat="", kelas="",noid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo);
        Intent intent = getIntent();
        idSiswa = intent.getStringExtra("idSiswa");
        //idSiswa="0113-08-2249";
        koneksiClass = new koneksi();
        textViewNama = (TextView) findViewById(R.id.txtNama);
        txtAlamat=findViewById(R.id.alamat);
        txtKelas=findViewById(R.id.kelas);
        txtSaldo=findViewById(R.id.saldo);
        bgFade = findViewById(R.id.bg_fade);
        btnLain = findViewById(R.id.btnLain);
        if(cekKoneksi()){
            GetData fillList = new GetData();
            fillList.execute("");
        }
        btnLain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SaldoActivity.this, MenuActivity.class));
            }
        });
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
            textViewNama.setText(nama);
            txtAlamat.setText(alamat);
            txtKelas.setText(kelas);
            txtSaldo.setText(KurensiIndonesia(Double.parseDouble(saldo.split("-")[0])));
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
                    String query = "select t.no_rek, t.noid, n.nama_lengkap, n.noid, n.detail_pekerjaan," +
                            "n.jalan_gg, n.blok, n.rt, n.rw, n.desa, n.kecamatan from nasabah_tabungan t inner join nasabah_table n on n.noid=t.noid where t.no_rek='"+idSiswa+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if(rs.wasNull()){
                            nama="kosong";
                        }else{
                            String al="";
                            nama=rs.getString("nama_lengkap");
                            kelas="Kelas "+rs.getString("detail_pekerjaan");
                            if(rs.getString("jalan_gg")==null){
                                alamat="alamat belum di set";
                            }else{
                                al=rs.getString("jalan_gg")+" "+rs.getString("blok")+" "+rs.getString("rt")+"/"+rs.getString("rw")+" "+rs.getString("desa")+" "+rs.getString("kecamatan");
                                if(al.length()>24){
                                    alamat=al;
                                }else{
                                    alamat="alamat belum di set";
                                }
                            }

                        }
                    }
                    String qsaldo = "select saldo from tbl_tabungan where no_rek='"+idSiswa+"' order by no_urut desc";
                    PreparedStatement psaldo = con.prepareStatement(qsaldo);
                    ResultSet rsaldo = psaldo.executeQuery();
                    Integer a=0;
                    if(rsaldo.wasNull()){
                        saldo="0";
                    }else{
                        while (rsaldo.next()) {
                            if(rsaldo.wasNull()){
                                saldo="0";
                            }else{
                                if(a<1){
                                    saldo=rsaldo.getString("saldo");
                                }
                                a++;
                            }
                        }
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