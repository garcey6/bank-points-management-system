package com.bank.points.dto;

import lombok.Data;

@Data
public class ExchangeRequest {

    private Long productId;

    private Integer quantity;
    
    private String shippingAddress;
    
    private String contactName;
    
    private String contactPhone;
}
