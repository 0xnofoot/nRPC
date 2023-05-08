package xyz.nofoot.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import xyz.nofoot.annotation.RpcService;
import xyz.nofoot.dto.Customer;
import xyz.nofoot.dto.Hello;
import xyz.nofoot.dto.Market;
import xyz.nofoot.dto.Order;
import xyz.nofoot.service.OrderService;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.serviceImpl
 * @className: OrderServiceImplG1V1
 * @author: NoFoot
 * @date: 5/8/2023 1:25 PM
 * @description: TODO
 */
@RpcService(group = "g2", version = "v1")
@Slf4j
public class OrderServiceImplG2V1 implements OrderService {

    @Override
    public String getOrder(Order order) {
        log.info("OrderServiceG2V1-getOrder");
        Integer orderId = order.getOrderId();
        Market market = order.getMarket();
        Customer customer = order.getCustomer();
        String result = orderId.toString() + "-" + market.toString() + "-" + customer.toString();
        log.info("执行结果：[{}]", result);
        return result;
    }

    @Override
    public Integer toPay(Order order) {
        log.info("OrderServiceG2V1-toPay");
        Integer cost = order.getCost();
        Boolean isPay = order.getIsPay();
        if (isPay) {
            log.info("已经付过款");
        } else {
            log.info("还未付款，现在付款, 金额：[{}]", cost);
        }
        order.setIsPay(true);
        log.info("执行结果：[{}]", cost);
        return cost;
    }

    @Override
    public Boolean isPay(Order order) {
        log.info("OrderServiceG2V1-isPay");
        Boolean isPay = order.getIsPay();
        log.info("执行结果：[{}]", isPay);
        return isPay;
    }

    @Override
    public String generateOrder(Order order) {
        log.info("OrderServiceG2V1-generateOrder");
        String result = "order1-G2-V1";
        log.info("执行结果：[{}]", result);
        return result;
    }

    @Override
    public String sayHello(Order order) {
        log.info("OrderServiceG2V1-sayHello");
        Hello hello = order.getHello();
        String result = hello.getMessage() + "-" + hello.getDescription();
        log.info("执行结果：[{}]", result);
        return result;
    }

}
