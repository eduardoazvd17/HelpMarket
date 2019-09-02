package br.net.helpmarket;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.mail.MailController;
import br.net.helpmarket.modelo.Usuario;

public class MinhaContaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nome, senhaAtual, novaSenha, confirmarNovaSenha;
    private Usuario usuario;
    private String nvSenha;
    private TextView email;
    private LinearLayout alterarSenha, cancelarSenha;
    private CardView senha, alterar;
    private Boolean desejaAlterarSenha;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhaconta);

        toolbar = findViewById(R.id.mc_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MobileAds.initialize(this, "ca-app-pub-6093298333256656~3639487257");
        final AdView adView = findViewById(R.id.mc_adview);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        nome = findViewById(R.id.mc_nome);
        email = findViewById(R.id.mc_email);
        senha = findViewById(R.id.mc_senha);
        senha.setVisibility(View.VISIBLE);
        alterar = findViewById(R.id.mc_alterar);
        alterar.setVisibility(View.GONE);
        cancelarSenha = findViewById(R.id.mc_cancelarSenha);
        senhaAtual = findViewById(R.id.senhaAtual);
        novaSenha = findViewById(R.id.novaSenha);
        confirmarNovaSenha = findViewById(R.id.confimarNovaSenha);
        alterarSenha = findViewById(R.id.mc_alterarSenha);
        usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");

        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());

        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senha.setVisibility(View.GONE);
                alterar.setVisibility(View.VISIBLE);
            }
        });

        cancelarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterar.setVisibility(View.GONE);
                senha.setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton salvarMinhaConta = findViewById(R.id.salvarMinhaConta);
        salvarMinhaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    DBController db = new DBController(v.getContext());
                    if (desejaAlterarSenha) {
                        db.alterarUsuario(usuario, nome.getText().toString(), nvSenha);
                        MailController mc = new MailController(v.getContext());
                        mc.enviarAlteracaoSenha(usuario);
                    } else {
                        db.alterarUsuario(usuario, nome.getText().toString());
                    }
                    Toast.makeText(v.getContext(), "Todas as alterações foram salvas", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        ImageButton voltar = findViewById(R.id.mc_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.layout_minhaconta);
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
            nome.setError("Insira o seu nome");
            return false;
        }
        if (alterar.getVisibility() == View.VISIBLE) {
            if (novaSenha.getText().toString().isEmpty()) {
                nome.setError("Insira a nova senha");
                return false;
            }
            if (confirmarNovaSenha.getText().toString().isEmpty()) {
                nome.setError("Confirme sua nova senha");
                return false;
            }
            desejaAlterarSenha = true;
            String atSenha = criptografarSenha(senhaAtual.getText().toString());
            nvSenha = criptografarSenha(novaSenha.getText().toString());
            String cnvSenha = criptografarSenha(confirmarNovaSenha.getText().toString());
            if (!atSenha.equals(usuario.getSenha())) {
                senhaAtual.setError("Senha incorreta");
                return false;
            }
            if (!nvSenha.equals(cnvSenha)) {
                novaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
                confirmarNovaSenha.setError("Os campos nova senha e confirmação de nova senha devem ser iguais");
                return false;
            }
        } else {
            desejaAlterarSenha = false;
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
}
