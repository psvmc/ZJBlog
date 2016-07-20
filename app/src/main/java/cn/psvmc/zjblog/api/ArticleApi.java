package cn.psvmc.zjblog.api;

import java.util.List;

import cn.psvmc.zjblog.model.ZJArticle;
import cn.psvmc.zjblog.model.ZJResult;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by PSVMC on 16/7/19.
 */
public interface ArticleApi {

    @GET("/navi_list.json")
    Call<ZJResult<List<ZJArticle>>> getListData();

}
