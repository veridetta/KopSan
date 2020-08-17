package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BackupMain extends AppCompatActivity implements View.OnClickListener {
    // View Object
    /*
    //qr code scanner object
    private IntentIntegrator intentIntegrator;
    private CompoundBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.setRotationY(90);
        CameraSettings s = new CameraSettings();
        s.setRequestedCameraId(0); // front/back/etc
        //barcodeView.getBarcodeView().setCameraSettings(s);
        //barcodeView.resume();
        barcodeView.decodeContinuous(callback);
        //IntentIntegrator(scanQR);
        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        intentIntegrator.setPrompt("Scan a barcode");
        intentIntegrator.setCameraId(0);  // Use a specific camera of the device
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(true);
        intentIntegrator.initiateScan();
    }*/
    /*
    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }*/
    /*
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                // barcodeView.setStatusText(result.getText());
                if (result.getText() == null) {
                    Toast.makeText(MainActivity.this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
                } else {
                    // jika qrcode berisi data
                    Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                    intent.putExtra("idSiswa", result.getText());
                    startActivity(intent);
                    /*
                    try{
                        // converting the data json
                        JSONObject object = new JSONObject(result.getText());
                        // atur nilai ke textviews
                        //textViewNama.setText(object.getString("nama"));

                        barcodeView.setVisibility(View.GONE);
                        //hasil.setVisibility(View.VISIBLE);
                    }catch (JSONException e){
                        e.printStackTrace();
                        // jika format encoded tidak sesuai maka hasil
                        // ditampilkan ke toast
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
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
                Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
                intent.putExtra("idSiswa",result.getContents());
                startActivity(intent);
               /* try{
                    // converting the data json
                    JSONObject object = new JSONObject(result.getContents());
                    // atur nilai ke textviews
                    textViewNama.setText(object.getString("nama"));
                }catch (JSONException e){
                    e.printStackTrace();
                    // jika format encoded tidak sesuai maka hasil
                    // ditampilkan ke toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                }


                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    };
    @Override
    public void onClick(View v) {
        // inisialisasi IntentIntegrator(scanQR)
        //intentIntegrator = new IntentIntegrator(this);
        //intentIntegrator.initiateScan();
        //barcodeView.setVisibility(View.VISIBLE);
        //hasil.setVisibility(View.GONE);
    }

}
*/

