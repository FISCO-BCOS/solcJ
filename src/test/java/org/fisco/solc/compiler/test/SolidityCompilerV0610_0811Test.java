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

public class SolidityCompilerV0610_0811Test {
    @SuppressWarnings("static-access")
    @Test
    public void compilerV0610Test() {
        compileCharitySplitterFactory(false, Version.V0_6_10, true);
        compileCharitySplitterFactory(false, Version.V0_6_10, false);
        compileCharitySplitterFactory(true, Version.V0_6_10, true);
        compileCharitySplitterFactory(true, Version.V0_6_10, false);
    }

    @Test
    public void compilerV0811Test() {
        compileCharitySplitterFactory(false, Version.V0_8_11, true);
        compileCharitySplitterFactory(false, Version.V0_8_11, false);
        compileCharitySplitterFactory(true, Version.V0_8_11, true);
        compileCharitySplitterFactory(true, Version.V0_8_11, false);
    }

    private void compileCharitySplitterFactory(boolean sm, Version version, boolean isFile) {
        try {
            URL url =
                    SolidityCompilerV0610_0811Test.class
                            .getClassLoader()
                            .getSystemResource("solidity/CharitySplitterFactory.sol");
            File file = new File(url.getFile());

            Result result = null;
            if (isFile) {
                result = SolidityCompiler.compile(file, sm, true, version, ABI, BIN, METADATA);
            } else {
                byte[] bytes = Files.readAllBytes(file.toPath());
                result = SolidityCompiler.compile(bytes, sm, true, version, ABI, BIN, METADATA);
            }

            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata contractMetadata =
                    compilationResult.getContract("CharitySplitter");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(contractMetadata.abi)));

            CompilationResult.ContractMetadata assetContractMetadata0 =
                    compilationResult.getContract("CharitySplitterFactory");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata0.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata0.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }
}
