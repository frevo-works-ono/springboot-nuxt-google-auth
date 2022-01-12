package springboot.nuxt.google.auth.backend.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.stereotype.Component;

@Component
public class FirebaseClient {
    private static final String FIREBASE_CREDENTIALS_PATH = "credential.json";

    public FirebaseClient() {

        try {
            FileWriter fw = new FileWriter(FIREBASE_CREDENTIALS_PATH);
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {

                // 環境変数から取得して設定する
                Map<String, String> credentials = new HashMap<>();
                credentials.put("type", "service_account");
                credentials.put("project_id", System.getenv("FIREBASE_PROJECT_ID"));
                credentials.put("private_key_id", System.getenv("FIREBASE_PRIVATE_KEY_ID"));
                credentials.put("private_key", System.getenv("FIREBASE_PRIVATE_KEY"));
                credentials.put("client_email", System.getenv("FIREBASE_CLIENT_EMAIL"));
                credentials.put("client_id", System.getenv("FIREBASE_CLIENT_ID"));
                credentials.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
                credentials.put("token_uri", "https://oauth2.googleapis.com/token");
                credentials.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
                credentials.put("client_x509_cert_url",
                        "https://www.googleapis.com/robot/v1/metadata/x509/" + System.getenv("FIREBASE_CLIENT_EMAIL"));

                // MapをJson形式の文字列に変換
                String str = new ObjectMapper().writeValueAsString(credentials);

                // ファイルに書き込み
                pw.println(str);
            }

            FileInputStream serviceAccount = new FileInputStream(FIREBASE_CREDENTIALS_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            // 初期化
            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FirebaseToken verify(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token);
    }

    @PreDestroy
    public void destroy() {
        FirebaseApp.getInstance().delete();
    }
}
