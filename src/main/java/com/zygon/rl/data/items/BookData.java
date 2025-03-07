package com.zygon.rl.data.items;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import com.zygon.rl.data.ItemClass;
import com.zygon.rl.util.StringUtil;

/**
 *
 */
public class BookData extends ItemClass {

    private static final Map<String, BookData> BY_ID = new HashMap<>();

    private static final Type TYPE = new TypeToken<List<BookData>>() {
    }.getType();

    private static final String BOOK_PATH = "/data/items/book.json";

    private static void load(String path) throws FileNotFoundException, IOException {
        try (Reader jsonReader = new BufferedReader(new InputStreamReader(
                BookData.class.getResourceAsStream(path)))) {
            List<BookData> items = StringUtil.JSON.fromJson(jsonReader, TYPE);

            BY_ID.putAll(items.stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m)));
        }
    }

    public static void load() throws FileNotFoundException, IOException {
        load(BOOK_PATH);
        // TODO: more
    }

    public static BookData get(String id) {
        return BY_ID.get(id);
    }

    public static Set<String> getAllIds() {
        return BY_ID.keySet();
    }
}
