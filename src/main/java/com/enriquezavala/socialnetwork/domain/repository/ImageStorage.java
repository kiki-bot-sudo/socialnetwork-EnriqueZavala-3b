package com.enriquezavala.socialnetwork.domain.repository;

/**
 * Almacenamiento de las imágenes que suben los usuarios.
 *
 * <p>El dominio solo conoce esta interfaz; la implementación decide dónde se
 * guardan los archivos (disco local, nube, etc.).</p>
 */
public interface ImageStorage {

    /**
     * Guarda la imagen y devuelve la ruta pública con la que se puede descargar
     * (por ejemplo {@code /uploads/3f2a….jpg}).
     *
     * @throws IllegalArgumentException si el archivo está vacío o no es una imagen permitida
     */
    String store(byte[] content);

    /** Borra la imagen si fue guardada por este almacenamiento; si no, no hace nada. */
    void deleteIfStored(String publicPath);
}
