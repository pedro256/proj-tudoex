package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.tudoex.R;
import com.app.tudoex.config.FirebaseConfig;
import com.app.tudoex.helper.Permissoes;
import com.app.tudoex.models.Anuncio;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CreateAnuncios extends AppCompatActivity implements View.OnClickListener {

    private EditText txtTitulo,txtDescricao;
    private ImageView img1,img2,img3;
    private Spinner spCategoria,spTam,spCor;
    private CurrencyEditText txtValor;
    private Anuncio anuncio = new Anuncio();
    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotoRecuperadas = new ArrayList<>();
    private List<String> listaURLFoto = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_anuncios);
        inicializarComponents();
        carregarDadosSpinner();
        Permissoes.validarPermissoes(permissoes,CreateAnuncios.this,1);
        storage = FirebaseConfig.getFirebaseStorage();
    }

    public CreateAnuncios() {
        super();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgCadastro1:
                escolherImagem(1);
                break;
            case R.id.imgCadastro2:
                escolherImagem(2);
                break;
            case R.id.imgCadastro3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //REQ IMG
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //config image imageView
            if(requestCode == 1){
                img1.setImageURI( imagemSelecionada );
            }else if(requestCode == 2) {
                img2.setImageURI(imagemSelecionada);
            }else if(requestCode == 3){
                img3.setImageURI( imagemSelecionada);
            }
            listaFotoRecuperadas.add(caminhoImagem);
        }else{
            Toast.makeText(this,"Erro!",Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDadosSpinner(){
        String[] categorias = getResources().getStringArray(R.array.categoriasroupa);
        String[] cores = getResources().getStringArray(R.array.cores);
        String[] tamanhos = getResources().getStringArray(R.array.tamanhoroupa);
        //spinner para categorias
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
        //spinner para cores
        ArrayAdapter<String> adapterCores = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cores
        );
        adapterCores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCor.setAdapter(adapterCores);

        //spinner para tamanhos
        ArrayAdapter<String> adapterTamanho = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tamanhos
        );
        adapterTamanho.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTam.setAdapter(adapterTamanho);

    }

    private void inicializarComponents(){
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtValor = findViewById(R.id.txtValor);
        spCategoria = findViewById(R.id.spinnerCategoria);
        spTam = findViewById(R.id.spinnerTam);
        spCor = findViewById(R.id.spinnerCor);

        img1 = findViewById(R.id.imgCadastro1);
        img2 = findViewById(R.id.imgCadastro2);
        img3 = findViewById(R.id.imgCadastro3);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado:grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertValidacaoPermissao();
            }
        }
    }

    private void alertValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessários aceitar permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void salvarAnuncio(){

        //salvar img no storage
        for(int i = 0;i<listaFotoRecuperadas.size();i++){
            String urlImg = listaFotoRecuperadas.get(i);
            int tamanhoLista = listaFotoRecuperadas.size();
            salvarFotoStorage(urlImg,tamanhoLista,i);
        }

    }
    private void salvarFotoStorage(String url, final int totalFotos, int contador){
        //cria nó no storage
        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getId())
                .child("imagem"+contador);
        //fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(url));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String urlConvertido = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                listaURLFoto.add(urlConvertido);
                if(totalFotos == listaURLFoto.size()){
                    anuncio.setFotos(listaURLFoto);
                    anuncio.salvar();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMsgErro("Falha ao fazer upload");
                Log.i("INFO","Falha ao fazer upload de imagem: "+e.getMessage());
            }
        });
    }
    private Anuncio configAnuncio(){

        Anuncio anuncio = new Anuncio();

        String categoria = spCategoria.getSelectedItem().toString();
        String cor = spCor.getSelectedItem().toString();
        String tam = spTam.getSelectedItem().toString();
        String titulo = txtTitulo.getText().toString();
        String valor = String.valueOf(txtValor.getRawValue());
        String descricao = txtDescricao.getText().toString();

        anuncio.setCategoria(categoria);
        anuncio.setCor(cor);
        anuncio.setTamanho(tam);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setDescricao(descricao);

        return anuncio;

    }
    public void validarDados(View v){
        anuncio = configAnuncio();

        if(listaFotoRecuperadas.size()!=0){
            if(!anuncio.getCategoria().isEmpty()){
                if(!anuncio.getCor().isEmpty()){
                    if(!anuncio.getTamanho().isEmpty()){
                        if(!anuncio.getTitulo().isEmpty()){
                            if(!anuncio.getValor().isEmpty() && !anuncio.getValor().equals("0")){
                                if(!anuncio.getDescricao().isEmpty()){
                                    salvarAnuncio();
                                }else{
                                    exibirMsgErro("Inserir uma descricão para o item!");
                                }
                            }else{
                                exibirMsgErro("Inserir uma valor para o item!");
                            }

                        }else{
                            exibirMsgErro("Inserir uma titulo para o anúncio!");
                        }

                    }else{
                        exibirMsgErro("Inserir um tamanho para a roupa");
                    }
                }else{
                    exibirMsgErro("Inserir uma cor");
                }
            }else{
                exibirMsgErro("Inserir uma categoria");
            }
        }else{
            exibirMsgErro("Inserir ao menos uma foto!");
        }


    }
    private void exibirMsgErro(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
