package com.e2esp.andreemilio.woocommerce;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.e2esp.andreemilio.models.orders.Notes;
import com.e2esp.andreemilio.models.orders.OrderUpdate;
import com.e2esp.andreemilio.utilities.Consts;
import com.e2esp.andreemilio.utilities.Utility;
import com.e2esp.andreemilio.woocommerce.WCBuilder;
import com.e2esp.andreemilio.models.orders.Count;
import com.e2esp.andreemilio.models.orders.Order;
import com.e2esp.andreemilio.models.orders.OrderResponse;
import com.e2esp.andreemilio.models.orders.Orders;
import com.e2esp.andreemilio.models.products.Product;
import com.e2esp.andreemilio.enums.RequestMethod;
import com.e2esp.andreemilio.models.shop.Shop;
import com.e2esp.andreemilio.woocommerce.helpers.Endpoints;
import com.e2esp.andreemilio.woocommerce.helpers.OAuthSigner;
import com.e2esp.andreemilio.interfaces.ListCallbacks;
import com.e2esp.andreemilio.interfaces.ObjectCallbacks;
import com.e2esp.andreemilio.models.products.Category;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit.RequestInterceptor;
import retrofit2.Call;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import retrofit.mime.TypedByteArray;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.stream.JsonReader;

import static com.e2esp.andreemilio.enums.RequestMethod.GET;
import static java.security.AccessController.getContext;

/**
 * Created by Zain on 2/20/17.
 */

public class WooCommerce {

    private final String TAG = WooCommerce.class.getName();

    private static WooCommerce ourInstance = new WooCommerce();
    static volatile WooCommerce singleton = null;

    private WCBuilder wcBuilder;
    private OAuthSigner OAuthSigner;
    private Gson gson = new GsonBuilder().create();

    public static WooCommerce getInstance() {
        if (singleton == null) {
            synchronized (WooCommerce.class) {
                if (singleton == null) {
                    singleton = ourInstance;
                }
            }
        }
        return singleton;
    }

    private WooCommerce() {
        Log.d(TAG, "Instance created");
    }

    public void initialize(WCBuilder builder) {
        this.wcBuilder = builder;
        OAuthSigner = new OAuthSigner(wcBuilder);
        Log.i(TAG, "onCreate");
    }

    private interface ShopInterface {
        @GET(Endpoints.SHOP_ENDPOINT)
        void getShop(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);
    }

    private interface ProductsInterface {
        @GET(Endpoints.PRODUCTS_ENDPOINT + "/count")
        void getCount(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.PRODUCTS_ENDPOINT + "/{id}")
         void getProduct(@Path("id")String id,@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.PRODUCTS_ENDPOINT)
         void getProducts(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.CATEGORIES_ENDPOINT)
         void getCategories(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);
    }

