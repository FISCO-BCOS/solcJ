package org.fisco.solc.compiler;

import static java.util.stream.Collectors.toList;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolidityCompiler {

    private static final Logger logger = LoggerFactory.getLogger(SolidityCompiler.class);

    /** singleton object */
    private static SolidityCompiler INSTANCE;
    /** ecdsa compiler */
    private Map<Version, Solc> solcMap = new ConcurrentHashMap<>();
    /** sm compiler */
    private Map<Version, Solc> sMSolcMap = new ConcurrentHashMap<>();

    private void initSolc(boolean sm, Version version) {
        if (sm && (!sMSolcMap.containsKey(version))) {
            Solc sMSolc = new Solc(true, version);
            sMSolcMap.put(version, sMSolc);
        } else if (!sm && (!solcMap.containsKey(version))) {
            Solc solc = new Solc(false, version);
            solcMap.put(version, solc);
        }
    }

    /**
     * @param source
     * @param sm
     * @param combinedJson
     * @param options
     * @return
     * @throws IOException
     */
    public static Result compile(
            File source, boolean sm, boolean combinedJson, Version version, Option... options)
            throws IOException {
        return getInstance().compileSrc(source, sm, false, combinedJson, version, options);
    }

    /**
     * @param source
     * @param sm
     * @param combinedJson
     * @param options
     * @return
     * @throws IOException
     */
    public static Result compile(
            byte[] source, boolean sm, boolean combinedJson, Version version, Option... options)
            throws IOException {
        return getInstance().compileSrc(source, sm, false, combinedJson, version, options);
    }

    /**
     * This class is mainly here for backwards compatibility; however we are now reusing it making
     * it the solely public interface listing all the supported options.
     */
    public static final class Options {
        public static final OutputOption AST = OutputOption.AST;
        public static final OutputOption BIN = OutputOption.BIN;
        public static final OutputOption ABI = OutputOption.ABI;
        public static final OutputOption METADATA = OutputOption.METADATA;
        public static final OutputOption ASTJSON = OutputOption.ASTJSON;
        public static final OutputOption USERDOC = OutputOption.USERDOC;
        public static final OutputOption DEVDOC = OutputOption.DEVDOC;

        private static final NameOnlyOption OPTIMIZE = NameOnlyOption.OPTIMIZE;
        private static final NameOnlyOption VERSION = NameOnlyOption.VERSION;

        private static class CombinedJson extends ListOption {
            private CombinedJson(List values) {
                super("combined-json", values);
            }
        }

        public static class AllowPaths extends ListOption {
            public AllowPaths(List values) {
                super("allow-paths", values);
            }
        }
    }

    public interface Option extends Serializable {
        String getValue();

        String getName();
    }

    private static class ListOption implements Option {
        private String name;
        private List values;

        private ListOption(String name, List values) {
            this.name = name;
            this.values = values;
        }

        @Override
        public String getValue() {
            StringBuilder result = new StringBuilder();
            for (Object value : values) {
                if (OutputOption.class.isAssignableFrom(value.getClass())) {
                    result.append(
                            (result.length() == 0)
                                    ? ((OutputOption) value).getName()
                                    : ',' + ((OutputOption) value).getName());
                } else if (Path.class.isAssignableFrom(value.getClass())) {
                    result.append(
                            (result.length() == 0)
                                    ? ((Path) value).toAbsolutePath().toString()
                                    : ',' + ((Path) value).toAbsolutePath().toString());
                } else if (File.class.isAssignableFrom(value.getClass())) {
                    result.append(
                            (result.length() == 0)
                                    ? ((File) value).getAbsolutePath()
                                    : ',' + ((File) value).getAbsolutePath());
                } else if (String.class.isAssignableFrom(value.getClass())) {
                    result.append((result.length() == 0) ? value : "," + value);
                } else {
                    throw new UnsupportedOperationException(
                            "Unexpected type, value '" + value + "' cannot be retrieved.");
                }
            }
            return result.toString();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum NameOnlyOption implements Option {
        OPTIMIZE("optimize"),
        VERSION("version");

        private String name;

        NameOnlyOption(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return "";
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum OutputOption implements Option {
        AST("ast"),
        BIN("bin"),
        ABI("abi"),
        METADATA("metadata"),
        ASTJSON("ast-json"),
        USERDOC("userdoc"),
        DEVDOC("devdoc");

        private String name;

        OutputOption(String name) {
            this.name = name;
        }

        @Override
        public String getValue() {
            return "";
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class CustomOption implements Option {
        private String name;
        private String value;

        public CustomOption(String name) {
            if (name.startsWith("--")) {
                this.name = name.substring(2);
            } else {
                this.name = name;
            }
        }

        public CustomOption(String name, String value) {
            this(name);
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Result {

        private String errors;
        private String output;
        private boolean success;

        public Result(String errors, String output, boolean success) {
            this.errors = errors;
            this.output = output;
            this.success = success;
        }

        public boolean isFailed() {
            return !success;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }
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

    private Result compileSrc(
            File source,
            boolean sm,
            boolean optimize,
            boolean combinedJson,
            Version version,
            Option... options)
            throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug(" source: {}, sm: {}", source.getAbsolutePath(), sm);
        }

        Solc tmpSolc = getSolc(sm, version);
        List<String> commandParts = prepareCommandOptions(tmpSolc, optimize, combinedJson, options);

        commandParts.add(source.getAbsolutePath());

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
        boolean success = process.exitValue() == 0;

        if (logger.isTraceEnabled()) {
            logger.trace(
                    " source : {}, success: {}, output: {}, error: {} ",
                    source.getAbsoluteFile(),
                    success,
                    output.getContent(),
                    error.getContent());
        }

        return new Result(error.getContent(), output.getContent(), success);
    }

    private List<String> prepareCommandOptions(
            Solc solc, boolean optimize, boolean combinedJson, Option... options)
            throws IOException {
        List<String> commandParts = new ArrayList<>();
        commandParts.add(solc.getExecutable().getCanonicalPath());
        if (optimize) {
            commandParts.add("--" + Options.OPTIMIZE.getName());
        }
        if (combinedJson) {
            Option combinedJsonOption =
                    new Options.CombinedJson(getElementsOf(OutputOption.class, options));
            commandParts.add("--" + combinedJsonOption.getName());
            commandParts.add(combinedJsonOption.getValue());
        } else {
            for (Option option : getElementsOf(OutputOption.class, options)) {
                commandParts.add("--" + option.getName());
            }
        }
        for (Option option : getElementsOf(ListOption.class, options)) {
            commandParts.add("--" + option.getName());
            commandParts.add(option.getValue());
        }

        for (Option option : getElementsOf(CustomOption.class, options)) {
            commandParts.add("--" + option.getName());
            if (option.getValue() != null) {
                commandParts.add(option.getValue());
            }
        }
        for (Option option : getElementsOf(NameOnlyOption.class, options)) {
            commandParts.add("--" + option.getName());
        }

        return commandParts;
    }

    private static <T> List<T> getElementsOf(Class<T> clazz, Option... options) {
        return Arrays.stream(options).filter(clazz::isInstance).map(clazz::cast).collect(toList());
    }

    private Result compileSrc(
            byte[] source,
            boolean sm,
            boolean optimize,
            boolean combinedJson,
            Version version,
            Option... options)
            throws IOException {
        Solc tmpSolc = getInstance().getSolc(sm, version);
        List<String> commandParts = prepareCommandOptions(tmpSolc, optimize, combinedJson, options);

        // new in solidity 0.5.0: using stdin requires an explicit "-". The following output
        // of 'solc' if no file is provided, e.g.,: solc --combined-json abi,bin,interface,metadata
        //
        // No input files given. If you wish to use the standard input please specify "-"
        // explicitly.
        //
        // For older solc version "-" is not an issue as it is accepet as well
        commandParts.add("-");

        ProcessBuilder processBuilder =
                new ProcessBuilder(commandParts).directory(tmpSolc.getExecutable().getParentFile());
        processBuilder
                .environment()
                .put("LD_LIBRARY_PATH", tmpSolc.getExecutable().getParentFile().getCanonicalPath());

        Process process = processBuilder.start();

        try (BufferedOutputStream stream = new BufferedOutputStream(process.getOutputStream())) {
            stream.write(source);
        }

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
        boolean success = process.exitValue() == 0;

        return new Result(error.getContent(), output.getContent(), success);
    }

    public static String runGetVersionOutput(boolean sm, Version version) throws IOException {
        List<String> commandParts = new ArrayList<>();
        Solc tmpSolc = getInstance().getSolc(sm, version);

        commandParts.add(tmpSolc.getExecutable().getCanonicalPath());
        commandParts.add("--" + Options.VERSION.getName());

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

    public Solc getSolc(boolean sm, Version version) {
        initSolc(sm, version);
        return (sm ? sMSolcMap.get(version) : solcMap.get(version));
    }

    public static SolidityCompiler getInstance() {
        if (INSTANCE == null) {
            synchronized (SolidityCompiler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SolidityCompiler();
                }
            }
        }
        return INSTANCE;
    }
}
