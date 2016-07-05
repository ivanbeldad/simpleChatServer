package com.rackian.services;

import com.rackian.models.User;

public class AliveTimeOutService implements Runnable {

    public static final int TIMEOUT_RESET = 3000;

    private User user;
    private int timeout;

    public AliveTimeOutService() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public synchronized int getTimeout() {
        return timeout;
    }

    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void run() {

        try {
            AliveAcceptService.add(this);
            Thread.sleep(1000);
            while (timeout > 0) {
                synchronized (this) {
                    timeout -= 100;
                }
                Thread.sleep(100);
            }

            AliveAcceptService.remove(this);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
