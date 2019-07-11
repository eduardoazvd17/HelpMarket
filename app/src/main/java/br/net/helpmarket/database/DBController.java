package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.Produto;

public class DBController {

    public void inserirProduto(Context context, Produto produto) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO PRODUTOS VALUES (" + produto.getCodigoBarras() + ",'" + produto.getNome() + "','" + produto.getUrlImagem() + "')");
    }

    public void inserirLista(Context context, Lista lista) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO LISTAS VALUES (" + lista.getNome() + ",'" + lista.getGastoMaximo() + "','" + lista.getDataCriacao().toString() + "', 'false')");
    }

    public void inserirCompra(Context context, Compra compra) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO COMPRAS VALUES (" + compra.getLista().getId() + ",'" + compra.getProduto().getCodigoBarras() + "','" + compra.getQuantidade() + "', '" + compra.getPreco() + "', 'false')");
    }

    public void deletarProduto(Context context, Long codigoBarras) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM PRODUTOS WHERE CODIGOBARRAS = '" + codigoBarras + "'");
    }

    public void deletarLista(Context context, Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM LISTAS WHERE ID = '" + id + "'");
    }

    public void deletarCompra(Context context, Long id) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM COMPRAS WHERE ID = '" + id + "'");
    }

    public List<Produto> selecionarProdutos(Context context) {
        DBHelper db = new DBHelper(context);
        db.executarSQL("insert into produtos values (12345,'coca-cola','www.google.com')");
        Cursor cursor = db.executarSQLSelect("select * from produtos");
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
        return produtos;
    }

}
