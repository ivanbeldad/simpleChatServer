package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.Message;
import com.rackian.models.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void send() throws IOException, ClassNotFoundException {

        if (!available(message)) return;

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

    private boolean available(Message message) throws IOException, ClassNotFoundException {

        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);
        List<User> users;

        synchronized (Main.FILE_USERS) {
            users = filer.readAll();
        }

        for (User user : users) {
            if (message.getUserDest().compareTo(user) == 0) {
                return user.isOnline();
            }
        }

        return false;

    }

}
