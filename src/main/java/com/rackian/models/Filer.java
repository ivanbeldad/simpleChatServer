package com.rackian.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Filer<T extends Serializable & Comparable> {

    private String location;

    public Filer() {
    }

    public Filer(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void save(T object) throws IOException {

        File file = new File(location);
        FileOutputStream fos;
        ObjectOutputStream oos;

        if (file.exists()) {
            fos = new FileOutputStream(file , true);
            oos = new ObjectOutputStreamNoHeader(fos);
        } else {
            fos = new FileOutputStream(file, true);
            oos = new ObjectOutputStream(fos);
        }

        oos.writeObject(object);

        fos.close();
        oos.close();

    }

    public void save(T ... objects) throws IOException {

        List<T> list = new ArrayList<>();

        for (T object : objects) {
            list.add(object);
        }

        save(list);

    }

    public void save(List<T> objects) throws IOException {

        File file = new File(location);
        FileOutputStream fos;
        ObjectOutputStream oos;

        if (!file.exists()) {
            fos = new FileOutputStream(file, true);
            oos = new ObjectOutputStream(fos);
        } else {
            fos = new FileOutputStream(file, true);
            oos = new ObjectOutputStreamNoHeader(fos);
        }

        for (T object : objects) {
            oos.writeObject(object);
        }

        fos.close();
        oos.close();

    }

    public T read(int position) throws IOException, ClassNotFoundException {

        if (!exists()) return null;

        T object = null;
        File file;
        FileInputStream fis;
        ObjectInputStream ois;

        file = new File(location);
        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        try {
            for (int i = 0; i <= position; i++) {
                if (i == position) {
                    object = (T) ois.readObject();
                } else {
                    ois.readObject();
                }
            }
        } catch (EOFException ex) {
        }

        ois.close();
        fis.close();
        return object;

    }

    public List<T> readAll() throws IOException, ClassNotFoundException {

        if (!exists()) return new ArrayList<>();

        List<T> objects;
        objects = new ArrayList<>();
        File file;
        FileInputStream fis;
        ObjectInputStream ois;

        file = new File(location);
        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);

        try {
            while (true) {
                objects.add((T) ois.readObject());
            }
        } catch (EOFException ex) {
            fis.close();
        }

        return objects;

    }

    public List<T> read(T comparableObject) throws IOException, ClassNotFoundException {

        if (!exists()) return new ArrayList<>();

        List<T> objects;
        objects = new ArrayList<>();
        List<T> objectsOnFile;
        objectsOnFile = readAll();

        for (T object : objectsOnFile) {
            if (object.compareTo(comparableObject) == 0) {
                objects.add(object);
            }
        }

        return objects;

    }

    public void update(T comparableObject) throws IOException, ClassNotFoundException {

        if (!exists()) return;

        if (read(comparableObject).get(0) != null) {
            remove(comparableObject);
            save(comparableObject);
        }

    }

    public void remove() {

        if (!exists()) return;

        File file = new File(location);
        file.delete();

    }

    public void remove(T comparableObject) throws IOException, ClassNotFoundException {

        if (!exists()) return;

        // LOAD FILE IN ARRAY AND DELETE ELEMENT FROM THIS
        List<T> elements;
        elements = readAll();

        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).compareTo(comparableObject) == 0) {
                elements.remove(i);
                i = elements.size();
            }
        }

        // SAVE THE NEW ARRAY

        File file = new File(location);
        file.delete();

        save(elements);

    }

    private boolean exists() {
        File file;
        file = new File(location);
        return file.exists();
    }

}

class ObjectOutputStreamNoHeader extends ObjectOutputStream {

    public ObjectOutputStreamNoHeader(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
    }

}
