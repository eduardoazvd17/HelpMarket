package br.net.helpmarket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VerificarProdutoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_produto);

        ImageButton voltar = findViewById(R.id.vp_voltar);
        ImageView imagem = findViewById(R.id.vp_imagem);
        TextView codigoBarras = findViewById(R.id.vp_codigoBarras);
        TextView nome = findViewById(R.id.vp_nome);
        TextView precoMedio = findViewById(R.id.vp_precoMedio);

        Picasso.get().load(getIntent().getExtras().getString("urlImagem")).into(imagem);
        codigoBarras.setText(getIntent().getExtras().getString("codigoBarras"));
        nome.setText(getIntent().getExtras().getString("nome"));
        precoMedio.setText(getIntent().getExtras().getString("precoMedio"));

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
