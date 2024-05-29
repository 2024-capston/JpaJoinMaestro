package org.sejong.jpajoinmaestro.dto;

import lombok.Getter;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.domain.*;

@Getter
@Setter
public class ShipmentOrder {
    @DTOFieldMapping(domain = User.class, fieldName = "username")
    String userName;

    @DTOFieldMapping(domain = Orders.class, fieldName = "id")
    Long ordersId;

    @DTOFieldMapping(domain = User.class, fieldName = "id")
    Long userId;

    @DTOFieldMapping(domain = Shipment.class, fieldName = "id")
    Long shipmentId;

}
