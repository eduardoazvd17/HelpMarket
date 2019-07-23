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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email, senha;
    private TextView cadastrar;
    private Button btnLogin;
    private CheckBox entrarAutomaticamente;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        senha = findViewById(R.id.login_senha);
        entrarAutomaticamente = findViewById(R.id.entrarAutomaticamente);

        buscarCredenciais();

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
                    String senhaHash = criptografarSenha(senha.getText().toString());
                    usuario = db.fazerLogin(email.getText().toString(), senhaHash);
                    if (null == usuario) {
                        Toast.makeText(v.getContext(), "Usuario ou senha incorretos.", Toast.LENGTH_LONG).show();
                    } else {
                        if (entrarAutomaticamente.isChecked()) {
                            salvarCredenciais();
                        }
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

    private void buscarCredenciais() {
        DBController db = new DBController(getBaseContext());
        Usuario u = db.buscarCredenciais();
        if (!(null == u)) {
            Toast.makeText(getBaseContext(), "Bem vindo, " + u.getNome(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("usuario", u);
            startActivity(intent);
            finish();
        }
    }

    private void salvarCredenciais() {
        DBController db = new DBController(getBaseContext());
        db.salvarCredenciais(usuario);
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

    private String criptografarSenha(String senha) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(senha.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
