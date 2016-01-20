package cn.dengx.cousyncdb;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.dengx.cousyncdb.util.LogUtil;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/12,14:23.
 */
public class ClassParser {

    private static final Map<String, Container> containers = Collections.synchronizedMap(
            new LinkedHashMap<String, Container>());

    public static Container parse(@NonNull Class clazz) {
        return Container.create(clazz);
    }

    /**
     * @param clazz
     * @return or null
     */
    public static Container getContainer(@NonNull Class clazz) {
        String key = clazz.getName();
        Container container = containers.get(key);
        if (container == null) {
            container = parse(clazz);
            if (container != null)
                putContainer(key, container);
        } else
            LogUtil.i(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "getContainer-" + container);
        return container;
    }

    public static void putContainer(@NonNull Class clazz) {
        Container container = parse(clazz);
        String key = clazz.getName();
        putContainer(key, container);
    }

    public static void putContainer(@NonNull String key, @NonNull Container container) {
        containers.put(key, container);
        LogUtil.i(CouSyncDb.TAG, CouSyncDb.LOG_HEADER + "putContainer-" + container);
    }
}
