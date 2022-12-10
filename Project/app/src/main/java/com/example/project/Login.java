package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
   Button button;
   EditText uText;
   EditText pText;
   FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        button = findViewById(R.id.button);
        uText = findViewById(R.id.editTextTextPersonName);
        pText = findViewById(R.id.editTextTextPassword);

        //firebase authentication
        auth = FirebaseAuth.getInstance();

    }
    public void onClick(View view) {
        String email = uText.getText().toString().trim();
        String pword = pText.getText().toString().trim();
        //if the pattern matches from the firebase then allow into main app
        auth.signInWithEmailAndPassword(email,pword).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
               sendMain();
            }
            else
            {
                Toast toast =  Toast.makeText(getApplicationContext(), "Retry", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }
    public void sendMain(){
        //this method send the user to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}