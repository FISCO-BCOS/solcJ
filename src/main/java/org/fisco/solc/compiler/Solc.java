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

    public Solc(boolean sm) {
        try {
            initBundled(sm);
        } catch (IOException e) {
            logger.error(" Can't init solc compiler, e: ", e);
            throw new RuntimeException("Can't init solc compiler: ", e);
        }
    }

    private void initBundled(boolean sm) throws IOException {

        File tmpDir =
                new File(
                        System.getProperty("user.home"),
                        ".fisco/solc" + "/" + Version.version + "/" + (sm ? "sm3" : "keccak256"));

        if (logger.isTraceEnabled()) {
            logger.trace(" sm: {}, tmpDir: {}", sm, tmpDir.getAbsolutePath());
        }

        tmpDir.mkdirs();
        String solcDir = getSolcDir(sm);

        try (InputStream is = getClass().getResourceAsStream(solcDir + "file.list"); ) {
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNext()) {
                    String s = scanner.next();
                    File targetFile = new File(tmpDir, s);

                    try (InputStream fis = getClass().getResourceAsStream(solcDir + s); ) {
                        Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        if (solc == null) {
                            // first file in the list denotes executable
                            if (logger.isTraceEnabled()) {
                                logger.trace(
                                        " source: {}, destination: {}",
                                        solcDir + s,
                                        targetFile.getAbsoluteFile());
                            }
                            solc = targetFile;
                            solc.setExecutable(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private String getSolcDir(boolean sm) {

        String osName = getOS();
        String resourceDir = "/native/" + (sm ? "sm/" : "ecdsa/") + getOS() + "/";
        if (osName.equals("linux")) {
            // Add support for arm
            String archName = getArch();
            if (!archName.isEmpty()) {
                resourceDir += getArch();
                resourceDir += "/";
            }
        }

        resourceDir += "solc/";

        return resourceDir;
    }

    private String getArch() {
        String archName = System.getProperty("os.arch", "");
        if (archName.contains("aarch64")) {
            return "arm";
        } else {
            return "";
            // throw new RuntimeException("Can't find solc compiler: unrecognized Arch: " +
            // archName);
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
