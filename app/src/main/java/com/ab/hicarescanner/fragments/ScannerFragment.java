package com.ab.hicarescanner.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.hicarescanner.BaseApplication;
import com.ab.hicarescanner.BaseFragment;
import com.ab.hicarescanner.R;
import com.ab.hicarescanner.activities.HomeActivity;
import com.ab.hicarescanner.databinding.FragmentScannerBinding;
import com.ab.hicarescanner.network.NetworkCallController;
import com.ab.hicarescanner.network.NetworkResponseListner;
import com.ab.hicarescanner.network.model.history.HistoryData;
import com.ab.hicarescanner.network.model.inventory.InventoryRequest;
import com.ab.hicarescanner.network.model.inventory.InventoryResponse;
import com.ab.hicarescanner.network.model.login.LoginData;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScannerFragment extends BaseFragment {
    private FragmentScannerBinding mFragmentScannerBinding;
    private CodeScanner mCodeScanner;
    private static final int INVENTORY_REQUEST = 1000;
    private Vibrator vibrator = null;

    public ScannerFragment() {
        // Required empty public constructor
    }

    public static ScannerFragment newInstance() {
        Bundle args = new Bundle();
        ScannerFragment fragment = new ScannerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentScannerBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_scanner, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return mFragmentScannerBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView toolCard = getActivity().findViewById(R.id.toolbar);
        toolCard.setVisibility(View.GONE);
        CodeScannerView scannerView = mFragmentScannerBinding.scannerView;
        vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            try {
                List<HistoryData> listHistory = new ArrayList<>();
                HistoryData data = new HistoryData(); // <-- create unmanaged
                data.setTime(String.valueOf(result.getTimestamp()));
                data.setCodeText(result.getText());
                data.setCodeFormat(String.valueOf(result.getBarcodeFormat()));
                data.setCodeType(String.valueOf(result.getBarcodeFormat()));
                listHistory.add(data);
                storeHistory(listHistory);

                getActivity().runOnUiThread(() -> {
                    try {
                        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                        tg.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(200);
                    }

                    showDetails(result);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        scannerView.setOnClickListener(view1 -> mCodeScanner.startPreview());

        mFragmentScannerBinding.imgHistory.setOnClickListener(view12 -> replaceFragment(ScannerDetailsFragment.newInstance(), "ScannnerFragment-ScannerDetailFragment"));

        mFragmentScannerBinding.imgLogout.setOnClickListener(view13 -> logout());
    }

    private void showDetails(Result result) {
        try {
            LayoutInflater li = LayoutInflater.from(getActivity());

            View promptsView = li.inflate(R.layout.layout_scanner_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);
            final AlertDialog alertDialog = alertDialogBuilder.create();

            final TextView txtCode =
                    promptsView.findViewById(R.id.txtCode);
            final TextView txtType =
                    promptsView.findViewById(R.id.txtType);
            final LinearLayout lnrSubmit =
                    promptsView.findViewById(R.id.lnrSubmit);

            txtCode.setText(result.getText());
            txtType.setText(String.valueOf(result.getBarcodeFormat()));

            lnrSubmit.setOnClickListener(v -> {
                RealmResults<LoginData> mLoginRealmModels = BaseApplication.getRealm().where(LoginData.class).findAll();
                if (mLoginRealmModels != null && mLoginRealmModels.size() > 0) {
                    if (((HomeActivity) getActivity()).getmLocation() != null) {
                        NetworkCallController controller = new NetworkCallController(ScannerFragment.this);
                        InventoryRequest request = new InventoryRequest();
                        request.setAssetCode(String.valueOf(result.getBarcodeFormat()));
                        String address = getCompleteAddressString(((HomeActivity) getActivity()).getmLocation().getLatitude(), ((HomeActivity) getActivity()).getmLocation().getLongitude());
                        request.setLocation(address);
                        request.setCreatedBy(mLoginRealmModels.get(0).getName());
                        request.setDevice(android.os.Build.MODEL);
                        request.setDescription(result.toString());
                        controller.setListner(new NetworkResponseListner<InventoryResponse>() {
                            @Override
                            public void onResponse(int requestCode, InventoryResponse response) {
                                if (response.getIsSuccess()) {
                                    alertDialog.dismiss();
                                    mCodeScanner.startPreview();
                                } else {
                                    alertDialog.dismiss();
                                    mCodeScanner.startPreview();
                                }
                            }

                            @Override
                            public void onFailure(int requestCode) {
                            }
                        });
                        controller.getInventoryData(INVENTORY_REQUEST, request);
                    } else {
                        Toast.makeText(getActivity(), "not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    private void storeHistory(final List<HistoryData> historyData) {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.insertOrUpdate(historyData));
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("current address", strReturnedAddress.toString());
            } else {
                Log.w("current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("current address", "Canont get Address!");
        }
        return strAdd;
    }

}
