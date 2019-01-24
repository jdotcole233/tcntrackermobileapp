package com.example.khoby.tcntracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context ,R.layout.farmer_list_card, null);
        }

        TextView output_farmer_name = convertView.findViewById(R.id.farmer_name);
        TextView output_farmer_location = convertView.findViewById(R.id.farmer_location);
        ImageButton make_phone_call = convertView.findViewById(R.id.call_farmer);
        final Button create_sale = convertView.findViewById(R.id.make_sale);

        output_farmer_name.setText(farmers.get(position).getFarmer_name());
        output_farmer_location.setText(farmers.get(position).getFarmer_location());

//        convertView.findViewById(R.id.call_farmer).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("Clicked", farmers.get(position).getFarmer_id().toString());
//            }
//        });

        make_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked", farmers.get(position).getFarmer_phone().toString());
                Intent makephonecall = new Intent(Intent.ACTION_CALL);
                makephonecall.setData(Uri.parse(farmers.get(position).getFarmer_phone()));

                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(makephonecall);
            }
        });

        create_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSaleDialog createSaleDialog = new CreateSaleDialog();
                createSaleDialog.show();
                Log.i("Second", farmers.get(position).getFarmer_name());
            }
        });

        return convertView;
    }
}
