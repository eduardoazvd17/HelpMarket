package br.net.helpmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.net.helpmarket.adapter.ListaComprasAdapter;
import br.net.helpmarket.adapter.ListaProdutosAdapter;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Usuario;

public class ListaProdutosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomeLista;
    private Usuario usuario;
    private Lista lista;
    private List<Compra> compras;
    private ListView lvProdutos;
    private ListaProdutosAdapter lpAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaprodutos);

        toolbar = findViewById(R.id.lp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
        this.lista = (Lista) getIntent().getExtras().getSerializable("lista");
        nomeLista = findViewById(R.id.lp_nomeLista);
        lvProdutos = findViewById(R.id.lvProdutos);
        listarProdutos();

        FloatingActionButton adicionarProduto = findViewById(R.id.adicionarProduto);
        adicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AdicionarProdutoActivity.class);
                startActivity(intent);
            }
        });

        ImageButton voltar = findViewById(R.id.lp_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_produtos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_produto:
                Intent intent = new Intent(getBaseContext(), AdicionarProdutoActivity.class);
                startActivity(intent);
                break;
            case R.id.excluir_produtos:
                //TODO: Entrar em modo de seleção para deletar.
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void listarProdutos() {
        DBController db = new DBController(getBaseContext());
        this.compras = db.selecionarCompras(usuario, lista);
        this.lpAdapter = new ListaProdutosAdapter(this.compras, this);
        this.lvProdutos.setAdapter(this.lpAdapter);
    }
}
