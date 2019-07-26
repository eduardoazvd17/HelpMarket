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
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.mail.MailController;
import br.net.helpmarket.modelo.Usuario;

public class RecuperarActivity extends AppCompatActivity {

    private EditText email, novaSenha, confirmarNovaSenha, codigo;
    private TextView passos;
    private Usuario usuario;
    private Button btnRecuperar, btnValidar, btnEnviar;
    private Integer codigoRecuperacao;
    private LinearLayout voltar, enviarCodigo, validarCodigo, alterarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        email = findViewById(R.id.recuperar_email);
        codigo = findViewById(R.id.recuperar_codigo);
        novaSenha = findViewById(R.id.recuperar_novaSenha);
        confirmarNovaSenha = findViewById(R.id.recuperar_confirmarNovaSenha);
        passos = findViewById(R.id.txtPassos);
        enviarCodigo = findViewById(R.id.r_layout_enviarCodigo);
        validarCodigo = findViewById(R.id.r_layout_validarCodigo);
        alterarSenha = findViewById(R.id.r_layout_alterarSenha);

        voltar = findViewById(R.id.recuperar_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEnviar = findViewById(R.id.recuperar_enviarCodigo);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                DBController db = new DBController(v.getContext());
                if (1 != verificarPreenchimento()) {
                    usuario = db.buscarUsuario(email.getText().toString());
                    if (null != usuario){
                        codigoRecuperacao = new Random().nextInt(900000) + 100000;
                        MailController mc = new MailController(v.getContext());
                        mc.enviarCodigoRecuperaco(usuario, codigoRecuperacao);
                        passos.setText("Passo 2/3 - Informe o código de recuperação");
                        enviarCodigo.setVisibility(View.GONE);
                        validarCodigo.setVisibility(View.VISIBLE);
                        alterarSenha.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(v.getContext(), "Usuário não encontrado", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnValidar = findViewById(R.id.recuperar_validarCodigo);
        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (2 != verificarPreenchimento()) {
                    passos.setText("Passo 3/3 - Digite sua nova senha");
                    enviarCodigo.setVisibility(View.GONE);
                    validarCodigo.setVisibility(View.GONE);
                    alterarSenha.setVisibility(View.VISIBLE);
                }
            }
        });

        btnRecuperar = findViewById(R.id.recuperar_alterarSenha);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (0 == verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    db.recuperarUsuario(email.getText().toString(), criptografarSenha(novaSenha.getText().toString()));
                    MailController mc = new MailController(v.getContext());
                    mc.enviarAlteracaoSenha(usuario);
                    Toast.makeText(getBaseContext(), "Sua senha foi alterada, entre utilizando sua nova senha", Toast.LENGTH_LONG).show();
                    finish();
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

    private int verificarPreenchimento() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Insira seu email");
            return 1;
        }
        if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
            email.setError("Digite um endereço de e-mail válido");
            return 1;
        }
        if (codigo.getText().toString().isEmpty()) {
            codigo.setError("Insira o código recuperação recebido");
            return 2;
        }
        if (!codigo.getText().toString().equals(codigoRecuperacao.toString())) {
            codigo.setError("Código de recuperação incorreto");
            return 2;
        }
        if (novaSenha.getText().toString().isEmpty()) {
            novaSenha.setError("Insira sua nova senha");
            return 3;
        }
        if (confirmarNovaSenha.getText().toString().isEmpty()) {
            confirmarNovaSenha.setError("Confirme sua nova senha");
            return 3;
        }
        if (!novaSenha.getText().toString().equals(confirmarNovaSenha.getText().toString())) {
            novaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
            confirmarNovaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
            return 3;
        }
        return 0;
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
