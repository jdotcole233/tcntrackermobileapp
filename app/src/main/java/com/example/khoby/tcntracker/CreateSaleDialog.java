package com.example.khoby.tcntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import cz.msebera.android.httpclient.client.cache.Resource;

public class CreateSaleDialog {




    public  static void displayAlertDialog(Context context, Integer resourceLayout, Integer [] widgetsID){
        AlertDialog.Builder buildAlertDialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buildAlertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            buildAlertDialog = new AlertDialog.Builder(context);
        }
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourceLayout , null);

        buildAlertDialog.setView(view).setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialogCreate = buildAlertDialog.create();
        dialogCreate.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogCreate.show();
    }


}
