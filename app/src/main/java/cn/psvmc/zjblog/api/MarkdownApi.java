package cn.psvmc.zjblog.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by PSVMC on 16/7/19.
 */
public interface MarkdownApi {

    @GET("/psvmc/psvmc.github.io/master/{path}")
    Call<String> getContentText(@Path("path") String path);
}
