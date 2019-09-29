package com.haiyunshan.pudding.utils;

import java.util.UUID;

/**
 *
 */
public class UUIDUtils {

    public static final String next() {
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");

        return id;
    }
}
