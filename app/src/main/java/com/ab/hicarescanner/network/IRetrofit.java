package com.ab.hicarescanner.network;


import com.ab.hicarescanner.network.model.inventory.InventoryRequest;
import com.ab.hicarescanner.network.model.inventory.InventoryResponse;
import com.ab.hicarescanner.network.model.login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRetrofit {

    String BASE_URL = "http://apps.hicare.in/api/api/";


    @GET("Authentication/Login")
    Call<LoginResponse> getLogin(@Query("username") String mobile, @Query("password") String isResend);

    @POST("Inventory/SaveData")
    Call<InventoryResponse> getInventoryData(@Body InventoryRequest request);

}
