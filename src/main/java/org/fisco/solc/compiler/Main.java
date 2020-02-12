package org.fisco.solc.compiler;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        BigInteger bigInteger =
                new BigInteger(
                        "115792089237316195423570985008687907852837564279074904382605163141518161494337");
        System.out.println(bigInteger.toString(16));
    }
}
