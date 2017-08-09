package com.e2esp.andreemilio.interfaces;

import com.e2esp.andreemilio.models.customers.Customer;

/**
 * Created by Zain on 2/17/2017.
 */

public interface CustomerActions {
    void sendEmail(Customer customer);
    void makeACall(Customer customer);
}
