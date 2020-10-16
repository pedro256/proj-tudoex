package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import com.app.tudoex.R;
import com.app.tudoex.helper.Permissoes;
import com.blackcat.currencyedittext.CurrencyEditText;

import java.util.Locale;

public class CadastroAnuncioActivity extends AppCompatActivity {

    private EditText txtTitulo,txtCor,txtTamanho,txtDescricao;
    private CurrencyEditText txtValor;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        //VALIDAR PERMISSOES
        Permissoes.validarPermissoes(permissoes,this,1);
        initComponents();
    }
    private void initComponents(){
        txtTitulo = findViewById(R.id.txtTitulo);
        txtCor = findViewById(R.id.txtCor);
        txtTamanho = findViewById(R.id.txtTamanho);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtValor = findViewById(R.id.txtValor);

        //Locale locale = new Locale("pt","BR");
        //txtValor.setLocale(locale);
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

}
