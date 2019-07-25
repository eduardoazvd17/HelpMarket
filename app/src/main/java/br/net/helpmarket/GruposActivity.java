package br.net.helpmarket;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Usuario;

public class GruposActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Usuario usuario;
    private TextView nomePessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        toolbar = findViewById(R.id.g_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.g_drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.g_navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");

        nomePessoa = navigationView.getHeaderView(0).findViewById(R.id.nomePessoa);
        nomePessoa.setText("Minha Conta (" + usuario.getNome() + ")");

        LinearLayout btnLogoff = findViewById(R.id.g_fazerLogoff);
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
    protected void onResume() {
        super.onResume();
        DBController db = new DBController(getBaseContext());
        usuario = db.buscarUsuario(usuario.getEmail());
        nomePessoa.setText("Minha Conta (" + usuario.getNome() + ")");
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
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_informacoes: {
                Intent intent = new Intent(getBaseContext(), InformacoesActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_configuracoes: {
                Intent intent = new Intent(getBaseContext(), ConfiguracoesActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
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
}
