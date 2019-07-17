package br.net.helpmarket.modelo;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Objects;

public class Usuario implements Serializable {

    private Long id;
    private String email;
    private String nome;
    private String senha;

    public Usuario(Long id, String email, String nome, String senha) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.senha = senha;
    }

    public Usuario(String email, String nome, String senha) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
    }

    public int verificarPreenchimento(Context context) {
        if (null == email) {
            Toast.makeText(context, "O campo email deve ser preenchido", Toast.LENGTH_LONG).show();
            return 1;
        }
        if (null == nome) {
            Toast.makeText(context, "O campo nome deve ser preenchido", Toast.LENGTH_LONG).show();
            return 2;
        }
        if (null == senha) {
            Toast.makeText(context, "O campo senha deve ser preenchido", Toast.LENGTH_LONG).show();
            return 3;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
