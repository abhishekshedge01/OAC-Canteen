package com.abhishek.canteen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    EditText update_name,update_price;
    Button change,update;
    ImageView image;
    FirebaseAuth firebaseAuth;
    Intent dataint;
    FirebaseStorage storage;
    Uri imageuri;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        update_name=findViewById(R.id.edit_name);
        update_price=findViewById(R.id.edit_price);
        change=findViewById(R.id.edit_upload);
        update=findViewById(R.id.edit_update);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        image=findViewById(R.id.edit_image);
        dataint=getIntent();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Picasso.get().load(dataint.getStringExtra("imagelink")).into(image);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName=update_name.getText().toString();
                String newPrice=update_price.getText().toString();

                if(newName.isEmpty() ||newPrice.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Something is Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {

                    StorageReference ref = storageReference.child("images").child(firebaseAuth.getUid()).child(firebaseAuth.getUid()+newName);
                    ref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            Toast.makeText(EditActivity.this, "Image Updated!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(EditActivity.this, "Failed " + e.getMessage(),  Toast.LENGTH_SHORT).show();
                        }
                    });


                    DocumentReference documentReference=firebaseFirestore.collection("admin").document(firebaseUser.getUid()).collection("iteminfo").document(dataint.getStringExtra("noteId"));
                    Map<String,Object> note = new HashMap<>();
                    String token=imageuri.toString();
                    note.put("name", newName);
                    note.put("price",newPrice);
                    note.put("imgurl",token);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Record Updated Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(EditActivity.this,MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
            image.setImageURI(imageuri);
        }
    }
}