package com.inc.vr.corp.app.kopsan;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.inc.vr.corp.app.kopsan.DB.koneksi;
import com.inc.vr.corp.app.kopsan.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit.NOW;

public class ScanResultActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private Button btnScan, btnBayar, btnKonfirm;
    private TextView textViewNama, txtClose, txtKelas, txtAlamat;
    EditText password;
    String idSiswa, pass="", tped_code,tnama, tno_rek, tnoid;
    CurrencyEditText nominal;
    LinearLayout hasil, bgFade, lyKonfirm;
    koneksi  koneksiClass;
    String z="";
    boolean saldoCukup=false, isSuccess=false, isPassword=false;
    int success;
    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog pDialog;
    String nama="default", alamat="", kelas="",noid="",saldo="", no_trans="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        koneksiClass = new koneksi();
        // initialize object
        hasil = findViewById(R.id.result);
        btnScan = (Button) findViewById(R.id.buttonScan);
        btnBayar = findViewById(R.id.btnbayar);
        btnKonfirm = findViewById(R.id.btnkonfirm);
        textViewNama = (TextView) findViewById(R.id.txtNama);
        txtAlamat=findViewById(R.id.alamat);
        txtKelas=findViewById(R.id.kelas);
        nominal = findViewById(R.id.nominal_input);
        password = findViewById(R.id.password_input);
        bgFade = findViewById(R.id.bg_fade);
        lyKonfirm = findViewById(R.id.lyKonfirm);
        txtClose = findViewById(R.id.tutup);
        //baca kiriman
        Intent kiriman = getIntent();
        idSiswa = kiriman.getStringExtra("idSiswa");
        //idSiswa="0113-08-2249";
        //textViewNama.setText(idSiswa);
        sharedPreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        tnama = sharedPreferences.getString("nama", null);
        tnoid = sharedPreferences.getString("noid", null);
        tno_rek= sharedPreferences.getString("no_rek", null);
        tped_code=sharedPreferences.getString("pedagang_code", null);
        pDialog = new ProgressDialog(ScanResultActivity.this);
        if(cekKoneksi()){
            GetData fillList = new GetData();
            fillList.execute("");
        }
        // nominal
        nominal.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i > 2) {

                }
                return false;
            }
        });
        // attaching onclickListener
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgFade.setVisibility(View.GONE);
                lyKonfirm.setVisibility(View.GONE);
                password.setText("");
            }
        });
        //buttonScan.setOnClickListener(this);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nominal.getNumericValue() > 100) {
                    bgFade.setVisibility(View.VISIBLE);
                    lyKonfirm.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ScanResultActivity.this, "Minimal transaksi Rp50", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnKonfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CEK Password jika sama
                //langsung insert transaksi
                //get transaksi kode
                if (cekKoneksi()) {
                    reCek cekk = new reCek();
                    cekk.execute("");
                } else {

                }

            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean cekKoneksi() {
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
                            noid=rs.getString("noid");
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
                    Log.d(TAG, "doInBackground: "+rs.getCursorName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return z;
        }
    }
    public class reCek extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog.setCancelable(false);
            pDialog.setMessage("Memverifikasi PIN");
            pDialog.show();
            bgFade.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            bgFade.setVisibility(View.GONE);
            //Toast.makeText(ScanResultActivity.this, z, Toast.LENGTH_SHORT).show();
            if(isPassword){
                if(saldoCukup){
                    new transaksi().execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Saldo tidak cukup",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"PIN Salah",Toast.LENGTH_LONG).show();
            }

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
                    String qpass = "select pwd from tbl_users where pwd='"+password.getText().toString()+"'";
                    PreparedStatement ppass = con.prepareStatement(qpass);
                    ResultSet rpass = ppass.executeQuery();
                    pDialog.dismiss();
                    while (rpass.next()) {
                        if(rpass.wasNull()){
                            pass="0";
                            isPassword=false;
                        }else{
                            isPassword=true;
                            pass=rpass.getString("pwd");
                            if(isPassword){
                                String qsaldo = "select saldo from tbl_tabungan where no_rek='"+idSiswa+"' order by no_urut desc";
                                PreparedStatement psaldo = con.prepareStatement(qsaldo);
                                ResultSet rsaldo = psaldo.executeQuery();
                                Integer a=0;
                                while (rsaldo.next()) {
                                    if(rsaldo.wasNull()){
                                        saldo="0";
                                    }else {
                                        if (a < 1) {
                                            double sal = Double.parseDouble(rsaldo.getString("saldo"));
                                            int sald = (int) sal;
                                            if (sald < nominal.getNumericValue()) {
                                                saldoCukup = false;
                                            } else {
                                                saldoCukup = true;
                                            }
                                        }
                                        a++;
                                    }
                                }
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
    public class transaksi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            bgFade.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            bgFade.setVisibility(View.GONE);
            //Toast.makeText(ScanResultActivity.this, z, Toast.LENGTH_SHORT).show();
            if(isSuccess){
                Intent intents = new Intent(getApplicationContext(), StatusActivity.class);
                intents.putExtra("status","sukses");
                startActivity(intents);
            }else{
                Intent intents = new Intent(getApplicationContext(), StatusActivity.class);
                intents.putExtra("status","gagal");
                startActivity(intents);
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
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

                    String tgl = new SimpleDateFormat("yymmdd", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    String tglkap = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS", Locale.ENGLISH)
                            .format(Calendar.getInstance().getTime());
                    String cek = "select count (*)  as total from tbl_penjualan where no_trans like 'PJL-"+tgl+"%'";
                    PreparedStatement pcek = con.prepareStatement(cek);
                    ResultSet rcek = pcek.executeQuery();
                    int b=1;
                    while (rcek.next()) {
                        if(rcek.wasNull()){
                            no_trans="PJL-"+tgl+0001;
                            Log.d(TAG, "doInBackground: total kosong"+rcek.toString());
                        }else{
                            int totale =rcek.getInt("total");
                            Log.d(TAG, "doInBackground: total"+totale);
                            int c =rcek.getInt("total")+1;
                            if (totale >= 9) {
                                if(totale<=999){
                                    no_trans="PJL-"+tgl+"0"+c;
                                    if(totale<=99){
                                        no_trans="PJL-"+tgl+"00"+c;
                                    }
                                }else{
                                    no_trans="PJL-"+tgl+c;
                                }
                            }else{
                                no_trans="PJL-"+tgl+"000"+c;
                            }
                        }
                    }
                    String keterangan = "TRX penjualan an. "+nama+" no Rekening "+idSiswa;
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
                    /*String qpass = "insert into tbl_penjualan ( date_trans, waktu_input, no_trans, pedagang_code, no_anggota," +
                            " norek_asal, norek_tujuan, type_trans, desc_account, no_account, sub_code, amount, keterangan, " +
                            "user_input) values(?,?,'"+no_trans+"','"+tped_code+"','"+noid+"'" +
                            ",'"+idSiswa+"','"+tno_rek+"','CASH','11110.0001','11110','11110.0001','"+nominal.getNumericValue()+"'" +
                            ",'"+keterangan+"','sa')";*/
                    String qpass =  "insert into tbl_penjualan ( date_trans, no_trans, pedagang_code, no_anggota," +
                            " norek_asal, norek_tujuan, type_trans, desc_account, no_account, sub_code, amount, keterangan, " +
                            "user_input,waktu_input) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    Log.d(TAG, "doInBackground: "+qpass);
                    PreparedStatement preparedStatement = con.prepareStatement(qpass);
                    preparedStatement.setDate(1, sqlDate);
                    preparedStatement.setString(2, no_trans);
                    preparedStatement.setString(3, tped_code);
                    preparedStatement.setString(4, noid);
                    preparedStatement.setString(5, idSiswa);
                    preparedStatement.setString(6, tno_rek);
                    preparedStatement.setString(7, "CASH");
                    preparedStatement.setString(8, "11110.0001");
                    preparedStatement.setString(9, "11110");
                    preparedStatement.setString(10, "11110.0001");
                    preparedStatement.setString(11, String.valueOf(nominal.getNumericValue()));
                    preparedStatement.setString(12, keterangan);
                    preparedStatement.setString(13, "sa");
                    preparedStatement.setDate(14, sqlDate);
                    preparedStatement.executeUpdate();
                    isSuccess = true;
                }
            } catch (SQLException e) {
                isSuccess = false;
                e.printStackTrace();
            }
            return z;
        }
    }
}