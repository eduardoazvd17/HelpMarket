package br.net.helpmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.net.helpmarket.database.DBController;

public class RecuperarActivity extends AppCompatActivity {

    private EditText email, novaSenha, confirmarNovaSenha, codigo;
    private Button btnRecuperar;
    private LinearLayout voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        email = findViewById(R.id.recuperar_email);
        codigo = findViewById(R.id.recuperar_codigo);
        novaSenha = findViewById(R.id.recuperar_novaSenha);
        confirmarNovaSenha = findViewById(R.id.recuperar_confirmarNovaSenha);

        voltar = findViewById(R.id.recuperar_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRecuperar = findViewById(R.id.recuperar_alterarSenha);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    String recuperacao = db.recuperarSenha(email.getText().toString(), Integer.parseInt(codigo.getText().toString()));
                    if ("emailIncorreto".equals(recuperacao)) {
                        email.setError("Endereço de e-mail incorreto");
                    } else if ("codigoIncorreto".equals(recuperacao)) {
                        codigo.setError("Código de recuperação incorreto");
                    } else {
                        db.recuperarUsuario(email.getText().toString(), criptografarSenha(novaSenha.getText().toString()));
                        Toast.makeText(getBaseContext(), "Sua senha foi alterada, entre utilizando sua nova senha", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });

        CoordinatorLayout layout = findViewById(R.id.layout_recuperar);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Insira seu email");
            return false;
        }
        if (codigo.getText().toString().isEmpty()) {
            codigo.setError("Insira seu código recuperação");
            return false;
        }
        if (novaSenha.getText().toString().isEmpty()) {
            novaSenha.setError("Insira sua nova senha");
            return false;
        }
        if (confirmarNovaSenha.getText().toString().isEmpty()) {
            confirmarNovaSenha.setError("Confirme sua nova senha");
            return false;
        }
        if (!novaSenha.getText().toString().equals(confirmarNovaSenha.getText().toString())) {
            novaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
            confirmarNovaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
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
