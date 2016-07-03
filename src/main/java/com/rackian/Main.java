package com.rackian;

import com.rackian.services.AliveService;
import com.rackian.services.LoginService;
import com.rackian.services.ReceiveMessagesService;
import com.rackian.services.RegisterService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static ExecutorService pool;

    public static final String FILE_USERS = "users.dat";
    public static final String FILE_MESSAGES = "messages.dat";

    public static final int PORT_LOGIN = 5000;
    public static final int PORT_REGISTER = 5001;
    public static final int PORT_ALIVE = 5002;
    public static final int PORT_RECEIVE_MESSAGES = 5003;
    public static final int PORT_SEND_MESSAGES = 5004;

    private static Runnable loginService;
    private static Runnable registerService;
    private static Runnable aliveService;
    private static Runnable receiveMessagesService;

    public static void main(String[] args) {

        pool = Executors.newCachedThreadPool();

        loginService = new LoginService(PORT_LOGIN, FILE_USERS);
        registerService = new RegisterService(PORT_REGISTER, FILE_USERS);
        aliveService = new AliveService(PORT_ALIVE, FILE_USERS);
        receiveMessagesService = new ReceiveMessagesService(PORT_RECEIVE_MESSAGES, FILE_MESSAGES);

        pool.execute(loginService);
        pool.execute(registerService);
        pool.execute(aliveService);
        pool.execute(receiveMessagesService);

        System.out.println("Servicios iniciados.");

    }

}
