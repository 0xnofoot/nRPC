package xyz.nofoot.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: Market
 * @author: NoFoot
 * @date: 5/8/2023 1:18 PM
 * @description: TODO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Market implements Serializable {
    private String name;
    private Integer version;
    private String address;
}
