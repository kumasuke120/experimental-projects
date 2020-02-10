package com.kumasuke.fetcher.ftr;

import com.kumasuke.fetcher.Header;
import com.kumasuke.util.Tools;

import java.util.*;

import static java.util.Objects.*;

/**
 * 存放歌曲基本信息的容器。<br>
 * 使用 {@code EnumMap} 存放信息。
 */
class EnumHeader implements Header {
    private final Map<Type, Set<String>> data;
    // 用作迭代的 Set 对象
    private Set<Item> iteratorSet;

    /**
     * 构造一个 {@code EnumHeader} 对象。
     */
    EnumHeader() {
        data = new EnumMap<>(Type.class);
    }

    /**
     * 获取歌曲标题。
     *
     * @return 歌曲标题
     */
    @Override
    public String getTitle() {
        Iterator<String> iterator = data.get(Type.TITLE).iterator();
        if (!iterator.hasNext()) {
            throw new RuntimeException("Unexpected Error!");
        }

        String result = iterator.next();
        if (iterator.hasNext()) {
            throw new RuntimeException("Unexpected Error!");
        }

        return result;
    }

    /**
     * 设置歌曲标题。
     *
     * @param title 歌曲标题
     * @return {@code EnumHeader} 对象，便于链式编程
     */
    EnumHeader setTitle(String title) {
        data.put(Type.TITLE, Tools.toSet(title));

        return this;
    }

    /**
     * 获取歌曲演唱者。
     *
     * @return 歌曲演唱者
     */
    @Override
    public Set<String> getArtist() {
        Set<String> result = data.get(Type.ARTIST);

        return isNull(result) ? null : Collections.unmodifiableSet(result);
    }

    /**
     * 设置歌曲演唱者。
     *
     * @param artist 歌曲演唱者
     * @return {@code EnumHeader} 对象，便于链式编程
     */
    EnumHeader setArtist(Set<String> artist) {
        data.put(Type.ARTIST, artist);

        return this;
    }

    /**
     * 获取歌曲作词者。
     *
     * @return 歌曲作词者
     */
    @Override
    public Set<String> getLyricist() {
        Set<String> result = data.get(Type.LYRICIST);

        return isNull(result) ? null : Collections.unmodifiableSet(result);
    }

    /**
     * 设置歌曲作词者。
     *
     * @param lyricist 歌曲作词者
     * @return {@code EnumHeader} 对象，便于链式编程
     */
    EnumHeader setLyricist(Set<String> lyricist) {
        data.put(Type.LYRICIST, lyricist);

        return this;
    }

    /**
     * 获取歌曲作曲者。
     *
     * @return 歌曲作曲者
     */
    @Override
    public Set<String> getComposer() {
        Set<String> result = data.get(Type.COMPOSER);

        return isNull(result) ? null : Collections.unmodifiableSet(result);
    }

    /**
     * 设置歌曲作曲者。
     *
     * @param composer 作曲者
     * @return {@code EnumHeader} 对象，便于链式编程
     */
    EnumHeader setComposer(Set<String> composer) {
        data.put(Type.COMPOSER, composer);

        return this;
    }

    /**
     * 获取歌曲编曲者。
     *
     * @return 歌曲编曲者
     */
    @Override
    public Set<String> getArranger() {
        Set<String> result = data.get(Type.ARRANGER);

        return isNull(result) ? null : Collections.unmodifiableSet(result);
    }

    /**
     * 设置歌曲编曲者。
     *
     * @param arranger 歌曲编曲者
     * @return {@code EnumHeader} 对象，便于链式编程
     */
    EnumHeader setArranger(Set<String> arranger) {
        data.put(Type.ARRANGER, arranger);

        return this;
    }

    /**
     * 获取 {@code Iterator} 对象以便进行迭代。
     * <p>
     * 将会按照标题、歌手、作词、作曲和编曲的顺序进行排列，如果不存在该项信息，则会跳过该信息。<br>
     * 如果对返回的 {@code Iterator} 对象进行修改操作将会抛出
     * {@code UnsupportedOperationException} 异常。</p>
     *
     * @return {@code Iterator} 对象
     * {@code Set.iterator()}，这将耗费一定的时间。
     */
    @Override
    public Iterator<Item> iterator() {
        if (isNull(iteratorSet)) {
            iteratorSet = new LinkedHashSet<>();

            data.entrySet()
                    .stream()
                    .map(e -> new SetItem(e.getKey().toString(), e.getValue()))
                    .forEach(iteratorSet::add);
        }

        return Collections.unmodifiableSet(iteratorSet).iterator();
    }

    /**
     * 歌曲基本信息类别枚举。
     */
    private enum Type {
        /**
         * 标题，歌曲名
         */
        TITLE("Title"),
        /**
         * 歌手，歌曲的演唱者或团体
         */
        ARTIST("Artist"),
        /**
         * 作词，歌曲的作词者
         */
        LYRICIST("Lyricist"),
        /**
         * 作曲，歌曲的作曲者
         */
        COMPOSER("Composer"),
        /**
         * 编曲，歌曲的编曲者
         */
        ARRANGER("Arranger");

        // 显示、获取用名
        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * 包含名称和值的歌曲基本信息条目。<br>
     * 使用 {@code String} 存放信息。
     */
    private static class SetItem implements Item {
        private final String name;
        private final Set<String> value;

        /**
         * 构造一个 {@code StringItem} 对象。
         *
         * @param name  条目名称
         * @param value 条目值
         */
        private SetItem(String name, Set<String> value) {
            this.name = requireNonNull(name, "The name value should not be null.");
            this.value = value;
        }

        /**
         * 获取条目名称。
         *
         * @return 条目名称
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * 获取条目值。
         *
         * @return 条目值
         */
        @Override
        public Set<String> getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return hash(name, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Item))
                return false;

            Item i = (Item) obj;
            return Objects.equals(name, i.getName()) &&
                    Objects.equals(value, i.getValue());
        }

        /**
         * 返回包含条目信息的字符串的字符串。
         *
         * @return 包含条目信息的字符串，其格式为 <code>"<i>Name</i> = <i>Value</i>"</code>
         */
        @Override
        public String toString() {
            return String.format("%s = %s", name, value);
        }
    }
}
