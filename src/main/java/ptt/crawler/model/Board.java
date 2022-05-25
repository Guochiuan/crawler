package ptt.crawler.model;

public class Board {
    private String url; // 看板網址
    private String nameCN; // 中文名稱
    private String nameEN; // 英文名稱
    private Boolean adultCheck; // 成年檢查

    public Board(String url, String nameCN, String nameEN, Boolean adultCheck) {
        this.url = url;
        this.nameCN = nameCN;
        this.nameEN = nameEN;
        this.adultCheck = adultCheck;
    }

    public String getUrl() {
        return url;
    }

    public String getNameCN() {
        return nameCN;
    }

    public String getNameEN() {
        return nameEN;
    }

    public Boolean getAdultCheck() {
        return adultCheck;
    }
}