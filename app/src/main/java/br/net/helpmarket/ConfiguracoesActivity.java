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

import java.io.Serializable;

import br.net.helpmarket.modelo.Usuario;

public class ConfiguracoesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        toolbar = findViewById(R.id.config_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.config_drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.config_navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
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
                Intent intent = new Intent(getBaseContext(), InformacoesActivity.class);
                intent.putExtra("usuario", (Serializable) usuario);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_configuracoes: {
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
}
