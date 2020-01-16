package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistationActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mPatientName,mEmail,mContactNo,mBirthdate,mAddress,mCity,mPassword;
    Button mRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        mPatientName = findViewById(R.id.patientName);
        mEmail=findViewById(R.id.emailIdrg);
        mContactNo=findViewById(R.id.contactNorg);
        mBirthdate=findViewById(R.id.birthIdrg);
        mAddress=findViewById(R.id.addressIdrg);
        mCity=findViewById(R.id.cityIdrg);
        mPassword=findViewById(R.id.passWordIdrg);

        fStore=FirebaseFirestore.getInstance();


        mRegister=findViewById(R.id.registerIdrg);

        fAuth=FirebaseAuth.getInstance();

        //if already registation
//        if (fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),Login_Activity.class));
//            finish();
//        }

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                final String patientName=mPatientName.getText().toString();
                final String contactNo=mContactNo.getText().toString();
                final String birthDate=mBirthdate.getText().toString();
                final String address=mAddress.getText().toString();
                final String city=mCity.getText().toString();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }
                if (password.length()<6){
                    mPassword.setError("Password Must be >= 6 Charecters");
                    return;
                }

                //Register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegistationActivity.this,"User Registered.",Toast.LENGTH_LONG).show();

                            //Store cloud Store

                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("users").document(userID);
                            Map<String, Object> user=new HashMap<>();
                            user.put("PatientName",patientName);
                            user.put("Email",email);
                            user.put("ContactNo",contactNo);
                            user.put("BirthDate",birthDate);
                            user.put("Address",address);
                            user.put("City",city);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"user profile is created for"+userID);
                                }
                            });

                            //go log in activity
                            Intent intent=new Intent(RegistationActivity.this,Login_Activity.class);
                            startActivity(intent);

                        }else {
                            Toast.makeText(RegistationActivity.this,"Error !"+ task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

    }
}
