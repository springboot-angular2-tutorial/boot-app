package com.myapp;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String md5(String source) {
        byte[] bytes;
        try {
            bytes = MessageDigest.getInstance("MD5")
                    .digest(source.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

}
