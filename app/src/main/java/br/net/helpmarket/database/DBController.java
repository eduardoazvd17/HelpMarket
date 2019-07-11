package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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
        Cursor cursor = db.executarSQLSelect("SELECT * FROM LISTAS");
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

    public List<Lista> selecionarListas(Context context) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM LISTAS");
        List<Lista> listas = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
            double gastoMaximo = cursor.getDouble(cursor.getColumnIndex("GASTOMAXIMO"));
            Date dataCriacao = new Date(cursor.getString(cursor.getColumnIndex("DATACRIACAO")));
            Boolean terminado = false;
            if ("true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("TERMINADO")))) {
                terminado = true;
            }
            Lista lista = new Lista(id, nome, gastoMaximo, dataCriacao, terminado);
            listas.add(lista);
            cursor.moveToNext();
        }
        return listas;
    }

    public List<Compra> selecionarCompras(Context context) {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM COMPRAS");
        List<Compra> compras = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex("ID"));
            List<Lista> listas = selecionarListas(context);
            Lista lista = null;
            for (Lista l: listas) {
                if (l.getId().equals(cursor.getLong(cursor.getColumnIndex("ID_LISTA")))) {
                    lista = l;
                }
            }
            List<Produto> produtos = selecionarProdutos(context);
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
            Compra compra = new Compra(id, lista, produto, nomePersonalizado, quantidade, preco, comprado);
            compras.add(compra);
            cursor.moveToNext();
        }
        return compras;
    }

}
