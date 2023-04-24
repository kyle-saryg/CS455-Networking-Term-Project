package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createRoom = (Button) findViewById(R.id.button);
        createRoom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                Intent createRoom = new Intent(this, GameActivity.class);
                startActivity(createRoom);
                break;
            case R.id.button3:
                Intent joinRoom = new Intent(this, GameActivity.class);
                startActivity(joinRoom);
                break;
        }
    }

    //This function is called when joinRoom is selected
    public boolean clientConnect(){
        //Attempt to connect to server
        //Wait for server to respond
        //Let player know we are in a room
        //Wait for server to send game start info
        //Call clientLoop

        int timeOut = 30;                                           //Time to wait until we give up connecting to server
        int waitTime = 1;                                           //How long to wait while trying to connect
        int timer = 0;                                              //Variable to hold how long we've waited
        String receivedData;                                        //String to store data received from server

        //Create the JSON file we send to the server
        JsonObject clientConnectJSON = JSon.createObjectBuilder()   
            .add("client", Json.createObjectBuilder()
                .add("connectionType", "joinRoom")
                .add("response", "null"))
            .build();
        //sendToServer(clientConnectJSON.toString)                  //Send the JSON to the server as a string
        System.out.println("Connecting to server\n");             //Let the user know we're trying to connect
        receivedData = "false";                                     //Set recveivedData to false to start the while loop
        //Loop until we have either timed out or received data from the server
        while(receivedData=="false"){                               //If we haven't received any data               
            receivedData = receiveFromServer();                     //Fetch data from the socket
            System.out.println(".");
            TimeUnit.SECONDS.sleep(waitTime);                       //Wait for waitTime 
            timer = timer + waitTime;                               //Increase how long we've waited for       
            if (timer > timeOut){                                   //Check to see if we've timed out
                println("\n[ERROR] Could not connect to server - Timed Out\n");
                return false;
            }
        }
        println("Connected to server\nWaiting for game to start...\n");
        receivedData = "false";

        //Loop until we receive data from the server
        while(receivedData=="false"){                               //If we haven't received any data
            receivedData = receiveFromServer();                     //Fetch data from the socket
            TimeUnit.SECONDS.sleep(waitTime);                       //Wait for waitTime
        }
        //Convert the data received from the server back to JSON
        //JSONParser parser = new JSONParser();
        //JSONObject serverData = (JSONObject) parser.parse(receivedData);
        try{
            JsonObject serverData = JSON.parse(receivedData);
        }catch (Exception e){
            System.out.println("[ERROR] Malformed server data - Invalid JSON\n");
        }
        //Parse the JSON into variables
        if (serverData["Question"] == "start"){                     //If the server is telling us to start
            System.out.println("Game is starting...\n");          //Alert the user the game is starting   
            return true;                                            //Return true (Advance to game state) 
        }
        System.out.println("[ERROR] Failed to start game - " + serverData["Question"]);
        return false;

    }
}