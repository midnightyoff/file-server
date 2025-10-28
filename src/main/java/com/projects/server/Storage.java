package com.projects.server;

import com.projects.common.Request;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class Storage {
    private final Path path = Path.of(System.getProperty("user.dir"), "src", "main", "java", "com", "projects", "server", "data");
    private ConcurrentHashMap<Integer, String> files;
    private final AtomicInteger fileID = new AtomicInteger(0);

    private static Storage instance = null;

    private Storage() {
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        loadFilesMap();
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public void saveFilesMap() {
        File file = path.resolve("filesMap").toFile();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(files);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadFilesMap() {
        File file = path.resolve("filesMap").toFile();
        if (file.exists()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                files = (ConcurrentHashMap<Integer, String>) objectInputStream.readObject();
                fileID.set(files.size());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            files = new ConcurrentHashMap<>();
        }
    }

    public File getFile(Request.ProcessFileBy processFileBy, String fileName) {
        if (processFileBy == Request.ProcessFileBy.BY_ID) {
            fileName = files.get(Integer.parseInt(fileName));
        }
        File file = path.resolve(fileName).toFile();
        if (file.exists() && file.isFile()){
            return file;
        }
        return null;
    }

    public File getFile(String fileName) {
        File file = path.resolve(fileName).toFile();
        if (!file.exists() && !file.isFile()){
            return file;
        }
        return null;
    }

    public void putFile(String fileName) {
        files.put(fileID.getAndIncrement(), fileName);
    }

    public boolean deleteFile(File file) {
        String valueToRemove = file.getName();
        if (file.exists() && file.isFile() && file.delete()) {
            Integer keyToRemove = null;
            for (Map.Entry<Integer, String> entry : files.entrySet()) {
                if (entry.getValue().equals(valueToRemove)) {
                    keyToRemove = entry.getKey();
                    break;
                }
            }
            if (keyToRemove != null) {
                files.remove(keyToRemove);
                return true;
            }
        }
        return false;
    }
}
