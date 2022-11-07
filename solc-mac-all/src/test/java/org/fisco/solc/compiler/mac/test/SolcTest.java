package org.fisco.solc.compiler.mac.test;

import org.fisco.solc.compiler.Solc;
import org.fisco.solc.compiler.SolidityCompiler;
import org.fisco.solc.compiler.Version;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class SolcTest {
    @Test
    public void smSolcTest() throws Exception {
        try {
            String versionOutput = runGetVersionOutput(true, Version.V0_8_11);
            Assert.assertTrue(versionOutput.contains("0.8.11"));
            Assert.assertTrue(versionOutput.toLowerCase().contains("gm"));

            String versionOutput2 = runGetVersionOutput(true, Version.V0_6_10);
            Assert.assertTrue(versionOutput2.contains("0.6.10"));
            Assert.assertTrue(versionOutput2.toLowerCase().contains("gm"));

            String versionOutput3 = runGetVersionOutput(true, Version.V0_5_2);
            Assert.assertTrue(versionOutput3.contains("0.5.2"));
            Assert.assertTrue(versionOutput3.toLowerCase().contains("gm"));

            String versionOutput4 = runGetVersionOutput(true, Version.V0_4_25);
            Assert.assertTrue(versionOutput4.contains("0.4.25"));
            Assert.assertTrue(versionOutput4.toLowerCase().contains("gm"));
        } catch (RuntimeException e) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                throw new Exception("Failed in Runtime, e:" + e.getMessage(), e.getCause());
            }
            assertTrue(e.getMessage().contains("Can't find solc compiler, only support macOS"));
        }
    }

    @Test
    public void ecdsaSolcTest() throws Exception {

        try {
            String versionOutput = runGetVersionOutput(false, Version.V0_8_11);
            Assert.assertTrue(versionOutput.contains("0.8.11"));
            Assert.assertFalse(versionOutput.toLowerCase().contains("gm"));

            String versionOutput2 = runGetVersionOutput(false, Version.V0_6_10);
            Assert.assertTrue(versionOutput2.contains("0.6.10"));
            Assert.assertFalse(versionOutput2.toLowerCase().contains("gm"));

            String versionOutput3 = runGetVersionOutput(false, Version.V0_5_2);
            Assert.assertTrue(versionOutput3.contains("0.5.2"));
            Assert.assertFalse(versionOutput3.toLowerCase().contains("gm"));

            String versionOutput4 = runGetVersionOutput(false, Version.V0_4_25);
            Assert.assertTrue(versionOutput4.contains("0.4.25"));
            Assert.assertFalse(versionOutput4.toLowerCase().contains("gm"));
        } catch (RuntimeException e) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                throw new Exception("Failed in Runtime, e:" + e.getMessage(), e.getCause());
            }
            assertTrue(e.getMessage().contains("Can't find solc compiler, only support macOS"));
        }
    }

    public static String runGetVersionOutput(boolean sm, Version version) throws IOException {
        List<String> commandParts = new ArrayList<>();
        SolidityCompiler solidityCompiler = new SolidityCompiler();
        Solc tmpSolc = solidityCompiler.getSolc(sm, version);

        commandParts.add(tmpSolc.getExecutable().getCanonicalPath());
        commandParts.add("--version");

        ProcessBuilder processBuilder =
                new ProcessBuilder(commandParts).directory(tmpSolc.getExecutable().getParentFile());
        processBuilder
                .environment()
                .put("LD_LIBRARY_PATH", tmpSolc.getExecutable().getParentFile().getCanonicalPath());

        Process process = processBuilder.start();

        ParallelReader error = new ParallelReader(process.getErrorStream());
        ParallelReader output = new ParallelReader(process.getInputStream());
        error.start();
        output.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        if (process.exitValue() == 0) {
            return output.getContent();
        }

        throw new RuntimeException("Problem getting solc version: " + error.getContent());
    }

    private static class ParallelReader extends Thread {

        private InputStream stream;
        private StringBuilder content = new StringBuilder();

        ParallelReader(InputStream stream) {
            this.stream = stream;
        }

        public String getContent() {
            return getContent(true);
        }

        public synchronized String getContent(boolean waitForComplete) {
            if (waitForComplete) {
                while (stream != null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
            return content.toString();
        }

        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                synchronized (this) {
                    stream = null;
                    notifyAll();
                }
            }
        }
    }
}
