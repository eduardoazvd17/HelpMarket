package br.net.helpmarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class ListasComprasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_compras);

        Toolbar toolbar = findViewById(R.id.toolbarListasCompras);
        setSupportActionBar(toolbar);

        ImageButton voltar = findViewById(R.id.lc_voltar);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listas_compras, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nova_lista:
                //TODO: Adiciona nova lista
                break;
            case R.id.excluir_listas:
                //TODO: Entra no modo seleção para excluir.
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
