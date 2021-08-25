package com.abhishek.canteen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateItem extends AppCompatActivity {

    EditText name,price;
    Button save,upload;
    ImageView foodimage;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    Uri imageuri;
    ProgressBar progressBar;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String imgtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        name = findViewById(R.id.create_item_name);
        price = findViewById(R.id.create_item_price);
        foodimage = findViewById(R.id.uploaded_image);
        save = findViewById(R.id.save_button);
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBar=findViewById(R.id.create_pgbar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        upload = findViewById(R.id.upload_button);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item_name = name.getText().toString();
                String item_price = price.getText().toString();

                if (item_name.equals("") || item_price.equals("") || imageuri==null) {
                    Toast.makeText(CreateItem.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        save.setVisibility(View.INVISIBLE);
                        StorageReference ref = storageReference.child("images").child(firebaseAuth.getUid()).child(firebaseAuth.getUid()+item_name);
                        ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                            {
                                                Toast.makeText(CreateItem.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(CreateItem.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    String Uid = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference=firebaseFirestore.collection("admin").document(Uid).collection("iteminfo").document();
                    Map<String, Object> user = new HashMap<>();
                    imgtoken=imageuri.toString();
                    user.put("name", item_name);
                    user.put("imgurl",imgtoken);
                    user.put("price", item_price);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent intent = new Intent(CreateItem.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                        progressBar.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void selectImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100 && data!=null)
        {
            imageuri=data.getData();
            foodimage.setImageURI(imageuri);
        }
    }

}