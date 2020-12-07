package com.davidagood.awssdkv2.dynamodb;

import java.util.ArrayList;
import java.util.List;

public class CustomerItemCollection {

    private Customer customer;
    private List<Order> orders = new ArrayList<>();

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public String toString() {
        return "CustomerItemCollection{" +
                "customer=" + customer +
                ", orders=" + orders +
                '}';
    }
}
