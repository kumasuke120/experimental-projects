package com.kumasuke.util;

import java.util.Objects;

/**
 * 组合，用于一次传递两个参数或组成 {@code Map}，其内容无法修改。
 *
 * @param <F> 第一个参数的类型
 * @param <S> 第二个参数的类型
 */
public class Pair<F, S> {
    private final F first;
    private final S second;

    /**
     * 构造一个 {@code Pair} 对象。
     *
     * @param first  第一个参数
     * @param second 第二个参数
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 利用泛型机制，构造一个 {@code Pair} 对象，构造时无需指定类型，可用于方法返回值。
     *
     * @param first  第一个参数
     * @param second 第二个参数
     * @param <T>    第一个参数的类型
     * @param <U>    第二哥参数的类型
     * @return {@code Pair} 对象
     */
    public static <T, U> Pair<T, U> make(T first, U second) {
        return new Pair<>(first, second);
    }

    /**
     * 构造一个新的 {@code Pair} 对象，其值与所给定的 {@code Pair} 对象相反。
     *
     * @param pair 给定的 {@code Pair} 对象
     * @param <T>  给定的 {@code Pair} 对象的第一个参数的类型
     * @param <U>  给定的 {@code Pair} 对象的第二个参数的类型
     * @return 交换给定 {@code Pair} 对象参数顺序的新 {@code Pair} 对象
     */
    public static <T, U> Pair<U, T> swap(Pair<T, U> pair) {
        return new Pair<>(pair.second, pair.first);
    }

    /**
     * 返回组合的第一个参数。
     *
     * @return 第一个参数
     */
    public F getFirst() {
        return first;
    }

    /**
     * 返回组合的第二个参数。
     *
     * @return 第二个参数
     */
    public S getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Pair))
            return false;

        Pair p = (Pair) obj;
        return Objects.equals(first, p.first) &&
                Objects.equals(second, p.second);
    }

    /**
     * 返回包含该组合信息的字符串。
     *
     * @return 包含该组合信息的字符串，其格式为  <code>"Pair: (<i>first</i>, <i>second</i>)"</code>
     */
    @Override
    public String toString() {
        return String.format("Pair: (%s, %s)", first, second);
    }
}
