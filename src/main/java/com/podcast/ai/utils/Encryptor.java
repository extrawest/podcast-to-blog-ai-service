package com.podcast.ai.utils;

import com.google.common.hash.Hashing;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class Encryptor {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static String sha1(String original) {
        return Hashing.sha1().hashString(original, UTF_8).toString();
    }

}
