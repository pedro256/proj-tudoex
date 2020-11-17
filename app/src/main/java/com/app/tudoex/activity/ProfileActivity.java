package com.app.tudoex.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tudoex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    String storagePath ="Users_Profile_Imgs/";

    private  static  final int CAMERA_REQUEST_CODE = 100;
    private  static  final int STORAGE_REQUEST_CODE = 200;
    private  static  final int IMAGE_PICK_GALLERY_CODE = 300;
    private  static  final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    String profilePhoto;
    Uri image_uri;


ImageButton fab;
Button btn;
    ImageView avatarTv;
    TextView userTv, emailTv,cidadeTv,estadoTv,phoneTv;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuarios");
        storageReference = getInstance().getReference();


        cameraPermissions = new  String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new  String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        avatarTv = findViewById(R.id.avatarIv);
        userTv = findViewById(R.id.userTv);
        cidadeTv = findViewById(R.id.cidadeTv);
        emailTv = findViewById(R.id.emailTv);
        estadoTv = findViewById(R.id.estadoTv);
        phoneTv = findViewById(R.id.telefoneTv);

        fab = findViewById(R.id.fab);
        btn = findViewById(R.id.button);

btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
            startActivity(new Intent(ProfileActivity.this, MeusAnunciosActivity.class));
    }
});
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren() ){
                    String user = ""+ds.child("nome").getValue();
                    String email = ""+ds.child("email").getValue();
                    String estado = ""+ds.child("estado").getValue();
                    String cidade = ""+ds.child("cidade").getValue();
                    String phone = ""+ds.child("telefone").getValue();
                    String image = ""+ds.child("image").getValue();



                    userTv.setText(user);
                    emailTv.setText(email);
                    estadoTv.setText(estado);
                    cidadeTv.setText(cidade);
                    phoneTv.setText(phone);
                    try{
                        Picasso.get().load(image).into(avatarTv);
                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_avatar).into(avatarTv);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfile();
            }


        });
  checkUserStatus();


    }


    private  boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(ProfileActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void  requestStoragePermission(){
        requestPermissions( storagePermissions,STORAGE_REQUEST_CODE);
    }


    private  boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(ProfileActivity.this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(ProfileActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void  requestCameraPermission(){
        requestPermissions( cameraPermissions,CAMERA_REQUEST_CODE);
    }






    private void showEditProfile() {
        String options[]= {"Editar foto de perfil","Editar nome ","Editar numero de telefone", "Editar Estado","Editar Cidade"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Editar dados");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    profilePhoto = "image";
                    showImagePicDialog();

                }else if (i ==1)
                {
                    showUpdateDialog("nome");
                }else if (i ==2)
                {

                    showUpdateDialog("telefone");

                }
                else if(i==3){
                    showUpdateDialog("estado");


                }else if (i==4){
                    showUpdateDialog("cidade");

                }
            }
        });

        builder.create().show();
    }

   private void showUpdateDialog(final String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atualizar  "+key);
       LinearLayout linearLayout = new LinearLayout(this);
       linearLayout.setOrientation(LinearLayout.VERTICAL);
       linearLayout.setPadding(10,10,10,10);
       final EditText editText = new EditText(this);
       editText.setHint(" "+key);
       linearLayout.addView(editText);

       builder.setView(linearLayout);

       builder.setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               final String value = editText.getText().toString().trim();
               if (!TextUtils.isEmpty(value)){

                   HashMap<String,Object> result = new HashMap<>();
                   result.put(key,value);
                   databaseReference.child(user.getUid()).updateChildren(result)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {

                                   Toast.makeText(ProfileActivity.this, "Atualizado", Toast.LENGTH_SHORT).show();
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {

                                   Toast.makeText(ProfileActivity.this, "falhou", Toast.LENGTH_SHORT).show();
                               }
                           });

                   if(key.equals("nome")){
                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("anuncios");
                       Query query = ref.orderByChild("email").equalTo(uid);
                       query.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               for (DataSnapshot ds:snapshot.getChildren()){
                                   String child = ds.getKey();
                                   snapshot.getRef().child(child).child("Nome").setValue(value);
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                   }


               }else{
                   Toast.makeText(ProfileActivity.this, "Por favor insira"+key, Toast.LENGTH_SHORT).show();

               }
           }
       });

       builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

           }
       });

       builder.create().show();


   }

    private void showImagePicDialog() {

        String options[]= {"Câmera","Galeria"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("Escolher a imagem de");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }else if (i ==1)
                {
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }

                }
            }


        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
            {
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&& writeStorageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(ProfileActivity.this, "Sem permissão", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if ( writeStorageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(ProfileActivity.this, "Sem permissão", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                uploadProfileCoverPhoto(image_uri);

            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadProfileCoverPhoto(image_uri);


            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {


        String filePathAndName  = storagePath+""+profilePhoto+"_"+ user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        final Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){

                            HashMap<String, Object> results = new HashMap<>();






                            results.put(profilePhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(ProfileActivity.this, "Image Atualizada", Toast.LENGTH_SHORT).show();



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(ProfileActivity.this, "Erro ao atualizar imagem", Toast.LENGTH_SHORT).show();

                                        }
                                    });


                            if(profilePhoto.equals("image")){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("anuncios");
                                Query query = ref.orderByChild("email").equalTo(uid);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds:snapshot.getChildren()){
                                            String child = ds.getKey();
                                            snapshot.getRef().child(child).child("image").setValue(downloadUri);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }



                        }else{

                            Toast.makeText(ProfileActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ProfileActivity.this, "Falhou", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");

        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");

        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            uid = user.getUid();

        }else{

        }
    }
}