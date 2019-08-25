package com.kriiskrishna.instantx;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText userName,userPassword;
    private TextView header,subText,forgotPassword,modeSet;
    private TextView button;
    private int mode = 0;
    private String name,password;
    private FirebaseAuth mAuth;
    private String TAG = "Tag";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            startActivity(new Intent(getApplicationContext(),Home.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.LoginEditText1);
        userPassword = findViewById(R.id.LoginEditText2);
        header = findViewById(R.id.LoginText1);
        subText = findViewById(R.id.LoginText2);
        forgotPassword = findViewById(R.id.LoginText3);
        modeSet = findViewById(R.id.LoginText4);
        button = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();

        modeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == 0){
                    header.setText("Signin");
                    subText.setText("Signin to experience the app");
                    button.setText("Signin");
                    forgotPassword.setText("");
                    modeSet.setText("Already an user? Login");
                    mode = 1;
                }else{
                    header.setText("Login");
                    subText.setText("Login to experience the app");
                    button.setText("Login");
                    forgotPassword.setText("Forgot password?");
                    modeSet.setText("New user? Signin");
                    mode = 0;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = userName.getText().toString().trim();
                password = userPassword.getText().toString().trim();
                if(password.length() <= 6 && mode == 1){
                    userPassword.setHint("Password length must be greater than 6");
                    userPassword.setText("");
                }
                else if(name.isEmpty()||password.isEmpty()){
                    if(name.isEmpty()) userName.setHint("Username is empty");
                    else userName.setHint("Username");
                    if(password.isEmpty()) userPassword.setHint("Password is empty");
                    else userPassword.setHint("Password");
                }else{
                    if(mode == 0) LoginToApp(name,password);
                    else          SigninToApp(name,password);
                }
            }
        });

    }
    public void LoginToApp(String name,String Password){
        mAuth.signInWithEmailAndPassword(name, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(),Home.class));
                }else{
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplication(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void SigninToApp(final String name, String Password){
        mAuth.createUserWithEmailAndPassword(name, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    myRef = firebaseDatabase.getReference().child("Profile").child(mAuth.getUid());
                    myRef.child("gmail").setValue(name);
                    myRef.child("uid").setValue(mAuth.getUid());
                    finish();
                    startActivity(new Intent(getApplicationContext(),Home.class));
                }else{
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(getApplication(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
                myRef.keepSynced(true);
            }
        });
    }

}
