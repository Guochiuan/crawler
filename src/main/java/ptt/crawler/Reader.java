package ptt.crawler;

import org.jsoup.select.Elements;
import ptt.crawler.model.*;
import ptt.crawler.config.Config;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reader {
    private OkHttpClient okHttpClient;
    private final Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private final CookieJar cookieJar;

    public Reader() throws IOException {
        /* 初始化 */
        cookieStore = new HashMap<>();
        cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                List<Cookie> cookies = cookieStore.getOrDefault(httpUrl.host(), new ArrayList<>());
                cookies.addAll(list);
                cookieStore.put(httpUrl.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookieStore.getOrDefault(httpUrl.host(), new ArrayList<>());
            }
        };
        okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        /* 獲得網站的初始 Cookie */
        Request request = new Request.Builder().get().url(Config.PTT_URL).build();
        okHttpClient.newCall(request).execute();
    }

    public List<Article> getList(String boardName) throws IOException, ParseException {
        Board board = Config.BOARD_LIST.get(boardName);

        /* 如果找不到指定的看板 */
        if (board == null) {
            return null;
        }

        /* 如果看板需要成年檢查 */
        if (board.getAdultCheck() == true) {
            runAdultCheck(board.getUrl());
        }

        /* 抓取目標頁面 */
        Request request = new Request.Builder()
                .url(Config.PTT_URL + board.getUrl())
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String body = response.body().string();

        /* 轉換 HTML 到 Article */
        List<Map<String, String>> articles = parseArticle(body);
        List<Article> result = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");

        for (Map<String, String> article: articles) {
            String url = article.get("url");
            String title = article.get("title");
            String author = article.get("author");
            Date date = simpleDateFormat.parse(article.get("date"));

            result.add(new Article(board, url, title, author, date));
        }

        return result;
    }

    /* 進行年齡確認 */
    private void runAdultCheck(String url) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("from", url)
                .add("yes", "yes")
                .build();

        Request request = new Request.Builder()
                .url(Config.PTT_URL + "/ask/over18")
                .post(formBody)
                .build();

        okHttpClient.newCall(request).execute();
    }

    /* 解析看板文章列表 */
    private List<Map<String, String>> parseArticle(String body) {
        List<Map<String, String>> result = new ArrayList<>();
        Document doc = Jsoup.parse(body);
        Elements articleList = doc.select(".r-ent");

        for (Element element: articleList) {
            String url = element.select(".title a").attr("href");
            String title = element.select(".title a").text();
            String author = element.select(".meta .author").text();
            String date = element.select(".meta .date").text();

            result.add(new HashMap<>(){{
                put("url", url);
                put("title", title);
                put("author", author);
                put("date", date);
            }});
        }

        return result;
    }

//    public String getBody(Article article) throws IOException {
//        /* 如果看板需要成年檢查 */
//        if (article.getParent().getAdultCheck()) {
//            runAdultCheck(article.getUrl());
//        }
//
//        /* 抓取目標頁面 */
//        Request request = new Request.Builder()
//                .url(Config.PTT_URL + article.getUrl())
//                .get()
//                .build();
//
//        Response response = okHttpClient.newCall(request).execute();
//        String body = response.body().string();
//        Document doc = Jsoup.parse(body);
//        Elements articleBody = doc.select("#main-content");
//
//        /* 移除部份不需要的資訊 */
//        articleBody.select(".article-metaline").remove();
//        articleBody.select(".article-metaline-right").remove();
//        articleBody.select(".push").remove(); // 回應內容
//
//        /* 回傳文章內容 */
//        return articleBody.text();
//    }


    public void getBody(Article article, Callback callback) throws IOException {
        /* 如果看板需要成年檢查 */
        if (article.getParent().getAdultCheck()) {
            runAdultCheck(article.getUrl());
        }

        /* 抓取目標頁面 */
        Request request = new Request.Builder()
                .url(Config.PTT_URL + article.getUrl())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(article);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Document doc = Jsoup.parse(body);
                Elements articleBody = doc.select("#main-content");

                /* 移除部份不需要的資訊 */
                articleBody.select(".article-metaline").remove();
                articleBody.select(".article-metaline-right").remove();
                articleBody.select(".push").remove(); // 回應內容

                /* 將內容直接設定給 Model */
                article.setBody(articleBody.text());

                callback.succeeded(article);
            }
        });
    }
    interface Callback {
        void succeeded(Article article);
        void failed(Article article);
    }

}
