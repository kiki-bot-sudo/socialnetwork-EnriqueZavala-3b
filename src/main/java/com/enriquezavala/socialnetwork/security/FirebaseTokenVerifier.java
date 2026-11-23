package com.enriquezavala.socialnetwork.security;

import com.enriquezavala.socialnetwork.domain.exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Verifica los ID tokens que emite Firebase Authentication.
 *
 * <p>La app inicia sesión con Firebase y envía su ID token; aquí se comprueba
 * que lo firmó Google (llaves públicas de securetoken@system.gserviceaccount.com),
 * que es para este proyecto de Firebase y que no ha vencido. Así el backend no
 * necesita guardar ni comparar la contraseña: Firebase es quien la valida, y la
 * recuperación de contraseña de Firebase funciona sin desincronizar cuentas.</p>
 */
@Component
public class FirebaseTokenVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseTokenVerifier.class);
    private static final String CERTS_URL =
            "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
    private static final Pattern MAX_AGE = Pattern.compile("max-age=(\\d+)");

    private final String projectId;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private Map<String, PublicKey> keys = Map.of();
    private Instant keysExpireAt = Instant.EPOCH;

    public FirebaseTokenVerifier(@Value("${app.firebase.project-id}") String projectId) {
        this.projectId = projectId;
    }

    /** Datos confiables del usuario sacados de un ID token válido. */
    public record FirebaseUser(String uid, String email) {
    }

    public FirebaseUser verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        }
        try {
            Claims claims = Jwts.parser()
                    .keyLocator(new LocatorAdapter<Key>() {
                        @Override
                        protected Key locate(ProtectedHeader header) {
                            return publicKey(header.getKeyId());
                        }
                    })
                    .requireIssuer("https://securetoken.google.com/" + projectId)
                    .requireAudience(projectId)
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();

            String email = claims.get("email", String.class);
            if (claims.getSubject() == null || claims.getSubject().isBlank()
                    || email == null || email.isBlank()) {
                throw new InvalidCredentialsException("Correo o contraseña incorrectos");
            }
            return new FirebaseUser(claims.getSubject(), email.trim().toLowerCase());
        } catch (JwtException | IllegalArgumentException ex) {
            LOGGER.warn("ID token de Firebase rechazado: {}", ex.getMessage());
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        }
    }

    private synchronized PublicKey publicKey(String keyId) {
        if (keyId == null) {
            throw new IllegalArgumentException("El token no indica su llave (kid)");
        }
        if (Instant.now().isAfter(keysExpireAt) || !keys.containsKey(keyId)) {
            refreshKeys();
        }
        PublicKey key = keys.get(keyId);
        if (key == null) {
            throw new IllegalArgumentException("Llave desconocida: " + keyId);
        }
        return key;
    }

    /** Descarga las llaves públicas de Google y las guarda el tiempo que indica Cache-Control. */
    private void refreshKeys() {
        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(URI.create(CERTS_URL)).timeout(Duration.ofSeconds(10)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Google respondió HTTP " + response.statusCode());
            }

            Map<String, String> pems = jsonMapper.readValue(response.body(), new TypeReference<>() { });
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Map<String, PublicKey> fresh = new HashMap<>();
            for (Map.Entry<String, String> entry : pems.entrySet()) {
                byte[] pem = entry.getValue().getBytes(StandardCharsets.UTF_8);
                fresh.put(entry.getKey(),
                        factory.generateCertificate(new ByteArrayInputStream(pem)).getPublicKey());
            }

            long maxAge = response.headers().firstValue("Cache-Control")
                    .map(MAX_AGE::matcher)
                    .filter(Matcher::find)
                    .map(m -> Long.parseLong(m.group(1)))
                    .orElse(3600L);
            keys = Map.copyOf(fresh);
            keysExpireAt = Instant.now().plusSeconds(maxAge);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("No se pudieron descargar las llaves de Firebase", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudieron descargar las llaves de Firebase", ex);
        }
    }
}
