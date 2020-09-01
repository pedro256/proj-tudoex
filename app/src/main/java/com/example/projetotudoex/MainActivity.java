package com.example.projetotudoex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEntrar = (Button) findViewById(R.id.btentrar);
        Button btnCadastro = (Button) findViewById(R.id.btcadastrar);

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximaTela(v,CadastroActivity.class);
            }
        });
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximaTela(v,Entrar.class);
            }
        });
        
    }

    public void proximaTela(View view,Class tela){
        Intent Ir = new Intent(getApplicationContext(), tela);
        startActivity(Ir);
    }
}
