package com.e2esp.andreemilio.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Zain on 2/17/2017.
 */

public class AndreEmilioProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AndreEmilioDbHelper mOpenHelper;

    private static final int SHOP = 100;
    private static final int SHOP_ID = 101;

    private static final int PRODUCT = 200;
    private static final int PRODUCT_ID = 201;

    private static final int CATEGORY = 300;
    private static final int CATEGORY_ID = 301;

    private static final int ORDER = 400;
    private static final int ORDER_ID = 401;

    private static final int CUSTOMER = 500;
    private static final int CUSTOMER_ID = 501;

    @Override
    public boolean onCreate() {
        mOpenHelper = new AndreEmilioDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "shop/#"
            case SHOP_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.ShopEntry.TABLE_NAME,
                        projection,
                        AndreEmilioContract.ShopEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "shop"
            case SHOP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.ShopEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "product/#"
            case PRODUCT_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.ProductEntry.TABLE_NAME,
                        projection,
                        AndreEmilioContract.ProductEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "product"
            case PRODUCT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "category/#"
            case CATEGORY_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.CategoryEntry.TABLE_NAME,
                        projection,
                        AndreEmilioContract.CategoryEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "category"
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "order/#"
            case ORDER_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.OrdersEntry.TABLE_NAME,
                        projection,
                        AndreEmilioContract.OrdersEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "order"
            case ORDER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.OrdersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "customer/#"
            case CUSTOMER_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.CustomerEntry.TABLE_NAME,
                        projection,
                        AndreEmilioContract.CustomerEntry.COLUMN_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "customer"
            case CUSTOMER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AndreEmilioContract.CustomerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHOP:
                return AndreEmilioContract.ShopEntry.CONTENT_TYPE;
            case SHOP_ID:
                return AndreEmilioContract.ShopEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return AndreEmilioContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_ID:
                return AndreEmilioContract.ProductEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return AndreEmilioContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return AndreEmilioContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case ORDER:
                return AndreEmilioContract.OrdersEntry.CONTENT_TYPE;
            case ORDER_ID:
                return AndreEmilioContract.OrdersEntry.CONTENT_ITEM_TYPE;
            case CUSTOMER:
                return AndreEmilioContract.CustomerEntry.CONTENT_TYPE;
            case CUSTOMER_ID:
                return AndreEmilioContract.CustomerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SHOP: {
                db.beginTransaction();
                try {
                    long _id = db.insert(AndreEmilioContract.ShopEntry.TABLE_NAME, null, contentValues);
                    if ( _id > 0 )
                        returnUri = AndreEmilioContract.ShopEntry.buildShopUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case PRODUCT: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(AndreEmilioContract.ProductEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = AndreEmilioContract.ProductEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case CATEGORY: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(AndreEmilioContract.CategoryEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = AndreEmilioContract.CategoryEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case ORDER: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(AndreEmilioContract.OrdersEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = AndreEmilioContract.OrdersEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case CUSTOMER: {
                db.beginTransaction();
                try {
                    long _id = db.insertWithOnConflict(AndreEmilioContract.CustomerEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if ( _id > 0 )
                        returnUri = AndreEmilioContract.CustomerEntry.buildOrderUri(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SHOP:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            AndreEmilioContract.ShopEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PRODUCT:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            AndreEmilioContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CATEGORY:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            AndreEmilioContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ORDER:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            AndreEmilioContract.OrdersEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CUSTOMER:
                db.beginTransaction();
                try {
                    rowsDeleted = db.delete(
                            AndreEmilioContract.CustomerEntry.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SHOP:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(AndreEmilioContract.ShopEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PRODUCT:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(AndreEmilioContract.ProductEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CATEGORY:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(AndreEmilioContract.CategoryEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ORDER:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(AndreEmilioContract.OrdersEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CUSTOMER:
                db.beginTransaction();
                try {
                    rowsUpdated = db.update(AndreEmilioContract.CustomerEntry.TABLE_NAME, contentValues, selection,
                            selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null, false);
        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AndreEmilioContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, AndreEmilioContract.PATH_SHOP, SHOP);
        matcher.addURI(authority, AndreEmilioContract.PATH_SHOP + "/#", SHOP_ID);

        matcher.addURI(authority, AndreEmilioContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, AndreEmilioContract.PATH_PRODUCT + "/#", PRODUCT_ID);

        matcher.addURI(authority, AndreEmilioContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, AndreEmilioContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        matcher.addURI(authority, AndreEmilioContract.PATH_ORDER, ORDER);
        matcher.addURI(authority, AndreEmilioContract.PATH_ORDER + "/#", ORDER_ID);

        matcher.addURI(authority, AndreEmilioContract.PATH_CUSTOMER, CUSTOMER);
        matcher.addURI(authority, AndreEmilioContract.PATH_CUSTOMER + "/#", CUSTOMER_ID);

        return matcher;
    }

}
