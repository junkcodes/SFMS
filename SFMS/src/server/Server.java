package server;// Java implementation of server.Server side
// It contains two classes : server.Server and server.ClientHandler
// Save file as server.Server.java

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

// server.Server class
public class Server
{
    public static void main(String[] args) throws IOException {
        String mainPath = "/home/groot/IdeaProjects/SFMS/src/server";
        FileArrayProvider fileArrayProvider = new FileArrayProvider();
        List<String> username = fileArrayProvider.readLines(mainPath+"/login/username.txt");
        List<String> password = fileArrayProvider.readLines(mainPath+"/login/password.txt");
        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(5056);
        List<Thread> clients = new ArrayList<Thread>();
        // running infinite loop for getting
        // client request
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                ClientHandler client = new ClientHandler(s, dis, dos, username,password, mainPath);
                Thread t = client;

                // Invoking the start() method
                t.start();

            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
    public String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
}


// server.ClientHandler class
