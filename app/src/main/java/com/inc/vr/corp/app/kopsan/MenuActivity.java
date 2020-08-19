package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    Button transaksi, saldo, toko;
    TextView namaToko;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        transaksi = findViewById(R.id.transaksi);
        saldo = findViewById(R.id.saldo);
        toko = findViewById(R.id.toko);
        namaToko = findViewById(R.id.nama_toko);

        sharedPreferences = getSharedPreferences("kopsan", Context.MODE_PRIVATE);
        namaToko.setText(sharedPreferences.getString("nama","Kopsan"));
        transaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("tipe","transaksi");
                startActivity(intent);
            }
        });
        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("tipe","saldo");
                startActivity(intent);
            }
        });
        toko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, TokoActivity.class);
                intent.putExtra("tipe","toko");
                startActivity(intent);
            }
        });
    }
}