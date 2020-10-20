package com.app.tudoex.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storageFB;

    public static String getIdUsuario(){
        FirebaseAuth auth = getFirebaseAutenticacao();
        return auth.getCurrentUser().getUid();
    }

    public static DatabaseReference getFirebaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getFirebaseStorage(){
        if(storageFB == null){
            storageFB = FirebaseStorage.getInstance().getReference();
        }
        return storageFB;
    }

}
