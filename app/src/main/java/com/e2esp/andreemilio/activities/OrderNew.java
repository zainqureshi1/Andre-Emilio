package com.e2esp.andreemilio.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e2esp.andreemilio.R;
import com.e2esp.andreemilio.data.AndreEmilioContract;
import com.e2esp.andreemilio.models.customers.BillingAddress;
import com.e2esp.andreemilio.models.customers.ShippingAddress;
import com.e2esp.andreemilio.models.orders.Item;
import com.e2esp.andreemilio.models.orders.MetaItem;
import com.e2esp.andreemilio.models.orders.Order;
import com.e2esp.andreemilio.models.orders.OrderResponse;
import com.e2esp.andreemilio.models.orders.OrderUpdate;
import com.e2esp.andreemilio.models.orders.OrderUpdateValues;
import com.e2esp.andreemilio.models.orders.PaymentDetails;
import com.e2esp.andreemilio.models.orders.ShippingLine;
import com.e2esp.andreemilio.models.products.Product;
import com.e2esp.andreemilio.models.products.Variation;
import com.e2esp.andreemilio.utilities.Utility;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zain on 2/18/2017.
 */

public class OrderNew extends AppCompatActivity {

    private final String LOG_TAG = OrderNew.class.getSimpleName();

    private Gson mGson = new GsonBuilder().create();
    private Order mOrderSelected;
    private float mTotal = 0;

    private EditText mEmail;
    private EditText mPhone;
    private TextView mPrice;
    private EditText mCustomerFirst;
    private EditText mCustomerLast;

    private EditText mBillingCompany;

    private EditText mBillingAddressOne;
    private EditText mBillingAddressTwo;
    private EditText mBillingAddressCity;
    private EditText mBillingAddressCountry;
    private EditText mBillingAddressCP;
    private EditText mBillingAddressState;

    private ProgressDialog mProgress;

