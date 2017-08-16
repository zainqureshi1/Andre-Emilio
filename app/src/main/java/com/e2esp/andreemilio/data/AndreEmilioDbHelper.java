package com.e2esp.andreemilio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zain on 2/17/2017
 */

public class AndreEmilioDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "andreemilio.db";

    public AndreEmilioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SHOP_TABLE = "CREATE TABLE " + AndreEmilioContract.ShopEntry.TABLE_NAME + " (" +
                AndreEmilioContract.ShopEntry._ID + " INTEGER PRIMARY KEY, " +
                AndreEmilioContract.ShopEntry.COLUMN_NAME + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_DESCRIPTION + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_URL + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_WC_VERSION + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_TIMEZONE + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_CURRENCY + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_CURRENCY_FORMAT + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_TAXI_INCLUDE + " INTEGER DEFAULT 0 NOT NULL, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_WEIGHT_UNIT + " TEXT, " +
                AndreEmilioContract.ShopEntry.COLUMN_META_DIMENSION_UNIT + " TEXT);";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + AndreEmilioContract.ProductEntry.TABLE_NAME + " (" +
                AndreEmilioContract.ProductEntry._ID + " INTEGER PRIMARY KEY, " +
                AndreEmilioContract.ProductEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                AndreEmilioContract.ProductEntry.COLUMN_TITLE + " TEXT, " +
                AndreEmilioContract.ProductEntry.COLUMN_SKU + " TEXT, " +
                AndreEmilioContract.ProductEntry.COLUMN_PRICE + " TEXT, " +
                AndreEmilioContract.ProductEntry.COLUMN_STOCK + " INTEGER, " +
                AndreEmilioContract.ProductEntry.COLUMN_CATEGORIES + " STRING, " +
                AndreEmilioContract.ProductEntry.COLUMN_JSON + " TEXT, " +
                AndreEmilioContract.ProductEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + AndreEmilioContract.CategoryEntry.TABLE_NAME + " (" +
                AndreEmilioContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                AndreEmilioContract.CategoryEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                AndreEmilioContract.CategoryEntry.COLUMN_NAME + " TEXT, " +
                AndreEmilioContract.CategoryEntry.COLUMN_IMAGE + " TEXT, " +
                AndreEmilioContract.CategoryEntry.COLUMN_JSON + " TEXT);";

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + AndreEmilioContract.OrdersEntry.TABLE_NAME + " (" +
                AndreEmilioContract.OrdersEntry._ID + " INTEGER PRIMARY KEY, " +
                AndreEmilioContract.OrdersEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                AndreEmilioContract.OrdersEntry.COLUMN_ORDER_NUMBER + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                AndreEmilioContract.OrdersEntry.COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                AndreEmilioContract.OrdersEntry.COLUMN_COMPLETED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                AndreEmilioContract.OrdersEntry.COLUMN_STATUS + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CURRENCY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_TOTAL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SUBTOTAL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY + " INTEGER, " +
                AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_TAX + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_SHIPPING + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CART_TAX + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_TAX + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CART_DISCOUNT + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_ORDER_DISCOUNT + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_METHODS + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_NOTE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_VIEW_ORDER_URL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID + " INTEGER DEFAULT 0 NOT NULL, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_COMPANY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_CITY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_STATE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_POSTCODE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_COUNTRY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_EMAIL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_COMPANY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_CITY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_STATE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_ID + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2 + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_JSON + " TEXT, " +
                AndreEmilioContract.OrdersEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_CONSUMER_TABLE = "CREATE TABLE " + AndreEmilioContract.CustomerEntry.TABLE_NAME + " (" +
                AndreEmilioContract.CustomerEntry._ID + " INTEGER PRIMARY KEY, " +
                AndreEmilioContract.CustomerEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                AndreEmilioContract.CustomerEntry.COLUMN_EMAIL + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_LAST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_SHIPPING_PHONE + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_USERNAME + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_LAST_ORDER_ID + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_JSON + " TEXT, " +
                AndreEmilioContract.CustomerEntry.COLUMN_ENABLE + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONSUMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AndreEmilioContract.ShopEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AndreEmilioContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AndreEmilioContract.CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AndreEmilioContract.OrdersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AndreEmilioContract.CustomerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
