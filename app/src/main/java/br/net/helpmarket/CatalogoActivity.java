package br.net.helpmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.adapter.ListaCatalogoAdapter;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;

public class CatalogoActivity extends AppCompatActivity {

    private ListView lvCatalogo;
    private LinearLayout catalogoVazio;
    private ListaCatalogoAdapter lcAdapter;
    private Toolbar toolbar;
    private ProgressBar load;
    private Lista lista;
    private LinearLayout layout;
    private List<Produto> produtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        toolbar = findViewById(R.id.catalogo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        MobileAds.initialize(this, "ca-app-pub-6093298333256656~3639487257");

        this.lista = (Lista) getIntent().getExtras().getSerializable("lista");

        lvCatalogo = findViewById(R.id.lvCatalogo);
        catalogoVazio = findViewById(R.id.catalogo_vazio);
        load = findViewById(R.id.catalogo_load);
        layout = findViewById(R.id.catalogo_layout);

        lvCatalogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), AdicionarProdutoActivity.class);
                intent.putExtra("produto", (Produto) lcAdapter.getItem(position));
                intent.putExtra("lista", lista);
                startActivity(intent);
                finish();
            }
        });

        ImageButton btnVoltar = findViewById(R.id.catalogo_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                finish();
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ocultarTeclado();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem mSearch = menu.findItem(R.id.menu_pesquisar);

        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                final SearchView mSearchView = (SearchView) mSearch.getActionView();
                mSearchView.setIconified(false);
                mSearchView.setIconifiedByDefault(false);
                mSearchView.setQuery("", false);
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        ocultarTeclado();
                        mSearchView.clearFocus();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        lcAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                lcAdapter.getFilter().filter("");
                ocultarTeclado();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
