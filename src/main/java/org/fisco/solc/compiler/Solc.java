package org.fisco.solc.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solc {

    private static final Logger logger = LoggerFactory.getLogger(Solc.class);

    private File solc = null;

    Solc(boolean sm) {
        try {
            initBundled(sm);
        } catch (IOException e) {
            logger.debug(
                    " Can't init solc compiler, sm: {}, error: {}, e: {}", sm, e.getMessage(), e);
            throw new RuntimeException("Can't init solc compiler: ", e);
        }
    }

    private void initBundled(boolean sm) throws IOException {

        File tmpDir =
                new File(System.getProperty("user.home"), "solc" + "/" + (sm ? "sm" : "ecdsa"));
        logger.debug(" sm: {}, tmpDir: {}", sm, tmpDir.getAbsolutePath());
        tmpDir.mkdirs();

        String solcFileDir = "/native/" + (sm ? "sm" : "ecdsa") + "/" + getOS() + "/solc/";
        try (InputStream is = getClass().getResourceAsStream(solcFileDir + "file.list"); ) {
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNext()) {
                    String s = scanner.next();
                    File targetFile = new File(tmpDir, s);
                    logger.debug(" targetFile: {}", targetFile.getAbsolutePath());
                    try (InputStream fis = getClass().getResourceAsStream(solcFileDir + s); ) {
                        Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        if (solc == null) {
                            // first file in the list denotes executable
                            solc = targetFile;
                            solc.setExecutable(true);
                        }
                    }
                }
            }
        }
    }

    private String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "win";
        } else if (osName.contains("linux")) {
            return "linux";
        } else if (osName.contains("mac")) {
            return "mac";
        } else {
            throw new RuntimeException("Can't find solc compiler: unrecognized OS: " + osName);
        }
    }

    public File getExecutable() {
        return solc;
    }
}
