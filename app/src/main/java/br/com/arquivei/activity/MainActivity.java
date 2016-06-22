package br.com.arquivei.activity;

import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import br.com.arquivei.R;
import br.com.arquivei.adapter.ListaNotasAdapter;
import br.com.arquivei.model.DAO;
import br.com.arquivei.model.NotaFiscal;
import br.com.arquivei.asynctask.ServerConnectionTask;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements ServerConnectionTask.serverConnectionListener, ListaNotasAdapter.onLongClickItem {
    static final int REQUEST_QR = 1; // Request ID para a activity do scanner
    private RecyclerView mListaNotas; // ListView
    private ListaNotasAdapter mAdapter; // List Adapter
    private ArrayList<NotaFiscal> mArrayNotas; // Notas Fiscais que serão visualizadas na Lista
    private Button mBotaoDoar; // Botão doar visível ou não
    private ImageView mTutorial;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        mAdapter.setOnLongClickListener(this);
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

        /*
        ImageView Tutorial
         */
        mTutorial = (ImageView) findViewById(R.id.tutorial);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotasFromDatabase();
        updateViewsVisibility();
    }

    /* Atualiza as notas que serão mostradas na lista */
    private void updateNotasFromDatabase() {
        DAO bd = DAO.getInstance();
        ArrayList<NotaFiscal> aux = bd.getNotasPendentes(this);
        mArrayNotas.clear();
        for (NotaFiscal temp : aux) {
            mArrayNotas.add(temp);
        }
        mAdapter.notifyDataSetChanged();
        updateViewsVisibility();
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

        if (id == R.id.action_records) {
            Intent i = new Intent(getApplicationContext(), HistoricoActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_about) {
            Intent i = new Intent(getApplicationContext(), SobreActivity.class);
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

    // Atualiza o Botão Doar e Imagem tutorial
    private void updateViewsVisibility() {
        if (mArrayNotas.size() > 0) {
            mBotaoDoar.setVisibility(View.VISIBLE);
            mTutorial.setVisibility(View.GONE);
        }
        else {
            mBotaoDoar.setVisibility(View.GONE);
            mTutorial.setVisibility(View.VISIBLE);
        }
    }


    // Executa ao apertar o botão doar
    private void executeDoar() {
        ServerConnectionTask enviarNotasTask = new ServerConnectionTask(MainActivity.this, mArrayNotas, this);
        enviarNotasTask.execute();
    }


    // Executa após a task de enviar as notas finaliza
    @Override
    public void onConnectionFinish(boolean result) {
        if (result)
            updateNotasFromDatabase();
    }

    @Override
    public void onLongClickNota(int position) {
       new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Deseja deletar esta nota?")
                .setContentText("Não será possível recupera-la")
                .setConfirmText("Sim!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
