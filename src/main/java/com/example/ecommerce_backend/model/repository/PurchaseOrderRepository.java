package com.example.ecommerce_backend.model.repository;

import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByUser(LocalUser user);
}
