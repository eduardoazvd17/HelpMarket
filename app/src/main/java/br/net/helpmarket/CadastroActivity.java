package br.net.helpmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText email, nome, senha;
    private ImageButton btnVoltar;
    private Button btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        email = findViewById(R.id.cadastro_email);
        nome = findViewById(R.id.cadastro_nome);
        senha = findViewById(R.id.cadastro_senha);

        btnVoltar = findViewById(R.id.cadastro_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCadastro = findViewById(R.id.fazerCadastro);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                Usuario usuario = new Usuario(email.getText().toString(), nome.getText().toString(), senha.getText().toString());
                if (0 == usuario.verificarPreenchimento(v.getContext())) {
                    DBController db = new DBController(v.getContext());
                    boolean status = db.fazerCadastro(usuario);
                    if (status) {
                        Toast.makeText(v.getContext(), "Cadastro efetuado. Seja bem vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        intent.putExtra("usuario", (Serializable) usuario);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(v.getContext(), "O endereço de e-mail informado já existe.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        ConstraintLayout layout = findViewById(R.id.layout_cadastro);
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
