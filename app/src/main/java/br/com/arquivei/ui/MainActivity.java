package br.com.arquivei.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.arquivei.R;
import br.com.arquivei.adapter.ListaNotasAdapter;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_QR = 1; // Request ID para a activity do scanner
    private ArrayList<String> mArrayNotas;
    private RecyclerView mListaNotas;
    private ListaNotasAdapter mAdapter;
    private Button mBotaoDoar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Doe Fácil");

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
        mArrayNotas = new ArrayList<String>();
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
                mArrayNotas.clear();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Parabéns! Notas Fiscais doadas com sucesso.", Toast.LENGTH_SHORT).show();
                updateBotaoDoar();
            }
        });
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
        if (id == R.id.action_settings) {
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
                ArrayList<String> recievedData = data.getStringArrayListExtra(ScannerActivity.NOTAS_FISCAIS);
                for (String temp : recievedData){
                    mArrayNotas.add(temp);
                }
                mAdapter.notifyDataSetChanged();
               updateBotaoDoar();

            }else {
                Toast.makeText(this, "Nada recebido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Atualiza o Botão Doar
    private void updateBotaoDoar(){
        if (mArrayNotas.size() > 0)
            mBotaoDoar.setVisibility(View.VISIBLE);
        else
            mBotaoDoar.setVisibility(View.GONE);
    }
}
