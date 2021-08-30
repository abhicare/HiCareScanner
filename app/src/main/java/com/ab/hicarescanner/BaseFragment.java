package com.ab.hicarescanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ab.hicarescanner.activities.LoginActivity;
import com.ab.hicarescanner.utils.SharedPreferencesUtility;

import io.realm.Realm;

/**
 * Created by arjun on 30/01/20.
 */

public class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    private Realm mRealm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mRealm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        Log.i("ProgressDialog", "showProgressDialog");
        try {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.TransparentProgressDialog);
            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (!mProgressDialog.isShowing()) {
                return;
            }
            Log.i("ProgressDialog", "dismissProgressDialog");
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showServerError() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Network Error");
            builder.setMessage(
                    "Unable to connect to server, please check your internet connection and try again.");
            builder.setNeutralButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showServerError(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg);
            builder.setNeutralButton("Dismiss", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void replaceFragment(BaseFragment fragment, String tag) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    protected void storeBoolean(String key, Boolean value) {
        SharedPreferences pref = getActivity().getApplicationContext()
                .getSharedPreferences("hicare_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected Boolean getBoolean(String key, Boolean defauleValue) {
        SharedPreferences pref = getActivity().getApplicationContext()
                .getSharedPreferences("hicare_pref", Context.MODE_PRIVATE);
        return pref.getBoolean(key, defauleValue);
    }

    protected void logout() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("Logout");
        dialog.setMessage("Do you want to logout?");
        dialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            SharedPreferencesUtility.savePrefBoolean(getActivity(), SharedPreferencesUtility.IS_USER_LOGIN,
                    false);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        dialog.setNegativeButton("No", null);
        dialog.show();
    }

}
