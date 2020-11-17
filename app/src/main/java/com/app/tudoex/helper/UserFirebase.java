package com.app.tudoex.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.tudoex.activity.AnunciosActivity;
import com.app.tudoex.config.FirebaseConfig;
import com.app.tudoex.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class UserFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = FirebaseConfig.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }
    public static boolean atualizarNomeUser(String nome){
        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro de atualizar nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public static void redirecionaUsuarioLogado(final Activity activity){

        FirebaseUser user = getUsuarioAtual();
        if(user != null){
            DatabaseReference userRef = FirebaseConfig.getFirebaseDatabase()
                    .child("usuarios")
                    .child(getIdentificadorUsuario());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if(usuario != null){
                        activity.startActivity(new Intent(activity, AnunciosActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}