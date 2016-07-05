package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class AliveServicev2 implements Runnable {

    // FIELDS

    private int port;
    private String userFileLoc;
    private User user;


    // CONSTRUCTORS

    public AliveServicev2() {
    }

    public AliveServicev2(int port, String userFileLoc, User user) {
        this.port = port;
        this.userFileLoc = userFileLoc;
        this.user = user;
    }

    // GETTER AND SETTERS

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    @Override
    public void run() {
        try {
            System.out.println("Servicio de comprobación de estado iniciado.");
            checkHosts();
        } catch (Exception e) {
        }
    }

    private void checkHosts() throws IOException, ClassNotFoundException, InterruptedException {

        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);
        List<User> users;
        ServerSocket serverSocket;
        Socket socket;
        OutputStream os;
        ObjectOutputStream oos;

        serverSocket = new ServerSocket(Main.PORT_ALIVE);
        serverSocket.setSoTimeout(3000);

        // COMPRUEBO SI SE HA DESCONECTADO
        try {
            while (true) {

                synchronized (Main.FILE_USERS) {
                    users = filer.readAll();
                }

                socket = serverSocket.accept();
                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);
                oos.writeObject(true);
                oos.writeObject(users);
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

}
