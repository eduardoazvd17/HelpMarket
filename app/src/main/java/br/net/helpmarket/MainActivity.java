package br.net.helpmarket;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private CardView listaCompras, gruposFamiliares, verificarProduto, informacoes;
    private int codErro=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listaCompras = findViewById(R.id.listasCompras);
        gruposFamiliares = findViewById(R.id.gruposFamiliares);
        verificarProduto = findViewById(R.id.verificarProduto);
        informacoes = findViewById(R.id.informacoes);

        listaCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListasComprasActivity.class);
                startActivity(intent);
            }
        });

        gruposFamiliares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Função indisponivel no momento. Em desenvolvimento...", Toast.LENGTH_SHORT).show();
            }
        });

        verificarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.setPrompt("Alinhe o código de barras no centro da tela")
                        .setBeepEnabled(true)
                        .setOrientationLocked(true)
                        .setBarcodeImageEnabled(true)
                        .initiateScan();
            }
        });

        informacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InformacoesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (null == scanningResult) {
            Toast.makeText(this, "Não foi possivel obter resultados, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
        } else {
            try {
                buscarProduto(scanningResult.getContents());
            } catch (Exception e) {
                codErro++;
                Toast.makeText(getBaseContext(), "Ocorreu um erro, tente novamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void buscarProduto(String codigoBarras) throws Exception {
        String urlApi = "https://api.cosmos.bluesoft.com.br";
        URL url = new URL(urlApi + "/gtins/" + codigoBarras);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setRequestProperty("X-Cosmos-Token", obterToken());
        conexao.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        JSONObject jsonObject = new JSONObject(br.readLine());
        conexao.disconnect();

        Intent intent = new Intent(getBaseContext(), VerificarProdutoActivity.class);
        intent.putExtra("urlImagem", jsonObject.getString("thumbnail"));
        intent.putExtra("codigoBarras", jsonObject.getString("gtin"));
        intent.putExtra("nome", jsonObject.getString("description"));
        intent.putExtra("precoMedio", jsonObject.getString("max_price"));
        startActivity(intent);
    }

    private String obterToken() {
        if (codErro == 1) {
            return Tokens.TK1.getKey();
        } else if (codErro == 2) {
            return Tokens.TK2.getKey();
        } else if (codErro == 3) {
            return Tokens.TK3.getKey();
        } else {
            return Tokens.TK4.getKey();
        }
    }
}
