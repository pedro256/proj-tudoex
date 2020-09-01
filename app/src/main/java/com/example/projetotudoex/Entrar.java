package com.example.projetotudoex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Entrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);
        Button btnVoltar = (Button)findViewById(R.id.btvoltar2);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximaTela(v,MainActivity.class);
            }
        });
    }
    public void proximaTela(View view,Class tela){
        Intent Ir = new Intent(getApplicationContext(), tela);
        startActivity(Ir);
    }
}
