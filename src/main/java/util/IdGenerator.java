package util;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: maxkeene
 * Date: 10/2/13
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */

public final class IdGenerator {

    private SecureRandom random = new SecureRandom();

    public String nextId()
    {
        return new BigInteger(130, random).toString(32);
    }

}

