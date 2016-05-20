package br.com.arquivei.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import br.com.arquivei.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    public static final String NOTAS_FISCAIS = "notasf";
    private final String TAG = "ScannerView";
    private ZXingScannerView mScannerView;
    private TextView mCounterText;
    private Button mConcluir;
    private static int count = 0;
    private ArrayList<String> mNotasLidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mNotasLidas = new ArrayList<String>();

        /*
        Scanner View
         */
        mScannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
        mScannerView.setAutoFocus(true);

        /*
        TextView que conta as notas
         */
        mCounterText = (TextView) findViewById(R.id.tv_count);
        mCounterText.setText(String.valueOf(count));

        /*
        Botão Concluir
         */
        mConcluir = (Button) findViewById(R.id.b_concluir);
        mConcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishScanner();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        finishScanner();
    }

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
        Log.v(TAG, result.getText()); // Prints scan results
        Log.v(TAG, result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // Vibrate Phone for 200 milliseconds
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);

        // Increment counter
        count++;
        mCounterText.setText(String.valueOf(count));

        // Add new nota
        mNotasLidas.add(result.getText());
        setResult(RESULT_OK);


        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }


    /* Método que encerra a atividade e determina se lêu notas fiscais
     */
    private void finishScanner(){
       if (count > 0){
           // Envia as notas lidas para main activity
           Intent returnIntent = new Intent();
           returnIntent.putStringArrayListExtra(NOTAS_FISCAIS, mNotasLidas);
           setResult(RESULT_OK, returnIntent);
       }

       else
          setResult(RESULT_CANCELED);

        count = 0;
        finish();
    }

}
