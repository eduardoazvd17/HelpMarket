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
import java.util.List;
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

    public boolean fazerCadastro(Usuario usuario) {
//        if (verificarExistenciaUsuario(usuario.getEmail())) {
//            return false;
//        } else {
//            DBHelper db = new DBHelper(context);
//            db.executarSQL("INSERT INTO USUARIOS (EMAIL, NOME, SENHA) VALUES ('" + usuario.getEmail() + "','" + usuario.getNome() + "','" + usuario.getSenha() + "')");
//            db.close();
//        }

        if (verificarExistenciaUsuario(usuario.getEmail())) {
            return false;
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").add(usuario);
            return true;
        }
    }

    private boolean verificarExistenciaUsuario(String email) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM USUARIOS WHERE EMAIL = '" + email + "'");
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            db.close();
//            return true;
//        }
//        db.close();
//        return false;

        List<Usuario> usuarios = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    Usuario u = doc.toObject(Usuario.class);
                    u.setId(doc.getId());
                    usuarios.add(u);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        if (usuarios.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Usuario fazerLogin(final String email, final String senha) {
//        Usuario usuario = null;
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM USUARIOS WHERE EMAIL = '" + email + "' AND SENHA = '" + senha + "'");
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            Long id = cursor.getLong(cursor.getColumnIndex("ID"));
//            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
//            usuario = new Usuario(id, email, nome, senha);
//            cursor.moveToNext();
//        }
//        db.close();
//        return usuario;

        List<Usuario> usuarios = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("usuarios")
                .whereEqualTo("email", email)
                .whereEqualTo("senha", senha)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    Usuario u = doc.toObject(Usuario.class);
                    u.setId(doc.getId());
                    usuarios.add(u);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        Usuario usuario = null;
        if (usuarios.size() != 0) {
            usuario = usuarios.get(0);
        }
        return usuario;
    }

    public Usuario buscarUsuario(String email) {
//        Usuario usuario = null;
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM USUARIOS WHERE EMAIL = '" + email + "'");
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            Long id = cursor.getLong(cursor.getColumnIndex("ID"));
//            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
//            String senha = cursor.getString(cursor.getColumnIndex("SENHA"));
//            usuario = new Usuario(id, email, nome, senha);
//            cursor.moveToNext();
//        }
//        db.close();
//        return usuario;

        List<Usuario> usuarios = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("usuarios")
                .whereEqualTo("email", email)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    Usuario u = doc.toObject(Usuario.class);
                    u.setId(doc.getId());
                    usuarios.add(u);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        Usuario usuario = null;
        if (usuarios.size() != 0) {
            usuario = usuarios.get(0);
        }
        return usuario;
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

    public void inserirCompra(Compra compra) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("INSERT INTO COMPRAS (ID_USUARIO, ID_LISTA, CODIGOBARRAS_PRODUTO, NOMEPERSONALIZADO, QUANTIDADE, PRECO, COMPRADO) VALUES ('" + compra.getUsuario().getId() + "','" + compra.getLista().getId() + "','" + compra.getProduto().getCodigoBarras() + "','" + compra.getNomePersonalizado() + "','" + compra.getQuantidade() + "', '" + compra.getPreco() + "', 'false')");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CompraDB compraDB = new CompraDB(compra.getUsuario().getId(), compra.getLista().getId(), compra.getProduto().getCodigoBarras(), compra.getNomePersonalizado(), compra.getQuantidade(), compra.getPreco(), compra.getComprado());
        db.collection("compras").add(compraDB);
    }

    public void deletarUsuario(String id) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM USUARIOS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(id).delete();
    }

    public void deletarProduto(Long codigoBarras) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM PRODUTOS WHERE CODIGOBARRAS = '" + codigoBarras + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("produtos").document(codigoBarras.toString()).delete();
    }

    public void deletarLista(String id) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM LISTAS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listas").document(id).delete();
    }

    public void deletarComprasDaLista(String id) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM COMPRAS WHERE ID_LISTA = '" + id + "'");
//        db.close();

        List<String> ids = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("compras")
                .whereEqualTo("idLista", id)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    ids.add(doc.getId());
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        for (String idCompra : ids) {
            db.collection("compras").document(idCompra).delete();
        }
    }

    public void deletarCompra(String id) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("DELETE FROM COMPRAS WHERE ID = '" + id + "'");
//        db.close();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").document(id).delete();
    }

    public Produto buscarProduto(Long codigoBarras) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM PRODUTOS WHERE CODIGOBARRAS = '" + codigoBarras + "'");
