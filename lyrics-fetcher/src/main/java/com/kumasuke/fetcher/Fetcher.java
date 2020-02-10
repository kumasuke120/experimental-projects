package com.kumasuke.fetcher;

/**
 * 歌词获取器，可以获取歌曲信息、歌词和歌词来源
 */
public interface Fetcher {
    /**
     * 获取一个新的 {@code FetcherBuilder} 对象<br>
     * 仅为方便使用而存在的方法，效果等同于调用 {@link FetcherBuilder#newBuilder()}。
     *
     * @return {@code FetcherBuilder} 对象
     */
    static FetcherBuilder builder() {
        return FetcherBuilder.newBuilder();
    }

    /**
     * 获取歌曲基本信息，包括标题、歌手、作词和作曲等。
     *
     * @return 装有歌曲信息的 {@code Header} 容器
     * @see Header
     */
    Header getHeader();

    /**
     * 获取歌词文本，按行存放在 {@code Lyrics} 对象中，如果存在空行则该行对应字符串为空。
     *
     * @return 装有歌词文本的 {@code Lyrics} 对象
     * @see Lyrics
     */
    Lyrics getLyrics();

    /**
     * 获取含有注音的歌词文本，按行存放在 {@code Lyrics} 对象中，如果存在空行则该行对应字符串为空。<br>
     * 只有部分站点支持获取含注音的歌词文本，当站点不支持时将会返回 {@code null} 值。
     *
     * @return 装有歌词文本的 {@code Lyrics} 对象或 {@code null} 值
     * @see Lyrics
     */
    Lyrics getLyricsWithRuby();

    /**
     * 获取歌词来源地址。
     *
     * @return 歌词来源地址
     */
    String getSource();
}
