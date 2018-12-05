package com.example.khoby.tcntracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.khoby.tcntracker.Model.FarmerModel;

import java.util.ArrayList;

public class FarmerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FarmerModel> farmers;

    public FarmerListAdapter(Context context, ArrayList<FarmerModel> farmers) {
        this.context = context;
        this.farmers = farmers;
    }

    @Override
    public int getCount() {
        return farmers.size();
    }

    @Override
    public Object getItem(int position) {
        return farmers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context ,R.layout.farmer_list_card, null);
        }

        TextView output_farmer_name = convertView.findViewById(R.id.farmer_name);
        TextView output_farmer_location = convertView.findViewById(R.id.farmer_location);

        output_farmer_name.setText(farmers.get(position).getFarmer_name());
        output_farmer_location.setText(farmers.get(position).getFarmer_location());


        return convertView;
    }
}
