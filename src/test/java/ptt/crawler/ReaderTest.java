package ptt.crawler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ptt.crawler.data.FileWriter;
import ptt.crawler.data.Writer;
import ptt.crawler.model.Article;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

class ReaderTest {
    private Reader reader;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            reader = new Reader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void list() {
        try {
            List<Article> result = reader.getList("Gossiping");
            Assertions.assertInstanceOf(List.class, result);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    void article() {
//        try {
//            List<Article> result = reader.getList("Gossiping");
//
//            for (Article article: result) {
//                System.out.println(reader.getBody(article));
//                Thread.sleep(2000);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    void articleAsync() {
        try {
            List<Article> result = reader.getList("Gossiping");

            for (Article article: result) {
                reader.getBody(article, new Reader.Callback() {
                    @Override
                    public void succeeded(Article article) {
                        System.out.println(article.getBody());
                    }

                    @Override
                    public void failed(Article article) {
                        System.out.println("失敗");
                    }
                });
                Thread.sleep(2000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    void saveArticle() {
        try {
            List<Article> result = reader.getList("Gossiping");
            Writer writer = new FileWriter();

            for (Article article: result) {
                reader.getBody(article, new Reader.Callback() {
                    @Override
                    public void succeeded(Article article) {
                        try {
                            writer.save(article);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Article article) {
                        System.out.println("失敗");
                    }
                });
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}