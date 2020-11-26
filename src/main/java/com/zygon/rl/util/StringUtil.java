package com.zygon.rl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author zygon
 */
public class StringUtil {

    public static Gson JSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
}
