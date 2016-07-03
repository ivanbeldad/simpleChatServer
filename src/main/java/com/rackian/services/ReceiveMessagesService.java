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
        saveMessage(message);
        sendMessage(message);

        System.out.println("Mensaje recibido de " + message.getUserOri().getEmail() + "\n" +
         "Destinatario del mensaje: " + message.getUserDest().getEmail());

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

    private void sendMessage(Message message) {

        SendMessagesService sendMessageService;
        sendMessageService = new SendMessagesService();
        sendMessageService.setMessage(message);
        sendMessageService.setPort(Main.PORT_SEND_MESSAGES);
        Thread messageSender;
        messageSender = new Thread(sendMessageService);
        messageSender.start();

    }

}
