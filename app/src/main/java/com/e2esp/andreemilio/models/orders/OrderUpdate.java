package com.e2esp.andreemilio.models.orders;

import com.e2esp.andreemilio.models.orders.OrderUpdateValues;

/**
 * Created by Zain on 2/17/2017.
 */
public class OrderUpdate {

    private OrderUpdateValues order;

    public OrderUpdateValues getOrder() {
        return order;
    }

    public void setOrder(OrderUpdateValues order) {
        this.order = order;
    }

}
