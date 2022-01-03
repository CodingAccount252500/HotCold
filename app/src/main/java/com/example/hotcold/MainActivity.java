package com.example.hotcold;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public BootstrapEditText emailField,passwordField;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Nodes> fetchedNodesFromServer;
    public static double nLat=0.0,nLong=0.0;
    public  void DefineMirrorObjectForEditText(){
        emailField=findViewById(R.id.loginEmailTextField);
        passwordField=findViewById(R.id.loginPasswordTextField);
        fetchedNodesFromServer=new ArrayList<Nodes>();
    }
    /*
    * The First Screen of Application Is The Login Page
    * Where The Users / Admin Can Access There's Accounts
    * Depands On Role Type In Application
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DefineMirrorObjectForEditText();

    }

    public void MoveToCreateAccount(View view) {
        Intent moveToCreateAccountScreen=new Intent(MainActivity.this,MainActivity3.class);
        startActivity(moveToCreateAccountScreen);
    }

    public void FetchNodeAndStartGame(){
        fetchedNodesFromServer.clear();
        //Display Loading Dialog
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setMessage("Please Wait ... ");
        loadingDialog.show();
        //Fetch Nodes From Server
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Nodes");

        final Query gameQuery = ref;
        gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for (DataSnapshot node : snapshot.getChildren()) {
                        Nodes fetchedNodes = node.getValue(Nodes.class);
                        fetchedNodesFromServer.add(new Nodes(fetchedNodes.nodeLabel,fetchedNodes.latitude,fetchedNodes.longtit));
                    }
                    //Select One of them Randomly
                    Random rand = new Random();
                    Nodes randomElement = fetchedNodesFromServer.get(rand.nextInt(fetchedNodesFromServer.size()));
                    nLat=randomElement.latitude;
                    nLong=randomElement.longtit;
                    Intent moveToUserMaps=new Intent(getApplicationContext(),MapsActivity2.class);
                    startActivity(moveToUserMaps);
                    loadingDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }



    public void LoginToApplicaion(View view) {
        //check if all required data is exist
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setMessage("Please Wait ... ");
        loadingDialog.show();
        if(emailField.getText().toString().equals("") || passwordField.getText().toString().equals("")){
            loadingDialog.dismiss();
            Toast.makeText(MainActivity.this, "All Fields Are Required ", Toast.LENGTH_LONG).show();
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
            //final Query gameQuery = ref.orderByChild("email").equalTo(emailField.getText().toString());
            final Query gameQuery = ref;
            gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        for (DataSnapshot user : snapshot.getChildren()) {
                            User fetchedUsers = user.getValue(User.class);
                            if (fetchedUsers.email.equals(emailField.getText().toString())&&fetchedUsers.password.equals(passwordField.getText().toString())) {
                                //user.getKey()
                                if(fetchedUsers.roleType.equals("Admin")){

                                    loadingDialog.dismiss();
                                    Intent moveToAdminMaps=new Intent(getApplicationContext(),MapsActivity.class);
                                    startActivity(moveToAdminMaps);

                                }else{
                                    loadingDialog.dismiss();


                                    FetchNodeAndStartGame();

                                }
                            }else if(fetchedUsers.email.equals(emailField.getText().toString())&&!(fetchedUsers.password.equals(passwordField.getText().toString()))){
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Login Failed Check Your Information", Toast.LENGTH_SHORT).show();
                            }else{
                                loadingDialog.dismiss();
                                //Toast.makeText(getApplicationContext(), "Login Failed Try Again Please", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}