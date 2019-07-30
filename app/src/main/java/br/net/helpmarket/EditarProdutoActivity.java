package br.net.helpmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;


public class EditarProdutoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView codigoBarras;
    private EditText nome, quantidade, preco;
    private Compra compra;
    private ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarproduto);

        toolbar = findViewById(R.id.ep_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        compra = (Compra) getIntent().getExtras().getSerializable("compra");
        img = findViewById(R.id.ep_imagem);
        codigoBarras = findViewById(R.id.ep_codigoBarras);
        nome = findViewById(R.id.ep_nome);
        quantidade = findViewById(R.id.ep_quantidade);
        preco = findViewById(R.id.ep_preco);

        Picasso.get().load(compra.getProduto().getUrlImagem()).into(img);
        codigoBarras.setText(compra.getProduto().getCodigoBarras().toString());
        nome.setText(compra.getNomePersonalizado());
        quantidade.setText(compra.getQuantidade().toString());
        preco.setText(compra.getPreco().toString());

        FloatingActionButton salvarAlteracoesProduto = findViewById(R.id.salvarAlteracoesProduto);
        salvarAlteracoesProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    db.atualizarProduto(compra, nome.getText().toString(), Integer.parseInt(quantidade.getText().toString()), Double.parseDouble(preco.getText().toString()));
                    Toast.makeText(v.getContext(), "Alterações concluídas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        ImageButton voltar = findViewById(R.id.ep_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.layout_editarproduto);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        if (nome.getText().toString().isEmpty()) {
            nome.setError("O nome do produto não pode ficar em branco");
            return false;
        }
        if (quantidade.getText().toString().isEmpty()) {
            quantidade.setText("1");
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
