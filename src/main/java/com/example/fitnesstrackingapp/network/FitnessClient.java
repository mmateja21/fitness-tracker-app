package com.example.fitnesstrackingapp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FitnessClient {

    private static final String SERVER_HOST = "localhost";

    private static final int SERVER_PORT = 5555;

    static void main() {
        try(
                Socket socket =
                        new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
                ){
            System.out.println("Klijent je povezan sa serverom. ");
            writer.println("PING");

            String response = reader.readLine();
            System.out.println("Odgovor servera: "+ response);
        }catch (IOException exception){
            System.err.println("Povezivanje nije uspelo: "+
                    exception.getMessage());
        }
    }
}
