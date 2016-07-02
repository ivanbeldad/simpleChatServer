package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.User;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AliveService implements Runnable {

    // FIELDS

    private int port;
    private String userFileLoc;


    // CONSTRUCTORS

    public AliveService() {
    }

    public AliveService(int port, String userFileLoc) {
        this.port = port;
        this.userFileLoc = userFileLoc;
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

    @Override
    public void run() {
        System.out.println("Servicio de comprobación de estado iniciado.");
        while (true) {
            try {
                checkHosts();
            } catch (Exception e) {
            }
        }
    }

    private void checkHosts() throws IOException, ClassNotFoundException, InterruptedException {

        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);

        List<User> users;

        while (true) {

            synchronized (Main.FILE_USERS) {
                users = filer.readAll();
            }

            // ELIMINO LOS QUE NO ESTAN ONLINE DE LA COMPROBACION
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).isOnline()) {
                    users.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < users.size(); i++) {

                Socket socket;
                InetSocketAddress address;

                socket = new Socket();
                address = new InetSocketAddress(users.get(i).getIp(), Main.PORT_ALIVE);

                try {

                    socket.connect(address, 1000);
                    System.out.println(users.get(i).getEmail() + ": Conectado.");
                    socket.close();

                } catch (IOException e) {

                    System.out.println(users.get(i).getEmail() + ": Desconectado. Cambiando su estado a no conectado.");

                    User user = users.get(i);
                    user.setOnline(false);
                    users.remove(i);

                    synchronized (Main.FILE_USERS) {
                        filer.update(user);
                    }

                    i--;

                }


            }

            Thread.sleep(3000);

        }

    }

}
