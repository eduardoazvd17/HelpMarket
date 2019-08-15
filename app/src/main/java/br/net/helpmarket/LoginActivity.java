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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Map;

import br.net.helpmarket.database.DBController;
import br.net.helpmarket.modelo.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText email, senha;
    private TextView cadastrar, recuperar;
    private Button btnLogin;
    private CheckBox entrarAutomaticamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        senha = findViewById(R.id.login_senha);
        entrarAutomaticamente = findViewById(R.id.entrarAutomaticamente);

        buscarCredenciais(LoginActivity.this);

        cadastrar = findViewById(R.id.cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CadastroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recuperar = findViewById(R.id.recuperar);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RecuperarActivity.class);
                startActivity(intent);
            }
        });

        btnLogin = findViewById(R.id.fazerLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ocultarTeclado();
                if (verificarPreenchimento()) {
                    final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Efetuando Login");
                    progressDialog.setMessage("Carregando...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String senhaHash = criptografarSenha(senha.getText().toString());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("usuarios")
                            .whereEqualTo("email", email.getText().toString())
                            .whereEqualTo("senha", senhaHash)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    List<Usuario> usuarios = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Usuario u = doc.toObject(Usuario.class);
                                        u.setId(doc.getId());
                                        usuarios.add(u);
                                    }

                                    Usuario usuario = null;
                                    if (usuarios.size() != 0) {
                                        usuario = usuarios.get(0);
                                    }

                                    if (null == usuario) {
                                        Toast.makeText(v.getContext(), "E-mail ou senha incorretos.", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (entrarAutomaticamente.isChecked()) {
                                            salvarCredenciais(usuario);
                                        }

                                        atualizarEmailSalvo();

                                        Toast.makeText(v.getContext(), "Bem vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                                        intent.putExtra("usuario", usuario);
                                        startActivity(intent);
                                        finish();
                                    }

                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

        CoordinatorLayout layout = findViewById(R.id.layout_login);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTeclado();
            }
        });
    }

    private void atualizarEmailSalvo() {
        DBController db = new DBController(getBaseContext());
        db.salvarEmail(email.getText().toString());
    }

    private void buscarEmailSalvo() {
        DBController db = new DBController(getBaseContext());
        String emailSalvo = db.buscarEmailSalvo();
        if (null != emailSalvo) {
            email.setText(emailSalvo);
        }
    }

    private void buscarCredenciais(Context c) {
        DBController dbc = new DBController(c);
        Map<String, String> credenciais = dbc.buscarCredenciais();

        if (credenciais.size() != 0) {
            final ProgressDialog progressDialog = new ProgressDialog(c);
            progressDialog.setTitle("Efetuando Login");
            progressDialog.setMessage("Carregando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios")
                    .whereEqualTo("email", credenciais.get("email"))
                    .whereEqualTo("senha", credenciais.get("senha"))
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Usuario> usuarios = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Usuario u = doc.toObject(Usuario.class);
                        u.setId(doc.getId());
                        usuarios.add(u);
                    }

                    Usuario usuario = null;
                    if (usuarios.size() != 0) {
                        usuario = usuarios.get(0);
                    }

                    if (null == usuario) {
                        Toast.makeText(getBaseContext(), "E-mail ou senha incorretos.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Bem vindo, " + usuario.getNome(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                        finish();
                    }

                    progressDialog.dismiss();
                }
            });
        } else {
            buscarEmailSalvo();
        }
    }

    private void salvarCredenciais(Usuario usuario) {
        DBController db = new DBController(getBaseContext());
        db.salvarCredenciais(usuario);
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
        if (senha.getText().toString().isEmpty()) {
            senha.setError("Insira sua senha");
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
