package com.example.khoby.tcntracker.Model;

import android.widget.ImageView;

public class FarmerModel {

    private Integer farmer_id;
    private String farmer_name;
    private  String farmer_location;
    private String farmer_phone;
    private Integer sync_status;
   // private ImageView imageView;

    public FarmerModel(Integer farmer_id, String farmer_name, String farmer_location, String farmer_phone, Integer sync_status) {
        this.farmer_id = farmer_id;
        this.farmer_name = farmer_name;
        this.farmer_location = farmer_location;
        this.farmer_phone = farmer_phone;
        this.sync_status = sync_status;
    }

    public Integer getFarmer_id() {
        return farmer_id;
    }

    public void setFarmer_id(Integer farmer_id) {
        this.farmer_id = farmer_id;
    }

    public String getFarmer_name() {
        return farmer_name;
    }

    public void setFarmer_name(String farmer_name) {
        this.farmer_name = farmer_name;
    }

    public String getFarmer_location() {
        return farmer_location;
    }

    public void setFarmer_location(String farmer_location) {
        this.farmer_location = farmer_location;
    }

    public String getFarmer_phone() {
        return farmer_phone;
    }

    public void setFarmer_phone(String farmer_phone) {
        this.farmer_phone = farmer_phone;
    }

    public Integer getSync_status() {
        return sync_status;
    }
}
