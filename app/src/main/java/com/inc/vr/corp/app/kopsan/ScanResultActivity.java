package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;

import java.text.NumberFormat;
import java.util.Currency;

public class ScanResultActivity extends AppCompatActivity {

    private Button btnScan, btnBayar, btnKonfirm;
    private TextView textViewNama, txtClose;
    EditText  password;
    String idSiswa;
    CurrencyEditText nominal;
    LinearLayout hasil, bgFade, lyKonfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        // initialize object
        hasil = findViewById(R.id.result);
        btnScan = (Button) findViewById(R.id.buttonScan);
        btnBayar = findViewById(R.id.btnbayar);
        btnKonfirm = findViewById(R.id.btnkonfirm);
        textViewNama = (TextView) findViewById(R.id.txtNama);
        nominal = findViewById(R.id.nominal_input);
        password = findViewById(R.id.password_input);
        bgFade = findViewById(R.id.bg_fade);
        lyKonfirm = findViewById(R.id.lyKonfirm);
        txtClose = findViewById(R.id.tutup);
        //baca kiriman
        Intent kiriman = getIntent();
        idSiswa = kiriman.getStringExtra("idSiswa");
        textViewNama.setText(idSiswa);
        // nominal
        nominal.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i>2){

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
            }
        });
        //buttonScan.setOnClickListener(this);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nominal.getNumericValue()>100){
                    bgFade.setVisibility(View.VISIBLE);
                    lyKonfirm.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(ScanResultActivity.this,"Minimal transaksi Rp50",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnKonfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CEK Password jika sama
                //langsung insert transaksi
                //get transaksi kode
                boolean sukses=false;
                if(sukses){
                    // kirim transaksi kode ke aktiviti berikutnya
                    String idTrans = "";
                    Intent intent = new Intent(ScanResultActivity.this, TransaksiResult.class);
                    intent.putExtra("idTrans", idTrans);
                    startActivity(intent);
                }else{

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

}