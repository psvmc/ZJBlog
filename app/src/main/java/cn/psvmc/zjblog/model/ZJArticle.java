package cn.psvmc.zjblog.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by PSVMC on 16/7/19.
 */

@Table("zjarticle")
public class ZJArticle {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    @Column("title")
    public String title;
    @Column("keywords")
    public String keywords;
    @Column("path")
    public String path;
    @Column("description")
    public String description;
    @Column("date")
    public String date;
    @Column("url")
    public String url;

    @Override
    public String toString() {
        return "ZJArticle{" +
                "title='" + title + '\'' +
                ", keywords='" + keywords + '\'' +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public ZJArticle() {
    }

}
