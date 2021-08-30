package com.ab.hicarescanner.network.model.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 1/31/2020.
 */
public class InventoryRequest {
    @SerializedName("AssetCode")
    @Expose
    private String assetCode;
    @SerializedName("Location")
    @Expose
    private String location;
    @SerializedName("Device")
    @Expose
    private String device;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("CreatedBy")
    @Expose
    private String createdBy;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
