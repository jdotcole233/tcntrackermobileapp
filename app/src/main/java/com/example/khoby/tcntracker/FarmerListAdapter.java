package com.example.khoby.tcntracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.khoby.tcntracker.Database.SQLSaledatabasehelper;
import com.example.khoby.tcntracker.Model.FarmerModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FarmerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FarmerModel> farmers;
    private Double current_price;
    LayoutInflater inflater;
    private  Integer buyerID;
    private  Integer companyID;

    public FarmerListAdapter(FarmerProfiles context, ArrayList<FarmerModel> farmers, Double current_price, Integer buyerID, Integer companyID) {
        this.context = context;
        this.farmers = farmers;
        this.current_price = current_price;
        this.buyerID = buyerID;
        this.companyID = companyID;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

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
        CardView farmer_card = convertView.findViewById(R.id.farmer_list_cardview);
        final Button create_sale = convertView.findViewById(R.id.make_sale);

        output_farmer_name.setText(farmers.get(position).getFarmer_name());
        output_farmer_location.setText(farmers.get(position).getFarmer_location());


        //Long press event to collect farmer field data
        farmer_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("tontracker", "Long click detected");
                AlertDialog.Builder alertdialogbuilder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertdialogbuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alertdialogbuilder = new AlertDialog.Builder(context);
                }

                alertdialogbuilder.setTitle("Survey Menu").setItems(R.array.survey, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("tontracker", "item click");
                    }
                });
                AlertDialog dialog = alertdialogbuilder.create();
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                return false;
            }
        });
        //End of long press event call


        make_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked", farmers.get(position).getFarmer_phone());

                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    makePhonecall("0503848404");
                }

            }
        });

        create_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Second", farmers.get(position).getFarmer_name());
                displayModal();
            }
        });

        return convertView;
    }


    public void displayModal(){
        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(context);
        }
        final View view =  inflater.inflate(R.layout.createsale_dialog, null);
        final EditText sale_input = view.findViewById(R.id.sale_input_amount);
        final  TextView sale_output = view.findViewById(R.id.sale_display_amount);
        final Double[] amount = new Double[1];

        alertDialog.setView(view);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


        sale_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_UP){
                    if(sale_input.getText().length() > 0){
                        amount[0] = Double.parseDouble(sale_input.getText().toString());
                        Double calulate_amount = current_price * Double.parseDouble(sale_input.getText().toString());
                        sale_output.setText("GHC " + calulate_amount);
                    }
                    Log.d("tontracker", "" + sale_input.getText());
                }

                return false;
            }
        });

        view.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLSaledatabasehelper sqlSaledatabasehelper = new SQLSaledatabasehelper(context);
                SQLiteDatabase sqLiteDatabase = sqlSaledatabasehelper.getWritableDatabase();
                Date date = new Date();
                long time = date.getTime();
                Timestamp timestamp = new Timestamp(time);

                HashMap<String, String> sale_vales = new HashMap<>();
                sale_vales.put("company_id", companyID.toString());
                sale_vales.put("buyer_id", buyerID.toString());
                sale_vales.put("unit_price", String.valueOf(current_price));
                sale_vales.put("total_amount_paid", String.valueOf((current_price * amount[0])));
                sale_vales.put("total_weight", String.valueOf(amount[0]));
                sale_vales.put("created_at", String.valueOf(timestamp));

                sqlSaledatabasehelper.populateSaleTable(sale_vales, sqLiteDatabase);
                Log.d("tontracker", "successfully created a sale");

                Log.d("tontracker", amount[0].toString());
            }
        });



        view.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void makePhonecall(String phonenumber){
        Intent makephonecall = new Intent(Intent.ACTION_DIAL);
        makephonecall.setData(Uri.parse("tel:" + phonenumber));
        context.startActivity(makephonecall);

    }


}
