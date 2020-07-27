package com.inc.vr.corp.app.kopsan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private Button btnScan, btnBayar, btnKonfirm;
    private TextView textViewNama, txtClose;
    EditText nominal, password;
    //qr code scanner object
    private IntentIntegrator intentIntegrator;
    private CompoundBarcodeView barcodeView;
    LinearLayout hasil, bgFade, lyKonfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        // attaching onclickListener
        //buttonScan.setOnClickListener(this);
        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgFade.setVisibility(View.VISIBLE);
                lyKonfirm.setVisibility(View.VISIBLE);
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
                    Intent intent = new Intent(MainActivity.this, TransaksiResult.class);
                    intent.putExtra("idTrans", idTrans);
                    startActivity(intent);
                }else{

                }
            }
        });
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