//        Produto produto = null;
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
//            String urlImagem = cursor.getString(cursor.getColumnIndex("URLIMAGEM"));
//            produto = new Produto(codigoBarras, nome, urlImagem);
//            cursor.moveToNext();
//        }
//        db.close();

        List<Produto> produtos = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("produtos")
                .whereEqualTo("codigoBarras", codigoBarras)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    Produto p = doc.toObject(Produto.class);
                    produtos.add(p);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        Produto produto = null;
        if (produtos.size() != 0) {
            produto = produtos.get(0);
        }
        return produto;
    }

    public List<Usuario> selecionarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("usuarios")
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    Usuario u = doc.toObject(Usuario.class);
                    usuarios.add(u);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        return usuarios;
    }

    public List<Produto> selecionarProdutos() {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM PRODUTOS");
//        List<Produto> produtos = new ArrayList<>();
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            long codigoBarras = cursor.getLong(cursor.getColumnIndex("CODIGOBARRAS"));
//            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
//            String urlImagem = cursor.getString(cursor.getColumnIndex("URLIMAGEM"));
//            Produto produto = new Produto(codigoBarras, nome, urlImagem);
//            produtos.add(produto);
//            cursor.moveToNext();
//        }
//        db.close();

        List<Produto> produtos = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("produtos")
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                        Produto p = doc.toObject(Produto.class);
                        produtos.add(p);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        return produtos;
    }

    public List<Lista> selecionarListas(Usuario usuario) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM LISTAS WHERE ID_USUARIO = '" + usuario.getId() + "'");
//        List<Lista> listas = new ArrayList<>();
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            long id = cursor.getLong(cursor.getColumnIndex("ID"));
//            String nome = cursor.getString(cursor.getColumnIndex("NOME"));
//            double gastoMaximo = cursor.getDouble(cursor.getColumnIndex("GASTOMAXIMO"));
//            int quantidadeProdutos = countProdutos(id);
//            String dataCriacao = cursor.getString(cursor.getColumnIndex("DATACRIACAO"));
//            Boolean terminado = false;
//            if ("true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("TERMINADO")))) {
//                terminado = true;
//            }
//            Lista lista = new Lista(id, usuario, nome, gastoMaximo, quantidadeProdutos, dataCriacao, terminado);
//            listas.add(lista);
//            cursor.moveToNext();
//        }
//        db.close();

        List<ListaDB> listasDB = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("listas")
                .whereEqualTo("idUsuario", usuario.getId())
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                        ListaDB l = doc.toObject(ListaDB.class);
                        l.setId(doc.getId());
                        listasDB.add(l);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        List<Lista> listas = new ArrayList<>();
        for (ListaDB l : listasDB) {
            int quantidadeProdutos = countProdutos(l.getId());
            listas.add(new Lista(l.getId(), usuario, l.getNome(), l.getGastoMaximo(), quantidadeProdutos, l.getDataCriacao(), l.getTerminado()));
        }
        return listas;
    }

    public List<Compra> selecionarCompras(Lista lista) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM COMPRAS WHERE ID_USUARIO = '" + lista.getUsuario().getId() + "' AND ID_LISTA = '" + lista.getId() + "'");
//        List<Compra> compras = new ArrayList<>();
//        List<Produto> produtos = selecionarProdutos();
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            long id = cursor.getLong(cursor.getColumnIndex("ID"));
//            Produto produto = null;
//            for (Produto p: produtos) {
//                if (p.getCodigoBarras().equals(cursor.getLong(cursor.getColumnIndex("CODIGOBARRAS_PRODUTO")))) {
//                    produto = p;
//                }
//            }
//            String nomePersonalizado = cursor.getString(cursor.getColumnIndex("NOMEPERSONALIZADO"));
//            int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADE"));
//            double preco = cursor.getFloat(cursor.getColumnIndex("PRECO"));
//
//            Boolean comprado = false;
//            if ("true".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("COMPRADO")))) {
//                comprado = true;
//            }
//            Compra compra = new Compra(id, lista.getUsuario(), lista, produto, nomePersonalizado, quantidade, preco, comprado);
//            compras.add(compra);
//            cursor.moveToNext();
//        }
//        db.close();

        final List<CompraDB> comprasDB = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("compras")
                .whereEqualTo("idLista", lista.getId())
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                        CompraDB cdb = doc.toObject(CompraDB.class);
                        cdb.setId(doc.getId());
                        comprasDB.add(cdb);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        List<Compra> compras = new ArrayList<>();
        for (CompraDB c : comprasDB) {
            Produto produto = buscarProduto(c.getCodigoBarrasProduto());
            compras.add(new Compra(c.getId(), lista.getUsuario(), lista, produto, c.getNomePersonalizado(), c.getQuantidade(), c.getPreco(), c.getComprado()));
        }
        return compras;
    }

    private int countProdutos(String idLista) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT COUNT(CODIGOBARRAS_PRODUTO) AS QUANTIDADEPRODUTOS FROM COMPRAS WHERE ID_LISTA='" + idLista +  "'");
