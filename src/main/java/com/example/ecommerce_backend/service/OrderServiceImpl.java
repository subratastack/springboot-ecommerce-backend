package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.PurchaseOrder;
import com.example.ecommerce_backend.model.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderServiceImpl(
            PurchaseOrderRepository purchaseOrderRepository
    ) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }
    @Override
    public List<PurchaseOrder> getOrders(LocalUser user) {
        return purchaseOrderRepository.findByUser(user);
    }
}
