package xyz.nofoot.registry;

import lombok.*;

import java.io.Serializable;

/**
 * @projectName: nRPC
 * @package: dto
 * @className: Hello
 * @author: NoFoot
 * @date: 4/21/2023 2:13 PM
 * @description: TODO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
