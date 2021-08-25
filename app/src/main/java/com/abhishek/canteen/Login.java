package com.abhishek.canteen;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText username_et,password_et;
    TextView signup_tv,forgot_tv;
    Button login_btn;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username_et=findViewById(R.id.login_user);
        login_btn=findViewById(R.id.login_login);
        forgot_tv=findViewById(R.id.login_forgot);
        password_et=findViewById(R.id.login_pwd);
        signup_tv=findViewById(R.id.login_signup);
        firebaseAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.login_pgbar);

        Intent x=getIntent();
        String update_username=x.getStringExtra("dataname");
        username_et.setText(update_username);

        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        forgot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Forgot.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void Login()
    {
        String username=username_et.getText().toString().trim();
        String password=password_et.getText().toString();

        if(username.length()==0 || username==null)
        {
            username_et.setError("Username is empty");
            return;
        }

        if(password.length()==0 || password==null)
        {
            password_et.setError("Password is empty");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        login_btn.setVisibility(View.INVISIBLE);
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        if(username=="abhishedge01@gmail.com" && password=="abhishek1")
                        {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.INVISIBLE);
                            login_btn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), User.class);
                            startActivity(intent);
                            finish();
                            progressBar.setVisibility(View.INVISIBLE);
                            login_btn.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        username_et.setText("");
                        password_et.setText("");
                        progressBar.setVisibility(View.INVISIBLE);
                        login_btn.setVisibility(View.VISIBLE);
                    }
                }
            });


        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }
}