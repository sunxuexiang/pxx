package com.wanmi.sbc.goods;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 零售商品服务
 * @author: XinJiang
 * @time: 2022/3/8 15:42
 */
@Api(tags = "StoreGoodsInfoController", description = "商品服务 API")
@RestController
@RequestMapping(value = "/supplier/retail")
@Slf4j
public class StoreRetailGoodsInfoController {
}
