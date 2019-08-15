package br.net.helpmarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.mail.MailController;
import br.net.helpmarket.modelo.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText email, nome, senha, confirmacaoSenha;
    private LinearLayout btnVoltar;
    private Button btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        email = findViewById(R.id.cadastro_email);
        nome = findViewById(R.id.cadastro_nome);
        senha = findViewById(R.id.cadastro_senha);
        confirmacaoSenha = findViewById(R.id.cadastro_confirmacaoSenha);

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
            public void onClick(final View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Efetuando Cadastro");
                    progressDialog.setMessage("Carregando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    final String senhaHash = criptografarSenha(senha.getText().toString());

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("usuarios")
                            .whereEqualTo("email", email.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Usuario usuario = new Usuario(email.getText().toString(), nome.getText().toString(), senhaHash);
                                    List<Usuario> usuarios = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Usuario u = doc.toObject(Usuario.class);
                                        u.setId(doc.getId());
                                        usuarios.add(u);
                                    }

                                    if (usuarios.size() == 0) {
                                        db.collection("usuarios").add(usuario);
                                        atualizarEmailSalvo();
                                        MailController mc = new MailController(v.getContext());
                                        mc.enviarMensagemBoasVindas(usuario);
                                        Toast.makeText(v.getContext(), "Cadastro efetuado. Seja bem vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        intent.putExtra("usuario", usuario);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(v.getContext(), "O endereço de e-mail informado já existe.", Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

        CoordinatorLayout layout = findViewById(R.id.layout_cadastro);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private boolean verificarPreenchimento() {
        if (email.getText().toString().isEmpty()) {
            email.setError("Insira seu e-mail");
            return false;
        }
        if (!validarEmail(email.getText().toString())) {
            email.setError("Digite um endereço de e-mail válido");
            return false;
        }
        if (nome.getText().toString().isEmpty()) {
            nome.setError("Insira seu nome");
            return false;
        }
        if (senha.getText().toString().isEmpty()) {
            senha.setError("Insira sua senha");
            return false;
        }
        if (confirmacaoSenha.getText().toString().isEmpty()) {
            senha.setError("Insira sua senha novamente");
            return false;
        }
        if (!senha.getText().toString().equals(confirmacaoSenha.getText().toString())) {
            senha.setError("Os campos senha e confirmação de senha devem ser iguais");
            confirmacaoSenha.setError("Os campos senha e confirmação de senha devem ser iguais");
            return false;
        }
        return true;
    }

    private boolean validarEmail(final String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    private void atualizarEmailSalvo() {
        DBController db = new DBController(getBaseContext());
        db.salvarEmail(email.getText().toString());
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
