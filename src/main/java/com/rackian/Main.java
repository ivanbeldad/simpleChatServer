package com.rackian;

import com.rackian.services.AliveService;
import com.rackian.services.LoginService;
import com.rackian.services.RegisterService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static ExecutorService pool;

    public static final String FILE_USERS = "users.dat";

    public static int PORT_LOGIN = 5000;
    public static int PORT_REGISTER = 5001;
    public static int PORT_ALIVE = 5002;

    private static Runnable loginService;
    private static Runnable registerService;
    private static Runnable aliveService;

    public static void main(String[] args) {

        pool = Executors.newCachedThreadPool();

        loginService = new LoginService(PORT_LOGIN, FILE_USERS);
        registerService = new RegisterService(PORT_REGISTER, FILE_USERS);
        aliveService = new AliveService(PORT_ALIVE, FILE_USERS);

        pool.execute(loginService);
        pool.execute(registerService);
        pool.execute(aliveService);

        System.out.println("Servicios iniciados.");

    }

}
