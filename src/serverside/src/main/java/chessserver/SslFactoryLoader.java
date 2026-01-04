package chessserver;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class SslFactoryLoader {
    public static SslContextFactory.Server createSslContextFactory() throws Exception {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

        // Load keystore as InputStream from resources
        try (InputStream keystoreStream = 
                 SslFactoryLoader.class.getResourceAsStream("/keystore.jks")) {
            if (keystoreStream == null) {
                throw new RuntimeException("Keystore not found in resources");
            }

            // Copy resource to a temp file (Jetty requires a file path)
            File tempKeystoreFile = File.createTempFile("keystore", ".jks");
            tempKeystoreFile.deleteOnExit();

            Files.copy(keystoreStream, tempKeystoreFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            sslContextFactory.setKeyStorePath(tempKeystoreFile.getAbsolutePath());
        }

        sslContextFactory.setKeyStorePassword("password");
        sslContextFactory.setKeyManagerPassword("password");

        return sslContextFactory;
    }
}
