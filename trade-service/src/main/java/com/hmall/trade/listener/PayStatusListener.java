package com.hmall.trade.listener;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hmall.trade.service.IOrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayStatusListener {

    private final IOrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "trade.pay.success.que", durable = "true"),
        exchange = @Exchange(name = "pay.direct"),
        key = "pay.success"
    ))
    public void listenPaySuccess(Long orderId){

        orderService.markOrderPaySuccess(orderId);
    }
}
