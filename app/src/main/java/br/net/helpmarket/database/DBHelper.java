package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private final String criarTabelaLoginAutomatico = "CREATE TABLE LOGINAUTOMATICO (ID INTEGER PRIMARY KEY, EMAIL TEXT, SENHA TEXT)";
    private final String criarTabelaEmailSalvo = "CREATE TABLE EMAILSALVO (EMAIL TEXT PRIMARY KEY)";

    public DBHelper(Context context) {
        super(context, "helpmarket.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(criarTabelaLoginAutomatico);
        db.execSQL(criarTabelaEmailSalvo);
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
