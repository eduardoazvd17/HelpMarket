package br.net.helpmarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.CompraDB;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.ListaDB;
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
            public void onClick(final View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Adicionando Produto");
                    progressDialog.setMessage("Carregando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("compras")
                            .whereEqualTo("idLista", lista.getId())
                            .whereEqualTo("produto", produto)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<CompraDB> comprasDB = new ArrayList<>();
                            Compra compra = new Compra(
                                    lista,
                                    produto,
                                    nome.getText().toString(),
                                    Integer.parseInt(quantidade.getText().toString()),
                                    Double.parseDouble(preco.getText().toString()),
                                    false
                            );

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                CompraDB cdb = doc.toObject(CompraDB.class);
                                cdb.setId(doc.getId());
                                comprasDB.add(cdb);
                            }

                            if (comprasDB.size() != 0) {
                                CompraDB c = comprasDB.get(0);
                                c.setQuantidade(c.getQuantidade() + compra.getQuantidade());
                                db.collection("compras").document(c.getId()).set(c);
                                Toast.makeText(v.getContext(), "O produto inserido já existe na sua lista, a quantidade foi aumentada.", Toast.LENGTH_LONG).show();
                            } else {
                                CompraDB compraDB = new CompraDB(compra.getLista().getId(), compra.getProduto(), compra.getNomePersonalizado(), compra.getQuantidade(), compra.getPreco(), compra.getComprado());
                                db.collection("compras").add(compraDB);
                                Lista lista = compra.getLista();
                                ListaDB ldb = new ListaDB(lista.getId(), lista.getUsuario().getId(), lista.getNome(), lista.getGastoMaximo(), lista.getQuantidadeProdutos()+1, lista.getDataCriacao(), lista.getTerminado());
                                db.collection("listas").document(lista.getId()).set(ldb);
                                Toast.makeText(v.getContext(), "Produto inserido.", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                            finish();
                        }
                    });
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
