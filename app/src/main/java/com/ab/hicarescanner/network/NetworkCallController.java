package com.ab.hicarescanner.network;


import android.os.Build;

import com.ab.hicarescanner.BaseApplication;
import com.ab.hicarescanner.BaseFragment;
import com.ab.hicarescanner.network.model.inventory.InventoryRequest;
import com.ab.hicarescanner.network.model.inventory.InventoryResponse;
import com.ab.hicarescanner.network.model.login.LoginResponse;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Arjun Bhatt on 1/23/2020.
 */
public class NetworkCallController {
    private final BaseFragment mContext;
    private NetworkResponseListner mListner;

    public NetworkCallController(BaseFragment context) {
        this.mContext = context;
    }

    public NetworkCallController() {
        this.mContext = null;
    }

    public void setListner(NetworkResponseListner listner) {
        this.mListner = listner;
    }

    public void login(final int requestCode, String username, String password) {

        mContext.showProgressDialog();

        BaseApplication.getRetrofitAPI(false)
                .getLogin(username, password)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        mContext.dismissProgressDialog();

                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getLoginData());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(response.errorBody().string());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Something went wrong, please try again !!!");
                    }
                });
    }

    public void getInventoryData(final int requestCode, InventoryRequest request) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(false)
                .getInventoryData(request)
                .enqueue(new Callback<InventoryResponse>() {
                    @Override
                    public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                        mContext.dismissProgressDialog();

                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(response.errorBody().string());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<InventoryResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Something went wrong, please try again!");
                    }
                });
    }

}
