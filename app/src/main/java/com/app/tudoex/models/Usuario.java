package com.app.tudoex.models;

import com.google.firebase.database.DatabaseReference;
import com.app.tudoex.config.FirebaseConfig;
import com.google.firebase.database.Exclude;

public class Usuario {
    private String id;
    private String Nome;
    private String Email;
    private String Senha;
    private String Telefone;
    private String Estado;
    private String Cidade;

    public Usuario() {

    }
    public void salvar(){
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(getId());
        usuarios.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Exclude
    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }
}
