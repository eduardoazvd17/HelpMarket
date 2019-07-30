package br.net.helpmarket.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import br.net.helpmarket.R;
import br.net.helpmarket.modelo.Compra;

public class ListaProdutosAdapter extends BaseAdapter {

    private List<Compra> compras;
    private Activity activity;

    public ListaProdutosAdapter(List<Compra> compras, Activity activity) {
        this.compras = compras;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return compras.size();
    }

    @Override
    public Object getItem(int position) {
        return compras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = activity.getLayoutInflater().inflate(R.layout.lp_item, parent, false);
        Compra compra = compras.get(position);

        //Instanciar objetos do xml;
        ImageView imagem = view.findViewById(R.id.lpitem_imagem);
        TextView nome = view.findViewById(R.id.lpitem_nome);
        TextView quantidade = view.findViewById(R.id.lpitem_quantidade);
        TextView precoUnitario = view.findViewById(R.id.lpitem_precoUnitario);
        TextView total = view.findViewById(R.id.lpitem_total);

        //Atribuir atributos nesses objetos;
        Picasso.get().load(compra.getProduto().getUrlImagem()).into(imagem);
        nome.setText(compra.getNomePersonalizado());
        quantidade.setText(compra.getQuantidade().toString());
        precoUnitario.setText(NumberFormat.getCurrencyInstance().format(compra.getPreco()));
        total.setText(NumberFormat.getCurrencyInstance().format((compra.getQuantidade()+0.0) * (compra.getPreco())));
        return view;
    }

    public void selecionar(View view, Drawable fundo) {
        view.findViewById(R.id.lcitem_bg).setBackground(fundo);
    }

}
