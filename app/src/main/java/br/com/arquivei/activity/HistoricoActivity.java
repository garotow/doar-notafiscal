package br.com.arquivei.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.arquivei.R;
import br.com.arquivei.adapter.ListaNotasAdapter;
import br.com.arquivei.model.DAO;
import br.com.arquivei.model.NotaFiscal;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView mListaNotas; // Lista
    private ListaNotasAdapter mAdapter; // Lista Adapter
    private ArrayList<NotaFiscal> mArrayNotas; // Notas Fiscais que serão visualizadas na Lista
    private TextView mTextTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        /* TextView */
        mTextTotal = (TextView) findViewById(R.id.tv_total);

        /*
        RecyclerView Lista de Notas Fiscais
        */
        mListaNotas = (RecyclerView) findViewById(R.id.list);
        mArrayNotas = new ArrayList<NotaFiscal>();
        mAdapter = new ListaNotasAdapter(this, mArrayNotas);
        mListaNotas.setAdapter(mAdapter);
        mListaNotas.setLayoutManager(new LinearLayoutManager(this));
        mListaNotas.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Pega as notas do banco de dados
        DAO bd = DAO.getInstance();
        ArrayList<NotaFiscal> aux = bd.getNotasEnviadas(this);
        mArrayNotas.clear();
        for (NotaFiscal temp : aux) {
            mArrayNotas.add(temp);
        }
        mAdapter.notifyDataSetChanged();

        mTextTotal.setText("Você doou um total de: R$" + getTotalDoado());

    }

    public double getTotalDoado(){
        double total = 0.0;

        for (NotaFiscal temp : mArrayNotas){
            total += Double.parseDouble(temp.getValor());
        }

        return total;
    }
}
