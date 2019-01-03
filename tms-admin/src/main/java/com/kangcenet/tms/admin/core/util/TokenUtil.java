package com.kangcenet.tms.admin.core.util;

import java.util.Random;

public class TokenUtil {
    private static final String POOL = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Random random = new Random();

    public static String genToken(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(POOL.length());
            builder.append(POOL.charAt(index));
        }
        return builder.toString();
    }
}
