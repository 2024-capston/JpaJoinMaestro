package org.sejong.jpajoinmaestro.dto;

import lombok.Getter;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.domain.OrderDetail;
import org.sejong.jpajoinmaestro.domain.Orders;
import org.sejong.jpajoinmaestro.domain.Product;
import org.sejong.jpajoinmaestro.domain.User;

@Getter
@Setter
public class MyOrder {
    @DTOFieldMapping(domain= User.class, fieldName="username")
    private String username;

    @DTOFieldMapping(domain = Orders.class, fieldName = "status")
    private String status;

    @DTOFieldMapping(domain=Product.class, fieldName="productName")
    private String productName;

    @DTOFieldMapping(domain= OrderDetail.class, fieldName = "quantity")
    private int quantity;

    @DTOFieldMapping(domain=Product.class, fieldName="price")
    private int price;

    public MyOrder(String name, String status, String productName, int quantity, int price) {
        this.username = name;
        this.status = status;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
}
