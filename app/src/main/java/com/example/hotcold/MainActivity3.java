package com.example.hotcold;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity3 extends AppCompatActivity {

    public BootstrapEditText emailField,passwordField,nameField,phoneField;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public  void DefineMirrorObjectForEditText(){
        emailField=findViewById(R.id.signupemailTextField);
        passwordField=findViewById(R.id.signuppasswordTextField);
        nameField=findViewById(R.id.signupNameTextField);
        phoneField=findViewById(R.id.signupPhoneTextField);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
         database = FirebaseDatabase.getInstance();
         databaseReference = database.getReference();
        DefineMirrorObjectForEditText();
    }


    public void CreateNewAccount(View view) {
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(MainActivity3.this);
        loadingDialog.setMessage("Please Wait ... ");
        loadingDialog.show();
        // Define Variable To Store Text Field Value
        String userName,userEmail,userPass,userPhone;
        userName=nameField.getText().toString();
        userEmail=emailField.getText().toString();
        userPass=passwordField.getText().toString();
        userPhone=phoneField.getText().toString();

        //check if the required  data is completed
        if(userName.equals("") &&userEmail.equals("") || userPass.equals("") &&userPhone.equals("") ){
            loadingDialog.dismiss();
            Toast.makeText(MainActivity3.this, "All Fields Are Required ", Toast.LENGTH_LONG).show();
        }else{
            //Define New Object
              User newUserAccount=new User(userName,userEmail,userPhone,userPass,"User");
              databaseReference.child("User").push().setValue(newUserAccount);
              loadingDialog.dismiss();
              Toast.makeText(MainActivity3.this, "Now You Have Account ", Toast.LENGTH_LONG).show();
              finish();
        }

    }
}