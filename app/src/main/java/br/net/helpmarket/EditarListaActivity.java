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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.net.helpmarket.R;
import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Lista;

public class EditarListaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nome, gastoMaximo;
    private Lista lista;
    private TextView data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarlista);

        toolbar = findViewById(R.id.el_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nome = findViewById(R.id.el_nome);
        gastoMaximo = findViewById(R.id.el_gastoMaximo);
        data = findViewById(R.id.el_data);

        lista = (Lista) getIntent().getExtras().getSerializable("lista");
        nome.setText(lista.getNome());
        gastoMaximo.setText(lista.getGastoMaximo().toString());
        data.setText(lista.getDataCriacao());

        FloatingActionButton salvarAlteracoesLista = findViewById(R.id.salvarAlteracoesLista);
        salvarAlteracoesLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    db.atualizarLista(lista, nome.getText().toString(), Double.parseDouble(gastoMaximo.getText().toString()));
                    Toast.makeText(v.getContext(), "Alterações concluídas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        ImageButton voltar = findViewById(R.id.el_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.layout_editarlista);
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

    private boolean verificarPreenchimento() {
        if (nome.getText().toString().isEmpty()) {
            nome.setError("O nome da lista não pode ficar em branco");
            return false;
        }
        if (gastoMaximo.getText().toString().isEmpty()) {
            gastoMaximo.setText("0.0");
        }
        return true;
    }
}
