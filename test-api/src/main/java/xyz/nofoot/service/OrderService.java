package xyz.nofoot.service;

import xyz.nofoot.dto.Order;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.service
 * @interfaceName: OrderService
 * @author: NoFoot
 * @date: 5/8/2023 1:20 PM
 * @description: TODO
 */
public interface OrderService {
    String getOrder(Order order);

    Integer toPay(Order order);

    Boolean isPay(Order order);

    String generateOrder(Order order);

    String sayHello(Order order);
}
