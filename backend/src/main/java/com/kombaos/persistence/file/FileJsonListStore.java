package com.kombaos.persistence.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FileJsonListStore<T> {

    private final ObjectMapper objectMapper;
    private final Path storageFile;
    private final TypeReference<List<T>> type;
    private final ReentrantLock lock = new ReentrantLock();

    public FileJsonListStore(ObjectMapper objectMapper, Path storageFile, TypeReference<List<T>> type) {
        this.objectMapper = objectMapper;
        this.storageFile = storageFile;
        this.type = type;
    }

    public <R> R withLock(java.util.function.Supplier<R> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    public void withLock(Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    public List<T> readAll() {
        try {
            if (!Files.exists(storageFile)) {
                return new ArrayList<>();
            }
            byte[] json = Files.readAllBytes(storageFile);
            if (json.length == 0) {
                return new ArrayList<>();
            }
            return new ArrayList<>(objectMapper.readValue(json, type));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read store: " + storageFile, e);
        }
    }

    public void writeAll(List<T> items) {
        try {
            Files.createDirectories(storageFile.getParent());
            Path temp = Files.createTempFile(storageFile.getParent(), storageFile.getFileName().toString(), ".tmp");
            byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(items);
            Files.write(temp, json);
            Files.move(temp, storageFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write store: " + storageFile, e);
        }
    }
}
