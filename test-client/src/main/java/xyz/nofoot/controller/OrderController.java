package xyz.nofoot.controller;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.annotation.RpcReference;
import xyz.nofoot.dto.Customer;
import xyz.nofoot.dto.Hello;
import xyz.nofoot.dto.Market;
import xyz.nofoot.dto.Order;
import xyz.nofoot.service.OrderService;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.controller
 * @className: OrderController
 * @author: NoFoot
 * @date: 4/21/2023 4:53 PM
 * @description: TODO
 */
@Slf4j
@RpcReference
public class OrderController {
    @RpcReference(group = "g1", version = "v1")
    private OrderService orderServiceG1V1;

    @RpcReference(group = "g1", version = "v2")
    private OrderService orderServiceG1V2;

    @RpcReference(group = "g1", version = "v3")
    private OrderService orderServiceG1V3;

    @RpcReference(group = "g2", version = "v1")
    private OrderService orderServiceG2V1;

    @RpcReference(group = "g2", version = "v2")
    private OrderService orderServiceG2V2;

    public void orderServiceTest(OrderService orderService, Order order) {
        log.info("[{}] 测试开始 [{}]", "///////////", "///////////");
        System.out.println(orderService.toString());

        String result1 = orderService.getOrder(order);
        log.info("执行 orderService, 返回 [{}]", result1);

        Integer result2 = orderService.toPay(order);
        log.info("执行 toPay, 返回 [{}]", result2);

        Boolean result3 = orderService.isPay(order);
        log.info("执行 isPay, 返回 [{}]", result3);

        String result4 = orderService.generateOrder(order);
        log.info("执行 generateOrder, 返回 [{}]", result4);

        String result5 = orderService.sayHello(order);
        log.info("执行 sayHello, 返回 [{}]", result5);

        log.info("[{}] 测试结束 [{}]", "///////////", "///////////");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignore) {
//        }
    }

    private Order getOrder(Integer orderId, Market market, Customer customer, Integer cost, Boolean isPay, Hello hello) {
        return Order.builder()
                .orderId(orderId).market(market).customer(customer)
                .cost(cost).isPay(isPay).hello(hello)
                .build();
    }

    private Market getMarket(String name, Integer version, String address) {
        return Market.builder()
                .name(name).version(version).address(address)
                .build();
    }

    private Customer getCustomer(String name, Integer id, Integer age) {
        return Customer.builder()
                .name(name)
                .id(id)
                .age(age)
                .build();
    }

    private Hello getHello(String message, String description) {
        return Hello.builder()
                .message(message)
                .description(description)
                .build();
    }


    public void orderControllerTest() {
        Order orderG1V1 = getOrder(
                1,
                getMarket("marketG1V1", 1, "Tokyo"),
                getCustomer("customerG1V1", 1, 18),
                100,
                false,
                getHello("老子就是不付钱", "CNM")
        );

        Order orderG1V2 = getOrder(
                2,
                getMarket("marketG1V2", 2, "NewYok"),
                getCustomer("customerG1V2", 2, 80),
                10086,
                true,
                getHello("吃饭就要付钱", "Yes!")
        );

        Order orderG1V3 = getOrder(
                3,
                getMarket("marketG1V3", 3, "China"),
                getCustomer("customerG1V3", 3, 25),
                11,
                true,
                getHello("我来自中国", "China No.1!!!")
        );

        Order orderG2V1 = getOrder(
                4,
                getMarket("marketG2V1", 4, "Tokyo"),
                getCustomer("customerG1V1", 4, 800),
                0,
                false,
                getHello("我活了800岁还要吃饭？", "???")
        );
        Order orderG2V2 = getOrder(
                5,
                getMarket("marketG2V2", 5, "Tokyo"),
                getCustomer("customerG2V2", 2, 1),
                792,
                true,
                getHello("我才刚出生，我家长给钱", "关我屁事")
        );

        orderServiceTest(orderServiceG1V1, orderG1V1);
        orderServiceTest(orderServiceG1V2, orderG1V2);
        orderServiceTest(orderServiceG1V2, orderG1V3);
        orderServiceTest(orderServiceG2V1, orderG2V1);
        orderServiceTest(orderServiceG2V2, orderG2V2);
    }

}
