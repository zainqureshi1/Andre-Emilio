package com.e2esp.andreemilio.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.e2esp.andreemilio.interfaces.ListCallbacks;
import com.e2esp.andreemilio.interfaces.ObjectCallbacks;
import com.e2esp.andreemilio.models.orders.Count;
import com.e2esp.andreemilio.models.orders.Order;
import com.e2esp.andreemilio.models.products.Category;
import com.e2esp.andreemilio.models.products.Variation;
import com.e2esp.andreemilio.models.shop.Shop;
import com.e2esp.andreemilio.utilities.Consts;
import com.e2esp.andreemilio.woocommerce.WooCommerce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.data.AndreEmilioContract;
import com.e2esp.andreemilio.models.products.Images;
import com.e2esp.andreemilio.models.products.Product;
import com.e2esp.andreemilio.utilities.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.RetrofitError;

/**
 * Created by Zain on 2/18/2017.
 */

public class AndreEmilioSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = AndreEmilioSyncAdapter.class.getSimpleName();
    // 60 seconds (1 minute) * 60 = 1 hour
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/4;

    private ArrayList<ContentValues> productsValues = new ArrayList<>();
    private ArrayList<ContentValues> categoriesValues = new ArrayList<>();
    private ArrayList<ContentValues> ordersValues = new ArrayList<>();

    private ThreadPoolExecutor threadPoolExecutor;

    private int sizePageProduct = 5;
    private int sizeProducts = 0;
    private int pageProduct= 1;

    private int sizePageOrder = 50;
    private int sizeOrders = 0;
    private int pageOrder= 1;

    private Gson gson = new GsonBuilder().create();

    final Set<Target> protectedFromGarbageCollectorTargets = new HashSet<>();

    public AndreEmilioSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        Long lastSyncTimeStamp =  Utility.getPreferredLastSync(getContext());
        Log.d(LOG_TAG, "Last sync " + lastSyncTimeStamp);

        AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
        final String authenticationHeader = "Basic " + Base64.encodeToString(
                (Consts.WC_API_KEY + ":" + accountManager.getPassword(account)).getBytes(),
                Base64.NO_WRAP);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .cache(null);

        //TODO Remove this if you don't have a self cert
        /*
        if(Utility.getSSLSocketFactory() != null){
            clientBuilder
                    .sslSocketFactory(Utility.getSSLSocketFactory())
                    .hostnameVerifier(Utility.getHostnameVerifier());
        }
        */

        Interceptor basicAuthenticatorInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request authenticateRequest = request.newBuilder()
                        .addHeader("Authorization", authenticationHeader)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(authenticateRequest);
            }
        };

        //OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(key, secret);
        //consumer.setSigningStrategy(new QueryStringSigningStrategy());
        //clientBuilder.addInterceptor(new SigningInterceptor(consumer));
        clientBuilder.addInterceptor(basicAuthenticatorInterceptor);

        boolean syncAll = false;
        if(lastSyncTimeStamp != 0) {
            long diff = System.currentTimeMillis() - lastSyncTimeStamp;
            long hours = diff / (60 * 60 * 1000);
            if (hours > 1) {
                syncAll = true;
            }
        } else {
            syncAll = true;
        }


        Date lastSync = new Date(lastSyncTimeStamp);
        if(syncAll && lastSyncTimeStamp == 0) {
            lastSync = null;
        }
        final Date finalLastSync = lastSync;

        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        //Shop
        Runnable runnableShop = new Runnable() {
            public void run() {
                synchronizeShop();
            }
        };
        threadPoolExecutor.submit(runnableShop);

        //Products
        Runnable runnableProducts = new Runnable() {
            public void run() {
                synchronizeProducts(null);
            }
        };
        threadPoolExecutor.submit(runnableProducts);

        //Categories
        Runnable runnableCategories = new Runnable() {
            public void run() {
                synchronizeCategories();
            }
        };
        threadPoolExecutor.submit(runnableCategories);

        //Orders
        Runnable runnableOrders = new Runnable() {
            public void run() {
                synchronizeOrders(finalLastSync);
            }
        };
        threadPoolExecutor.submit(runnableOrders);

    }

    private void getImages(Product product) {
        for(Images image : product.getImages()) {
            final String folder = "AndreEmilio/" + product.getId();
            final String filename =  image.getTitle() + ".jpg";
            Target folderTarget = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    Runnable runnableImages = new Runnable() {
                        public void run() {
                            try {
                                File directory = Environment.getExternalStoragePublicDirectory((Environment.DIRECTORY_PICTURES));
                                File andreEmilio = new File(directory + "/" + folder);
                                andreEmilio.mkdirs();
                                File file = new File(andreEmilio + "/" + filename);
                                file.createNewFile();

                                FileOutputStream out = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                                Log.v(LOG_TAG, "Product folder: " + folder +  " image: " + filename);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("IOException", e.getLocalizedMessage());
                            }
                            protectedFromGarbageCollectorTargets.remove(this);
                        }
                    };
                    threadPoolExecutor.submit(runnableImages);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e(LOG_TAG, "onBitmapFailed");
                    protectedFromGarbageCollectorTargets.remove(this);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }

            };
            protectedFromGarbageCollectorTargets.add(folderTarget);

            Picasso.with(getContext())
                    .load(image.getSrc())
                    .into(folderTarget);
        }
    }

    private void synchronizeShop() {
        Log.v(LOG_TAG, "Shop sync start");
        WooCommerce.getInstance().getShop(new ObjectCallbacks() {
            @Override
            public void Callback(Object content, RetrofitError error) {
                if (content != null) {
                    Shop shop = (Shop) content;
                    Log.v(LOG_TAG, "Shop sync success : "+shop);

                    int shopRowsDeleted = getContext().getContentResolver().delete(AndreEmilioContract.ShopEntry.CONTENT_URI, null, null);
                    Log.v(LOG_TAG, shopRowsDeleted + " Shop rows deleted");

                    ContentValues shopValues = new ContentValues();
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_NAME, shop.getStore().getName());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_DESCRIPTION, shop.getStore().getDescription());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_URL, shop.getStore().getUrl());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_WC_VERSION, shop.getStore().getWcVersion());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_CURRENCY, shop.getStore().getMeta().getCurrency());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_CURRENCY_FORMAT, shop.getStore().getMeta().getCurrencyFormat());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_DIMENSION_UNIT, shop.getStore().getMeta().getDimensionUnit());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_TAXI_INCLUDE, shop.getStore().getMeta().isTaxIncluded() ? "1" : "0");
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_TIMEZONE, shop.getStore().getMeta().getTimezone());
                    shopValues.put(AndreEmilioContract.ShopEntry.COLUMN_META_WEIGHT_UNIT, shop.getStore().getMeta().getWeightUnit());

                    Uri insertedShopUri = getContext().getContentResolver().insert(AndreEmilioContract.ShopEntry.CONTENT_URI, shopValues);
                    long shopId = ContentUris.parseId(insertedShopUri);
                    Log.d(LOG_TAG, "Shop successful inserted ID: " + shopId);
                }
            }
        });
    }

    private void synchronizeProducts(final Date date) {
        Log.v(LOG_TAG, "Products sync start");
        /*if(date == null) {
            WooCommerce.getInstance().getProductsCount(new ObjectCallbacks() {
                @Override
                public void Callback(Object content, RetrofitError error) {
                    if (content != null) {
                        try {
                            sizeProducts = Integer.valueOf(((Count)content).getCount());
                        } catch (NumberFormatException exception) {
                            Log.e(LOG_TAG, "NumberFormatException " + exception.getMessage());
                        }
                        productsValues.clear();
                        synchronizeBatchProducts(date);
                    }
                }
            });
        } else {
            productsValues.clear();
            synchronizeBatchProducts(date);
        }*/
        // TODO : SYNC : Use above technique instead of following
        productsValues.clear();
        synchronizeBatchProducts(date);
    }

    private void synchronizeBatchProducts(final Date date) {
        Log.v(LOG_TAG, "Products Total:" + sizeProducts + " Current: " + (pageProduct - 1) * sizePageProduct + " Page : " + pageProduct);

        HashMap<String, String> options = new HashMap<>();
        options.put("filter[limit]", String.valueOf(sizePageProduct));
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            options.put("filter[updated_at_min]", dateFormat.format(date));
        }
        options.put("page", String.valueOf(pageProduct));
        options.put("filter[post_status]", "any");

        WooCommerce.getInstance().getProducts(options, new ListCallbacks() {
            @Override
            public void Callback(List<?> content, Throwable error) {
                int count = 0;
                if (content != null) {
                    for (int i = 0; i < content.size(); i++) {
                        count++;
                        Product product = (Product) content.get(i);

                        ContentValues productValues = new ContentValues();
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_ID, product.getId());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_SKU, product.getSku());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_JSON, gson.toJson(product));
                        productValues.put(AndreEmilioContract.ProductEntry.COLUMN_ENABLE, 1);

                        productsValues.add(productValues);

                        for(Variation variation : product.getVariations()) {
                            //TODO, CHANGE THIS APPROACH
                            product.setSku(variation.getSku());
                            product.setPrice(variation.getPrice());
                            product.setStockQuantity(variation.getStockQuantity());

                            ContentValues variationValues = new ContentValues();
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_ID, variation.getId());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_SKU, product.getSku());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_JSON, gson.toJson(product));
                            variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_ENABLE, 1);

                            productsValues.add(variationValues);
                        }
                    }
                }


                //if ((sizePageProduct * pageProduct) < sizeProducts) {
                // TODO : SYNC : Use above condition instead of following
                if (count >= sizePageProduct) {
                    pageProduct++;
                    synchronizeBatchProducts(date);
                } else {
                    finalizeSyncProducts();
                }
            }
        });


    }

    private void finalizeSyncProducts() {
        ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
        productsValuesArray = productsValues.toArray(productsValuesArray);
        int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(AndreEmilioContract.ProductEntry.CONTENT_URI, productsValuesArray);
        Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");

        getContext().getContentResolver().notifyChange(AndreEmilioContract.ProductEntry.CONTENT_URI, null, false);
        pageProduct = 1;
    }

    private void synchronizeCategories() {
        Log.v(LOG_TAG, "Categories sync start");

        WooCommerce.getInstance().getCategories(new ListCallbacks() {
            @Override
            public void Callback(List<?> content, Throwable error) {
                if (content != null && content.size() > 0) {
                    categoriesValues.clear();
                    for (int i = 0; i < content.size(); i++) {
                        Category category = (Category) content.get(i);

                        ContentValues categoryValues = new ContentValues();
                        categoryValues.put(AndreEmilioContract.CategoryEntry.COLUMN_ID, category.getId());
                        categoryValues.put(AndreEmilioContract.CategoryEntry.COLUMN_NAME, category.getName());
                        categoryValues.put(AndreEmilioContract.CategoryEntry.COLUMN_IMAGE, category.getImage());
                        categoryValues.put(AndreEmilioContract.CategoryEntry.COLUMN_JSON, gson.toJson(category));

                        categoriesValues.add(categoryValues);
                    }
                }

                ContentValues[] categoriesValuesArray = new ContentValues[categoriesValues.size()];
                categoriesValuesArray = categoriesValues.toArray(categoriesValuesArray);
                int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(AndreEmilioContract.CategoryEntry.CONTENT_URI, categoriesValuesArray);
                Log.v(LOG_TAG, "Categories " + ordersRowsUpdated + " updated");

                getContext().getContentResolver().notifyChange(AndreEmilioContract.CategoryEntry.CONTENT_URI, null, false);
            }
        });
    }

    private void synchronizeOrders(final Date date) {
        Log.v(LOG_TAG, "Orders sync start");
        if(date == null) {
            WooCommerce.getInstance().getOrdersCount(new ObjectCallbacks() {
                @Override
                public void Callback(Object content, RetrofitError error) {
                    if (content != null) {
                        try {
                            sizeOrders = Integer.valueOf(((Count)content).getCount());
                        } catch (NumberFormatException exception) {
                            Log.e(LOG_TAG, "NumberFormatException " + exception.getMessage());
                        }
                        ordersValues.clear();
                        synchronizeBatchOrders(date);
                    }
                }
            });
        } else {
            synchronizeBatchOrders(date);
        }
    }

    private void synchronizeBatchOrders(final Date date) {
        Log.v(LOG_TAG,"Orders Total:" + sizeOrders + " Current: " + (pageOrder - 1) * sizePageOrder + " Page : " + pageOrder);

        HashMap<String, String> options = new HashMap<>();
        options.put("status", "any");
        options.put("filter[limit]", String.valueOf(sizePageOrder));
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            options.put("filter[updated_at_min]", dateFormat.format(date));
        }
        options.put("page", String.valueOf(pageOrder));
        
        WooCommerce.getInstance().getOrders(options, new ListCallbacks() {
            @Override
            public void Callback(List<?> content, Throwable error) {
                if (content != null) {
                    for (int i = 0; i < content.size(); i++) {
                        Order order = (Order) content.get(i);

                        ContentValues orderValues = new ContentValues();
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_ID, order.getId());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_ORDER_NUMBER, order.getOrderNumber());
                        if(order.getCreatedAt() != null) {
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CREATED_AT, AndreEmilioContract.getDbDateString(order.getCreatedAt()));
                        }
                        if(order.getUpdatedAt() != null) {
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_UPDATED_AT, AndreEmilioContract.getDbDateString(order.getUpdatedAt()));
                        }
                        if(order.getCompletedAt() != null) {
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_COMPLETED_AT, AndreEmilioContract.getDbDateString(order.getCompletedAt()));
                        }
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_STATUS, order.getStatus());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CURRENCY, order.getCurrency());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_TOTAL, order.getTotal());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SUBTOTAL, order.getSubtotal());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY, order.getTotalLineItemsQuantity());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_TAX, order.getTotalTax());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_SHIPPING, order.getTotalShipping());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CART_TAX, order.getCartTax());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_TAX, order.getShippingTax());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT, order.getTotalDiscount());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CART_DISCOUNT, order.getCartDiscount());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_ORDER_DISCOUNT, order.getOrderDiscount());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_METHODS, order.getShippingMethods());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_NOTE, order.getNote());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_VIEW_ORDER_URL, order.getViewOrderUrl());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID, order.getPaymentDetails().getMethodId());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE, order.getPaymentDetails().getMethodTitle());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID, order.getPaymentDetails().isPaid() ? "1" : "0");
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME, order.getBillingAddress().getFirstName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_LAST_NAME , order.getBillingAddress().getLastName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_COMPANY, order.getBillingAddress().getCompany());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1, order.getBillingAddress().getAddressOne());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2, order.getBillingAddress().getAddressTwo());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_CITY, order.getBillingAddress().getCity());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_STATE, order.getBillingAddress().getState());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_POSTCODE, order.getBillingAddress().getPostcode());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_COUNTRY, order.getBillingAddress().getCountry());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_EMAIL, order.getBillingAddress().getEmail());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_BILLING_PHONE, order.getBillingAddress().getPhone());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME, order.getShippingAddress().getFirstName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME, order.getShippingAddress().getLastName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_COMPANY, order.getShippingAddress().getCompany());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1, order.getShippingAddress().getAddressOne());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2, order.getShippingAddress().getAddressTwo());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_CITY, order.getShippingAddress().getCity());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_STATE, order.getShippingAddress().getState());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE, order.getShippingAddress().getPostcode());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY, order.getShippingAddress().getCountry());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_ID, order.getCustomerId());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL, order.getCustomer().getEmail());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME, order.getCustomer().getFirstName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME, order.getCustomer().getLastName());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME, order.getCustomer().getUsername());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID, order.getCustomer().getLastOrderId());
                        if(order.getCustomer().getLastOrderDate() != null) {
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE, AndreEmilioContract.getDbDateString(order.getCustomer().getLastOrderDate()));
                        }
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT, order.getCustomer().getOrdersCount());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND, order.getCustomer().getTotalSpent());
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL, order.getCustomer().getAvatarUrl());
                        if(order.getCustomer().getBillingAddress()!= null){
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME, order.getCustomer().getBillingAddress().getFirstName());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME, order.getCustomer().getBillingAddress().getLastName());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY, order.getCustomer().getBillingAddress().getCompany());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1, order.getCustomer().getBillingAddress().getAddressOne());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2, order.getCustomer().getBillingAddress().getAddressTwo());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY, order.getCustomer().getBillingAddress().getCity());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE, order.getCustomer().getBillingAddress().getState());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE, order.getCustomer().getBillingAddress().getPostcode());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY, order.getCustomer().getBillingAddress().getCountry());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL, order.getCustomer().getBillingAddress().getEmail());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE, order.getCustomer().getBillingAddress().getPhone());
                        }
                        if(order.getCustomer().getShippingAddress() != null){
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME, order.getCustomer().getShippingAddress().getFirstName());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME , order.getCustomer().getShippingAddress().getLastName());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY, order.getCustomer().getShippingAddress().getCompany());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1, order.getCustomer().getShippingAddress().getAddressOne());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2, order.getCustomer().getShippingAddress().getAddressTwo());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY, order.getCustomer().getShippingAddress().getCity());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE, order.getCustomer().getShippingAddress().getState());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE, order.getCustomer().getShippingAddress().getPostcode());
                            orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY, order.getCustomer().getShippingAddress().getCountry());
                        }
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_JSON,gson.toJson(order));
                        orderValues.put(AndreEmilioContract.OrdersEntry.COLUMN_ENABLE, 1);

                        ordersValues.add(orderValues);
                    }
                }
                if ((sizePageOrder * pageOrder) < sizeOrders) {
                    pageOrder ++;
                    synchronizeBatchOrders(date);
                } else {
                    finalizeSyncOrders();
                }
            }
        });
    }

    private void finalizeSyncOrders() {
        Utility.setPreferredLastSync(getContext(), System.currentTimeMillis());

        ContentValues[] ordersValuesArray = new ContentValues[ordersValues.size()];
        ordersValuesArray = ordersValues.toArray(ordersValuesArray);
        int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(AndreEmilioContract.OrdersEntry.CONTENT_URI, ordersValuesArray);
        Log.v(LOG_TAG,"Orders " + ordersRowsUpdated + " updated");

        getContext().getContentResolver().notifyChange(AndreEmilioContract.OrdersEntry.CONTENT_URI, null, false);
        pageOrder = 1;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account("AndreEmilio", context.getString(R.string.sync_account_type));
        if ( accountManager.getPassword(account) == null  ) {
            if (!accountManager.addAccountExplicitly(account, Consts.WC_API_SECRET, null)) {
                return null;
            }
            onAccountCreated(account, context);
        }
        return account;

    }

    public static void removeAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account("AndreEmilio", context.getString(R.string.sync_account_type));
        if ( accountManager.getPassword(account) != null  ) {
            accountManager.removeAccount(account,null,null);
        }
    }

    public static void disablePeriodSync(Context context){
        Log.e(LOG_TAG, "disablePeriodSync");

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.cancelSync(account, authority);

        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        accountManager.removeAccount(account, null, null);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        AndreEmilioSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(final Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

}
