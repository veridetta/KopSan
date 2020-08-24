package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class StatusActivity extends AppCompatActivity {
    Intent intent;
    String status;
    LinearLayout sukses, gagal;
    Button menu, toko;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        intent = getIntent();
        status = intent.getStringExtra("status");
        sukses = findViewById(R.id.sukses);
        gagal = findViewById(R.id.gagal);
        menu = findViewById(R.id.btnMenu);
        toko=findViewById(R.id.btnToko);

        if(status.equals("sukses")){
            sukses.setVisibility(View.VISIBLE);
            gagal.setVisibility(View.GONE);
        }else{
            sukses.setVisibility(View.GONE);
            gagal.setVisibility(View.VISIBLE);
        }
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            }
        });
        toko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), TokoActivity.class));
            }
        });
    }
}