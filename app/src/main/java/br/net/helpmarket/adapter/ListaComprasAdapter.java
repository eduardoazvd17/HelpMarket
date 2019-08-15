package br.net.helpmarket.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.ListaProdutosActivity;
import br.net.helpmarket.R;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.CompraDB;
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
        final Lista lista = listas.get(position);

        //Instanciar objetos do xml;
        TextView nome = view.findViewById(R.id.lcitem_nome);
        TextView gastoMaximo = view.findViewById(R.id.lcitem_gastoMaximo);
        final TextView totalProdutos = view.findViewById(R.id.lcitem_quantidadeProdutos);
        TextView data = view.findViewById(R.id.lcitem_dataCriacao);

        //Atribuir atributos nesses objetos;
        nome.setText(lista.getNome());
        gastoMaximo.setText(NumberFormat.getCurrencyInstance().format(lista.getGastoMaximo()));

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras")
                .whereEqualTo("idLista", lista.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<CompraDB> comprasDB = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    CompraDB cdb = doc.toObject(CompraDB.class);
                    cdb.setId(doc.getId());
                    comprasDB.add(cdb);
                }
                double total=0.0;
                for (CompraDB c : comprasDB) {
                    total = total + c.getPreco();
                }
                totalProdutos.setText(NumberFormat.getCurrencyInstance().format(total));
            }
        });

        data.setText(lista.getDataCriacao());

        return view;
    }

    public void selecionar(View view, Drawable fundo) {
        view.findViewById(R.id.lcitem_bg).setBackground(fundo);
    }
}