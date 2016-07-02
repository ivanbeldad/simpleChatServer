package com.rackian.listeners;

import com.rackian.models.User;

import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {

        ArrayList<User> users = new ArrayList<>();
        users.add(new User());
        Test test = new Test();

        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();

        users.add(new User());

    }

    private ArrayList<User> users;

    public Test() {
    }

    public Test(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    private void test() {

        ObjectChangeListener hostsChange = new ObjectChangeListener() {
            @Override
            public void namingExceptionThrown(NamingExceptionEvent evt) {

            }

            @Override
            public void objectChanged(NamingEvent evt) {
                System.out.println("Cambio!");
            }
        };

    }

}
