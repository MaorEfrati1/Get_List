package dev.maore.getlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //Views
    private Button btn_LogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Database
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

        //Views
        btn_LogOut = findViewById(R.id.button2);



        btn_LogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,Login.class));
            finish();
        });

    }


}