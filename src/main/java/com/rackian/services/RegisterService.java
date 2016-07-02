package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class RegisterService implements Runnable {

    // FIELDS

    private int port;
    private String userFileLoc;

    public RegisterService() {
    }

    public RegisterService(int port, String userFileLoc) {
        this.port = port;
        this.userFileLoc = userFileLoc;
    }

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
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws IOException {

        User user;

        ServerSocket serverSocket;
        Socket socket;
        InputStream is;
        OutputStream os;
        ObjectInputStream ois;
        ObjectOutputStream oos;

        serverSocket = new ServerSocket(port);
        System.out.println("Servicio de registro iniciado.");

        while (true) {
            try {

                socket = serverSocket.accept();
                is = socket.getInputStream();
                ois = new ObjectInputStream(is);
                os = socket.getOutputStream();
                oos = new ObjectOutputStream(os);

                user = (User) ois.readObject();

                if (available(user)) {
                    oos.writeObject(true);
                    // GUARDO LA INFORMACION DEL USUARIO
                    saveUser(user);
                    System.out.println("Nuevo usuario registrado: " + user.getEmail());
                } else {
                    oos.writeObject(false);
                    System.out.println("Intento de registro de usuario existente: " + user.getEmail());
                }

                socket.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private boolean available(User user) throws IOException, ClassNotFoundException {

        Filer<User> filer;
        filer = new Filer<>(userFileLoc);
        List<User> users;

        synchronized (Main.FILE_USERS) {
            users = filer.readAll();
        }

        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
                return false;
            }
        }

        return true;

    }

    private void saveUser(User user) throws IOException {

        User newUser;
        newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setNick(user.getNick());
        newUser.setPassword(user.getPassword());
        newUser.setOnline(false);
        newUser.setIp("");

        Filer<User> filer;
        filer = new Filer<>(userFileLoc);

        synchronized (Main.FILE_USERS) {
            filer.save(newUser);
        }

    }

}
