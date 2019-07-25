package br.net.helpmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;


public class AdicionarProdutoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView codigoBarras;
    private EditText nome, quantidade, preco;
    private Lista lista;
    private Produto produto;
    private ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionarproduto);

        toolbar = findViewById(R.id.ap_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        lista = (Lista) getIntent().getExtras().getSerializable("lista");
        produto = (Produto) getIntent().getExtras().getSerializable("produto");
        img = findViewById(R.id.ap_imagem);
        codigoBarras = findViewById(R.id.ap_codigoBarras);
        nome = findViewById(R.id.ap_nome);
        quantidade = findViewById(R.id.ap_quantidade);
        preco = findViewById(R.id.ap_preco);

        Picasso.get().load(produto.getUrlImagem()).into(img);
        codigoBarras.setText(produto.getCodigoBarras().toString());
        nome.setText(produto.getNome());

        FloatingActionButton salvarProduto = findViewById(R.id.salvarProduto);
        salvarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    Compra compra = new Compra(
                            lista.getUsuario(),
                            lista,
                            produto,
                            nome.getText().toString(),
                            Integer.parseInt(quantidade.getText().toString()),
                            Double.parseDouble(preco.getText().toString()),
                            false
                    );
                    DBController db = new DBController(v.getContext());
                    db.inserirCompra(compra);
                    finish();
                }
            }
        });

        ImageButton voltar = findViewById(R.id.ap_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.layout_adicionarproduto);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        if (nome.getText().toString().isEmpty()) {
            nome.setError("Insira o nome do produto");
            return false;
        }
        if (quantidade.getText().toString().isEmpty()) {
            quantidade.setText("0");
        }
        if (preco.getText().toString().isEmpty()) {
            preco.setText("0.0");
        }
        return true;
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
