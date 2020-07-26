package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // View Object
    private Button buttonScan;
    private TextView textViewNama, textViewTinggi;

    //qr code scanner object
    private IntentIntegrator intentIntegrator;
    private CompoundBarcodeView barcodeView;
    View hasil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize object
        hasil = findViewById(R.id.result);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewTinggi = (TextView) findViewById(R.id.textViewTinggi);
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        // attaching onclickListener
        //buttonScan.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
               // barcodeView.setStatusText(result.getText());
                if (result.getText() == null){
                    Toast.makeText(MainActivity.this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
                }else{
                    // jika qrcode berisi data
                    try{
                        // converting the data json
                        JSONObject object = new JSONObject(result.getText());
                        // atur nilai ke textviews
                        textViewNama.setText(object.getString("nama"));
                        textViewTinggi.setText(object.getString("tinggi"));
                        barcodeView.setVisibility(View.GONE);
                        hasil.setVisibility(View.VISIBLE);
                    }catch (JSONException e){
                        e.printStackTrace();
                        // jika format encoded tidak sesuai maka hasil
                        // ditampilkan ke toast
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //Do something with code result
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    // Mendapatkan hasil scan


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
            }else{
                // jika qrcode berisi data
                try{
                    // converting the data json
                    JSONObject object = new JSONObject(result.getContents());
                    // atur nilai ke textviews
                    textViewNama.setText(object.getString("nama"));
                    textViewTinggi.setText(object.getString("tinggi"));
                }catch (JSONException e){
                    e.printStackTrace();
                    // jika format encoded tidak sesuai maka hasil
                    // ditampilkan ke toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        // inisialisasi IntentIntegrator(scanQR)
        //intentIntegrator = new IntentIntegrator(this);
        //intentIntegrator.initiateScan();
        barcodeView.setVisibility(View.VISIBLE);
        hasil.setVisibility(View.GONE);
    }
}
