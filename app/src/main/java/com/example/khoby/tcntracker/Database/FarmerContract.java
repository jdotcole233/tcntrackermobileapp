package com.example.khoby.tcntracker.Database;

import android.provider.BaseColumns;

public class FarmerContract {

    public static final int SYNC_STATUS_SUCCESS = 1;
    public static  final int SYNC_STATUS_FAILED = 0;
    public static final String DATABASE_NAME = "tontrackerdatabase";
    public static final String BUYER_DATABASE_NAME = "buyerdatabse";
    public static  final String LOGIN_URL = "http://192.168.100.7:8000/loginfrommobile";
    public static final String REGISTER_FARMER_URL = "http://192.168.100.7:8000/registerFarmerFromMobile";
    public static final String LOCATION_URL = "http://192.168.100.7:8000/communities";
    public static  final String UPDATE_APPLICATION = "com.example.tcntracker.updateapplication";

    private  FarmerContract(){ }

    public static class FarmerDatabaseEntry implements BaseColumns {
        public  static final String TABLE_NAME = "farmers";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_OTHER_NAME = "other_name";
        public static final String  COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_COMMUNITY_ID = "communitiescommunity_id";
        public static final String COLUMN_NAME_COMMUNITY_NAME = "community_name";
        public static final String COLUMN_NAME_SYNC_STATUS = "sync_status";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
    }


    public  static class BuyerDatabaseEntry implements BaseColumns{
        public static final String TABLE_NAME = "buyer";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_OTHER_NAME = "other_name";
        public static final String  COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_COMMPANY_ID = "company_id";
        public static final String COLUMN_NAME_BUYER_ID = "buyer_id";
    }
}
