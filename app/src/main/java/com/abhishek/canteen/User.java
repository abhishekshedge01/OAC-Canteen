package com.abhishek.canteen;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class User extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    private String imgtoken;
    FloatingActionButton fltact;
    Button logoutfltact;

    private ImageView nimg;
    RecyclerView mrecyclerview;
    GridLayoutManager gridLayoutManager;
    FirestoreRecyclerAdapter <model,viewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mrecyclerview=findViewById(R.id.mainactivity_recycler);
        firebaseUser=firebaseAuth.getCurrentUser();
        storageReference=firebaseStorage.getReference();
        storageReference=firebaseStorage.getReference();



        fltact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(User.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Query query=firebaseFirestore.collection("admin").document(firebaseUser.getUid()).collection("iteminfo").orderBy("name",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<model> itemdetails=new FirestoreRecyclerOptions.Builder<model>().setQuery(query,model.class).build();
        noteAdapter=new FirestoreRecyclerAdapter<model, viewHolder>(itemdetails) {
            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull model model)
            {
                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();
                holder.nname.setText(model.getName());
                holder.nprice.setText(model.getPrice());
                String uri=model.getImgurl();
                Picasso.get().load(uri).into(nimg);
            }

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_layout,parent,false);
                return new viewHolder(view);
            }
        };




        mrecyclerview.setHasFixedSize(true);
        gridLayoutManager=new GridLayoutManager(this,2);
        mrecyclerview.setLayoutManager(gridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private TextView nname;
        private TextView nprice;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            nname=itemView.findViewById(R.id.dashboard_itemname);
            nprice=itemView.findViewById(R.id.dashboard_itemprice);
            nimg=itemView.findViewById(R.id.foodimg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(getApplicationContext(), "Settings up", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
                return true;

            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null)
        {
            noteAdapter.stopListening();
        }
    }
}