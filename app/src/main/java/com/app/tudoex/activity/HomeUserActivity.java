package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.tudoex.R;
import com.app.tudoex.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;

public class HomeUserActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                autenticacao.signOut();
                finish();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
