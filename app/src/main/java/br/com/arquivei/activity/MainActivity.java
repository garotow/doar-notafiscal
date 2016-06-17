package br.com.arquivei.activity;

import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import br.com.arquivei.R;
import br.com.arquivei.adapter.ListaNotasAdapter;
import br.com.arquivei.model.DAO;
import br.com.arquivei.model.NotaFiscal;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_QR = 1; // Request ID para a activity do scanner
    private RecyclerView mListaNotas; // Lista
    private ListaNotasAdapter mAdapter; // Lista Adapter
    private ArrayList<NotaFiscal> mArrayNotas; // Notas Fiscais que serão visualizadas na Lista
    private Button mBotaoDoar; // Botão doar visível ou não

    private SweetAlertDialog pDialog; // Dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Easy Donate");
        toolbar.setNavigationIcon(R.drawable.donate_icon);

        /*
        Floating Action Button
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScannerActivity.class);
                startActivityForResult(i, REQUEST_QR);
            }
        });

        /*
        RecyclerView Lista de Notas Fiscais
        */
        mListaNotas = (RecyclerView) findViewById(R.id.list);
        mArrayNotas = new ArrayList<NotaFiscal>();
        mAdapter = new ListaNotasAdapter(this, mArrayNotas);
        mListaNotas.setAdapter(mAdapter);
        mListaNotas.setLayoutManager(new LinearLayoutManager(this));
        mListaNotas.setHasFixedSize(true);

        /*
        Botão Doar
         */
        mBotaoDoar = (Button) findViewById(R.id.bdoar);
        mBotaoDoar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDoar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotasFromDatabase();
        updateBotaoDoar();
    }

    private void updateNotasFromDatabase() {
        DAO bd = DAO.getInstance();
        ArrayList<NotaFiscal> aux = bd.getNotasPendentes(this);
        mArrayNotas.clear();
        for (NotaFiscal temp : aux) {
            mArrayNotas.add(temp);
        }
        mAdapter.notifyDataSetChanged();
        updateBotaoDoar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_records) {
            Intent i = new Intent(getApplicationContext(), HistoricoActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_QR) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            } else {
                Toast.makeText(this, "Nada recebido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Atualiza o Botão Doar
    private void updateBotaoDoar() {
        if (mArrayNotas.size() > 0)
            mBotaoDoar.setVisibility(View.VISIBLE);
        else
            mBotaoDoar.setVisibility(View.GONE);
    }

    private void executeDoar() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Enviando...");
        pDialog.show();
        pDialog.setCancelable(false);

        DAO dao = DAO.getInstance();
        dao.confirmarNotasEnviadas(getApplicationContext());
        updateNotasFromDatabase();

        new EnviarNotasTask().execute();
    }

    // AsyncTask que vai enviar as notas para o site
    private class EnviarNotasTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.setTitleText("Parabéns! Notas enviadas!")
                    .setConfirmText("OK")
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }
}
