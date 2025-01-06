package com.hmall.api.client;

import java.util.Collection;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;


@FeignClient(value = "item-service", fallbackFactory = ItemClientFallbackFactory.class) // fallback可能会导致XA失效，原因是不产生异常，二阶段提交
public interface ItemClient {
    
    @GetMapping("/items")
    List<ItemDTO> queryItemsByIds(@RequestParam("ids") Collection<Long> ids);

    // 批量扣减库存
    @PutMapping("/items/stock/deduct")
    public void deductStock(@RequestBody List<OrderDetailDTO> items);
}
