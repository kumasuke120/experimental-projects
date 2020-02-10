package com.kumasuke.fetcher.ftr;

import com.kumasuke.fetcher.Lyrics;

/**
 * 歌词分析器，以便于 {@code AbstractSplitFetcher} 进行代码复用。<br>
 * 用于分析获取歌词文本。
 */
abstract class LyricsParser extends Parser {
    /**
     * 获取歌词文本。
     *
     * @return 歌词文本
     */
    abstract Lyrics lyrics();

    /**
     * 获取含有注音的歌词文本。
     *
     * @return 装有歌词文本的 {@code Lyrics} 对象或 {@code null} 值
     */
    Lyrics lyricsWithRuby() {
        return null;
    }
}
