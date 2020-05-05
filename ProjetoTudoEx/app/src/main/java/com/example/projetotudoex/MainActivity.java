package com.example.projetotudoex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btcadastrar;
    Button btEntrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btcadastrar = (Button) findViewById(R.id.btcadastrar);
        btEntrar = (Button) findViewById(R.id.btentrar);

        btcadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Chama = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(Chama);
            }
        });

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Chama = new Intent(MainActivity.this, Entrar.class);
                startActivity(Chama);
            }
        });
    }
}
