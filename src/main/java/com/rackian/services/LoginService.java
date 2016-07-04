package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.Message;
import com.rackian.models.User;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoginService implements Runnable {

    // FIELDS

    private int port;
    private String userFileLoc;

    // CONSTRUCTORS

    public LoginService() {
    }

    public LoginService(int port, String userFileLoc) {
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
        try {
            listen();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void listen() throws Exception {

        ServerSocket serverSocket;
        Socket socket;
        InputStream is;
        OutputStream os;
        ObjectInputStream ois;
        ObjectOutputStream oos;
        User user;

        serverSocket = new ServerSocket(port);
        System.out.println("Servicio de login iniciado.");

        while (true) {

            socket = serverSocket.accept();
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);

            user = (User) ois.readObject();

            if (checkLogin(user)) {

                if (!isOnline(user.getEmail())) {
                    // GUARDO EL USUARIO
                    user = saveUser(user.getEmail(), socket);

                    // CONFIRMO INICIO
                    oos.writeObject(true);

                    // WRITE USER
                    oos.writeObject(user);
                    System.out.println("Inicio de sesión con éxito.");
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Ip: " + user.getIp());

                    // WRITE CONTACTS
                    oos.writeObject(getAllContacts());

                    // WRITE MESSAGES
                    oos.writeObject(loadMessages(user));

                    // START ALIVESERVICE
                    AliveServicev2 aliveServicev2;
                    aliveServicev2 = new AliveServicev2();
                    aliveServicev2.setPort(Main.PORT_ALIVE);
                    aliveServicev2.setUserFileLoc(Main.FILE_USERS);
                    aliveServicev2.setUser(user);
                    Main.pool.execute(aliveServicev2);
                } else {
                    // YA ESTABA LOGUEADO, DESCONECTESE ANTES COÑO
                    oos.writeObject(false);
                    System.out.println("Intento de inicio de sesión. Usuario ya conectado en otra máquina.");
                }

            } else {
                // CONFIRMO ERROR
                oos.writeObject(false);
                System.out.println("Intento de inicio de sesión no autorizado.");
            }

            socket.close();

        }

    }

    private boolean checkLogin(User user) throws IOException, ClassNotFoundException {

        User userLoaded;
        Filer<User> filer;
        filer = new Filer<>(userFileLoc);

        synchronized (Main.FILE_USERS) {

            if (filer.read(user).size() > 0) {
                userLoaded = filer.read(user).get(0);
                if (userLoaded.getEmail().equals(user.getEmail()) &&
                        userLoaded.getPassword().equals(user.getPassword())) {
                    return true;
                }
            }

        }

        return false;

    }

    private User saveUser(String email, Socket socket) throws IOException, ClassNotFoundException {

        User user;
        user = new User();
        user.setEmail(email);

        Filer<User> filer;
        filer = new Filer<>(userFileLoc);

        synchronized (Main.FILE_USERS) {

            if (filer.read(user).size() != 0) {
                // SI EXISTE LO CARGO Y ELIMINO DEL ARCHIVO
                user = filer.read(user).get(0);
                filer.remove(user);
            } else {
                // SI NO EXISTE LE ASIGNO UN NOMBRE DE USUARIO
                user.setNick("Usuario");
            }

            user.setIp(socket.getInetAddress().getHostAddress());
            user.setOnline(true);

            // GUARDO LA INFORMACION EN EL ARCHIVO
            filer.save(user);

            filer.readAll().forEach(e -> System.out.println(e.getEmail() + ": " + e.isOnline()));

        }

        return user;

    }

    private boolean isOnline(String email) throws IOException, ClassNotFoundException {

        User user;
        user = new User();
        user.setEmail(email);

        Filer<User> filer;
        filer = new Filer<>(userFileLoc);

        synchronized (Main.FILE_USERS) {

            filer.readAll().forEach(e -> System.out.println(e.getEmail()));

            if (filer.read(user).size() > 0) {
                return filer.read(user).get(0).isOnline();
            }

        }

        return false;

    }

    private List<Message> loadMessages(User user) throws IOException, ClassNotFoundException {

        Filer<Message> filer;
        filer = new Filer<>(Main.FILE_MESSAGES);
        List<Message> messages;

        synchronized (Main.FILE_MESSAGES) {
            messages = filer.readAll();
        }

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getUserOri().compareTo(user) != 0 &&
                    messages.get(i).getUserDest().compareTo(user) != 0) {
                messages.remove(i);
                i--;
            }
        }

        return messages;

    }

    private List<User> getAllContacts() throws IOException, ClassNotFoundException {

        List<User> users;
        Filer<User> filer;
        filer = new Filer<>(Main.FILE_USERS);

        synchronized (Main.FILE_USERS) {
            users = filer.readAll();
        }

        return users;

    }

}
