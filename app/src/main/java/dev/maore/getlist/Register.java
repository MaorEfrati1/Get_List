package dev.maore.getlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    //FireBase

    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //Views
    private EditText editText_FirstName;
    private EditText editText_LastName;
    private EditText editText_Email;
    private EditText editText_Password;
    private Button btn_register;
    private TextView textView_SignIn;
    private ProgressBar pB_Loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //database
        database = FirebaseDatabase.getInstance();


        //Views
        editText_FirstName = findViewById(R.id.register_Et_FirstName);
        editText_LastName = findViewById(R.id.register_Et_LastName);
        editText_Email = findViewById(R.id.register_Et_Email);
        editText_Password = findViewById(R.id.register_Et_Password);

        btn_register = findViewById(R.id.register_Btn_Register);

        textView_SignIn = findViewById(R.id.register_Tv_SignIn);

        pB_Loading = findViewById(R.id.register_Pb_Loading);

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        }

        btn_register.setOnClickListener(v -> {
            String firstName = editText_FirstName.getText().toString().trim();
            String lastName = editText_LastName.getText().toString().trim();
            String email = editText_Email.getText().toString().trim();
            String password = editText_Password.getText().toString().trim();

            // fields validation
            if (TextUtils.isEmpty(firstName)) {
                editText_FirstName.setError("First Name Is Required");
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                editText_LastName.setError("Last Name Is Required");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                editText_Email.setError("Email Name Is Required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                editText_Password.setError("Password Is Required");
                return;
            }
            if (password.length() < 8) {
                editText_Password.setError("Password Must Be 8 or More Characters");
                return;
            }

            pB_Loading.setVisibility(View.VISIBLE);

            // Create user to FireBase
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    //New User
                    if (fAuth.getCurrentUser() != null) {
                        User user = new User(fAuth.getCurrentUser().getUid())
                                .setFirstName(firstName)
                                .setLastName(lastName)
                                .setEmail(email)
                                .setPassword(password);

                        //Add User To DB
                        DatabaseReference userRef = database.getReference("users");
                        userRef.child(user.getUid()).setValue(user);

                    } else {
                        Toast.makeText(this, "Error Add User To DB", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(Register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                } else {
                    pB_Loading.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            });

        });

        //Sign In
        textView_SignIn.setOnClickListener(v1 -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

    }

}