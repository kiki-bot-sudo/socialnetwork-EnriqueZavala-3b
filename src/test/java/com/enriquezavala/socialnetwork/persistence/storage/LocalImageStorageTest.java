package com.enriquezavala.socialnetwork.persistence.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalImageStorageTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    private static final byte[] JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};

    @TempDir
    Path dir;

    @Test
    void storesRealImageWithRandomNameAndCorrectExtension() throws Exception {
        LocalImageStorage storage = new LocalImageStorage(dir.toString());

        String path = storage.store(PNG);

        assertTrue(path.matches("^/uploads/[0-9a-f-]{36}\\.png$"), path);
        Path file = dir.resolve(path.substring("/uploads/".length()));
        assertArrayEquals(PNG, Files.readAllBytes(file));
        assertTrue(storage.store(JPG).endsWith(".jpg"));
    }

    @Test
    void rejectsFilesThatAreNotImages() {
        LocalImageStorage storage = new LocalImageStorage(dir.toString());
        byte[] script = "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class, () -> storage.store(script));
        assertThrows(IllegalArgumentException.class, () -> storage.store(new byte[0]));
    }

    @Test
    void deleteOnlyTouchesItsOwnFiles() throws Exception {
        LocalImageStorage storage = new LocalImageStorage(dir.toString());
        String path = storage.store(JPG);
        Path outside = Files.writeString(dir.resolve("importante.txt"), "no borrar");

        storage.deleteIfStored("/uploads/../importante.txt");
        storage.deleteIfStored("https://example.com/foto.jpg");
        assertTrue(Files.exists(outside));

        storage.deleteIfStored(path);
        assertFalse(Files.exists(dir.resolve(path.substring("/uploads/".length()))));
    }
}
