package br.net.helpmarket.database;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import br.net.helpmarket.modelo.Compra;
import br.net.helpmarket.modelo.CompraDB;
import br.net.helpmarket.modelo.Lista;
import br.net.helpmarket.modelo.ListaDB;
import br.net.helpmarket.modelo.Produto;
import br.net.helpmarket.modelo.Usuario;

public class DBController {

    private Context context;

    public DBController(Context context) {
        this.context = context;
    }

    public void inserirProduto(Produto produto) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("INSERT INTO PRODUTOS VALUES ('" + produto.getCodigoBarras() + "','" + produto.getNome() + "','" + produto.getUrlImagem() + "')");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("produtos").document(produto.getCodigoBarras().toString()).set(produto);
    }

    public void inserirLista(Lista lista) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("INSERT INTO LISTAS (ID_USUARIO, NOME, GASTOMAXIMO, DATACRIACAO, TERMINADO) VALUES ('" + lista.getUsuario().getId() + "','" + lista.getNome() + "','" + lista.getGastoMaximo() + "','" + lista.getDataCriacao() + "', 'false')");
//        db.close();

        ListaDB listaDB = new ListaDB(lista.getUsuario().getId(), lista.getNome(), lista.getGastoMaximo(), lista.getQuantidadeProdutos(), lista.getDataCriacao(), lista.getTerminado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas").add(listaDB);
    }

    public void deletarUsuario(Usuario usuario) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM USUARIOS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getId()).delete();
    }

    public void deletarLista(Lista lista) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM LISTAS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas").document(lista.getId()).delete();
        deletarComprasDaLista(lista);
    }

    public void deletarComprasDaLista(Lista lista) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM COMPRAS WHERE ID_LISTA = '" + id + "'");
//        db.close();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras")
                .whereEqualTo("idLista", lista.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> ids = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    ids.add(doc.getId());
                }
                for (String idCompra : ids) {
                    db.collection("compras").document(idCompra).delete();
                }
            }
        });
    }

    public void deletarCompra(Compra compra) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM COMPRAS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").document(compra.getId()).delete();

        Lista lista = compra.getLista();
        ListaDB ldb = new ListaDB(lista.getId(), lista.getUsuario().getId(), lista.getNome(), lista.getGastoMaximo(), lista.getQuantidadeProdutos() - 1, lista.getDataCriacao(), lista.getTerminado());
        db.collection("listas").document(lista.getId()).set(ldb);
    }

    public void atualizarProduto(Compra compra, String nomePersonalizado, int quantidade, double preco) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE COMPRAS SET NOMEPERSONALIZADO = '" + nomePersonalizado + "', QUANTIDADE = '" + quantidade + "', PRECO = '" + preco + "' WHERE ID = '" + compra.getId() + "'");
//        db.close();

        CompraDB compraDB = new CompraDB(compra.getId(), compra.getLista().getId(), compra.getProduto(), nomePersonalizado, quantidade, preco, compra.getComprado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").document(compraDB.getId()).set(compraDB);
    }

    public void comprarProduto(Compra compra, Boolean comprar) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE COMPRAS SET COMPRADO = '" + comprar.toString() + "' WHERE ID = '" + compra.getId() + "'");
//        db.close();

        CompraDB compraDB = new CompraDB(compra.getId(), compra.getLista().getId(), compra.getProduto(), compra.getNomePersonalizado(), compra.getQuantidade(), compra.getPreco(), comprar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").document(compraDB.getId()).set(compraDB);
    }

    public void atualizarLista(Lista lista, String nome, double gastoMaximo) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE LISTAS SET NOME = '" + nome + "', GASTOMAXIMO = '" + gastoMaximo + "' WHERE ID = '" + lista.getId() + "'");
//        db.close();

        ListaDB listaDB = new ListaDB(lista.getId(), lista.getUsuario().getId(), nome, gastoMaximo, lista.getQuantidadeProdutos(), lista.getDataCriacao(), lista.getTerminado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas").document(listaDB.getId()).set(listaDB);
    }

    public void terminarLista(Lista lista, Boolean terminar) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE LISTAS SET TERMINADO = '" + terminar + "' WHERE ID = '" + lista.getId() + "'");
//        db.close();

        ListaDB listaDB = new ListaDB(lista.getId(), lista.getUsuario().getId(), lista.getNome(), lista.getGastoMaximo(), lista.getQuantidadeProdutos(), lista.getDataCriacao(), terminar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas").document(listaDB.getId()).set(listaDB);
    }

    public void salvarCredenciais(Usuario usuario) {
        if (buscarCredenciais().size() == 0) {
            DBHelper db = new DBHelper(context);
            db.executarSQL("INSERT INTO LOGINAUTOMATICO (ID, EMAIL, SENHA) VALUES ('1','" + usuario.getEmail() + "','" + usuario.getSenha() + "')");
            db.close();
        }
    }

    public Map<String, String> buscarCredenciais() {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM LOGINAUTOMATICO");
        cursor.moveToFirst();
        Map<String, String> credenciais = new HashMap<>();
        while (!cursor.isAfterLast()) {
            String email = cursor.getString(cursor.getColumnIndex("EMAIL"));
            String senha = cursor.getString(cursor.getColumnIndex("SENHA"));
            credenciais.put("email", email);
            credenciais.put("senha", senha);
            cursor.moveToNext();
        }
        db.close();
        return credenciais;
    }

    public void alterarUsuario(Usuario usuario, String nome) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE USUARIOS SET NOME = '" + nome + "' WHERE ID = '" + usuario.getId() + "'");
//        db.close();

        usuario.setNome(nome);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getId()).set(usuario);
    }

    public void alterarUsuario(Usuario usuario, String nome, String senha) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE USUARIOS SET NOME = '" + nome + "', SENHA = '" + senha + "' WHERE ID = '" + usuario.getId() + "'");
//        db.close();

        usuario.setNome(nome);
        usuario.setSenha(senha);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getId()).set(usuario);

        if (buscarCredenciais().get("email") == usuario.getEmail()) {
            apagarCredenciais();
            salvarCredenciais(usuario);
        }
    }

    public void apagarCredenciais() {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM LOGINAUTOMATICO");
        db.close();
    }

    public void salvarEmail(String email) {
        if (existeEmailSalvo()) {
            DBHelper db = new DBHelper(context);
            db.executarSQL("UPDATE EMAILSALVO SET EMAIL = '" + email + "'");
            db.close();
        } else {
            DBHelper db = new DBHelper(context);
            db.executarSQL("INSERT INTO EMAILSALVO (EMAIL) VALUES ('" + email + "')");
            db.close();
        }
    }

    public String buscarEmailSalvo() {
        String email = null;
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM EMAILSALVO");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            email = cursor.getString(cursor.getColumnIndex("EMAIL"));
            cursor.moveToNext();
        }
        db.close();
        return email;
    }

    private boolean existeEmailSalvo() {
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM EMAILSALVO");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            return true;
        }
        db.close();
        return false;
    }

    public void apagarEmailSalvo() {
        DBHelper db = new DBHelper(context);
        db.executarSQL("DELETE FROM EMAILSALVO");
        db.close();
    }

}