//        cursor.moveToFirst();
//        int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADEPRODUTOS"));
//        db.close();
//        return quantidade;

        final List<String> ids = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("compras")
                .whereEqualTo("idLista", idLista)
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    ids.add(doc.getId());
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        return ids.size();
    }

    public void atualizarProduto(Compra compra, String nomePersonalizado, int quantidade, double preco) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE COMPRAS SET NOMEPERSONALIZADO = '" + nomePersonalizado + "', QUANTIDADE = '" + quantidade + "', PRECO = '" + preco + "' WHERE ID = '" + compra.getId() + "'");
//        db.close();

        CompraDB compraDB = new CompraDB(compra.getId(), compra.getUsuario().getId(), compra.getLista().getId(), compra.getProduto().getCodigoBarras(), nomePersonalizado, quantidade, preco, compra.getComprado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").document(compraDB.getId()).set(compraDB);
    }

    public void comprarProduto(Compra compra, Boolean comprar) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE COMPRAS SET COMPRADO = '" + comprar.toString() + "' WHERE ID = '" + compra.getId() + "'");
//        db.close();

        CompraDB compraDB = new CompraDB(compra.getId(), compra.getUsuario().getId(), compra.getLista().getId(), compra.getProduto().getCodigoBarras(), compra.getNomePersonalizado(), compra.getQuantidade(), compra.getPreco(), comprar);
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
        DBHelper db = new DBHelper(context);
        db.executarSQL("INSERT INTO LOGINAUTOMATICO (ID, EMAIL, SENHA) VALUES ('1','" + usuario.getEmail() + "','" + usuario.getSenha() + "')");
        db.close();
    }

    public Usuario buscarCredenciais() {
        Usuario usuario = null;
        DBHelper db = new DBHelper(context);
        Cursor cursor = db.executarSQLSelect("SELECT * FROM LOGINAUTOMATICO");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            String email = cursor.getString(cursor.getColumnIndex("EMAIL"));
            String senha = cursor.getString(cursor.getColumnIndex("SENHA"));
            usuario = fazerLogin(email, senha);
            cursor.moveToNext();
        }
        db.close();
        return usuario;
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
    }

    public void recuperarUsuario(String email, String novaSenha) {
//        DBHelper db = new DBHelper(context);
//        db.executarSQL("UPDATE USUARIOS SET SENHA = '" + novaSenha + "' WHERE EMAIL = '" + email + "'");
//        db.close();

        Usuario usuario = buscarUsuario(email);
        usuario.setSenha(novaSenha);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getId()).set(usuario);
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
        while (!cursor.isAfterLast()){
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
        while (!cursor.isAfterLast()){
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

    public boolean verificarExistenciaProduto(Lista lista, Produto produto, Compra compra) {
//        DBHelper db = new DBHelper(context);
//        Cursor cursor = db.executarSQLSelect("SELECT * FROM COMPRAS WHERE ID_USUARIO = '" + lista.getUsuario().getId() + "' AND ID_LISTA = '" + lista.getId() + "' AND CODIGOBARRAS_PRODUTO = '" + produto.getCodigoBarras() + "'");
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            int quantidade = cursor.getInt(cursor.getColumnIndex("QUANTIDADE")) + compra.getQuantidade();
//            db.executarSQL("UPDATE COMPRAS SET QUANTIDADE = '" + quantidade + "' WHERE ID_USUARIO = '" + lista.getUsuario().getId() + "' AND ID_LISTA = '" + lista.getId() + "' AND CODIGOBARRAS_PRODUTO = '" + produto.getCodigoBarras() + "'");
//            return true;
//        }
//        db.close();
//        return false;

        final List<CompraDB> comprasDB = new ArrayList<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> tasks = db.collection("compras")
                .whereEqualTo("idLista", lista.getId())
                .whereEqualTo("codigoBarrasProduto", produto.getCodigoBarras())
                .get();

        boolean isTerminado = false;
        do {
            if (tasks.isComplete()) {
                for (QueryDocumentSnapshot doc : tasks.getResult()) {
                    CompraDB cdb = doc.toObject(CompraDB.class);
                    cdb.setId(doc.getId());
                    comprasDB.add(cdb);
                }
                isTerminado=true;
            }
        } while (!isTerminado);

        if (comprasDB.size() != 0) {
            CompraDB c = comprasDB.get(0);
            c.setQuantidade(c.getQuantidade() + compra.getQuantidade());
            db.collection("compras").document(c.getId()).set(c);
            return true;
        } else {
            return false;
        }
    }
}
