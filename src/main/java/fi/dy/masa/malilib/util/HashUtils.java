package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import fi.dy.masa.malilib.MaLiLib;

public class HashUtils
{
    public static String getHashAsHexString(File file, MessageDigest digest)
    {
        StringBuilder sb = new StringBuilder(64);
        digest.reset();

         try (DigestInputStream din = new DigestInputStream(new FileInputStream(file), digest))
         {
             byte[] buf = new byte[4096];

             while (din.read(buf) != -1)
             {
             }

             byte[] raw = digest.digest();

             for (byte b : raw)
             {
                 sb.append(String.format("%02X", b));
             }
         }
         catch (Exception e)
         {
             MaLiLib.LOGGER.warn("Exception while hashing file '{}': {}", file.getAbsolutePath(), e.getMessage());
         }

         return sb.toString();
    }
}
