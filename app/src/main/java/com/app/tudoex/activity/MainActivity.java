package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.tudoex.R;
import com.app.tudoex.config.FirebaseConfig;
import com.app.tudoex.helper.UserFirebase;
import com.app.tudoex.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText textEmail,textSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        textEmail = findViewById(R.id.txtEmail);
        textSenha = findViewById(R.id.txtSenha);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserFirebase.redirecionaUsuarioLogado(MainActivity.this);
    }

    public void telaCadastro(View view){
        startActivity(new Intent(this,CadastroActivity.class));
    }

    public void autenticarUsuario(View view){
        String Email = textEmail.getText().toString();
        String Senha = textSenha.getText().toString();

        if(!Email.isEmpty()){
            if(!Senha.isEmpty()){
                Usuario usuario = new Usuario();
                usuario.setEmail(Email);
                usuario.setSenha(Senha);
                logarUser(usuario);
            }else{
                Toast.makeText(MainActivity.this, "Preencha senha corretamente!",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Preencha email corretamente!",Toast.LENGTH_SHORT).show();

        }
    }
    public void logarUser(final Usuario usuario){
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserFirebase.redirecionaUsuarioLogado(MainActivity.this);
                    Toast.makeText(MainActivity.this,"Usuario Logado",Toast.LENGTH_SHORT).show();
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuario não cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Email e senha não correspondem ao usuario cadastrado!" ;
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+ e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,excecao,Toast.LENGTH_SHORT).show();

                    Log.w("Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this,"Erro!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
