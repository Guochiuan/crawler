package ptt.crawler.model;

import java.util.*;

public class Article {
    private Board parent; // 所屬板塊
    private String url; // 網址
    private String title; // 標題
    private String body; // 內容
    private String author; // 作者
    private Date date; // 發文時間

    public Article(Board parent, String url, String title, String author, Date date) {
        this.parent = parent;
        this.url = url;
        this.title = title;
        this.author = author;
        this.date = date;
    }

    public Board getParent() {
        return parent;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("Article{ url='%s', title='%s', body='%s', author='%s', date='%s' }", url, title, body, author, date);
    }
}