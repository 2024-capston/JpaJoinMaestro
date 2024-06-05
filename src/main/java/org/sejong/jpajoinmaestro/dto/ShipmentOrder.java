package org.sejong.jpajoinmaestro.dto;

import lombok.Getter;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.domain.*;

@Getter
@Setter
public class ShipmentOrder {
    @DTOFieldMapping(domain = User.class, fieldName = "id")
    Long userId;

    @DTOFieldMapping(domain = Shipment.class, fieldName = "shipmentStatus")
    String shipmentStatus;

    @DTOFieldMapping(domain = Orders.class, fieldName = "id")
    Long ordersId;

    @DTOFieldMapping(domain = Shipment.class, fieldName = "id")
    Long shipmentId;

    @Override
    public String toString() {
        return "ShipmentOrder{" +
                "shipmentStatus=" + shipmentStatus +
                ", ordersId='" + ordersId + '\'' +
                ", userId=" + userId +
                ", shipmentId=" + shipmentId +
                '}';
    }
    public ShipmentOrder() {}

    public ShipmentOrder(String shipmentStatus, Long ordersId, Long userId, Long shipmentId) {
        this.shipmentStatus = shipmentStatus;
        this.ordersId = ordersId;
        this.userId = userId;
        this.shipmentId = shipmentId;
    }

}
