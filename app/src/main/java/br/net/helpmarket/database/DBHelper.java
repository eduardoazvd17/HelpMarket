package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private final String criarTabelaUsuarios = "CREATE TABLE USUARIOS (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, NOME TEXT, SENHA TEXT)";
    private final String criarTabelaProdutos = "CREATE TABLE PRODUTOS (CODIGOBARRAS INTEGER PRIMARY KEY, NOME TEXT, URLIMAGEM TEXT)";
    private final String criarTabelaListas = "CREATE TABLE LISTAS (ID INTEGER PRIMARY KEY AUTOINCREMENT, ID_USUARIO INTEGER, NOME TEXT, QUANTIDADEPRODUTOS INTEGER, TOTALGASTO TEXT, GASTOMAXIMO TEXT, DATACRIACAO TEXT, TERMINADO TEXT)";
    private final String criarTabelaCompras = "CREATE TABLE COMPRAS (ID INTEGER PRIMARY KEY AUTOINCREMENT, ID_USUARIO INTEGER, ID_LISTA INTEGER, CODIGOBARRAS_PRODUTO, NOMEPERSONALIZADO TEXT, QUANTIDADE NTEGER, PRECO TEXT, COMPRADO TEXT)";

    public DBHelper(Context context) {
        super(context, "helpmarket.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(criarTabelaUsuarios);
        db.execSQL(criarTabelaProdutos);
        db.execSQL(criarTabelaListas);
        db.execSQL(criarTabelaCompras);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor executarSQLSelect(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery(query, new String[] {});
        db.setTransactionSuccessful();
        db.endTransaction();
        return cursor;
    }

    public void executarSQL(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        db.execSQL(query);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
