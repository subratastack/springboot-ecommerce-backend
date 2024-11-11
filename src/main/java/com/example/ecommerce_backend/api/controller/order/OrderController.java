package com.example.ecommerce_backend.api.controller.order;

import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.PurchaseOrder;
import com.example.ecommerce_backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getOrders(@AuthenticationPrincipal LocalUser user) {
        List<PurchaseOrder> orders = orderService.getOrders(user);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
