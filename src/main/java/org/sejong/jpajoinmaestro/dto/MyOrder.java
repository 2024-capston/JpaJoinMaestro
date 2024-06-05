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
    private Long quantity;

    @DTOFieldMapping(domain=Product.class, fieldName="price")
    private Long price;

    public MyOrder(){}

    public MyOrder(Long quantity, String productName, Long price, String status, String username) {
        this.username = username;
        this.status = status;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
}
