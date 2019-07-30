package br.net.helpmarket.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.net.helpmarket.R;
import br.net.helpmarket.modelo.Produto;

public class ListaCatalogoAdapter extends BaseAdapter {

    private List<Produto> produtos;
    private Activity activity;

    public ListaCatalogoAdapter(List<Produto> produtos, Activity activity) {
        this.produtos = produtos;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = activity.getLayoutInflater().inflate(R.layout.catalogo_item, parent, false);
        Produto produto = produtos.get(position);

        //Instanciar objetos do xml;
        ImageView imagem = view.findViewById(R.id.catalogo_imagem);
        TextView nome = view.findViewById(R.id.catalogo_nome);

        //Atribuir atributos nesses objetos;
        Picasso.get().load(produto.getUrlImagem()).into(imagem);
        nome.setText(produto.getNome());
        return view;
    }

}
