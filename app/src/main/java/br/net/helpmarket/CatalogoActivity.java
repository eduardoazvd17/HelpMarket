package br.net.helpmarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;
import br.net.helpmarket.modelo.Usuario;

public class CatalogoActivity extends AppCompatActivity {

    private ListView lvCatalogo;
    private Toolbar toolbar;
    private Usuario usuario;
    private Lista lista;
    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        toolbar = findViewById(R.id.catalogo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
        this.lista = (Lista) getIntent().getExtras().getSerializable("lista");
        this.produto = (Produto) getIntent().getExtras().getSerializable("produto");

        lvCatalogo = findViewById(R.id.lvCatalogo);

        ImageButton btnVoltar = findViewById(R.id.catalogo_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
