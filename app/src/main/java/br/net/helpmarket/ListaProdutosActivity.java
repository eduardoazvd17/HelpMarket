package br.net.helpmarket;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.adapter.ListaProdutosAdapter;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.CompraDB;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;

public class ListaProdutosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomeLista, gastoMaximo, totalGasto, totalEconomizado;
    private Lista lista;
    private List<Compra> compras = new ArrayList<>();
    private ListView lvProdutos;
    private Produto produto;
    private ListaProdutosAdapter lpAdapter;
    private int numeroToken=1;
    private Tokens token = Tokens.TK1;
    private CoordinatorLayout layout;
    private LinearLayout lpVazio;
    private ActionMode mActionMode;
    private FloatingActionButton adicionarProduto;
    private List<Compra> comprasSelecionadas = new ArrayList<>();
    private ProgressBar load;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listaprodutos);

        toolbar = findViewById(R.id.lp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.lista = (Lista) getIntent().getExtras().getSerializable("lista");
        nomeLista = findViewById(R.id.lp_nomeLista);
        nomeLista.setText(lista.getNome());
        lvProdutos = findViewById(R.id.lvProdutos);
        lpVazio = findViewById(R.id.lp_vazio);
        load = findViewById(R.id.lp_load);
        gastoMaximo = findViewById(R.id.lp_gastoMaximo);
        totalGasto = findViewById(R.id.lp_totalGasto);
        totalEconomizado = findViewById(R.id.lp_totalEconomizado);
        layout = findViewById(R.id.lp_layout);
        registerForContextMenu(lvProdutos);

        adicionarProduto = findViewById(R.id.adicionarProduto);
        adicionarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduto();
            }
        });

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null == mActionMode) {
                    //TODO: Ao clicar no item.
                } else {
                    if (comprasSelecionadas.contains(lpAdapter.getItem(position))) {
                        comprasSelecionadas.remove(lpAdapter.getItem(position));
                        lpAdapter.selecionar(view, getDrawable(R.color.colorWhite));
                    } else {
                        comprasSelecionadas.add((Compra) lpAdapter.getItem(position));
                        lpAdapter.selecionar(view, getDrawable(R.color.colorPrimaryA));
                    }
                    if (comprasSelecionadas.size() == 1) {
                        mActionMode.setTitle(comprasSelecionadas.size() + " Produto Selecionado");
                    } else {
                        mActionMode.setTitle(comprasSelecionadas.size() + " Produtos Selecionados");
                    }
                }
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

    private void addProduto() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_listaprodutos, null, false);
        final CardView btnCatalogo = view.findViewById(R.id.catalogoProdutos);
        final CardView btnLeitor = view.findViewById(R.id.leitorCodigoBarras);

        btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CatalogoActivity.class);
                intent.putExtra("lista", lista);
                startActivity(intent);
            }
        });

        btnLeitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(ListaProdutosActivity.this);
                scanIntegrator.setPrompt("Alinhe o código de barras com a linha para ser lido")
                        .setBeepEnabled(true)
                        .setOrientationLocked(true)
                        .setBarcodeImageEnabled(true)
                        .initiateScan();
            }
        });

        builder.setView(view)
                .setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                }
        });
        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_longpress, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Compra compraSelecionada = (Compra) lpAdapter.getItem(info.position);
        menu.setHeaderTitle(compraSelecionada.getProduto().getNome());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Compra compraSelecionada = (Compra) lpAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.lpr_editar:
                Intent intent = new Intent(getBaseContext(), EditarProdutoActivity.class);
                intent.putExtra("compra", compraSelecionada);
                startActivity(intent);
                return true;
            case R.id.lpr_excluir:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DBController db = new DBController(getBaseContext());
                                db.deletarCompra(compraSelecionada);
                                listarProdutos();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getApplicationContext(), "Operação cancelada.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage("Deseja realmente excluir o produto " + compraSelecionada.getNomePersonalizado() + "?")
                        .setNegativeButton("Não", dialogClickListener)
                        .setPositiveButton("Sim", dialogClickListener)
                        .show();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (null == scanningResult) {
            Toast.makeText(this, "Não foi possivel efetuar a leitura deste codigo de barras", Toast.LENGTH_LONG).show();
        } else {
            buscarProduto(scanningResult.getContents());
        }
    }

    private void gerarToken(int numeroToken) {
        if (numeroToken == 1) {
            this.token = Tokens.TK2;
        } else if (numeroToken == 2) {
            this.token = Tokens.TK3;
        } else if (numeroToken == 3) {
            this.token = Tokens.TK4;
        }
    }

    private void buscarProduto(final String codigo) {
        final ProgressDialog progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setTitle("Buscando Produto");
        progressDialog.setMessage("Carregando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DBController db = new DBController(getBaseContext());
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection("produtos").document(codigo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Produto produtoDB = task.getResult().toObject(Produto.class);

                if (null == produtoDB) {
                    try {
                        String tk = token.getKey();
                        String endpoint = "https://api.cosmos.bluesoft.com.br/";
                        URL url = new URL(endpoint + "gtins/" + codigo);
                        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                        conexao.setRequestMethod("GET");
                        conexao.setRequestProperty("X-Cosmos-Token", tk);
                        conexao.connect();
                        BufferedReader br = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                        String jsonString = br.readLine();
                        JSONObject jsonObject = new JSONObject(jsonString);
                        produto = new Produto(Long.parseLong(jsonObject.getString("gtin")), jsonObject.getString("description"), jsonObject.getString("thumbnail"));
                        produto.verificarPreenchimento();
                        db.inserirProduto(produto);
                        conexao.disconnect();
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Produto não encontrado...", Toast.LENGTH_LONG).show();
                        gerarToken(numeroToken);
                        numeroToken++;
                    }
                } else {
                    produto = produtoDB;
                }

                progressDialog.dismiss();

                Intent i = new Intent(getBaseContext(), AdicionarProdutoActivity.class);
                i.putExtra("lista", lista);
                i.putExtra("produto", produto);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarProdutos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_produtos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_selecao, menu);
            mode.setTitle(comprasSelecionadas.size() + " Produtos Selecionados");
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
                    for (Compra c : comprasSelecionadas) {
                        DBController db = new DBController(getBaseContext());
                        db.deletarCompra(c);
                        compras.remove(c);
                    }
                    listarProdutos();
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
                    adicionarProduto.setVisibility(View.VISIBLE);
                }
            }, 300);
            comprasSelecionadas.clear();
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_produto:
                addProduto();
                break;
            case R.id.selecionar_produtos:
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = startSupportActionMode(mActionModeCallback);
                toolbar.setVisibility(View.GONE);
                adicionarProduto.setVisibility(View.GONE);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void calcularGastos() {
        gastoMaximo.setText(NumberFormat.getCurrencyInstance().format(lista.getGastoMaximo()));
        double total = 0;
        for (Compra c : compras) {
            total = total + ((c.getQuantidade()+0.0) * c.getPreco());
        }
        totalGasto.setText(NumberFormat.getCurrencyInstance().format(total));

        if (lista.getGastoMaximo() == 0) {
            totalEconomizado.setText(NumberFormat.getCurrencyInstance().format(0.0));
        } else {
            double economizado = lista.getGastoMaximo() - total;
            totalEconomizado.setText(NumberFormat.getCurrencyInstance().format(economizado)+" ");

            if (total > lista.getGastoMaximo()) {
                totalEconomizado.setTextColor(Color.RED);
                totalEconomizado.setError("As compras ultrapassaram o limite definido");
            } else {
                totalEconomizado.setTextColor(Color.BLACK);
                totalEconomizado.setError(null);
            }
        }
    }

    public void listarProdutos() {
        compras.clear();
        load.setVisibility(View.VISIBLE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras")
                .whereEqualTo("idLista", lista.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<CompraDB> comprasDB = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    CompraDB cdb = doc.toObject(CompraDB.class);
                    cdb.setId(doc.getId());
                    comprasDB.add(cdb);
                }
                for (CompraDB c : comprasDB) {
                    compras.add(new Compra(c.getId(), lista, c.getProduto(), c.getNomePersonalizado(), c.getQuantidade(), c.getPreco(), c.getComprado()));
                }
                lpAdapter = new ListaProdutosAdapter(compras, ListaProdutosActivity.this);
                lvProdutos.setAdapter(lpAdapter);
                load.setVisibility(View.GONE);
                atualizarFundo();
                calcularGastos();
            }
        });
    }

    private void atualizarFundo() {
        if (compras.size() == 0) {
            lvProdutos.setVisibility(View.GONE);
            lpVazio.setVisibility(View.VISIBLE);
        } else {
            lvProdutos.setVisibility(View.VISIBLE);
            lpVazio.setVisibility(View.GONE);
        }
    }
}
