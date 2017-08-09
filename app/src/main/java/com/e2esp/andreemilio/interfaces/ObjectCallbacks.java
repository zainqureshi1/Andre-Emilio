package com.e2esp.andreemilio.interfaces;

import retrofit.RetrofitError;

/**
 * Created by Zain on 2/20/17.
 */

public interface ObjectCallbacks {
    void Callback(Object content, RetrofitError error);
}
