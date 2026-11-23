package com.enriquezavala.socialnetwork.persistence.storage;

import com.enriquezavala.socialnetwork.domain.repository.ImageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Guarda las imágenes en una carpeta del servidor ({@code app.uploads.dir}).
 *
 * <p>Seguridad: el tipo se detecta por los primeros bytes del archivo (no por el
 * nombre ni por el Content-Type que manda el cliente), el nombre en disco es un
 * UUID aleatorio y nunca se usa el nombre original, así que no hay forma de
 * escribir fuera de la carpeta ni de subir otro tipo de archivo.</p>
 */
@Component
public class LocalImageStorage implements ImageStorage {

    public static final String PUBLIC_PREFIX = "/uploads/";

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalImageStorage.class);
    private static final Pattern STORED_NAME =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp|gif)$");

    private final Path directory;

    public LocalImageStorage(@Value("${app.uploads.dir:uploads}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.directory);
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo crear la carpeta de imágenes " + this.directory, ex);
        }
    }

    public Path getDirectory() {
        return directory;
    }

    @Override
    public String store(byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("La imagen está vacía");
        }
        String extension = detectExtension(content);
        if (extension == null) {
            throw new IllegalArgumentException("Solo se permiten imágenes JPG, PNG, WEBP o GIF");
        }

        String name = UUID.randomUUID() + "." + extension;
        try {
            Files.write(directory.resolve(name), content);
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo guardar la imagen", ex);
        }
        return PUBLIC_PREFIX + name;
    }

    @Override
    public void deleteIfStored(String publicPath) {
        if (publicPath == null || !publicPath.startsWith(PUBLIC_PREFIX)) return;
        String name = publicPath.substring(PUBLIC_PREFIX.length());
        if (!STORED_NAME.matcher(name).matches()) return; // nunca borrar rutas raras
        try {
            Files.deleteIfExists(directory.resolve(name));
        } catch (IOException ex) {
            LOGGER.warn("No se pudo borrar la imagen {}", name, ex);
        }
    }

    /** Detecta el formato por la "firma" (magic bytes) del archivo. */
    static String detectExtension(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return "png";
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        if (b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a') {
            return "gif";
        }
        return null;
    }
}
