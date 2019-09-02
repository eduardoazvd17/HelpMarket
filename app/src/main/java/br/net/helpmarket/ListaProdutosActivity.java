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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.net.helpmarket.adapter.ListaProdutosAdapter;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.CompraDB;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;
import br.net.helpmarket.modelo.Token;

public class ListaProdutosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomeLista, gastoMaximo, totalGasto, totalEconomizado;
    private Lista lista;
    private List<Compra> compras = new ArrayList<>();
    private ListView lvProdutos;
    private Produto produto;
    private ListaProdutosAdapter lpAdapter;
    private Token token = null;
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

        MobileAds.initialize(this, "ca-app-pub-6093298333256656~3639487257");
        final AdView adView = findViewById(R.id.lp_adview);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
                    Compra compraSelecionada = (Compra) lpAdapter.getItem(position);
                    DBController db = new DBController(view.getContext());
                    if (compraSelecionada.getComprado()) {
                        db.comprarProduto(compraSelecionada, false);
                    } else {
                        db.comprarProduto(compraSelecionada, true);
                    }
                    listarProdutos();
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
                .setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Digitar Código de Barras", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         digitarCodigoBarras();
                     }
                });
        builder.show();
    }

    private void digitarCodigoBarras() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_codigomanual, null, false);
        final EditText codigoBarrasManual = view.findViewById(R.id.codigoBarrasManual);
        builder.setCancelable(false);
        builder.setView(view)
                .setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Buscar Produto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String codigo = codigoBarrasManual.getText().toString();
                        if (null == codigo || codigo.isEmpty()) {
                            codigoBarrasManual.setError("Insira o código de barras");
                        } else {
                            dialogInterface.dismiss();
                            buscarProduto(codigo);
                        }
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
            case R.id.lpr_marcar:
                DBController db = new DBController(this);
                if (compraSelecionada.getComprado()) {
                    db.comprarProduto(compraSelecionada, false);
                } else {
                    db.comprarProduto(compraSelecionada, true);
                }
                listarProdutos();
                return true;
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
                ab.setMessage("Deseja realmente excluir o produto: " + compraSelecionada.getNomePersonalizado() + "?")
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
        buscarProduto(scanningResult.getContents());
    }

    private void buscarProduto(final String codigo) {
        if (null == codigo || codigo.isEmpty()) {
            Toast.makeText(this, "Não foi possivel efetuar a leitura deste codigo de barras", Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Buscando Produto");
        progressDialog.setMessage("Carregando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final DBController db = new DBController(getBaseContext());
        final FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection("produtos").document(codigo).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Produto produtoDB = task.getResult().toObject(Produto.class);
                if (null == produtoDB) {
                    ff.collection("tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Token> tokens = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Token tk = doc.toObject(Token.class);
                                tk.setId(doc.getId());
                                tokens.add(tk);
                            }
                            for (Token tk : tokens) {
                                if (null != tk.getPrimeiroUso()) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                        Date atual = sdf.parse(sdf.format(new Date()));
                                        Date primeiroUso = sdf.parse(tk.getPrimeiroUso());
                                        if (atual.after(primeiroUso)) {
                                            tk.setUsos(0);
                                            ff.collection("tokens").document(tk.getId()).set(tk);
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getBaseContext(), "Erro ao gerar o token da API.", Toast.LENGTH_LONG).show();
                                    }
                                }
                                if (tk.getUsos() < 25) {
                                    token = tk;
                                }
                            }
                            if (null == token) {
                                Toast.makeText(getBaseContext(), "Ocorreu um erro ao buscar o produto, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    String endpoint = "https://api.cosmos.bluesoft.com.br/";
                                    URL url = new URL(endpoint + "gtins/" + codigo);
                                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                    conexao.setRequestMethod("GET");
                                    conexao.setRequestProperty("X-Cosmos-Token", token.getId());
                                    conexao.connect();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                                    String jsonString = br.readLine();
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    produto = new Produto(Long.parseLong(jsonObject.getString("gtin")), jsonObject.getString("description"), jsonObject.getString("thumbnail"));
                                    produto.verificarPreenchimento();

                                    token.setUsos(token.getUsos()+1);
                                    if (token.getUsos() == 1) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                        token.setPrimeiroUso(sdf.format(new Date()));
                                    }
                                    ff.collection("tokens").document(token.getId()).set(token);

                                    db.inserirProduto(produto);
                                    conexao.disconnect();
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "Produto não encontrado.", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    return;
                                }
                                Intent i = new Intent(getBaseContext(), AdicionarProdutoActivity.class);
                                i.putExtra("lista", lista);
                                i.putExtra("produto", produto);
                                startActivity(i);

                                progressDialog.dismiss();
                            }
                        }
                    });
                } else {
                    produto = produtoDB;
                    Intent i = new Intent(getBaseContext(), AdicionarProdutoActivity.class);
                    i.putExtra("lista", lista);
                    i.putExtra("produto", produto);
                    startActivity(i);

                    progressDialog.dismiss();
                }
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
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.excluirSelecionados:
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    for (Compra c : comprasSelecionadas) {
                                        DBController db = new DBController(getBaseContext());
                                        db.deletarCompra(c);
                                        compras.remove(c);
                                    }
                                    listarProdutos();
                                    mode.finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(getBaseContext(), "Operação cancelada.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder ab = new AlertDialog.Builder(ListaProdutosActivity.this);
                    ab.setMessage("Deseja realmente excluir todos os produtos selecionados? \n\nProdutos selecionados: " + comprasSelecionadas.size())
                            .setNegativeButton("Não", dialogClickListener)
                            .setPositiveButton("Sim", dialogClickListener)
                            .show();
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
