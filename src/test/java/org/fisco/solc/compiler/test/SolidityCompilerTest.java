package org.fisco.solc.compiler.test;

import static org.fisco.solc.compiler.SolidityCompiler.Options.ABI;
import static org.fisco.solc.compiler.SolidityCompiler.Options.BIN;
import static org.fisco.solc.compiler.SolidityCompiler.Options.INTERFACE;
import static org.fisco.solc.compiler.SolidityCompiler.Options.METADATA;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.fisco.solc.compiler.CompilationResult;
import org.fisco.solc.compiler.SolidityCompiler;
import org.fisco.solc.compiler.SolidityCompiler.Result;
import org.junit.Test;

public class SolidityCompilerTest {
    @SuppressWarnings("static-access")
    @Test
    public void ecdsasolidityCompilerTest() {
        try {
            URL url =
                    SolidityCompilerTest.class
                            .getClassLoader()
                            .getSystemResource("solidity/Asset.sol");
            File file = new File(url.getFile());
            Result result =
                    SolidityCompiler.compile(file, false, true, ABI, BIN, INTERFACE, METADATA);
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

    @Test
    public void smSolidityCompilerTest() {
        try {
            URL url =
                    SolidityCompilerTest.class
                            .getClassLoader()
                            .getSystemResource("solidity/Asset.sol");
            File file = new File(url.getFile());
            Result result =
                    SolidityCompiler.compile(file, true, true, ABI, BIN, INTERFACE, METADATA);
            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata assetContractMetadata =
                    compilationResult.getContract("Asset");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }
}
