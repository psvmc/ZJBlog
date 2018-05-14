package cn.psvmc.zjblog.db;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;

import cn.psvmc.zjblog.application.ZJApp;
import cn.psvmc.zjblog.model.ZJArticle;

/**
 * Created by PSVMC on 16/7/20.
 */
public class ZJArticleDb {
    public static LiteOrm liteOrm;
    public static ZJArticleDb articleDb;

    ZJArticleDb() {
    }

    public static ZJArticleDb getInstance() {
        if (null == articleDb) {
            liteOrm = ZJApp.liteOrm;
            articleDb = new ZJArticleDb();
        }
        return articleDb;
    }

    public void insertArticles(List<ZJArticle> articles) {
        for (ZJArticle article : articles) {
            liteOrm.insert(article, ConflictAlgorithm.Abort);
        }
    }

    public List<ZJArticle> queryByPage(int page) {
        int start = page *  20;
        List<ZJArticle> articles = liteOrm.query(
                new QueryBuilder<ZJArticle>(ZJArticle.class)
                .distinct(true)
                .limit(start, 20)
        );
        return articles;
    }

    public void deleteAll(){
        liteOrm.deleteAll(ZJArticle.class);
    }
}