    private static final String[] PRODUCT_PROJECTION = {
            AndreEmilioContract.ProductEntry.COLUMN_ID,
            AndreEmilioContract.ProductEntry.COLUMN_JSON,
    };
    private int COLUMN_PRODUCT_COLUMN_JSON = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private boolean mSaveIncomplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);
        mProgress = new ProgressDialog(OrderNew.this);
        mProgress.setTitle(getString(R.string.app_name));

        mEmail = (EditText) findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phone);
        mPrice = (TextView) findViewById(R.id.price);
        mCustomerFirst = (EditText) findViewById(R.id.customer_first);
        mCustomerLast = (EditText) findViewById(R.id.customer_last);

        mBillingCompany = (EditText) findViewById(R.id.company);

        mBillingAddressOne = (EditText) findViewById(R.id.billing_address_one);
        mBillingAddressTwo = (EditText) findViewById(R.id.billing_address_two);
        mBillingAddressCity = (EditText) findViewById(R.id.billing_city);
        mBillingAddressCountry = (EditText) findViewById(R.id.billing_country);
        mBillingAddressCP = (EditText) findViewById(R.id.billing_postal_code);
        mBillingAddressState = (EditText) findViewById(R.id.billing_state);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(TextUtils.isEmpty(mCustomerFirst.getText().toString())){

                        mCustomerFirst.setError("First Name cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mCustomerLast.getText().toString())){

                        mCustomerLast.setError("Last Name cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mEmail.getText().toString())){

                        mEmail.setError("Email cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mPhone.getText().toString())){

                        mPhone.setError("Phone Number cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingCompany.getText().toString())){

                        mBillingCompany.setError("Company cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressOne.getText().toString())){

                        mBillingAddressOne.setError("Address One cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressTwo.getText().toString())){

                        mBillingAddressTwo.setError("Address Two cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressCP.getText().toString())){

                        mBillingAddressCP.setError("Postal Code cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressCity.getText().toString())){

                        mBillingAddressCity.setError("City cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressState.getText().toString())){

                        mBillingAddressState.setError("Stae cannot be Empty.");
                        return;

                    }

                    if(TextUtils.isEmpty(mBillingAddressCountry.getText().toString())){

                        mBillingAddressCountry.setError("Country cannot be Empty.");
                        return;

                    } else if (mOrderSelected.getItems().size() > 0) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderNew.this)
                                .setTitle(getString(R.string.new_order_title))
                                .setMessage(getString(R.string.order_create_confirmation))
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        createOrder();
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        alertDialogBuilder.create().show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.invalid_items), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSaveIncomplete) {
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setMethodId(getString(R.string.default_payment_code));
            paymentDetails.setMethodTitle(getString(R.string.default_payment));
            paymentDetails.setPaid(true);
            mOrderSelected.setPaymentDetails(paymentDetails);

            ShippingLine shippingLine = new ShippingLine();
            shippingLine.setMethodId(getString(R.string.default_shipping_method_id));
            shippingLine.setMethodTitle(getString(R.string.default_shipping_method_title));
            shippingLine.setTotal(getString(R.string.default_shipping_method_title));
            mOrderSelected.getShippingLines().clear();
            mOrderSelected.getShippingLines().add(shippingLine);

            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setFirstName(mCustomerFirst.getText().toString());
            billingAddress.setLastName(mCustomerLast.getText().toString());
            billingAddress.setCompany(mBillingCompany.getText().toString());
            billingAddress.setAddressOne(mBillingAddressOne.getText().toString());
            billingAddress.setAddressTwo(mBillingAddressTwo.getText().toString());
            billingAddress.setCity(mBillingAddressCity.getText().toString());
            billingAddress.setState(mBillingAddressState.getText().toString());
            billingAddress.setPostcode(mBillingAddressCP.getText().toString());
            billingAddress.setCountry(mBillingAddressCountry.getText().toString());
            billingAddress.setEmail(mEmail.getText().toString());
            billingAddress.setPhone(mPhone.getText().toString());
            mOrderSelected.setBillingAddress(billingAddress);

            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setFirstName(mCustomerFirst.getText().toString());
            shippingAddress.setLastName(mCustomerLast.getText().toString());
            shippingAddress.setCompany(mBillingCompany.getText().toString());
            shippingAddress.setAddressOne(mBillingAddressOne.getText().toString());
            shippingAddress.setAddressTwo(mBillingAddressTwo.getText().toString());
            shippingAddress.setCity(mBillingAddressCity.getText().toString());
            shippingAddress.setState(mBillingAddressState.getText().toString());
            shippingAddress.setPostcode(mBillingAddressCP.getText().toString());
            shippingAddress.setCountry(mBillingAddressCountry.getText().toString());
            mOrderSelected.setShippingAddress(shippingAddress);

            String json = mGson.toJson(mOrderSelected);
            Log.i(LOG_TAG, "Json " +json);

            Utility.setPreferredShoppingCard(getApplicationContext(), json);
        }

    }

    private void refreshView() {
        mTotal = 0;
        String json = Utility.getPreferredShoppingCard(getApplicationContext());
        if (json != null) {
            mOrderSelected = mGson.fromJson(json, Order.class);
        } else {
            mOrderSelected = new Order();

            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setCompany(getString(R.string.default_company));
            billingAddress.setFirstName(getString(R.string.default_first_name));
            billingAddress.setLastName(getString(R.string.default_last_name));
            billingAddress.setEmail(getString(R.string.default_email));
            billingAddress.setPhone(getString(R.string.default_phone));

            billingAddress.setAddressOne(getString(R.string.default_line_one));
            billingAddress.setAddressTwo(getString(R.string.default_line_two));
            billingAddress.setCity(getString(R.string.default_city));
            billingAddress.setState(getString(R.string.default_state));
            billingAddress.setPostcode(getString(R.string.default_postal_code));
            billingAddress.setCountry(getString(R.string.default_country));

            mOrderSelected.setBillingAddress(billingAddress);

            Utility.setPreferredShoppingCard(getApplicationContext(), mGson.toJson(mOrderSelected));
        }

        Button clear = (Button) findViewById(R.id.clear);
        if (mOrderSelected.getItems().size() == 0) {
            clear.setVisibility(View.GONE);
        }
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreProducts();
                Utility.setPreferredShoppingCard(getApplicationContext(), null);
                mSaveIncomplete = false;
                finish();
            }
        });

        mPrice.setText(getString(R.string.price, '0'));

        mCustomerFirst.setText(mOrderSelected.getBillingAddress().getFirstName());
        mCustomerLast.setText(mOrderSelected.getBillingAddress().getLastName());
        mEmail.setText(mOrderSelected.getBillingAddress().getEmail());
        mPhone.setText(mOrderSelected.getBillingAddress().getPhone());

        mBillingCompany.setText(mOrderSelected.getBillingAddress().getCompany());

        /*mBillingAddressOne.setText(mOrderSelected.getBillingAddress().getAddressOne());
        mBillingAddressTwo.setText(mOrderSelected.getBillingAddress().getAddressTwo());
        mBillingAddressCP.setText(mOrderSelected.getBillingAddress().getPostcode());
        mBillingAddressState.setText(mOrderSelected.getBillingAddress().getState());
        mBillingAddressCity.setText(mOrderSelected.getBillingAddress().getCity());
        mBillingAddressCountry.setText(mOrderSelected.getBillingAddress().getCountry());*/

        LinearLayout cardDetails = (LinearLayout) findViewById(R.id.shopping_card_details);
        while(cardDetails.getChildCount() > 2) {
            cardDetails.removeViewAt(2);
        }

        List<String> ids = new ArrayList<>();
        List<String> parameters = new ArrayList<>();

        for (Item item : mOrderSelected.getItems()) {
            ids.add(String.valueOf(item.getProductId()));
            parameters.add("?");
        }

        String query = AndreEmilioContract.ProductEntry.COLUMN_ID + " IN (" + TextUtils.join(", ", parameters) + ")";
        Cursor cursor = getContentResolver().query(AndreEmilioContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                ids.toArray(new String[ids.size()]),
                null);

        List<Product> products = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if (json != null) {
                        Product product = mGson.fromJson(json, Product.class);
                        products.add(product);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        for (final Item item : mOrderSelected.getItems()) {
            if(item.getTotal() == null){
                item.setTotal("0");
            }
            mTotal += Float.valueOf(item.getTotal());

            View child = getLayoutInflater().inflate(R.layout.activity_order_item, null);
            ImageView imageView = (ImageView) child.findViewById(R.id.image);
            TextView quantity = (TextView) child.findViewById(R.id.quantity);
            TextView description = (TextView) child.findViewById(R.id.description);
            TextView price = (TextView) child.findViewById(R.id.price);
            TextView sku = (TextView) child.findViewById(R.id.sku);

            quantity.setText(String.valueOf(item.getQuantity()));
            if (item.getMeta().size() > 0) {
                String descriptionWithMeta = item.getName();
                for (MetaItem itemMeta : item.getMeta()) {
                    descriptionWithMeta += "\n" + itemMeta.getLabel() + " " + itemMeta.getValue();
                }
                description.setText(descriptionWithMeta);
            } else {
                description.setText(item.getName());
            }
            price.setText(getString(R.string.price, item.getTotal()));
            sku.setText(item.getSku());

            Product productForItem = null;
            for (Product product : products) {
                if (product.getId() == item.getProductId()) {
                    productForItem = product;
                    break;
                }
                for (Variation variation : product.getVariations()) {
                    if (variation.getId() == item.getProductId()) {
                        productForItem = product;
                        break;
                    }
                }
            }

            if (productForItem == null) {
                Log.v(LOG_TAG, "Missing product");
            } else {
                Picasso.with(getApplicationContext())
                        .load(productForItem.getFeaturedSrc())
                        .resize(50, 50)
                        .centerCrop()
                        .placeholder(android.R.color.transparent)
                        .error(R.drawable.ic_action_cancel)
                        .into(imageView);
            }
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView sku = (TextView) v.findViewById(R.id.sku);
                    //Toast.makeText(getApplicationContext(), sku.getText().toString(), Toast.LENGTH_LONG).show();

                    Product product = null;
                    String query = AndreEmilioContract.ProductEntry.COLUMN_SKU + " == ?" ;
                    String[] parametersOrder = new String[]{ sku.getText().toString() };
                    Cursor cursor = getContentResolver().query(AndreEmilioContract.ProductEntry.CONTENT_URI,
                            PRODUCT_PROJECTION,
                            query,
                            parametersOrder,
                            null);
                    if(cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                                if(json!=null){
                                    product = mGson.fromJson(json, Product.class);
                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }

                    if(product != null) {
                        Intent intent = new Intent(getApplicationContext(), OrderAddProduct.class);
                        intent.putExtra("product", product.getId());
                        startActivity(intent);
                    } else {
                        mOrderSelected.getItems().remove(item);
                        String json = mGson.toJson(mOrderSelected);
                        Utility.setPreferredShoppingCard(getApplicationContext(), json);
                        refreshView();
                    }

                }
            });
            cardDetails.addView(child);
        }

        mPrice.setText(getString(R.string.price, String.valueOf(mTotal)));

    }

    private void restoreProducts() {

        List<String> ids = new ArrayList<>();
        List<String> parameters = new ArrayList<>();

        for (Item item : mOrderSelected.getItems()) {
            ids.add(String.valueOf(item.getProductId()));
            parameters.add("?");
        }

        String query = AndreEmilioContract.ProductEntry.COLUMN_ID + " IN (" + TextUtils.join(", ", parameters) + ")";
        Cursor cursor = getContentResolver().query(AndreEmilioContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                ids.toArray(new String[ids.size()]),
                null);

        List<Product> products = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if (json != null) {
                        Product product = mGson.fromJson(json, Product.class);
                        products.add(product);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        ArrayList<ContentValues> productsValues = new ArrayList<ContentValues>();

        for (Item item : mOrderSelected.getItems()) {
            for (Product product : products) {

                int stockRestore = product.getStockQuantity() + item.getQuantity();
                if (product.getId() == item.getProductId()) {
                    product.setStockQuantity(stockRestore);
                }

                ContentValues productValues = new ContentValues();
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_ID, product.getId());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_SKU, product.getSku());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_JSON, mGson.toJson(product));
                productValues.put(AndreEmilioContract.ProductEntry.COLUMN_ENABLE, 1);

                productsValues.add(productValues);

                for (Variation variation : product.getVariations()) {

                    //TODO, CHANGE THIS APPROACH
                    product.setSku(variation.getSku());
                    product.setPrice(variation.getPrice());

                    if (variation.getId() == item.getProductId()) {
                        product.setStockQuantity(variation.getStockQuantity() + item.getQuantity());
                    }

                    ContentValues variationValues = new ContentValues();
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_ID, variation.getId());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_SKU, product.getSku());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_CATEGORIES, product.getCategoryNames());
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_JSON, mGson.toJson(product));
                    variationValues.put(AndreEmilioContract.ProductEntry.COLUMN_ENABLE, 1);

                    productsValues.add(variationValues);

                }


            }
        }

        ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
        productsValuesArray = productsValues.toArray(productsValuesArray);
        int ordersRowsUpdated = getContentResolver().bulkInsert(AndreEmilioContract.ProductEntry.CONTENT_URI, productsValuesArray);
        Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");

        getContentResolver().notifyChange(AndreEmilioContract.ProductEntry.CONTENT_URI, null, false);

    }

    private void createOrder() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(true);

        mProgress.setMessage(getString(R.string.create_order));
        mProgress.show();

        mOrderSelected.setTotalLineItemsQuantity(-1);
        //mOrderSelected.setStatus("completed");

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setMethodId(getString(R.string.default_payment_code));
        paymentDetails.setMethodTitle(getString(R.string.default_payment));
        paymentDetails.setPaid(true);
        mOrderSelected.setPaymentDetails(paymentDetails);

        ShippingLine shippingLine = new ShippingLine();
        shippingLine.setMethodId(getString(R.string.default_shipping_method_id));
        shippingLine.setMethodTitle(getString(R.string.default_shipping_method_title));
        shippingLine.setTotal(getString(R.string.default_shipping_method_title));
        mOrderSelected.getShippingLines().add(shippingLine);

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setFirstName(mCustomerFirst.getText().toString());
        billingAddress.setLastName(mCustomerLast.getText().toString());
        billingAddress.setCompany(mBillingCompany.getText().toString());
        billingAddress.setAddressOne(mBillingAddressOne.getText().toString());
        billingAddress.setAddressTwo(mBillingAddressTwo.getText().toString());
        billingAddress.setCity(mBillingAddressCity.getText().toString());
        billingAddress.setState(mBillingAddressState.getText().toString());
        billingAddress.setPostcode(mBillingAddressCP.getText().toString());
        billingAddress.setCountry(mBillingAddressCountry.getText().toString());
        billingAddress.setEmail(mEmail.getText().toString());
        billingAddress.setPhone(mPhone.getText().toString());
        mOrderSelected.setBillingAddress(billingAddress);

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setFirstName(mCustomerFirst.getText().toString());
        shippingAddress.setLastName(mCustomerLast.getText().toString());
        shippingAddress.setCompany(mBillingCompany.getText().toString());
        shippingAddress.setAddressOne(mBillingAddressOne.getText().toString());
        shippingAddress.setAddressTwo(mBillingAddressTwo.getText().toString());
        shippingAddress.setCity(mBillingAddressCity.getText().toString());
        shippingAddress.setState(mBillingAddressState.getText().toString());
        shippingAddress.setPostcode(mBillingAddressCP.getText().toString());
        shippingAddress.setCountry(mBillingAddressCountry.getText().toString());
        mOrderSelected.setShippingAddress(shippingAddress);

        for (Item item : mOrderSelected.getItems()) {
            item.setName(null);
            item.setPrice(null);
            item.setSku(null);
            item.setTotal(null);
        }

        String json = mGson.toJson(mOrderSelected);
        Log.i(LOG_TAG, "Json1 "+json);

        OrderResponse orderCreate = new OrderResponse();
        orderCreate.setOrder(mOrderSelected);


       finalizeOrder(mOrderSelected);

        // TODO:Z
      /*  WooCommerce woocommerceApi = ((Dezynish) getApplication()).getWoocommerceApiHandler();
        Call<OrderResponse> call = woocommerceApi.insertOrder(orderCreate);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                int statusCode = response.code();
                if (statusCode == 201) {

            Order order = response.body().getOrder();

            String json = mGson.toJson(order);
            Log.i(LOG_TAG, json);

                    ContentValues orderValues = new ContentValues();
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ID, order.getId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_NUMBER, order.getOrderNumber());
                    if (order.getCreatedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CREATED_AT, DezynishContract.getDbDateString(order.getCreatedAt()));
                    }
                    if (order.getUpdatedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_UPDATED_AT, DezynishContract.getDbDateString(order.getUpdatedAt()));
                    }
                    if (order.getCompletedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_COMPLETED_AT, DezynishContract.getDbDateString(order.getCompletedAt()));
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_STATUS, order.getStatus());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CURRENCY, order.getCurrency());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL, order.getTotal());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SUBTOTAL, order.getSubtotal());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY, order.getTotalLineItemsQuantity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_TAX, order.getTotalTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_SHIPPING, order.getTotalShipping());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_TAX, order.getCartTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_TAX, order.getShippingTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT, order.getTotalDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_DISCOUNT, order.getCartDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_DISCOUNT, order.getOrderDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_METHODS, order.getShippingMethods());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_NOTE, order.getNote());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_VIEW_ORDER_URL, order.getViewOrderUrl());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID, order.getPaymentDetails().getMethodId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE, order.getPaymentDetails().getMethodTitle());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID, order.getPaymentDetails().isPaid() ? "1" : "0");
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME, order.getBillingAddress().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_LAST_NAME, order.getBillingAddress().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COMPANY, order.getBillingAddress().getCompany());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1, order.getBillingAddress().getAddressOne());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2, order.getBillingAddress().getAddressTwo());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_CITY, order.getBillingAddress().getCity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_STATE, order.getBillingAddress().getState());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_POSTCODE, order.getBillingAddress().getPostcode());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COUNTRY, order.getBillingAddress().getCountry());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_EMAIL, order.getBillingAddress().getEmail());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_PHONE, order.getBillingAddress().getPhone());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME, order.getShippingAddress().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME, order.getShippingAddress().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COMPANY, order.getShippingAddress().getCompany());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1, order.getShippingAddress().getAddressOne());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2, order.getShippingAddress().getAddressTwo());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_CITY, order.getShippingAddress().getCity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_STATE, order.getShippingAddress().getState());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE, order.getShippingAddress().getPostcode());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY, order.getShippingAddress().getCountry());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ID, order.getCustomerId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL, order.getCustomer().getEmail());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME, order.getCustomer().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME, order.getCustomer().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME, order.getCustomer().getUsername());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID, order.getCustomer().getLastOrderId());
                    if (order.getCustomer().getLastOrderDate() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE, DezynishContract.getDbDateString(order.getCustomer().getLastOrderDate()));
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT, order.getCustomer().getOrdersCount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND, order.getCustomer().getTotalSpent());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL, order.getCustomer().getAvatarUrl());
                    if (order.getCustomer().getBillingAddress() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME, order.getCustomer().getBillingAddress().getFirstName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME, order.getCustomer().getBillingAddress().getLastName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY, order.getCustomer().getBillingAddress().getCompany());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1, order.getCustomer().getBillingAddress().getAddressOne());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2, order.getCustomer().getBillingAddress().getAddressTwo());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY, order.getCustomer().getBillingAddress().getCity());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE, order.getCustomer().getBillingAddress().getState());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE, order.getCustomer().getBillingAddress().getPostcode());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY, order.getCustomer().getBillingAddress().getCountry());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL, order.getCustomer().getBillingAddress().getEmail());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE, order.getCustomer().getBillingAddress().getPhone());
                    }
                    if (order.getCustomer().getShippingAddress() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME, order.getCustomer().getShippingAddress().getFirstName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME, order.getCustomer().getShippingAddress().getLastName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY, order.getCustomer().getShippingAddress().getCompany());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1, order.getCustomer().getShippingAddress().getAddressOne());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2, order.getCustomer().getShippingAddress().getAddressTwo());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY, order.getCustomer().getShippingAddress().getCity());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE, order.getCustomer().getShippingAddress().getState());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE, order.getCustomer().getShippingAddress().getPostcode());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY, order.getCustomer().getShippingAddress().getCountry());
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_JSON, mGson.toJson(order));
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ENABLE, 1);

                    Uri insertedOrderUri = getContentResolver().insert(DezynishContract.OrdersEntry.CONTENT_URI, orderValues);
                    long orderId = ContentUris.parseId(insertedOrderUri);
                    Log.d(LOG_TAG, "Orders successful updated ID: " + orderId);

                    finalizeOrder(order);

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.dismiss();
                        }
                    });
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setEnabled(true);
                }
            }

            public void onFailure(Call<OrderResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.dismiss();
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.setEnabled(true);
                    }
                });
            }
        });*/
    }

    private void finalizeOrder(Order order) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setMessage(getString(R.string.finalize_order));
            }
        });

        OrderUpdate orderUpdate = new OrderUpdate();
        OrderUpdateValues orderUpdateValues = new OrderUpdateValues();
        orderUpdateValues.setStatus("completed");
        orderUpdate.setOrder(orderUpdateValues);

        // TODO:Z
       /* WooCommerce woocommerceApi = ((Dezynish) getApplication()).getWoocommerceApiHandler();
        Call<OrderResponse> call = woocommerceApi.updateOrder(order.getOrderNumber(), orderUpdate);
        call.enqueue(new Callback<OrderResponse>() {

            @Override
            public void onResponse(final Call<OrderResponse> call, final Response<OrderResponse> response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.dismiss();
                    }
                });

                int statusCode = response.code();
                if (statusCode == 200) {
                    Order order = response.body().getOrder();
                    ContentValues orderValues = new ContentValues();
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ID, order.getId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_NUMBER, order.getOrderNumber());
                    if (order.getCreatedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CREATED_AT, DezynishContract.getDbDateString(order.getCreatedAt()));
                    }
                    if (order.getUpdatedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_UPDATED_AT, DezynishContract.getDbDateString(order.getUpdatedAt()));
                    }
                    if (order.getCompletedAt() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_COMPLETED_AT, DezynishContract.getDbDateString(order.getCompletedAt()));
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_STATUS, order.getStatus());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CURRENCY, order.getCurrency());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL, order.getTotal());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SUBTOTAL, order.getSubtotal());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY, order.getTotalLineItemsQuantity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_TAX, order.getTotalTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_SHIPPING, order.getTotalShipping());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_TAX, order.getCartTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_TAX, order.getShippingTax());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT, order.getTotalDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CART_DISCOUNT, order.getCartDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ORDER_DISCOUNT, order.getOrderDiscount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_METHODS, order.getShippingMethods());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_NOTE, order.getNote());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_VIEW_ORDER_URL, order.getViewOrderUrl());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID, order.getPaymentDetails().getMethodId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE, order.getPaymentDetails().getMethodTitle());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID, order.getPaymentDetails().isPaid() ? "1" : "0");
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME, order.getBillingAddress().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_LAST_NAME, order.getBillingAddress().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COMPANY, order.getBillingAddress().getCompany());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1, order.getBillingAddress().getAddressOne());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2, order.getBillingAddress().getAddressTwo());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_CITY, order.getBillingAddress().getCity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_STATE, order.getBillingAddress().getState());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_POSTCODE, order.getBillingAddress().getPostcode());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_COUNTRY, order.getBillingAddress().getCountry());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_EMAIL, order.getBillingAddress().getEmail());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_BILLING_PHONE, order.getBillingAddress().getPhone());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME, order.getShippingAddress().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME, order.getShippingAddress().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COMPANY, order.getShippingAddress().getCompany());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1, order.getShippingAddress().getAddressOne());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2, order.getShippingAddress().getAddressTwo());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_CITY, order.getShippingAddress().getCity());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_STATE, order.getShippingAddress().getState());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE, order.getShippingAddress().getPostcode());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY, order.getShippingAddress().getCountry());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ID, order.getCustomerId());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL, order.getCustomer().getEmail());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME, order.getCustomer().getFirstName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME, order.getCustomer().getLastName());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME, order.getCustomer().getUsername());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID, order.getCustomer().getLastOrderId());
                    if (order.getCustomer().getLastOrderDate() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE, DezynishContract.getDbDateString(order.getCustomer().getLastOrderDate()));
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT, order.getCustomer().getOrdersCount());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND, order.getCustomer().getTotalSpent());
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL, order.getCustomer().getAvatarUrl());
                    if (order.getCustomer().getBillingAddress() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME, order.getCustomer().getBillingAddress().getFirstName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME, order.getCustomer().getBillingAddress().getLastName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY, order.getCustomer().getBillingAddress().getCompany());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1, order.getCustomer().getBillingAddress().getAddressOne());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2, order.getCustomer().getBillingAddress().getAddressTwo());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY, order.getCustomer().getBillingAddress().getCity());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE, order.getCustomer().getBillingAddress().getState());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE, order.getCustomer().getBillingAddress().getPostcode());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY, order.getCustomer().getBillingAddress().getCountry());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL, order.getCustomer().getBillingAddress().getEmail());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE, order.getCustomer().getBillingAddress().getPhone());
                    }
                    if (order.getCustomer().getShippingAddress() != null) {
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME, order.getCustomer().getShippingAddress().getFirstName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME, order.getCustomer().getShippingAddress().getLastName());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY, order.getCustomer().getShippingAddress().getCompany());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1, order.getCustomer().getShippingAddress().getAddressOne());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2, order.getCustomer().getShippingAddress().getAddressTwo());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY, order.getCustomer().getShippingAddress().getCity());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE, order.getCustomer().getShippingAddress().getState());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE, order.getCustomer().getShippingAddress().getPostcode());
                        orderValues.put(DezynishContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY, order.getCustomer().getShippingAddress().getCountry());
                    }
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_JSON, mGson.toJson(order));
                    orderValues.put(DezynishContract.OrdersEntry.COLUMN_ENABLE, 1);

                    Uri insertedOrderUri = getContentResolver().insert(DezynishContract.OrdersEntry.CONTENT_URI, orderValues);
                    long orderId = ContentUris.parseId(insertedOrderUri);
                    Log.d(LOG_TAG, "Orders successful updated ID: " + orderId);

                    Utility.setPreferredShoppingCard(getApplicationContext(), null);
                    mSaveIncomplete = false;

                    getContentResolver().notifyChange(DezynishContract.OrdersEntry.CONTENT_URI, null, false);
                    finish();

                } else {
                    Log.e(LOG_TAG, "onFailure ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_update), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.error_update), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });*/
    }

}
