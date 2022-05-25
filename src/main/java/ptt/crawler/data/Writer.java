package ptt.crawler.data;

import ptt.crawler.model.Article;

public interface Writer {
    void save(Article article) throws Exception;
}