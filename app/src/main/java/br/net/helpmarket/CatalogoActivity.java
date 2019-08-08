package br.net.helpmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

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
    private Lista lista;
    private List<Produto> produtos;
    private boolean executarOnResume = false;

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

        listarProdutos();
        atualizarFundo();

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
        if (executarOnResume) {
            listarProdutos();
            atualizarFundo();
        } else {
            executarOnResume = true;
        }
    }

    public void listarProdutos() {
        DBController db = new DBController(getBaseContext());
        this.produtos = db.selecionarProdutos();
        this.lcAdapter = new ListaCatalogoAdapter(this.produtos, this);
        this.lvCatalogo.setAdapter(this.lcAdapter);
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
