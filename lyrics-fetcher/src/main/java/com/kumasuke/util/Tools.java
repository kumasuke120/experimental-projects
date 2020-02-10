package com.kumasuke.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工具类，提供系列工具方法。
 *
 * @author Joash Lee (bearcomingx#gmail.com)
 * @version 1.1
 */
public class Tools {
    // 工具类，防止被创建
    private Tools() {
        throw new AssertionError();
    }

    /**
     * 返回集合类是否非 null 且非空。
     *
     * @param collection 集合类
     * @return 是否非 null 且非空
     */
    public static boolean nonNullAndNonEmpty(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 返回字符序列类是否非 null 且非空。
     *
     * @param charSequence 字符序列类
     * @return 是否非 null 且非空
     */
    public static boolean nonNullAndNonEmpty(CharSequence charSequence) {
        return charSequence != null && charSequence.length() != 0;
    }

    /**
     * 返回集合类是否为 null 或空。
     *
     * @param collection 集合类
     * @return 是否为 null 或空
     */
    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 返回字符序列类是否为 null 或空。
     *
     * @param charSequence 字符序列类
     * @return 是否为 null 或空
     */
    public static boolean isNullOrEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    /**
     * 要求传入的集合类对象非 null 且非空，否则抛出异常。
     *
     * @param collection 集合类
     * @param <T>        集合类具体类型
     * @return 传入的集合类对象
     * @throws NullPointerException     传入的集合类对象为 null
     * @throws IllegalArgumentException 传入的集合类对象为空
     */
    public static <T extends Collection> T requireNonNullAndNonEmpty(T collection) {
        if (collection == null)
            throw new NullPointerException();
        if (collection.isEmpty())
            throw new IllegalArgumentException();

        return collection;
    }

    /**
     * 要求传入的集合类对象非 null 且非空，否则抛出异常，并指定抛出的异常信息。
     *
     * @param collection   集合类
     * @param nullMessage  对象为 null 的异常信息
     * @param emptyMessage 对象为空的异常信息
     * @param <T>          集合类具体类型
     * @return 传入的集合类对象
     * @throws NullPointerException     传入的集合类对象为 null
     * @throws IllegalArgumentException 传入的集合类对象为空
     */
    public static <T extends Collection> T requireNonNullAndNonEmpty
    (T collection, String nullMessage, String emptyMessage) {
        if (collection == null)
            throw new NullPointerException(nullMessage);
        if (collection.isEmpty())
            throw new IllegalArgumentException(emptyMessage);

        return collection;
    }

    public static <T extends CharSequence> T requireNonNullAndNonEmpty(T charSequence) {
        if (charSequence == null)
            throw new NullPointerException();
        if (charSequence.length() == 0)
            throw new IllegalArgumentException();

        return charSequence;
    }

    public static <T extends CharSequence> T requireNonNullAndNonEmpty
            (T charSequence, String nullMessage, String emptyMessage) {
        if (charSequence == null)
            throw new NullPointerException(nullMessage);
        if (charSequence.length() == 0)
            throw new IllegalArgumentException(emptyMessage);

        return charSequence;
    }

    /**
     * 将一个或多个对象转换为一个 {@code Set} 对象。
     *
     * @param args 需要转换的对象
     * @param <T>  输入参数类型
     * @return 转换后的 {@code Set} 对象
     */
    @SafeVarargs
    public static <T> Set<T> toSet(T... args) {
        return Stream.of(args)
                .collect(Collectors.toSet());
    }

    /**
     * 将一个或多个对象转换为一个 {@code Set} 对象，同时使用给定的映射转换对象。
     *
     * @param mapper 指定映射
     * @param args   需要转换的对象
     * @param <T>    输入参数类型
     * @param <R>    返回集合内部值类型
     * @return 装有映射后的对象的 {@code Set} 对象
     */
    @SafeVarargs
    public static <T, R> Set<R> toSet(Function<T, R> mapper, T... args) {
        return Stream.of(args)
                .map(mapper)
                .collect(Collectors.toSet());
    }

    /**
     * 生成一个键值对，一旦创建完成，无法修改。
     *
     * @param key   {@code key} 值
     * @param value {@code value} 值
     * @param <K>   key 值类型
     * @param <V>   value 值类型
     * @return 生成的键值对，可作为 {@link Tools#toMap(Pair[]) toMap(P&lt;K, V&gt;...)} 的参数
     */
    public static <K, V> Pair<K, V> p(K key, V value) {
        return new Pair<>(key, value);
    }

    /**
     * 将一个或多个键值对转换为一个 {@code Map} 对象。
     *
     * @param args 需要转换的键值对，由 {@link Tools#p(Object, Object) p(K, V)} 方法生成
     * @param <K>  key 值类型
     * @param <V>  value 值类型
     * @return 装有传入的键值对的 {@code Map} 对象
     */
    @SafeVarargs
    public static <K, V> Map<K, V> toMap(Pair<K, V>... args) {
        return Stream.of(args)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * 将一个或多个对象转换为一个 {@code List} 对象。
     *
     * @param args 需要转换的对象
     * @param <T>  输入参数类型
     * @return 转换后的 {@code List} 对象
     */
    @SafeVarargs
    public static <T> List<T> toList(T... args) {
        return Stream.of(args)
                .collect(Collectors.toList());
    }

    /**
     * 将一个或多个对象转换为一个 {@code List} 对象，同时使用给定的映射转换对象。
     *
     * @param mapper 指定映射
     * @param args   需要转换的对象
     * @param <T>    输入参数类型
     * @param <R>    返回集合内部值类型
     * @return 装有映射后的对象的 {@code List} 对象
     */
    @SafeVarargs
    public static <T, R> List<R> toList(Function<T, R> mapper, T... args) {
        return Stream.of(args)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
