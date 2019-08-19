package br.net.helpmarket.modelo;

import java.util.Objects;

public class Token {

    private String id;
    private int usos;
    private String dtPrimeiroUso;

    public Token() {

    }

    public Token(String id, int usos, String primeiroUso) {
        this.id = id;
        this.usos = usos;
        this.dtPrimeiroUso = primeiroUso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(id, token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUsos() {
        return usos;
    }

    public void setUsos(int usos) {
        this.usos = usos;
    }

    public String getPrimeiroUso() {
        return dtPrimeiroUso;
    }

    public void setPrimeiroUso(String primeiroUso) {
        this.dtPrimeiroUso = primeiroUso;
    }
}
