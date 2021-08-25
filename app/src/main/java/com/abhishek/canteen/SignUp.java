package com.abhishek.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name_et,regid_et,email_et,password_et;
    TextView login_tv,DOB;
    Button register_btn;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    ArrayAdapter <CharSequence> adapter;
    Spinner spinner;
    String status;
    int day, month, year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        name_et=findViewById(R.id.signup_name);
        regid_et=findViewById(R.id.signup_regid);
        email_et=findViewById(R.id.signup_email);
        password_et=findViewById(R.id.signup_password);
        firebaseAuth=FirebaseAuth.getInstance();
        login_tv=findViewById(R.id.signup_login);
        register_btn=findViewById(R.id.signup_register);
        progressBar=findViewById(R.id.signup_pgbar);
        spinner=findViewById(R.id.signup_spinner);

        adapter= ArrayAdapter.createFromResource(this,R.array.relation,R.layout.spinner_box);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                status=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(SignUp.this,Login.class);
                startActivity(intent);
                finish();
            }
        });


        DOB = findViewById(R.id.signup_dob);
        Calendar calendar = Calendar.getInstance();
        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(SignUp.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        date = dayOfMonth + " / " + month + " / " +  year;
                        DOB.setText(date);
                    }
                }, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

    }

    private void Register()
    {
        String name=name_et.getText().toString().trim();
        String registrationID=regid_et.getText().toString().trim();
        String email=email_et.getText().toString();
        String password=password_et.getText().toString();

        if(name.length()==0 || name==null)
        {
            name_et.setError("Name of User is Empty");
            return;
        }

        if(registrationID.length()==0 || registrationID==null)
        {
            regid_et.setError("Reg Id of User is Empty");
            return;
        }

        if(email.length()==0 || email==null)
        {
            email_et.setError("Email of User is Empty");
            return;
        }

        if(!vaildEmail(email))
        {
            email_et.setError("Invaild email");
            return;
        }

        if(password.length()<8 || password==null)
        {
            password_et.setError("Password of User is Empty");
            return;
        }

        if(password.length()>=8)
        {
            int flag=0;
            for(int i=0;i<password.length();++i)
            {
                if((password.charAt(i)>='a' && password.charAt(i)<='z') || (password.charAt(i)>='A' && password.charAt(i)<='Z'))
                {
                    flag=1;
                    break;
                }
            }
            if(flag==0)
            {
                password_et.setError("Alpha-numeric character missing");
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        register_btn.setVisibility(View.INVISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    if(email=="abhishedge01@gmail.com" && password=="abhishek1")
                    {
                        Toast.makeText(getApplicationContext(),"Successfully created the Admin Account",Toast.LENGTH_LONG).show();
                        String Uid=firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=firebaseFirestore.collection("admin").document(Uid).collection("admininfo").document();
                        Map <String,Object> users=new HashMap<>();
                        users.put("Name",name);
                        users.put("RegisterationId",registrationID);
                        users.put("Email",email);
                        users.put("Password",password);
                        users.put("DOB",date);
                        users.put("Relation", status);
                        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(getApplicationContext(),"Successfully created the Admin Account",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Successfully created the User Account", Toast.LENGTH_LONG).show();
                        String Uid = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firebaseFirestore.collection("user").document(Uid).collection("userinfo").document();
                        Map<String, Object> users = new HashMap<>();
                        users.put("Name", name);
                        users.put("RegisterationId", registrationID);
                        users.put("Email", email);
                        users.put("Password", password);
                        users.put("DOB", date);
                        users.put("Relation", status);
                        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"credentials already existing",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
                register_btn.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean vaildEmail(String target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}