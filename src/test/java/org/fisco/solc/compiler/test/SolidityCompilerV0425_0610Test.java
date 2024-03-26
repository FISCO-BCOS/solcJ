package org.fisco.solc.compiler.test;

import org.fisco.solc.compiler.CompilationResult;
import org.fisco.solc.compiler.SolidityCompiler;
import org.fisco.solc.compiler.SolidityCompiler.Result;
import org.fisco.solc.compiler.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.fisco.solc.compiler.SolidityCompiler.Options.ABI;
import static org.fisco.solc.compiler.SolidityCompiler.Options.BIN;
import static org.fisco.solc.compiler.SolidityCompiler.Options.METADATA;
import static org.junit.Assert.assertTrue;

public class SolidityCompilerV0425_0610Test {
    @SuppressWarnings("static-access")
    @Test
    public void compilerV0425Test() {
        compileAsset(false, Version.V0_4_25);
        compileAsset(true, Version.V0_4_25);
        compileTable(false, Version.V0_4_25);
        compileTable(true, Version.V0_4_25);
    }

    @Test
    public void compilerV052Test() {
        compileAsset(false, Version.V0_5_2);
        compileAsset(true, Version.V0_5_2);
        compileTable(false, Version.V0_5_2);
        compileTable(true, Version.V0_5_2);
    }

    @Test
    public void compilerV0610Test() {
        compileAsset(false, Version.V0_6_10);
        compileAsset(true, Version.V0_6_10);
        compileTable(false, Version.V0_6_10);
        compileTable(true, Version.V0_6_10);
    }

    private void compileAsset(boolean sm, Version version) {
        try {
            URL url =
                    SolidityCompilerVersionTest.class
                            .getClassLoader()
                            .getSystemResource("solidity/Asset.sol");
            File file = new File(url.getFile());
            Result result =
//                    SolidityCompiler.compile(file, false, true, ABI, BIN, INTERFACE, METADATA);
                    SolidityCompiler.compile(file, sm, true, version, ABI, BIN, METADATA);
            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata contractMetadata =
                    compilationResult.getContract("Asset");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }

    private void compileTable(boolean sm, Version version) {
        try {
            URL url =
                    SolidityCompilerVersionTest.class
                            .getClassLoader()
                            .getSystemResource("solidity/Table.sol");
            File file = new File(url.getFile());
            byte[] bytes = Files.readAllBytes(file.toPath());
            Result result =
//                    SolidityCompiler.compile(file, false, true, ABI, BIN, INTERFACE, METADATA);
                    SolidityCompiler.compile(bytes, sm, true, version, ABI, BIN, METADATA);
            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata contractMetadata =
                    compilationResult.getContract("Table");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }
}
