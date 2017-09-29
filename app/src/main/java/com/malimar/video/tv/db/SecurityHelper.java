package com.malimar.video.tv.db;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by SAARA on 10-01-2017.
 */
public class SecurityHelper   {
    private static final boolean HAS_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION.SDK_INT;

    /* Security */

    private static final byte[] SEED_BYTES = "thisisseed".getBytes();
    private static final String ALG = "AES";
    private static final String TAG = "SecurityHelper";

    public static byte[] getRawKey(byte[] seed) throws Exception {

        KeyGenerator kgen = KeyGenerator.getInstance(ALG);

        SecureRandom sr = HAS_JELLY_BEAN
                ? SecureRandom.getInstance("SHA1PRNG", "Crypto") // getProvider(): AndroidOpenSSL by default
                // in JB, so force to use "Crypto"
                : SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    public static String encodeString(String str) {
        if (str == null)
            return null;

        byte[] result = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(getRawKey(SEED_BYTES), ALG);
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            result = cipher.doFinal(str.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Can't encrypt, problem: ");
            e.printStackTrace();
        }

        return new String(Base64.encode(result, Base64.DEFAULT));
    }

    public static String decodeString(String encrypted) {
        String result = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(getRawKey(SEED_BYTES), ALG);
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            result = new String(decrypted);
        } catch (Exception e) {
            Log.e(TAG, "Can't decrypt, problem: ");
            e.printStackTrace();
        }
        return result;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encode(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));
    }

    public static String getCheckSum(JSONObject jsonObject, String userId) {
        String checkSum = "";

        try {
            //get all the keys
            List<String> keys = new ArrayList<>();

            Iterator<String> iterator = jsonObject.keys();

            while(iterator.hasNext()){
                String key = iterator.next();

                if(!key.equalsIgnoreCase("checksum")) {
                    keys.add(key);
                }
            }

            Collections.sort(keys);

            StringBuffer buffer = new StringBuffer();

            for(String key : keys) {
                buffer.append(jsonObject.get(key));
            }

            String encodedValue = URLEncoder.encode(buffer.toString(), "UTF-8");
            String md5key = SecurityHelper.md5(userId);
            checkSum = SecurityHelper.encode(md5key, encodedValue);
        }
        catch(Exception e) {

        }

        return checkSum;
    }
}
