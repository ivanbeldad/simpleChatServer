package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
        System.out.println("Servicio de comprobación de estado iniciado.");
        try {
            checkHosts();
        } catch (Exception e) {
        }
    }

    private void checkHosts() throws IOException, ClassNotFoundException, InterruptedException {

        boolean changes;
        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);
        Socket socket;
        OutputStream os;
        ObjectOutputStream oos;
        InetSocketAddress address;

        address = new InetSocketAddress(user.getIp(), Main.PORT_ALIVE);

        List<User> users;

        // COMPRUEBO SI SE HA DESCONECTADO
        try {
            while (true) {
                synchronized (Main.FILE_USERS) {
                    users = filer.readAll();
                }
                Thread.sleep(500);
                socket = new Socket();
                socket.connect(address, 500);
                System.out.println(user.getEmail() + ": Conectado.");

                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);
                oos.writeObject(true);
                oos.writeObject(users);

                socket.close();
            }
        } catch (IOException ex) {
            System.out.println(user.getEmail() + ": Se ha desconectado.");
            user.setOnline(false);
            synchronized (Main.FILE_USERS) {
                filer.update(user);
            }
        }

            /*
            // COMPRUEBO LOS QUE SE HAN DESCONECTADO
            for (int i = 0; i < users.size(); i++) {

                socket = new Socket();
                try {

                    socket.connect(address, 500);
                    System.out.println(users.get(i).getEmail() + ": Conectado.");

                    // LE PASO TODOS LOS USUARIOS
                    List<User> allUsers;
                    os = socket.getOutputStream();
                    oos = new ObjectOutputStream(os);
                    synchronized (Main.FILE_USERS) {
                        allUsers = filer.readAll();
                    }
                    oos.writeObject(true);
                    oos.writeObject(allUsers);

                } catch (IOException e) {
                    System.out.println(users.get(i).getEmail() + ": " + users.get(i).getIp() + ": Desconectado.");

                    User user = users.get(i);
                    user.setOnline(false);
                    users.remove(i);

                    synchronized (Main.FILE_USERS) {
                        filer.update(user);
                    }

                    i--;
                }
                socket.close();
            }
            */

    }

}
