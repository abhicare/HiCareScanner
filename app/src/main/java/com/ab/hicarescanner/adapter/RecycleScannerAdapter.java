package com.ab.hicarescanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.ab.hicarescanner.R;
import com.ab.hicarescanner.databinding.LayoutScannerAdapterBinding;
import com.ab.hicarescanner.handler.OnListItemClickHandler;
import com.ab.hicarescanner.network.model.history.HistoryData;
import com.ab.hicarescanner.utils.TimeUtil;
import java.util.List;

public class RecycleScannerAdapter extends RecyclerView.Adapter<RecycleScannerAdapter.ViewHolder> {

    private OnListItemClickHandler onItemClickHandler;
    private final Context mContext;
    private static List<HistoryData> items = null;

    public RecycleScannerAdapter(Context context, List<HistoryData> mHistoryData) {
        items = mHistoryData;
        this.mContext = context;
    }


    @Override
    public RecycleScannerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutScannerAdapterBinding mLayoutScannerAdapterBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.layout_scanner_adapter, parent, false);
        return new RecycleScannerAdapter.ViewHolder(mLayoutScannerAdapterBinding);
    }

    @Override
    public void onBindViewHolder(final RecycleScannerAdapter.ViewHolder holder, final int position) {
        try {
            final HistoryData model = items.get(position);
            holder.mLayoutScannerAdapterBinding.txtCodeTitle.setText(model.getCodeText());
            String date = TimeUtil.getDate(Long.parseLong(model.getTime()), "dd MMM yyyy hh:mm aa");
            holder.mLayoutScannerAdapterBinding.txtCodeDesc.setText(model.getCodeFormat() + " \u2022 " + date);
            if (model.getCodeFormat().equalsIgnoreCase("QR_CODE")) {
                holder.mLayoutScannerAdapterBinding.imgCode.setImageResource(R.drawable.ic_qr_code);
            } else {
                holder.mLayoutScannerAdapterBinding.imgCode.setImageResource(R.drawable.ic_scan);

            }

            holder.itemView.setOnClickListener(v -> onItemClickHandler.onItemClick(position));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setOnItemClickHandler(OnListItemClickHandler onItemClickHandler) {
        this.onItemClickHandler = onItemClickHandler;
    }

    public void removeAll() {
        items.removeAll(items);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LayoutScannerAdapterBinding mLayoutScannerAdapterBinding;

        public ViewHolder(LayoutScannerAdapterBinding mLayoutScannerAdapterBinding) {
            super(mLayoutScannerAdapterBinding.getRoot());
            this.mLayoutScannerAdapterBinding = mLayoutScannerAdapterBinding;
        }
    }

}
