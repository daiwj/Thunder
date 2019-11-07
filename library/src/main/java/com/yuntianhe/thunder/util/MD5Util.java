package com.yuntianhe.thunder.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * desc:
 * author: daiwj on 2019-11-01 15:42
 */
public class MD5Util {

    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        md5.update(str.getBytes());
        byte[] md5Bytes = md5.digest();
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        String result = hexValue.toString();
        if (result != null) {
            result = result.toUpperCase(Locale.getDefault());
        }
        return result;
    }

    /**
     * MD5加密16位
     *
     * @param plainText
     * @return
     */
    public static String Md516(String plainText) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // result = buf.toString(); //md5 32bit
            // result = buf.toString().substring(8, 24))); //md5 16bit
            result = buf.toString().substring(8, 24);
            // LogUtil.I("md5 16bit: " + buf.toString().substring(8,
            // 24));
            // LogUtil.I("md5 32bit: " + buf.toString() );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (result != null) {
            return result.toUpperCase(Locale.getDefault());
        }
        return "";
    }

}
