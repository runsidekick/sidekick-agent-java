package com.runsidekick.agent.core.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.IOUtils;
import org.slf4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Helper class for license key related stuffs.
 *
 * @author serkan
 */
public final class LicenseKeyHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger("LicenseKeyHelper");

    private static final PublicKey PUBLIC_KEY;

    static {
        PublicKey pc = null;
        // TODO Activate license key check mechanism
//        try {
//            pc = AsymmetricCryptography.getPublicKey("sidekickPublicKey");
//        } catch (Throwable t) {
//            LOGGER.error("Unable to initialize feature checker. So non-standard features will not be available", t);
//        }
        PUBLIC_KEY = pc;
    }

    private LicenseKeyHelper() {
    }

    public static LicenseKeyInfo decodeLicenseKeyInfo(String licenseKey) throws Exception {
        if (PUBLIC_KEY == null) {
            return null;
        }

        // For preventing reflection hacks,
        // fields are not neither defined as member and nor cached

        AsymmetricCryptography crypt = new AsymmetricCryptography(PUBLIC_KEY);
        if (licenseKey != null && licenseKey.trim().length() > 0) {
            try {
                String licenseKeyInfo = crypt.decryptText(licenseKey);
                ObjectMapper om = new ObjectMapper();
                return om.readValue(licenseKeyInfo, LicenseKeyInfo.class);
            } catch (Throwable t) {
                throw new IllegalArgumentException(
                        String.format(
                                "Unable to decode (reason: %s) license key: %s",
                                t.getMessage(), licenseKey),
                        t);
            }
        } else {
            throw new IllegalArgumentException("License key is not set");
        }
    }

    public static void checkLicenseKeyInfo(LicenseKeyInfo licenseKeyInfo) {
        if (licenseKeyInfo.getExpireTime() < System.currentTimeMillis()) {
            throw new IllegalStateException("Expired License key");
        }
    }

    private static class AsymmetricCryptography {

        private final Cipher cipher;
        private final PublicKey publicKey;

        private AsymmetricCryptography(PublicKey publicKey) throws Exception {
            this.cipher = Cipher.getInstance("RSA");
            this.publicKey = publicKey;
        }

        // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
        private static PublicKey getPublicKey(String filename) throws Exception {
            InputStream is = IOUtils.getResourceAsStream(
                    AsymmetricCryptography.class.getClassLoader(), filename);
            byte[] keyBytes = IOUtils.readAll(is);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }

        private String decryptText(String msg)
                throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(msg)), "UTF-8");
        }

    }

}
