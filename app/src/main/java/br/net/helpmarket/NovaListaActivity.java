package br.net.helpmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NovaListaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nome, gastoMaximo;
    private TextView data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novalista);

        toolbar = findViewById(R.id.nl_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nome = findViewById(R.id.nl_nome);
        gastoMaximo = findViewById(R.id.nl_gastoMaximo);
        data = findViewById(R.id.nl_data);

        data.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        FloatingActionButton salvarNovaLista = findViewById(R.id.salvarNovaLista);
        salvarNovaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                //TODO: Salvar a lista.
            }
        });

        ImageButton voltar = findViewById(R.id.nl_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CoordinatorLayout layout = findViewById(R.id.layout_novalista);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