    private interface OrdersInterface {
        @GET(Endpoints.ORDERS_ENDPOINT + "/count")
        void getCount(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.ORDERS_ENDPOINT + "/{id}")
        void getOrder(@Path("id")String id,@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @GET(Endpoints.ORDERS_ENDPOINT )
        void getOrders(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @PUT(Endpoints.ORDERS_ENDPOINT)
        void updateOrder(@QueryMap LinkedHashMap<String, String> options, Callback<Response> response);

        @POST(Endpoints.ORDERS_ENDPOINT)
        void insertOrder(@QueryMap LinkedHashMap<String, String> options, @Body OrderResponse order,Callback<Response> response);

        /*@POST(Endpoints.ORDERS_ENDPOINT)
        Call<OrderResponse> insertOrder(@Body OrderResponse order);*/

    }

    public void insertOrder(OrderResponse order,final ListCallbacks fetched){

        /*StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i("Insert Order builder",builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        HashMap<String, String> options = new HashMap<>();*/
        /*options.put("Accept", "application/json");
        options.put("Content-Type", "application/json");*/
        /*options.put("Username","ali.naqi");
        options.put("Password","0%t1y0ETlYiE^Um%HQ%Qa%W$");
*/
        //Long lastSyncTimeStamp =  Utility.getPreferredLastSync(getContext());

        /*String UserName = "ali.naqi";
        String Password = "0%t1y0ETlYiE^Um%HQ%Qa%W$";
        //AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
        final String authenticationHeader = "Basic " + Base64.encodeToString(
                (UserName+ ":" + Password).getBytes(),
                Base64.NO_WRAP);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .cache(null);

        //TODO Remove this if you don't have a self cert
        *//*
        if(Utility.getSSLSocketFactory() != null){
            clientBuilder
                    .sslSocketFactory(Utility.getSSLSocketFactory())
                    .hostnameVerifier(Utility.getHostnameVerifier());
        }
        */


        /*Interceptor basicAuthenticatorInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request authenticateRequest = request.newBuilder()
                        .addHeader("Authorization", authenticationHeader)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(authenticateRequest);
            }
        };*/


        //OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(key, secret);
        //consumer.setSigningStrategy(new QueryStringSigningStrategy());
        //clientBuilder.addInterceptor(new SigningInterceptor(consumer));
        //clientBuilder.addInterceptor(basicAuthenticatorInterceptor);

      /*  RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent","AndreEmilio");
                request.addHeader("Accept", "application/json");
                request.addHeader("Content-Type", "application/json");
            }
        };*/

      /*  OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .cache(null);

        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                requestBuilder.removeHeader("Content-Type");
                requestBuilder.addHeader("Content-Type", "application/json");
                requestBuilder.addHeader("Accept", "application/json");
                return chain.proceed(requestBuilder.build());
            }
        });*/

        final String authenticationHeader = "Basic " + Base64.encodeToString(
                (Consts.WC_API_KEY+ ":" + Consts.WC_API_SECRET).getBytes(),
                Base64.NO_WRAP);
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent","AndreEmilio");
                request.addHeader("Authorization", authenticationHeader);
                request.addHeader("Accept", "application/json");
                request.addHeader("Content-Type", "application/json");

            }
        };
        /*RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", "AndreEmilio");
                request.addHeader("Content-Type", "application/json");

            }
        };*/


        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i("Insert Order builder",builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .setRequestInterceptor(requestInterceptor)
                .build();

        HashMap<String, String> options = new HashMap<>();

        OrdersInterface insertOrderApi = adapter.create(OrdersInterface.class);
        insertOrderApi.insertOrder(OAuthSigner.getSignature(RequestMethod.POST, Endpoints.ORDERS_ENDPOINT, null),order, new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(" Error ", " Error Cause: " + error.getCause());
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });
    }

    public void updateOrder(String orderNumber, OrderUpdate orderUpdate,final ListCallbacks fetched){

        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        System.gc();
        HashMap<String, String> params = new HashMap<>();
        params.put("Order Number",orderNumber);
        params.put("Order Update",String.valueOf(orderUpdate));
        OrdersInterface api = adapter.create(OrdersInterface.class);

        api.updateOrder(OAuthSigner.getSignature(params,RequestMethod.PUT, Endpoints.ORDERS_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {

            }
            @Override
            public void failure(RetrofitError error) {

                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });


    }

    public void getShop(final ObjectCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ShopInterface api = adapter.create(ShopInterface.class);
        LinkedHashMap<String, String> options = new LinkedHashMap<>();

        api.getShop(OAuthSigner.getSignature(options,RequestMethod.GET, Endpoints.SHOP_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    Gson gson = new Gson();
                    Shop shop = gson.fromJson(bodyString, Shop.class);
                    System.out.println(shop);
                    fetched.Callback(shop, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getUrl());
                fetched.Callback(null, error);
            }
        });
    }

    public void getAllProducts(final ListCallbacks fetched) {
        getProducts(null, fetched);
    }

    public void getProducts(int page, final ListCallbacks fetched) {
        getProducts(page, 2, fetched);

    }

