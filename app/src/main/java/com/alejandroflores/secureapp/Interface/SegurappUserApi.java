package com.alejandroflores.secureapp.Interface;

import com.alejandroflores.secureapp.Model.UsersPosts;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SegurappUserApi {

    @GET("api/users")
    Call<List<UsersPosts>> getNearestUsers(@Query("lng") Double lng, @Query("lat") Double lat);

    @POST("api/users")
    Call<UsersPosts> createPost(@Body UsersPosts usersPosts);

    @PUT("api/users/{id}")
    Call<UsersPosts> putPost(@Path("id") String id, @Body UsersPosts usersPosts);

}
