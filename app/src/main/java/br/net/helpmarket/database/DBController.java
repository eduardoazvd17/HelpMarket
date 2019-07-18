package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;
import br.net.helpmarket.modelo.Usuario;

public class DBController {

    Context context;

    public DBController(Context context) {
        this.context = context;
    }

    public boolean fazerCadastro(Usuario usuario) {
        if (verificarExistenciaUsuario(usuario.getEmail())) {
            return false;
        } else {
            DBHelper db = new DBHelper(context);
            db.executarSQL("INSERT INTO USUARIOS (EMAIL, NOME, SENHA) VALUES ('" + usuario.getEmail() + "','" + usuario.getNome() + "','" + usuario.getSenha() + "')");
            db.close();
            return true;
        }
    }

    private boolean verificarExistenciaUsuario(String email) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM USUARIOS WHERE EMAIL = '" + email + "'");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public Usuario fazerLogin(String email, String senha) {
        Usuario usuario = null;
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM USUARIOS WHERE EMAIL = '" + email + "' AND SENHA = '" + senha + "'");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Long id = cursor.getLong(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
            usuario = new Usuario(id, email, nome, senha);
            cursor.moveToNext();
        }
        db.close();
        return usuario;
    }

    public void inserirProduto(Produto produto) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO PRODUTOS VALUES ('" + produto.getCodigoBarras() + "','" + produto.getNome() + "','" + produto.getUrlImagem() + "')");
        db.close();
    }

    public void inserirLista(Lista lista) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO LISTAS (ID_USUARIO, NOME, GASTOMAXIMO, DATACRIACAO, TERMINADO) VALUES ('" + lista.getUsuario().getId() + "','" + lista.getNome() + "','" + lista.getGastoMaximo() + "','" + lista.getDataCriacao() + "', 'false')");
        db.close();
    }

    public void inserirCompra(Compra compra) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO COMPRAS (ID_USUARIO, ID_LISTA, CODIGOBARRAS_PRODUTO, QUANTIDADE, PRECO, COMPRADO) VALUES ('" + compra.getUsuario().getId() + "','" + compra.getLista().getId() + "','" + compra.getProduto().getCodigoBarras() + "','" + compra.getQuantidade() + "', '" + compra.getPreco() + "', 'false')");
        db.close();
    }

    public void deletarUsuario(Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM USUARIOS WHERE ID = '" + id + "'");
        db.close();
    }

    public void deletarProduto(Long codigoBarras) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM PRODUTOS WHERE CODIGOBARRAS = '" + codigoBarras + "'");
        db.close();
    }

    public void deletarLista(Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM LISTAS WHERE ID = '" + id + "'");
        db.close();
    }

    public void deletarComprasDaLista(Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM COMPRAS WHERE ID_LISTA = '" + id + "'");
        db.close();
    }

    public void deletarCompra(Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM COMPRAS WHERE ID = '" + id + "'");
        db.close();
    }

    public Produto buscarProduto(Long codigoBarras) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM PRODUTOS WHERE CODIGOBARRAS = '" + codigoBarras + "'");
        Produto produto = null;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
            String urlImagem = cursor.getString(cursor.getColumnIndex("URLIMAGEM"));
            produto = new Produto(codigoBarras, nome, urlImagem);
            cursor.moveToNext();
        }
        db.close();
        return produto;
    }

    public List<Produto> selecionarProdutos() {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM PRODUTOS");
        List<Produto> produtos = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            long codigoBarras = cursor.getLong(cursor.getColumnIndex("CODIGOBARRAS"));
            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
            String urlImagem = cursor.getString(cursor.getColumnIndex("URLIMAGEM"));
            Produto produto = new Produto(codigoBarras, nome, urlImagem);
            produtos.add(produto);
            cursor.moveToNext();
        }
        db.close();
        return produtos;
    }

    public List<Lista> selecionarListas(Usuario usuario) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM LISTAS WHERE ID_USUARIO = '" + usuario.getId() + "'");
        List<Lista> listas = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
            double gastoMaximo = cursor.getDouble(cursor.getColumnIndex("GASTOMAXIMO"));
            int quantidadeProdutos = countProdutos(id);
            double totalGasto = sumTotalGasto(id);
            String dataCriacao = cursor.getString(cursor.getColumnIndex("DATACRIACAO"));
            Boolean terminado = false;
            if ("true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("TERMINADO")))) {
                terminado = true;
            }
            Lista lista = new Lista(id, usuario, nome, gastoMaximo, quantidadeProdutos, totalGasto, dataCriacao, terminado);
            listas.add(lista);
            cursor.moveToNext();
        }
        db.close();
        return listas;
    }

    public List<Compra> selecionarCompras(Lista lista) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM COMPRAS WHERE ID_USUARIO = '" + lista.getUsuario().getId() + "' AND ID_LISTA = '" + lista.getId() + "'");
        List<Compra> compras = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex("ID"));
            List<Produto> produtos = selecionarProdutos();
            Produto produto = null;
            for (Produto p: produtos) {
                if (p.getCodigoBarras().equals(cursor.getLong(cursor.getColumnIndex("CODIGOBARRAS_PRODUTO")))) {
                    produto = p;
                }
            }
            String nomePersonalizado = cursor.getString(cursor.getColumnIndex("NOMEPERSONALIZADO"));
            int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADE"));
            double preco = cursor.getFloat(cursor.getColumnIndex("PRECO"));

            Boolean comprado = false;
            if ("true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("COMPRADO")))) {
                comprado = true;
            }
            Compra compra = new Compra(id, lista.getUsuario(), lista, produto, nomePersonalizado, quantidade, preco, comprado);
            compras.add(compra);
            cursor.moveToNext();
        }
        db.close();
        return compras;
    }

    private int countProdutos(Long idLista) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT COUNT(CODIGOBARRAS_PRODUTO) AS QUANTIDADEPRODUTOS FROM COMPRAS WHERE ID_LISTA='" + idLista +  "'");
        cursor.moveToFirst();
        int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADEPRODUTOS"));
        db.close();
        return quantidade;
    }

    private double sumTotalGasto(long idLista) {
        double total = 0.0;
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT QUANTIDADE, PRECO FROM COMPRAS WHERE ID_LISTA='" + idLista +  "'");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADE"));
            double preco = cursor.getFloat(cursor.getColumnIndex("PRECO"));
            total = total + (quantidade*preco);
        }
        db.close();
        return total;
    }

}
