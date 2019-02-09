package com.example.khoby.tcntracker.Database;

import android.provider.BaseColumns;

public class FarmerContract {

    public static final int SYNC_STATUS_SUCCESS = 1;
    public static  final int SYNC_STATUS_FAILED = 0;
    public static final String DATABASE_NAME = "tontrackerdatabase";
    public static final String BUYER_DATABASE_NAME = "buyerdatabse";
    public static final String SALE_DATABASE_NAME = "saledatabse";

    public static  final String LOGIN_URL = "http://tontracker.com/loginfrommobile";
    public static final String REGISTER_FARMER_URL = "http://tontracker.com/registerFarmerFromMobile";
    public static final String LOCATION_URL = "http://tontracker.com/communities";
    public static final  String TRANSACTIONS_URL = "http://tontracker.com/farmertransactions";
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
        public static final String COLUMN_NAME_CURRENT_PRICE = "current_price";
        public static final String COLUMN_NAME_COMMPANY_ID = "company_id";
        public static final String COLUMN_NAME_BUYER_ID = "buyer_id";
    }

    public static  class  SaleDatabaseEntry implements BaseColumns{
        public static final String TABLE_NAME = "farmer_transactions";
        public static final String COLUMN_NAME_UNIT_PRICE = "unit_price";
        public static final String COLUMN_NAME_WEIGHT = "total_weight";
        public static final String COLUMN_NAME_TOTAL_AMOUNT_PAID = "total_amount_paid";
        public static final String COLUMN_BUYER_ID = "buyer_id";
        public static final String COLUMN_NAME_SYNC_STATUS = "sync_status";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_COMPANY_ID = "company_id";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}