    public void getProducts(int page, int pageSize, final ListCallbacks fetched) {
        HashMap<String, String> params = new HashMap<>();
        if (page > 0) {
            params.put("page", String.valueOf(page));
        }
        if (pageSize > 0) {
            params.put("filter[limit]", String.valueOf(pageSize));

            Log.d(TAG,"Products Load");
        }
        getProducts(params, fetched);
    }

    public void getProducts(HashMap<String, String> params, final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        System.gc();

        ProductsInterface api = adapter.create(ProductsInterface.class);

        api.getProducts(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.PRODUCTS_ENDPOINT, params), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getProducts :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    Gson gson = new Gson();
                    ArrayList<Product> products = gson.fromJson(jsonArray.toString(), new TypeToken<List<Product>>(){}.getType());
                    fetched.Callback(products, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }

            @Override
            public void failure(RetrofitError error) {

                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });
    }

    public void getProduct(String id, final ObjectCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("id",id);

        api.getProduct(id, OAuthSigner.getSignature(options,RequestMethod.GET, Endpoints.PRODUCTS_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONObject jsonProduct = jsonObject.getJSONObject("product");
                    Gson gson = new Gson();
                    Product product = gson.fromJson(jsonProduct.toString(), Product.class);
                    System.out.println(product);
                    fetched.Callback(product, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getUrl());
                fetched.Callback(null, error);
            }
        });
    }

    public void getProductsCount(final ObjectCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG," Get Products Count " + builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);
        LinkedHashMap<String, String> options = new LinkedHashMap<>();

        api.getCount(OAuthSigner.getSignature(options,RequestMethod.GET, Endpoints.PRODUCTS_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    Gson gson = new Gson();
                    Count count = gson.fromJson(bodyString, Count.class);
                    System.out.println(count);
                    fetched.Callback(count, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getUrl());
                fetched.Callback(null, error);
            }
        });
    }

    public void getCategories(final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        ProductsInterface api = adapter.create(ProductsInterface.class);

        api.getCategories(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.CATEGORIES_ENDPOINT, null), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getCategories :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("product_categories");
                    Gson gson = new Gson();
                    ArrayList<Category> categories = gson.fromJson(jsonArray.toString(), new TypeToken<List<Category>>(){}.getType());
                    fetched.Callback(categories, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });
    }

    public void getOrders(HashMap<String, String> params, final ListCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        OrdersInterface api = adapter.create(OrdersInterface.class);

        api.getOrders(OAuthSigner.getSignature(RequestMethod.GET, Endpoints.ORDERS_ENDPOINT, params), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "getProducts :: response:"+bodyString);
                try {
                    JSONObject jsonObject = new JSONObject(bodyString);
                    JSONArray jsonArray = jsonObject.getJSONArray("orders");
                    Gson gson = new Gson();
                    ArrayList<Order> orders = gson.fromJson(jsonArray.toString(), new TypeToken<List<Order>>(){}.getType());
                    fetched.Callback(orders, null);
                } catch (Exception error) {
                    error.printStackTrace();
                    fetched.Callback(null, error);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                fetched.Callback(null, error);
            }
        });
    }

    public void getOrdersCount(final ObjectCallbacks fetched) {
        StringBuilder builder = new StringBuilder();
        builder.append(wcBuilder.isHttps() ? "https://" : "http://");
        builder.append(wcBuilder.getBaseUrl() + "/");
        builder.append("wc-api/v3");
        Log.i(TAG,builder.toString());
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(builder.toString())
                .build();

        OrdersInterface api = adapter.create(OrdersInterface.class);
        LinkedHashMap<String, String> options = new LinkedHashMap<>();

        api.getCount(OAuthSigner.getSignature(options,RequestMethod.GET, Endpoints.ORDERS_ENDPOINT), new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {
                String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                try {
                    Gson gson = new Gson();
                    Count count = gson.fromJson(bodyString, Count.class);
                    System.out.println(count);
                    fetched.Callback(count, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(RetrofitError error) {
                System.out.println(error.getUrl());
                fetched.Callback(null, error);
            }
        });
    }

}
