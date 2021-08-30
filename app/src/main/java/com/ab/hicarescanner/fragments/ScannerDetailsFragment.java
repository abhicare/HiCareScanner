package com.ab.hicarescanner.fragments;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.hicarescanner.BaseApplication;
import com.ab.hicarescanner.BaseFragment;
import com.ab.hicarescanner.R;
import com.ab.hicarescanner.activities.HomeActivity;
import com.ab.hicarescanner.adapter.RecycleScannerAdapter;
import com.ab.hicarescanner.databinding.FragmentScannerDetailsBinding;
import com.ab.hicarescanner.handler.OnListItemClickHandler;
import com.ab.hicarescanner.network.NetworkCallController;
import com.ab.hicarescanner.network.NetworkResponseListner;
import com.ab.hicarescanner.network.model.history.HistoryData;
import com.ab.hicarescanner.network.model.inventory.InventoryRequest;
import com.ab.hicarescanner.network.model.inventory.InventoryResponse;
import com.ab.hicarescanner.network.model.login.LoginData;
import com.ab.hicarescanner.utils.TimeUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.ab.hicarescanner.BaseApplication.getRealm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScannerDetailsFragment extends BaseFragment {
    FragmentScannerDetailsBinding mFragmentScannerDetailsBinding;
    private RecycleScannerAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    public static ScannerDetailsFragment newInstance() {
        Bundle args = new Bundle();
        ScannerDetailsFragment fragment = new ScannerDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ScannerDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentScannerDetailsBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_scanner_details, container, false);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return mFragmentScannerDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView toolCard = getActivity().findViewById(R.id.toolbar);
        toolCard.setVisibility(View.VISIBLE);
        mFragmentScannerDetailsBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mFragmentScannerDetailsBinding.recycleView.setLayoutManager(layoutManager);
        ImageView imgDelete = getActivity().findViewById(R.id.imgDelete);

        getScannerDetails();

        RealmResults<HistoryData> mHistoryData = getRealm().where(HistoryData.class).findAll();
        List<HistoryData> data = mHistoryData;
        if (data != null) {
            if (data.size() > 0) {
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                imgDelete.setVisibility(View.GONE);
            }
        }

        imgDelete.setOnClickListener(view1 -> {
            try {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Clear History?");
                builder1.setMessage("All barcodes will be removed and this will be permanently deleted. ")
                        .setNegativeButton("CANCEL", (dialog, id) -> dialog.cancel())
                        .setPositiveButton("CLEAR HISTORY", (dialog, id) -> {
                            try {
                                Realm.getDefaultInstance().beginTransaction();
                                Realm.getDefaultInstance().where(HistoryData.class).findAll().deleteAllFromRealm();
                                Realm.getDefaultInstance().commitTransaction();
                                getScannerDetails();
                                mAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                AlertDialog alertdialog = builder1.create();
                alertdialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void getScannerDetails() {
        try {
            RealmResults<HistoryData> mHistoryData =
                    getRealm().where(HistoryData.class).findAll();
            List<HistoryData> data = mHistoryData;
            if (mHistoryData != null && mHistoryData.size() > 0) {
                mFragmentScannerDetailsBinding.txtEmpty.setVisibility(View.GONE);
                mAdapter = new RecycleScannerAdapter(getActivity(), data);
                mFragmentScannerDetailsBinding.recycleView.setAdapter(mAdapter);

                mAdapter.setOnItemClickHandler(position -> showCodeDetails(position, data));
            }else {
                mFragmentScannerDetailsBinding.txtEmpty.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showCodeDetails(int position, List<HistoryData> data) {
        try {
            LayoutInflater li = LayoutInflater.from(getActivity());

            View promptsView = li.inflate(R.layout.layout_code_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);
            final AlertDialog alertDialog = alertDialogBuilder.create();

            final TextView txtCode =
                    promptsView.findViewById(R.id.txtCode);
            final TextView txtDate =
                    promptsView.findViewById(R.id.txtDate);
            final TextView txtDetails =
                    promptsView.findViewById(R.id.txtDetails);
            final TextView txtDone =

                    promptsView.findViewById(R.id.txtDone);
            txtCode.setText(data.get(position).getCodeFormat());
            String date = TimeUtil.getDate(Long.parseLong(data.get(position).getTime()), "dd MMM yyyy hh:mm aa");
            txtDate.setText(date);
            txtDetails.setText(data.get(position).getCodeText());

            txtDone.setOnClickListener(v -> {
                alertDialog.dismiss();
            });
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
