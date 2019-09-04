package br.net.helpmarket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.mail.MailController;
import br.net.helpmarket.modelo.Usuario;

public class InformacoesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Usuario usuario;
    private EditText nome, email, titulo, mensagem;
    private Spinner motivo;
    private TextView nomePessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes);

        toolbar = findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MobileAds.initialize(this, "ca-app-pub-6093298333256656~3639487257");
        final AdView adView = findViewById(R.id.info_adview);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        drawerLayout = findViewById(R.id.info_drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nome = findViewById(R.id.info_nome);
        email = findViewById(R.id.info_email);
        motivo = findViewById(R.id.info_motivo);
        titulo = findViewById(R.id.info_titulo);
        mensagem = findViewById(R.id.info_msg);

        NavigationView navigationView = findViewById(R.id.info_navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);
        //TODO: Remover para exibir o menu compartilhamento
        navigationView.getMenu().getItem(2).setVisible(false);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");

        nomePessoa = navigationView.getHeaderView(0).findViewById(R.id.nomePessoa);
        nomePessoa.setText(usuario.getNome());

        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());
        motivo.setAdapter(ArrayAdapter.createFromResource(this, R.array.motivoContato, android.R.layout.simple_spinner_dropdown_item));

        LinearLayout btnLogoff = findViewById(R.id.info_fazerLogoff);
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

        FloatingActionButton enviar = findViewById(R.id.enviarMensagem);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    MailController mc = new MailController(view.getContext());
                    mc.enviarMensagemSuporte(nome.getText().toString(), email.getText().toString(), motivo.getSelectedItem().toString(), titulo.getText().toString(), mensagem.getText().toString());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            titulo.setText("");
                                            mensagem.setText("");
                                            titulo.requestFocus();
                                            dialog.dismiss();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                                            intent.putExtra("usuario", usuario);
                                            startActivity(intent);
                                            finish();
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder ab = new AlertDialog.Builder(view.getContext());
                            ab.setMessage("Sua mensagem foi enviada com sucesso!\nDeseja enviar uma nova mensagem?")
                                    .setNegativeButton("Não", dialogClickListener)
                                    .setPositiveButton("Sim", dialogClickListener)
                                    .setCancelable(false)
                                    .show();
                        }
                    }, 3000);
                }
            }
        });

        CoordinatorLayout layout = findViewById(R.id.layout_info);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
            }
        });

        LinearLayout layout2 = findViewById(R.id.layout2_info);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        boolean b = true;
        if (nome.getText().toString().isEmpty()) {
            nome.setError("Insira o seu nome");
            b = false;
        }
        if (email.getText().toString().isEmpty()) {
            email.setError("Insira o seu e-mail");
            b = false;
        }
        if (null == motivo.getSelectedItem()) {
            Toast.makeText(getBaseContext(), "Selecione o motivo do contato", Toast.LENGTH_LONG).show();
            b = false;
        }
        if (titulo.getText().toString().isEmpty()) {
            titulo.setError("Insira o titulo da sua mensagem");
            b = false;
        }
        if (mensagem.getText().toString().isEmpty()) {
            mensagem.setError("Insira a sua mensagem");
            b = false;
        }
        return b;
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarNome();
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
                    usuario = usuarios.get(0);
                    nomePessoa.setText(usuario.getNome());
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_inicio: {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_listaCompras: {
                Intent intent = new Intent(getBaseContext(), ListaComprasActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_grupos: {
                Intent intent = new Intent(getBaseContext(), GruposActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_informacoes: {
                drawerLayout.closeDrawer(GravityCompat.START);
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

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
