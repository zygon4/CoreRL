package com.zygon.rl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

/**
 *
 * @author zygon
 */
public class StringUtil {

    private StringUtil() {
    }

    public static final Gson JSON = new GsonBuilder()
            .setPrettyPrinting()
            .setStrictness(Strictness.LENIENT)
            .create();
}
