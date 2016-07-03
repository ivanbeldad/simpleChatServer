package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendMessagesService implements Runnable {

    private int port;
    private Message message;

    public SendMessagesService() {
    }

    public SendMessagesService(int port, Message message) {
        this.port = port;
        this.message = message;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            send();
            System.out.println("Mensaje enviado a " + message.getUserDest().getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void send() throws IOException {

        Socket socket;
        InetSocketAddress address;
        OutputStream os;
        ObjectOutputStream oos;

        address = new InetSocketAddress(message.getUserDest().getIp(), port);
        socket = new Socket();
        socket.connect(address, 1000);
        os = socket.getOutputStream();
        oos = new ObjectOutputStream(os);
        oos.writeObject(message);

        socket.close();

    }

}
