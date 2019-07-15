package br.net.helpmarket.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.net.helpmarket.R;
import br.net.helpmarket.modelo.Lista;

public class ListaComprasAdapter extends BaseAdapter {

    private List<Lista> listas;
    private Activity activity;

    public ListaComprasAdapter(List<Lista> listas, Activity activity) {
        this.listas = listas;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listas.size();
    }

    @Override
    public Object getItem(int position) {
        return listas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = activity.getLayoutInflater().inflate(R.layout.lc_item, parent, false);
        Lista lista = listas.get(position);

        //Instanciar objetos do xml;
        TextView nome = view.findViewById(R.id.lcitem_nome);
        TextView gastoMaximo = view.findViewById(R.id.lcitem_gastoMaximo);
        TextView totalProdutos = view.findViewById(R.id.lcitem_quantidadeProdutos);
        TextView data = view.findViewById(R.id.lcitem_dataCriacao);

        //Atribuir atributos nesses objetos;
        nome.setText(lista.getNome());
        gastoMaximo.setText(lista.getGastoMaximo().toString());
        totalProdutos.setText(lista.getQuantidadeProdutos().toString());
        data.setText(lista.getDataCriacao().toString());
        return view;
    }
}