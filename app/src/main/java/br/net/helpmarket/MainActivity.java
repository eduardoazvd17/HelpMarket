package br.net.helpmarket;

import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Produto;
import br.net.helpmarket.modelo.Usuario;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton btnLogoff;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbarInicio);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");

        btnLogoff = findViewById(R.id.fazerLogoff);
        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Até a próxima, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_inicio: {
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_listaCompras: {
                Intent intent = new Intent(getBaseContext(), ListaComprasActivity.class);
                intent.putExtra("usuario", usuario);
                startActivity(intent);
                finish();
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
}
