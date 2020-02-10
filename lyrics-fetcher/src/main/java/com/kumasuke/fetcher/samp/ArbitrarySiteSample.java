package com.kumasuke.fetcher.samp;

import com.kumasuke.fetcher.Fetcher;
import com.kumasuke.util.UserAgent;

import java.io.IOException;
import java.util.Scanner;

/**
 * 任意站点歌词获取示例
 */
public class ArbitrarySiteSample {
    public static void main(String[] args) {
        String page = new Scanner(System.in)
                .next();
        try {
            Fetcher fetcher = Fetcher.builder()
                    .autoMatch()
                    .page(page)
                    .userAgent(UserAgent.getUserAgent())
                    .build();

            // 使用 Java 8 新的 Stream API 输出结果
            fetcher.getHeader()
                    .forEach((n, v) -> System.out.println(String.format("%s = %s", n, v)));
            System.out.println();
            fetcher.getLyrics()
                    .forEach(System.out::println);
        } catch (IllegalArgumentException ignored) {
            System.out.println("输入的页面不受支持或有误：" + page);
        } catch (IOException ignored) {
            System.out.println("无法获取页面：" + page);
            ignored.printStackTrace();
        }
    }
}
