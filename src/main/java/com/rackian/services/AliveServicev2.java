package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.Message;
import com.rackian.models.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.List;

public class AliveServicev2 implements Runnable {

    // FIELDS

    private String userFileLoc;
    private User user;
    private static int serverPort = 10000;


    // CONSTRUCTORS

    public AliveServicev2() {
        serverPort++;
    }

    public AliveServicev2(String userFileLoc, User user) {
        this.userFileLoc = userFileLoc;
        this.user = user;
        serverPort++;
    }

    // GETTER AND SETTERS

    public String getUserFileLoc() {
        return userFileLoc;
    }

    public void setUserFileLoc(String userFileLoc) {
        this.userFileLoc = userFileLoc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static int getServerPort() {
        return serverPort;
    }

    @Override
    public void run() {
        try {
            System.out.println("Servicio de comprobación de estado iniciado. Puerto: " + serverPort);
            checkPing();
        } catch (Exception e) {
        }
    }

    private void checkPing() throws IOException, ClassNotFoundException, InterruptedException {

        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);
        List<User> users;
        ServerSocket serverSocket;
        Socket socket;
        OutputStream os;
        ObjectOutputStream oos;

        // COMPRUEBO SI SE HA DESCONECTADO
        try {

            serverSocket = new ServerSocket(serverPort);
            serverSocket.setSoTimeout(3000);

            while (true) {

                synchronized (Main.FILE_USERS) {
                    users = filer.readAll();
                }

                socket = serverSocket.accept();

                if (!socket.getInetAddress().getHostAddress().equals(user.getIp())) {
                    socket.close();
                    break;
                }

                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);

                // WRITE USERS
                oos.writeObject(true);
                oos.writeObject(users);

                // WRITE MESSAGES
                List<Message> messages;
                messages = checkNewMessages();
                if (messages != null) {
                    oos.writeObject(true);
                    oos.writeObject(messages);
                } else {
                    oos.writeObject(false);
                }

                socket.close();
                System.out.println(user.getEmail() + ": Conectado.");

            }

        } catch (IOException ex) {
            user.setOnline(false);
            synchronized (Main.FILE_USERS) {
                filer.update(user);
            }
            System.out.println(user.getEmail() + ": Se ha desconectado.");
        }

    }

    private List<Message> checkNewMessages() throws IOException, ClassNotFoundException {

        List<Message> messages;
        Filer<Message> filer;
        filer = new Filer<>(Main.FILE_MESSAGES);
        synchronized (Main.FILE_MESSAGES) {
            messages = filer.readAll();
        }

        for (int i = 0; i < messages.size(); i++) {
            if (( messages.get(i).getUserDest().compareTo(user) != 0) ||
                    messages.get(i).getStatus() != Message.STATUS_SENT) {
                messages.remove(i);
                i--;
            }
        }

        if (messages.size() == 0) return null;

        for (int i = 0; i < messages.size(); i++) {
            System.out.println("MENSAJE ENVIADO a " + user.getEmail() + ": " + messages.get(i).getMessage());
            messages.get(i).setStatus(Message.STATUS_RECEIVED);
            synchronized (Main.FILE_MESSAGES) {
                filer.update(messages.get(i));
            }
        }

        return messages;

    }

}
