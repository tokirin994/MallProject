package com.hmall.api.client.fallback;

import java.util.Collection;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;

import com.hmall.api.client.ItemClient;
import com.hmall.api.domain.dto.ItemDTO;
import com.hmall.api.domain.dto.OrderDetailDTO;
import com.hmall.common.utils.CollUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemClientFallBackFactory implements FallbackFactory{
    @Override
    public Object create(Throwable cause) {
        return new ItemClient() {

            @Override
            public List<ItemDTO> queryItemsByIds(Collection<Long> ids) {
                log.error("查询商品失败！！", cause);
                return CollUtils.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("减少商品库存失败！！", cause);
                return;
            }
            
        };
    }

}
