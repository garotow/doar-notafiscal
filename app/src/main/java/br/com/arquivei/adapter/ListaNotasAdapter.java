package br.com.arquivei.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.arquivei.R;
import br.com.arquivei.model.NotaFiscal;

import java.util.ArrayList;

/**
 * Created by Henrique
 */
public class ListaNotasAdapter extends RecyclerView.Adapter<ListaNotasAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NotaFiscal> mData; // Lista com os eventos mostrados na data

    //  Constructor
    public ListaNotasAdapter(Context c, ArrayList<NotaFiscal> data) {
        mContext = c;
        mData = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(mContext).inflate(R.layout.nota_row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.name.setText(mData.get(position).getCnpj());
        holder.dia.setText(mData.get(position).getDia());
        holder.mes.setText(mData.get(position).getMes());
        holder.valor.setText(mData.get(position).getValor());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView dia;
        public TextView mes;
        public TextView valor;
        public View v;

        public MyViewHolder(View v) {
            super(v);
            this.v = v;
            name = (TextView) v.findViewById(R.id.tv_name);
            dia = (TextView) v.findViewById(R.id.tv_dia);
            mes = (TextView) v.findViewById(R.id.tv_mes);
            valor = (TextView) v.findViewById(R.id.tv_valor);
        }
    }
}