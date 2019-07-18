package br.net.helpmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email, senha;
    private TextView cadastrar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        senha = findViewById(R.id.login_senha);

        cadastrar = findViewById(R.id.cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CadastroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin = findViewById(R.id.fazerLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    Usuario usuario = db.fazerLogin(email.getText().toString(), senha.getText().toString());
                    if (null == usuario) {
                        Toast.makeText(v.getContext(), "Usuario ou senha incorretos.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(v.getContext(), "Bem vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        ConstraintLayout layout = findViewById(R.id.layout_login);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Insira seu email ou nome de usuario");
            return false;
        }
        if (senha.getText().toString().isEmpty()) {
            senha.setError("Insira sua senha");
            return false;
        }
        return true;
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
