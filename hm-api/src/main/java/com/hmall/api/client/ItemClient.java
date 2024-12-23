package com.hmall.api.client;

import java.util.Collection;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hmall.api.client.fallback.ItemClientFallBackFactory;
import com.hmall.api.dto.ItemDTO;

@FeignClient(value = "item-service", fallbackFactory = ItemClientFallBackFactory.class)
public interface ItemClient {
    
    @GetMapping("/items")
    List<ItemDTO> queryItemsByIds(@RequestParam("ids") Collection<Long> ids);
}
