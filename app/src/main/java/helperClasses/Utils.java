/*
 * (C) Copyright 2016 HP Development Company, L.P.
 * All Rights Reserved Worldwide
 * 09/14/2016 SMKAB
 *
 * Utils.java
 */

package helperClasses;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

public class Utils {
    public static boolean isEmpty(Collection<?> aCollection) {
        return aCollection == null || aCollection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> aCollection) {
        return aCollection != null && !aCollection.isEmpty();
    }

    public static void close(Closeable aCloseable) {
        if (aCloseable != null) {
            try {
                aCloseable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
