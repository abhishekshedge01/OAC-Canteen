package com.abhishek.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {

    TextView  tv1;
    Button btn;
    EditText ed1;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        getSupportActionBar().hide();
        firebaseAuth=FirebaseAuth.getInstance();
        tv1=findViewById(R.id.forgot_back_to_login);
        btn=findViewById(R.id.forgot_recover);
        ed1=findViewById(R.id.forgot_username);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Forgot.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = ed1.getText().toString();
                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(Forgot.this, "email field is empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(Forgot.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful()) {
                                String data=ed1.getText().toString();
                                Toast.makeText(Forgot.this, "Mail sent, You can Recover your password", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Forgot.this, Login.class);
                                intent.putExtra("dataname",data);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(Forgot.this, "Mail enter is wrong or not Registerd", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}