package com.example.khoby.tcntracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.khoby.tcntracker.Model.FarmerModel;

import java.util.ArrayList;

public class FarmerProfiles extends AppCompatActivity {

    private ListView farmer_list;
    private ArrayList<FarmerModel> farmerModels;
    private FarmerListAdapter farmerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_profiles);

        farmer_list = findViewById(R.id.farmer_list);
        farmerModels = getFarmers();
        farmerListAdapter = new FarmerListAdapter(this, farmerModels);
        farmer_list.setAdapter(farmerListAdapter);
    }

    public static ArrayList<FarmerModel> getFarmers(){
            ArrayList<FarmerModel> farmerModels = new ArrayList<>();

        farmerModels.add(new FarmerModel(1,"Kwame Ampong","Drobo","0503848404"));
        farmerModels.add(new FarmerModel(2,"Mary Affum","Techiman","0503848404"));
        farmerModels.add(new FarmerModel(3,"Sampson Hackeem","Kumasi","0503848404"));
        farmerModels.add(new FarmerModel(4,"Ama Mercy","Bono","0503848404"));
        farmerModels.add(new FarmerModel(5,"Sassu Sarfo","Techiman","0503848404"));
        farmerModels.add(new FarmerModel(6,"Francis Duman","Drobo","0503848404"));
        farmerModels.add(new FarmerModel(7,"Eric Aseidu","Kumasi","0503848404"));
        farmerModels.add(new FarmerModel(8,"Deborah Sare","Bono","0503848404"));

            return  farmerModels;
    }
}
