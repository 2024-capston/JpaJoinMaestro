package org.sejong.jpajoinmaestro.dto;

import lombok.Getter;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.domain.Orders;
import org.sejong.jpajoinmaestro.domain.Shipment;

@Getter
@Setter
public class ShipmentOrder {
    @DTOFieldMapping(domain = Shipment.class, fieldName = "id")
    Long shipmentId;

    @DTOFieldMapping(domain = Orders.class, fieldName = "id")
    Long ordersId;
}
