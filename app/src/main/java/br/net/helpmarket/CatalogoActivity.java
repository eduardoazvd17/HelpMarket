package br.net.helpmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.adapter.ListaCatalogoAdapter;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;

public class CatalogoActivity extends AppCompatActivity {

    private ListView lvCatalogo;
    private LinearLayout catalogoVazio;
    private ListaCatalogoAdapter lcAdapter;
    private Toolbar toolbar;
    private ProgressBar load;
    private Lista lista;
    private List<Produto> produtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        toolbar = findViewById(R.id.catalogo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.lista = (Lista) getIntent().getExtras().getSerializable("lista");

        lvCatalogo = findViewById(R.id.lvCatalogo);
        catalogoVazio = findViewById(R.id.catalogo_vazio);
        load = findViewById(R.id.catalogo_load);

        lvCatalogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), AdicionarProdutoActivity.class);
                intent.putExtra("produto", (Produto) lcAdapter.getItem(position));
                intent.putExtra("lista", lista);
                startActivity(intent);
            }
        });

        ImageButton btnVoltar = findViewById(R.id.catalogo_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarProdutos();
    }

    public void listarProdutos() {
        load.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("produtos")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Produto p = doc.toObject(Produto.class);
                            produtos.add(p);
                        }
                        lcAdapter = new ListaCatalogoAdapter(produtos, CatalogoActivity.this);
                        lvCatalogo.setAdapter(lcAdapter);
                        load.setVisibility(View.GONE);
                        atualizarFundo();
                    }
                });
    }

    private void atualizarFundo() {
        if (produtos.size() == 0) {
            lvCatalogo.setVisibility(View.GONE);
            catalogoVazio.setVisibility(View.VISIBLE);
        } else {
            lvCatalogo.setVisibility(View.VISIBLE);
            catalogoVazio.setVisibility(View.GONE);
        }
    }
}
