package org.fisco.solc.compiler.test;

import org.fisco.solc.compiler.Solc;
import org.fisco.solc.compiler.Version;
import org.junit.Assert;
import org.junit.Test;

public class SolcTest {

    @Test
    public void smSolcTest() {
        Solc solc = new Solc(true, Version.V0_4_25);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(true, Version.V0_5_2);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(true, Version.V0_6_10);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(true, Version.V0_8_11);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());
    }

    @Test
    public void ecdsaSolcTest() {
        Solc solc = new Solc(false, Version.V0_4_25);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(false, Version.V0_5_2);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(false, Version.V0_6_10);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());

        solc = new Solc(false, Version.V0_8_11);
        Assert.assertNotNull(solc);
        Assert.assertTrue(solc.getExecutable().exists());
        Assert.assertTrue(solc.getExecutable().canExecute());
    }
}
