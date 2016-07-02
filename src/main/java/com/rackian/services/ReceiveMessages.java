package com.rackian.services;

import com.rackian.models.Message;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiveMessages implements Runnable {

    // FIELDS

    private int port;
    private ArrayList<Message> messages;


    // CONSTRUCTORS

    public ReceiveMessages() {
    }

    public ReceiveMessages(int port, ArrayList<Message> messages) {
        this.port = port;
        this.messages = messages;
    }


    // GETTER AND SETTERS

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    @Override
    public void run() {
        while (true) {
            try {
                listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void listen() throws Exception {

        ServerSocket serverSocket;
        Socket socket;
        InputStream is;
        ObjectInputStream ois;
        Message message;

        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        is = socket.getInputStream();
        ois = new ObjectInputStream(is);

        message = (Message) ois.readObject();

        ois.close();
        is.close();
        socket.close();
        serverSocket.close();

        System.out.println(message.getMessage());

    }

}
