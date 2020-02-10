package com.kumasuke.fetcher.ftr;

import com.kumasuke.fetcher.Lyrics;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * 歌詞タイム (Kasi-Time.com) 的歌词分析器。
 * 使用 {@code Jsoup} 包获取页面信息。
 */
class KasiTimeLyricsParser extends LyricsParser {
    // 提取歌词的正则表达式
    private static final Pattern lyricsPattern;

    static {
        lyricsPattern = Pattern.compile("var lyrics = '([^']+)");
    }

    private String js;

    private ListLyrics lyrics;

    /**
     * 构造一个 {@code KasiTimeLyricsParser} 对象。
     *
     * @param songPage {@code KasiTimeSongPageParser} 对象<br>
     * @throws IOException 页面连接、处理失败
     */
    KasiTimeLyricsParser(KasiTimeSongPageParser songPage) throws IOException {
        Matcher lyricsMatcher = lyricsPattern.matcher(songPage.lrcPageContent());
        if (lyricsMatcher.find())
            this.js = lyricsMatcher.group(1);
    }

    /**
     * 获取歌词文本。
     *
     * @return 装有歌词文本的 {@code Lyrics} 容器
     */
    @Override
    Lyrics lyrics() {
        if (isNull(lyrics)) {
            String[] lyricsText = js.split("<br(?: /)?>");

            lyrics = toLyrics(Parser::parseHtml, lyricsText);
        }

        return lyrics;
    }
}
