package ptt.crawler.data;

import ptt.crawler.model.Article;
import java.io.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileWriter implements Writer {
    @Override
    public void save(Article article) throws IOException {

        String title = article.getTitle();
        Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(title);
        if(hasSpecial.find()){
            Random rand = new Random();
            int n = rand.nextInt(99999999);
            title = "no_title_" +n;
        }


        File file = new File(
                String.format("data/%s/%s.txt", article.getParent().getNameEN(), title)
        );
        file.getParentFile().mkdirs();
        file.createNewFile();

        java.io.FileWriter writer = new java.io.FileWriter(file);
        writer.append(String.format("%s\r\n%s", article.getAuthor(), article.getBody()));
        writer.close();
    }
}