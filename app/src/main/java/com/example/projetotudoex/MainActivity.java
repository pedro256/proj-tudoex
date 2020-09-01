package com.example.projetotudoex;

import androidx.appcompat.app.AppCompatActivity;
import jdk.javadoc.internal.tool.Start;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       Public void IrParaActvity_Cadastro(View view){

        Intent IrParaActvity_Cadastro = new Intent(getApplicationContext(), Cadastro.class);
        startActivity(Act_Cad);
       }

       Public void IrParaActvity_Cadastro(View view){

        Intent Act_Ent = new Intent(getApplicationContext(), Entrar.class);
        startActivity(Act_Ent);
       }

        
    }
}
