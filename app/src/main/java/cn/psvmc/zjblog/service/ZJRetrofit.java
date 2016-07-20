package cn.psvmc.zjblog.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.psvmc.zjblog.api.ArticleApi;
import cn.psvmc.zjblog.api.MarkdownApi;
import cn.psvmc.zjblog.model.ApiModel;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by PSVMC on 16/7/19.
 */
public class ZJRetrofit {
    final ArticleApi articleApi;

    final MarkdownApi markdownApi;

    static ZJRetrofit zjRetrofit;

    final static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create();


    public static ZJRetrofit getInstance() {
        if (null == zjRetrofit) {
            zjRetrofit = new ZJRetrofit();
        }
        return zjRetrofit;
    }

    private ZJRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiModel.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        articleApi = retrofit.create(ArticleApi.class);

        markdownApi = new Retrofit.Builder()
                .baseUrl(ApiModel.baseUrlForMD)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(MarkdownApi.class);
    }


    public ArticleApi getArticleService() {
        return articleApi;
    }

    public MarkdownApi getMarkdownService() {
        return markdownApi;
    }

}
