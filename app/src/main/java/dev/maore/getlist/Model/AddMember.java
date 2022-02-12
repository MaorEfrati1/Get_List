package dev.maore.getlist.Model;

import static dev.maore.getlist.RecycleView.ListAdapter.getGetListUidForEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


import dev.maore.getlist.R;

public class AddMember extends AppCompatActivity {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;
    //DataBase
    private FirebaseDatabase database;

    //Views
    private EditText addEmailOfMember;
    private SwitchMaterial permissionsReadOnly;
    private SwitchMaterial permissionsEdit;
    private Button AddMemberToList;

    //parameters
    private boolean editAble;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddMember.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();


        //View
        //Edit Text add Task
        addEmailOfMember = findViewById(R.id.addMember_Ev_AddMail);

        //Switch permissions ReadOnly
        permissionsReadOnly = findViewById(R.id.addMember_Switch_Read);
        permissionsReadOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                permissionsEdit.setChecked(false);
                editAble = false;
                toast("Member Can Read List");
            } else {
                permissionsEdit.setChecked(true);
            }
        });

        //Switch permissions Edit
        permissionsEdit = findViewById(R.id.addMember_Switch_Edit);
        permissionsEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                permissionsReadOnly.setChecked(false);
                editAble = true;
                toast("Member Can Edit List");
            } else {
                permissionsReadOnly.setChecked(true);

            }
        });

        //Btn Add Task
        AddMemberToList = findViewById(R.id.addMember_Btn_AddMember);
        AddMemberToList.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addEmailOfMember.getText())) {
                addEmailOfMember.setError("Add Task Details");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(addEmailOfMember.getText()).matches()){
                addEmailOfMember.setError("Email Address Not Valid ");
                return;
            }
            //check email already exist or not.
            String email = addEmailOfMember.getText().toString();
            fAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            toast("User Not Exist");
                        } else {
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("users")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            HashMap<String, Object> map1 = new HashMap<>();
                                            String str;
                                            boolean bool = false;
                                            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                                map1.put(dataSnap.getKey(), dataSnap.getValue());
                                                str = map1.values().toString();
                                                if (str.contains(email)) {
                                                    if (!bool) {
                                                        bool = true;
                                                        String[] temp = str.split(",");
                                                        for (String s : temp) {
                                                            if (s.contains("uid")) {
                                                                String userUid = s.substring(s.indexOf("=") + 1);
                                                                if (!userUid.equals(fAuth.getCurrentUser().getUid())) {
                                                                    addListToUserDb(userUid, getGetListUidForEdit());
                                                                    toast("List Added To \n" + email);
                                                                }else{
                                                                    toast("You Can't Add List To Yourself");
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    map1.remove(dataSnap.getKey());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                        }

                    });

        });


    }

    public void addListToUserDb(String userUid, String lid) {
        // add List to DB (/users/lists/)
        DatabaseReference listsRef = database.getReference("users");

        Lists lists = new Lists(lid)
                .setEditAble(editAble)
                .setLid(lid);

        //Add To Users Lists In DB
        if (lid != null) {
            listsRef.child(userUid).child("lists").child(lid).setValue(lists);
        }
    }

    public void toast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}