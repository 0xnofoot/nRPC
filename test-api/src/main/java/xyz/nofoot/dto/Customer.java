package xyz.nofoot.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.dto
 * @className: Customer
 * @author: NoFoot
 * @date: 5/8/2023 1:19 PM
 * @description: TODO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Customer implements Serializable {
    private String name;
    private Integer id;
    private Integer age;
}
