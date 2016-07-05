package com.rackian.services;

import com.rackian.Main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

final public class AliveAcceptService implements Runnable {

    private static int port;
    private static List<AliveTimeOutService> services;

    private AliveAcceptService() {
        services = new ArrayList<>();
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        AliveAcceptService.port = port;
    }

    public static List<AliveTimeOutService> getServices() {
        return services;
    }

    public static void setServices(List<AliveTimeOutService> services) {
        AliveAcceptService.services = services;
    }

    @Override
    public void run() {

        listen();

    }

    private void listen() {

        ServerSocket serverSocket;
        Socket socket;

        try {
            while (true) {

                serverSocket = new ServerSocket(Main.PORT_ALIVE);
                socket = serverSocket.accept();

                resetTimeOut(socket.getInetAddress().getHostAddress());

                socket.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void resetTimeOut(String hostAddress) {

        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getUser().getIp().compareTo(hostAddress) == 0) {
                services.get(i).setTimeout(AliveTimeOutService.TIMEOUT_RESET);
                break;
            }
        }

    }

    public static void add (AliveTimeOutService service) {

        services.add(service);

    }

    public static void remove (AliveTimeOutService service) {

        services.remove(service);

    }

}
