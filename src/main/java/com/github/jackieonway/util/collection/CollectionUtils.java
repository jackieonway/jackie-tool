/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.collection;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jackie
 * @version $id: CollectionUtils.java v 0.1 2021-01-28 11:03 Jackie Exp $$
 */
public enum  CollectionUtils {

    /**
     * CollectionUtil instance
     */
    INSTANCE;

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code false} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code true}.
     * @param collection the Collection to check
     * @return whether the given Collection is not empty
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Return {@code false} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code true}.
     * @param map the Map to check
     * @return whether the given Map is not empty
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }
}
