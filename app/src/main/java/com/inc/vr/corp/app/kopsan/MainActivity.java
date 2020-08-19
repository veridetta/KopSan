package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ZXingScannerView.ResultHandler {
    // View Object

    //qr code scanner object
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        //Log.v(TAG, rawResult.getText()); // Prints scan results
        //Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        if (result.getText() != null) {
            // barcodeView.setStatusText(result.getText());
            if (result.getText() == null){
                Toast.makeText(MainActivity.this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
            }else{
                // jika qrcode berisi data
                Intent tipeinten = getIntent();
                String tipe = tipeinten.getStringExtra("tipe");
                if(tipe.equals("saldo")){
                    Intent intent = new Intent(MainActivity.this, SaldoActivity.class);
                    intent.putExtra("idSiswa",result.getText());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                    intent.putExtra("idSiswa",result.getText());
                    startActivity(intent);
                }


            }
        }
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview((ZXingScannerView.ResultHandler) this);
    };

    @Override
    public void onClick(View view) {

    }
}
