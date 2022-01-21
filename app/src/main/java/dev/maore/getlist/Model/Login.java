package dev.maore.getlist.Model;

import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.maore.getlist.R;

public class Login extends AppCompatActivity {
    //FireBase

    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //Views
    private EditText editText_Email;
    private EditText editText_Password;
    private TextView textView_ForgotPassword;
    private Button btn_Login;
    private TextView textView_SignUp;
    private ProgressBar pB_Loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //database
        database = FirebaseDatabase.getInstance();


        //Views
        editText_Email = findViewById(R.id.login_Et_Email);
        editText_Password = findViewById(R.id.login_Et_Password);

        textView_ForgotPassword = findViewById(R.id.login_Tv_ForgotPassword);

        btn_Login = findViewById(R.id.login_Btn_Login);

        textView_SignUp = findViewById(R.id.login_Btn_SignUp);

        pB_Loading = findViewById(R.id.login_Pb_Loading);


        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        btn_Login.setOnClickListener(v -> {

            String email = editText_Email.getText().toString().trim();
            String password = editText_Password.getText().toString().trim();

            // fields validation
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

            //login to user account
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {



                    Toast.makeText(Login.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainActivity.class));
                } else {
                    pB_Loading.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        //Forgot Password
        textView_ForgotPassword.setOnClickListener(v -> {
            final EditText editText_resetEmail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter your Email To Received Reset Link");
            passwordResetDialog.setView(editText_resetEmail);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                String email = editText_resetEmail.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Reset Link Sent to your Email", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, "Error! Reset Link Is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
            });
            passwordResetDialog.create().show();
        });

        //Sign Up
        textView_SignUp.setOnClickListener(v1 -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    //update user data from auth to DB
    public void updateUserPasswordAfterForgot(String password) {
        if (fAuth.getCurrentUser() != null) {
            String userUid = fAuth.getCurrentUser().getUid();

            // Read from the database
            DatabaseReference userRef = database.getReference("users");

            userRef.child(userUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(Login.this, "Error - Cannot Read User From Database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}