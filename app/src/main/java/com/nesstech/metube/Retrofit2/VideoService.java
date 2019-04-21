package com.nesstech.metube.Retrofit2;

import com.nesstech.metube.youmodel.YoutubeData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VideoService {

    @GET("videos?")
    Call<YoutubeData> getTopRatedMovies(
            @Query("part") String snippet,
            @Query("maxResults") int maxResults,
            @Query("chart") String chart,
            //@Query("regionCode") String regionCode,
            @Query("videoCategoryId") String vid,
            @Query("key") String key,
            @Query("pageToken") String pageToken);


    @GET("videos?")
    Call<YoutubeData> getToTrandingVideo(
            @Query("part") String snippet,
            @Query("maxResults") String maxResults,
            @Query("chart") String chart,
            @Query("regionCode") String regionCode,
            @Query("key") String key);
}
