package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.PurchaseOrder;

import java.util.List;

public interface OrderService {

    List<PurchaseOrder> getOrders(LocalUser user);
}
