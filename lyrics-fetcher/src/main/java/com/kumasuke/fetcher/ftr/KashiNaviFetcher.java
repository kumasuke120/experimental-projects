package com.kumasuke.fetcher.ftr;

import com.kumasuke.fetcher.FetcherBuilder;

import java.io.IOException;

/**
 * 歌詞ナビ (KashiNavi.com) 的歌词获取器。
 */
public class KashiNaviFetcher extends AbstractSplitFetcher<KashiNaviSongPageParser, KashiNaviLyricsParser> {
    /**
     * 构造一个 {@code KashiNaviFetcher} 对象，用于获取对应网站歌词相关信息。<br>
     * 也可使用 {@link FetcherBuilder FetcherBuilder} 来进行构造。
     *
     * @param page      歌词页地址或歌曲代码
     * @param userAgent {@code UserAgent} 字符串
     * @throws IOException 页面连接、处理失败
     */
    public KashiNaviFetcher(String page, String userAgent) throws IOException {
        super(page, userAgent);

        songPageParser = new KashiNaviSongPageParser(page, userAgent);
        lyricsParser = new KashiNaviLyricsParser(songPageParser, userAgent);
    }
}
