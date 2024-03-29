package org.fisco.solc.compiler.test;

import static org.fisco.solc.compiler.SolidityCompiler.Options.ABI;
import static org.fisco.solc.compiler.SolidityCompiler.Options.BIN;
import static org.fisco.solc.compiler.SolidityCompiler.Options.DEVDOC;
import static org.fisco.solc.compiler.SolidityCompiler.Options.METADATA;
import static org.fisco.solc.compiler.SolidityCompiler.Options.USERDOC;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.fisco.solc.compiler.CompilationResult;
import org.fisco.solc.compiler.SolidityCompiler;
import org.fisco.solc.compiler.SolidityCompiler.Result;
import org.fisco.solc.compiler.Version;
import org.junit.Test;

public class SolidityCompilerV0610_0811Test {
    @SuppressWarnings("static-access")
    @Test
    public void compilerV0610Test() {
        compileCharitySplitterFactory(false, Version.V0_6_10, true);
        compileCharitySplitterFactory(false, Version.V0_6_10, false);
        compileCharitySplitterFactory(true, Version.V0_6_10, true);
        compileCharitySplitterFactory(true, Version.V0_6_10, false);
        docTest(Version.V0_6_10);
    }

    @Test
    public void compilerV0811Test() {
        compileCharitySplitterFactory(false, Version.V0_8_11, true);
        compileCharitySplitterFactory(false, Version.V0_8_11, false);
        compileCharitySplitterFactory(true, Version.V0_8_11, true);
        compileCharitySplitterFactory(true, Version.V0_8_11, false);
        docTest(Version.V0_6_10);
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

    public void docTest(Version version) {
        try {
            URL url =
                    SolidityCompilerV0610_0811Test.class
                            .getClassLoader()
                            .getSystemResource("solidity/IERC721.sol");
            File file = new File(url.getFile());
            Result result =
                    SolidityCompiler.compile(
                            file, true, true, version, ABI, BIN, METADATA, USERDOC, DEVDOC);
            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata assetContractMetadata =
                    compilationResult.getContract("IERC721");
            assertTrue(
                    "BIN NOT empty, compile error: " + result.getErrors(),
                    ("".equals(assetContractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
            assertTrue(
                    "User doc empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
            assertTrue(
                    "Dev doc empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }

    public void weCossProxyContractTest(Version version) {
        try {
            URL url =
                    SolidityCompilerV0610_0811Test.class
                            .getClassLoader()
                            .getSystemResource("solidity/WeCrossProxy.sol");
            File file = new File(url.getFile());
            Result result =
                    SolidityCompiler.compile(
                            file, true, true, version, ABI, BIN, METADATA, USERDOC, DEVDOC);
            assertTrue(
                    "compile solidity failed, solidity error: " + result.getErrors(),
                    !result.isFailed());
            CompilationResult compilationResult = CompilationResult.parse(result.getOutput());
            CompilationResult.ContractMetadata assetContractMetadata =
                    compilationResult.getContract("WeCrossProxy");
            assertTrue(
                    "BIN empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.bin)));
            assertTrue(
                    "ABI empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
            assertTrue(
                    "User doc empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
            assertTrue(
                    "Dev doc empty, compile error: " + result.getErrors(),
                    !("".equals(assetContractMetadata.abi)));
        } catch (IOException e) {
            assertTrue("compile solidity failed, error: " + e.getMessage(), false);
        }
    }
}
