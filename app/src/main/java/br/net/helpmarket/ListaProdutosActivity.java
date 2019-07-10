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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListaProdutosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomeLista;
    private ListView lvProdutos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaprodutos);

        toolbar = findViewById(R.id.lp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nomeLista = findViewById(R.id.lp_nomeLista);
        lvProdutos = findViewById(R.id.lvProdutos);

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
}
