package br.net.helpmarket;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.adapter.ListaComprasAdapter;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.ListaDB;
import br.net.helpmarket.modelo.Usuario;

public class ListaComprasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Usuario usuario;
    private TextView nomePessoa;
    private List<Lista> listas = new ArrayList<>();
    private List<Lista> listasSelecionadas = new ArrayList<>();
    private ListaComprasAdapter lcAdapter;
    private ListView lvListas;
    private LinearLayout lcVazio;
    private FloatingActionButton novaLista;
    private CoordinatorLayout layout;
    private ActionMode mActionMode;
    private ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listacompras);

        toolbar = findViewById(R.id.lc_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.lc_drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.lc_navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
        layout = findViewById(R.id.lc_layout);
        lvListas = findViewById(R.id.lvListas);
        lcVazio = findViewById(R.id.lc_vazio);
        load = findViewById(R.id.lc_load);
        registerForContextMenu(lvListas);

        lvListas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null == mActionMode) {
                    Lista lista = (Lista) lcAdapter.getItem(position);
                    Intent intent = new Intent(getBaseContext(), ListaProdutosActivity.class);
                    intent.putExtra("lista", lista);
                    startActivity(intent);
                } else {
                    if (listasSelecionadas.contains(lcAdapter.getItem(position))) {
                        listasSelecionadas.remove(lcAdapter.getItem(position));
                        lcAdapter.selecionar(view, getDrawable(R.color.colorWhite));
                    } else {
                        listasSelecionadas.add((Lista) lcAdapter.getItem(position));
                        lcAdapter.selecionar(view, getDrawable(R.color.colorPrimaryA));
                    }
                    if (listasSelecionadas.size() == 1) {
                        mActionMode.setTitle(listasSelecionadas.size() + " Lista Selecionada");
                    } else {
                        mActionMode.setTitle(listasSelecionadas.size() + " Listas Selecionadas");
                    }
                }
            }
        });

        novaLista = findViewById(R.id.novaLista);
        novaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NovaListaActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });

        nomePessoa = navigationView.getHeaderView(0).findViewById(R.id.nomePessoa);
        nomePessoa.setText(usuario.getNome());

        LinearLayout btnLogoff = findViewById(R.id.lc_fazerLogoff);
        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBController db = new DBController(v.getContext());
                db.apagarCredenciais();
                Toast.makeText(v.getContext(), "Até a próxima, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout btnMinhaConta = navigationView.getHeaderView(0).findViewById(R.id.minhaConta);
        btnMinhaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MinhaContaActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_longpress, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Lista listaSelecionada = (Lista) lcAdapter.getItem(info.position);
        menu.setHeaderTitle(listaSelecionada.getNome());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Lista listaSelecionada = (Lista) lcAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.lpr_editar:
                Intent intent = new Intent(getBaseContext(), EditarListaActivity.class);
                intent.putExtra("lista", listaSelecionada);
                startActivity(intent);
                return true;
            case R.id.lpr_excluir:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DBController db = new DBController(getBaseContext());
                                db.deletarLista(listaSelecionada);
                                listarListas();
                                atualizarFundo();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getApplicationContext(), "Operação cancelada.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage("Deseja realmente excluir a lista " + listaSelecionada.getNome() + "?")
                        .setNegativeButton("Não", dialogClickListener)
                        .setPositiveButton("Sim", dialogClickListener)
                        .show();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarNome();
        listarListas();
    }

    private void atualizarNome() {
        final List<Usuario> usuarios = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .whereEqualTo("email", usuario.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Usuario u = doc.toObject(Usuario.class);
                            u.setId(doc.getId());
                            usuarios.add(u);
                        }
                        if (usuarios.size() != 0) {
                            nomePessoa.setText(usuarios.get(0).getNome());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listas_compras, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nova_lista:
                Intent intent = new Intent(getBaseContext(), NovaListaActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                break;
            case R.id.selecionar_listas:
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = startSupportActionMode(mActionModeCallback);
                toolbar.setVisibility(View.GONE);
                novaLista.setVisibility(View.GONE);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selecao, menu);
            mode.setTitle(listasSelecionadas.size() + " Listas Selecionadas");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.excluirSelecionados:
                    for (Lista l : listasSelecionadas) {
                        DBController db = new DBController(getBaseContext());
                        db.deletarLista(l);
                    }
                    listarListas();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @SuppressLint("RestrictedApi")
                @Override
                public void run() {
                    toolbar.setVisibility(View.VISIBLE);
                    novaLista.setVisibility(View.VISIBLE);
                }
            }, 300);
            listasSelecionadas.clear();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_inicio: {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_listaCompras: {
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_grupos: {
                Intent intent = new Intent(getBaseContext(), GruposActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_informacoes: {
                Intent intent = new Intent(getBaseContext(), InformacoesActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_configuracoes: {
                Intent intent = new Intent(getBaseContext(), ConfiguracoesActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void listarListas() {
        listas.clear();
        load.setVisibility(View.VISIBLE);
        final List<ListaDB> listasDB = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas")
                .whereEqualTo("idUsuario", usuario.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    ListaDB l = doc.toObject(ListaDB.class);
                    l.setId(doc.getId());
                    listasDB.add(l);
                }
                for (ListaDB l : listasDB) {
                    listas.add(new Lista(l.getId(), usuario, l.getNome(), l.getGastoMaximo(), l.getQuantidadeProdutos(), l.getDataCriacao(), l.getTerminado()));
                }
                lcAdapter = new ListaComprasAdapter(listas, ListaComprasActivity.this);
                lvListas.setAdapter(lcAdapter);
                load.setVisibility(View.GONE);
                atualizarFundo();
            }
        });
    }

    private void atualizarFundo() {
        if (listas.size() == 0) {
            lvListas.setVisibility(View.GONE);
            lcVazio.setVisibility(View.VISIBLE);
        } else {
            lvListas.setVisibility(View.VISIBLE);
            lcVazio.setVisibility(View.GONE);
        }
    }
}
