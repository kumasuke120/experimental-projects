package com.kumasuke.fetcher.ftr;

import com.kumasuke.fetcher.FetcherBuilder;

import java.io.IOException;

/**
 * 歌詞検索 (J-Lyric.net) 的歌词获取器。
 */
public class JLyricFetcher extends AbstractUnitedFetcher<JLyricUnitedParser> {
    /**
     * 构造一个 {@code JLyricFetcher} 对象，用于获取对应网站歌词相关信息。<br>
     * 也可使用 {@link FetcherBuilder FetcherBuilder} 来进行构造。
     *
     * @param page      歌词页地址
     * @param userAgent {@code UserAgent} 字符串
     * @throws IOException 页面连接、处理失败
     */
    public JLyricFetcher(String page, String userAgent) throws IOException {
        super(page, userAgent);

        parser = new JLyricUnitedParser(page, userAgent);
    }
}
