package com.kumasuke.fetcher;

import com.kumasuke.util.TestHelper;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * 获取和输出测试
 */
public class FetchAndPrintResultTest {
    private static void printFetcher(Fetcher fetcher) {
        System.out.println("Source = " + fetcher.getSource());
        fetcher.getHeader().forEach(System.out::println);
        System.out.println("------------------------------------");
        fetcher.getLyrics().forEach(System.out::println);
    }

    private static void testStart(String site, String page) {
        Fetcher fetcher = TestHelper.tryToGet(args ->
                Fetcher.builder()
                        .site((String) args[0])
                        .page((String) args[1])
                        .build(), site, page);

        assertNotNull(fetcher);
        assertNotNull(fetcher.getHeader());
        assertNotNull(fetcher.getLyrics());
        assertNotNull(fetcher.getSource());

        printFetcher(fetcher);
    }

    @Test
    public void aniMap() {
        testStart("AniMap.jp", "k-140806-069");
    }

    @Test
    public void evesta() {
        testStart("Evesta.jp", "www.evesta.jp/lyric/artists/a359772/lyrics/l223589.html");
    }

    @Test
    public void jLyric() {
        testStart("J-Lyric.net", "http://j-lyric.net/artist/a057818/l031ba7.html");
    }

    @Test
    public void joySound() {
        testStart("JoySound.com", "405267");
    }

    @Test
    public void animeSong() {
        testStart("Jtw.Zaq.Ne.jp/AnimeSong", "http://www.jtw.zaq.ne.jp/animesong/me/konan/munega.html");
    }

    @Test
    public void kashiNavi() {
        testStart("KashiNavi.com", "83934");
    }

    @Test
    public void kasiTime() {
        testStart("Kasi-Time.com", "http://www.kasi-time.com/item-73631.html");
    }

    @Test
    public void kGet() {
        testStart("KGet.jp",
                "http://www.kget.jp/lyric/171135/Good+Time+%28with+Owl+City%29_Carly+Rae+Jepsen%2C+Owl+City");
    }

    @Test
    public void petitLyrics() {
        testStart("PetitLyrics.com", "1031097");
    }

    @Test
    public void utaMap() {
        testStart("UtaMap.com", "k-150415-182");
    }

    @Test
    public void utaNet() {
        testStart("Uta-Net.com", "183656");
    }

    @Test
    public void utaTen() {
        testStart("UtaTen.com", "utaten.com/lyric/Neru,鏡音リン,鏡音レン/ハウトゥー世界征服/");
    }
}
