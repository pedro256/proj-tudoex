package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.app.tudoex.R;
import com.app.tudoex.adapter.AdapterAnuncios;
import com.app.tudoex.config.FirebaseConfig;
import com.app.tudoex.helper.RecyclerItemClickListener;
import com.app.tudoex.models.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recycleAnuncios;
    private Button btnCategoria, btnTamanho;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anuncioPubRef;
    private AlertDialog dialog;
    private String filtroCategoria = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        inicializarComponentes();
        //config anuncio recycle view

        autenticacao = FirebaseConfig.getFirebaseAutenticacao();
        anuncioPubRef = FirebaseConfig.getFirebaseDatabase().child("anuncios");

        recycleAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recycleAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios,this);
        recycleAnuncios.setAdapter(adapterAnuncios);

        recycleAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recycleAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Anuncio anunciosSelecionado = listaAnuncios.get(position);
                                Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                             i.putExtra("anuncioSelecionado",anunciosSelecionado);
                             startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
        recuperarTodosAnunciosPublicos();

    }
    public void recuperarTodosAnunciosPublicos(){
       /* dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();*/
        listaAnuncios.clear();
        anuncioPubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot categorias : dataSnapshot.getChildren()){
                    for(DataSnapshot cor : categorias.getChildren()){
                        for(DataSnapshot anuncio: cor.getChildren()){
                            Anuncio anun = anuncio.getValue(Anuncio.class);
                            listaAnuncios.add(anun);

                            Collections.reverse(listaAnuncios);
                            adapterAnuncios.notifyDataSetChanged();
                            //dialog.dismiss();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void filtrarPorCategoria(View view){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        // Configuração do spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);


        // Configuração do spinner de estados
        final Spinner spinnerCategorias = viewSpinner.findViewById(R.id.spinnerFiltro);

        String[] categorias = getResources().getStringArray(R.array.categoriasroupa);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adapterEstados);

        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtroCategoria= spinnerCategorias.getSelectedItem().toString();
                recuperarAnunciosPorCategoria();
                //filtrandoPorEstado = true;
            }
        });

        dialogEstado.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }
    public void recuperarAnunciosPorCategoria(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios!")
                .setCancelable(false)
                .build();
        dialog.show();
        anuncioPubRef = FirebaseConfig.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroCategoria);

        anuncioPubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAnuncios.clear();

                for (DataSnapshot anuncios: dataSnapshot.getChildren()){
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!TextUtils.isEmpty(query)){

                    searchPosts(query);
                }else{
                    recuperarTodosAnunciosPublicos();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText)){
                    searchPosts(newText);
                }else{
                    recuperarTodosAnunciosPublicos();
                }
                return false;
            }
        });

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
            case R.id.menu_perfil:
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                break;
            case R.id.menu_anunciar:
                startActivity(new Intent(getApplicationContext(),CreateAnuncios.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void searchPosts(final String searchQuery){

        listaAnuncios.clear();
        anuncioPubRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot categorias : dataSnapshot.getChildren()){

                    for(DataSnapshot cor : categorias.getChildren()){
                        for(DataSnapshot anuncio: cor.getChildren()){
                            Anuncio anun = anuncio.getValue(Anuncio.class);
                            if (anun.getTitulo().contains(searchQuery.toLowerCase())||
                                    anun.getDescricao().toLowerCase().contains(searchQuery.toLowerCase())){
                                listaAnuncios.add(anun);
                            }

                            listaAnuncios.add(anun);

                            Collections.reverse(listaAnuncios);
                            adapterAnuncios.notifyDataSetChanged();
                            //dialog.dismiss();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    public void inicializarComponentes(){
        recycleAnuncios = findViewById(R.id.recyclerAnunciosPublicos);
    }
}
