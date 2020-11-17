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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.santalu.maskara.widget.MaskEditText;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText nome,email,senha,estado,cidade;
    private MaskEditText telefone;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.txtNome);
        email = findViewById(R.id.txtEmail);
        senha = findViewById(R.id.txtSenha);
        telefone = findViewById(R.id.txtTelefone);
        estado = findViewById(R.id.txtEstado);
        cidade = findViewById(R.id.txtCidade);

    }


    public void validarCadastro(View view){
        String txtNome = nome.getText().toString();
        String txtEmail = email.getText().toString();
        String txtSenha = senha.getText().toString();
        String txtTelefone = telefone.getText().toString();
        String txtEstado = estado.getText().toString();
        String txtCidade = cidade.getText().toString();

        if(!txtNome.isEmpty()){
            if(!txtEmail.isEmpty()){
                if(!txtSenha.isEmpty()){
                    if(!txtTelefone.isEmpty()){
                        if(!txtEstado.isEmpty()){
                            if(!txtCidade.isEmpty()){
                                Usuario usuario = new Usuario();
                                usuario.setNome(txtNome);
                                usuario.setImage("");
                                usuario.setEmail(txtEmail);
                                usuario.setSenha(txtSenha);
                                usuario.setTelefone(txtTelefone);
                                usuario.setEstado(txtEstado);
                                usuario.setCidade(txtCidade);
                                cadastrarUsuario(usuario);


                            }else{
                                Toast.makeText(CadastroActivity.this,"Preencha o campo Cidade!",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(CadastroActivity.this,"Preencha o campo Estado!",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(CadastroActivity.this,"Preencha o campo Telefone!",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(CadastroActivity.this,"Preencha o campo Senha!",Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(CadastroActivity.this,"Preencha o campo Email!",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(CadastroActivity.this,"Preencha o campo Nome!",Toast.LENGTH_SHORT).show();
        }
    }
    private void cadastrarUsuario(final Usuario usuario){
        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try{
                        String idUser = task.getResult().getUser().getUid();
                        usuario.setId(idUser);
                        usuario.salvar();
                        UserFirebase.atualizarNomeUser(usuario.getNome());
                        startActivity(new Intent(CadastroActivity.this, AnunciosActivity.class));
                        finish();
                        Toast.makeText(CadastroActivity.this,"Sucesso ao cadastrar usuário!",Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-mail válido!" ;
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta ja foi criada";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+ e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this,excecao,Toast.LENGTH_SHORT).show();
                    Log.w("MyActivity", "signInWithEmail:failure", task.getException());
                    Toast.makeText(CadastroActivity.this,"Erro!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
