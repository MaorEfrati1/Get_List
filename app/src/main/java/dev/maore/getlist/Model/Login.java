package dev.maore.getlist.Model;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import dev.maore.getlist.R;

public class Login extends AppCompatActivity {
    //FireBase

    //Auth
    private FirebaseAuth fAuth;

    //Views
    private EditText editText_Email;
    private EditText editText_Password;
    private TextView textView_ForgotPassword;
    private MaterialButton btn_Login;
    private TextView textView_SignUp;
    private ProgressBar pB_Loading;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

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
            if (!Patterns.EMAIL_ADDRESS.matcher(editText_Email.getText()).matches()) {
                editText_Email.setError("Email Address Not Valid ");
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
                    finish();
                } else {
                    pB_Loading.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        //LogOut
        textView_ForgotPassword.setOnClickListener(v -> {
            final EditText editText_resetEmail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter Your Email To Received Reset Link");
            passwordResetDialog.setView(editText_resetEmail);

            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
            });

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                String email = editText_resetEmail.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Reset Link Sent to your Email", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, "Error! Reset Link Is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
            passwordResetDialog.create().show();

        });

        //Sign Up
        textView_SignUp.setOnClickListener(v1 -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });
    }

}