package br.net.helpmarket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CardView listaCompras, gruposFamiliares, verificarProduto, informacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaCompras = findViewById(R.id.listasCompras);
        gruposFamiliares = findViewById(R.id.gruposFamiliares);
        verificarProduto = findViewById(R.id.verificarProduto);
        informacoes = findViewById(R.id.informacoes);

        listaCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListaComprasActivity.class);
                startActivity(intent);
            }
        });
    }
}
