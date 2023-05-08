package xyz.nofoot.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: Order
 * @author: NoFoot
 * @date: 5/8/2023 1:16 PM
 * @description: TODO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Order implements Serializable {
    private Integer orderId;
    private Market market;
    private Customer customer;
    private Integer cost;
    private Boolean isPay;
    private Hello hello;
}
