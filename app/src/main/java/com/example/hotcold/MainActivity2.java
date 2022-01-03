package com.example.hotcold;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {
    MediaPlayer m;
    /*
    * The Second Screen of Application Is Create Account
    * Which Allow the New User To Create Account In Application
    * */
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Nodes> fetchedNodesFromServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fetchedNodesFromServer=new ArrayList<Nodes>();

        m=MediaPlayer.create(this,R.raw.win);
        m.start();
    }

    public void ExitGame(View view) {
        Intent backToLogin=new Intent(MainActivity2.this,MainActivity.class);
        startActivity(backToLogin);
    }
    public void RestartGame(View view) {
        FetchNodeAndStartGame();
    }
    public void FetchNodeAndStartGame(){
        fetchedNodesFromServer.clear();
        //Display Loading Dialog
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(MainActivity2.this);
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
                    MainActivity.nLat=randomElement.latitude;
                    MainActivity.nLong=randomElement.longtit;
                    loadingDialog.dismiss();
                    Intent moveToUserMaps=new Intent(MainActivity2.this,MapsActivity2.class);
                    startActivity(moveToUserMaps);
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