package org.fisco.solc.compiler.test;

import org.fisco.solc.compiler.SolidityCompiler;
import org.fisco.solc.compiler.Version;
import org.junit.Assert;
import org.junit.Test;

public class SolidityCompilerVersionTest {
    @SuppressWarnings("static-access")
    @Test
    public void smSolcVersionTest() throws Exception {
        String versionOutput = SolidityCompiler.runGetVersionOutput(true, Version.V0_8_11);
        Assert.assertTrue(versionOutput.contains("0.8.11"));
        Assert.assertTrue(versionOutput.toLowerCase().contains("gm"));

        String versionOutput2 = SolidityCompiler.runGetVersionOutput(true, Version.V0_6_10);
        Assert.assertTrue(versionOutput2.contains("0.6.10"));
        Assert.assertTrue(versionOutput2.toLowerCase().contains("gm"));

        String versionOutput3 = SolidityCompiler.runGetVersionOutput(true, Version.V0_5_2);
        Assert.assertTrue(versionOutput3.contains("0.5.2"));
        Assert.assertTrue(versionOutput3.toLowerCase().contains("gm"));

        String versionOutput4 = SolidityCompiler.runGetVersionOutput(true, Version.V0_4_25);
        Assert.assertTrue(versionOutput4.contains("0.4.25"));
        Assert.assertTrue(versionOutput4.toLowerCase().contains("gm"));
    }

    @Test
    public void ecdsaSolcVersionTest() throws Exception {
        String versionOutput = SolidityCompiler.runGetVersionOutput(false, Version.V0_8_11);
        Assert.assertTrue(versionOutput.contains("0.8.11"));
        Assert.assertFalse(versionOutput.toLowerCase().contains("gm"));

        String versionOutput2 = SolidityCompiler.runGetVersionOutput(false, Version.V0_6_10);
        Assert.assertTrue(versionOutput2.contains("0.6.10"));
        Assert.assertFalse(versionOutput2.toLowerCase().contains("gm"));

        String versionOutput3 = SolidityCompiler.runGetVersionOutput(false, Version.V0_5_2);
        Assert.assertTrue(versionOutput3.contains("0.5.2"));
        Assert.assertFalse(versionOutput3.toLowerCase().contains("gm"));

        String versionOutput4 = SolidityCompiler.runGetVersionOutput(false, Version.V0_4_25);
        Assert.assertTrue(versionOutput4.contains("0.4.25"));
        Assert.assertFalse(versionOutput4.toLowerCase().contains("gm"));
    }
}
