package com.rackian.services;

import com.rackian.Main;
import com.rackian.models.Filer;
import com.rackian.models.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveMessagesService implements Runnable {

    // FIELDS

    private int port;
    private String messagesFileLoc;


    // CONSTRUCTORS

    public ReceiveMessagesService() {
    }

    public ReceiveMessagesService(int port, String messagesFileLoc) {
        this.port = port;
        this.messagesFileLoc = messagesFileLoc;
    }

    // GETTER AND SETTERS

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMessagesFileLoc() {
        return messagesFileLoc;
    }

    public void setMessagesFileLoc(String messagesFileLoc) {
        this.messagesFileLoc = messagesFileLoc;
    }

    @Override
    public void run() {
        System.out.println("Servicio de recepción de mensajes iniciado.");
        while (true) {
            try {
                listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void listen() throws Exception {

        Message message;
        message = receiptMessage();
        Message newMessage = new Message();
        newMessage.setUserOri(message.getUserOri());
        newMessage.setUserDest(message.getUserDest());
        newMessage.setStatus(message.getStatus());
        newMessage.setTime(message.getTime());
        newMessage.setMessage(message.getMessage());
        saveMessage(newMessage);

        System.out.println("Mensaje recibido de " + message.getUserOri().getEmail());
        System.out.println("Destinatario del mensaje: " + message.getUserDest().getEmail());

    }

    private Message receiptMessage() throws IOException, ClassNotFoundException {

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
        message.setStatus(Message.STATUS_SENT);

        ois.close();
        is.close();
        socket.close();
        serverSocket.close();

        System.out.println(message.getMessage());

        return message;

    }

    private void saveMessage(Message message) throws IOException {

        Filer<Message> filer;
        filer = new Filer<>(messagesFileLoc);

        synchronized (Main.FILE_MESSAGES) {
            filer.save(message);
        }

    }

}
