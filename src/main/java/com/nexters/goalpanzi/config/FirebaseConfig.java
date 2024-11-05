package com.nexters.goalpanzi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
@Configuration
public class FirebaseConfig {

    @Value("${firebase.admin-sdk}")
    private String encodedFirebaseAdminSdk;

    @PostConstruct
    public FirebaseApp firebaseApp() throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedFirebaseAdminSdk);
        ByteArrayInputStream adminSdk = new ByteArrayInputStream(decodedBytes);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(adminSdk))
                .build();
        return FirebaseApp.initializeApp(options);
    }
}
