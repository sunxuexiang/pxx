package com.wanmi.sbc.logisticsapp.controller;

import com.wanmi.sbc.logisticscore.entity.WareHouse;
import com.wanmi.sbc.logisticscore.entity.WmsGoodsStock;
import com.wanmi.sbc.logisticscore.service.WareHouseService;
import com.wanmi.sbc.logisticscore.service.WmsGoodsStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author lm
 * @date 2022/11/03 9:30
 */
@RestController
@RequestMapping("/logistics")
@Slf4j
public class SyncWmsStockController {

    @Autowired
    private WmsGoodsStockService wmsGoodsStockService;

    @Autowired
    private WareHouseService wareHouseService;

    @GetMapping("/syncStock")
    public Boolean findAllWmsStock() throws InterruptedException {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.now();
        log.info("开始时间：{}", pattern.format(start));
        // 查询仓库
        List<WareHouse> allWareHouse = wareHouseService.findAllWareHouse(null);
        Set<String> warehouseCodes = allWareHouse.stream().map(WareHouse::getWareCode).collect(Collectors.toSet());
        CountDownLatch countDownLatch = new CountDownLatch(warehouseCodes.size());
        ExecutorService executorService = Executors.newFixedThreadPool(warehouseCodes.size());
        for (String warehouseCode : warehouseCodes) {
            executorService.submit(() -> {
                log.info("开始读WMS,仓库：{}，时间：{}",warehouseCode,pattern.format(start));
                List<WmsGoodsStock> allWmsStock = wmsGoodsStockService.findAllWmsStock(warehouseCode);
                log.info("结束读WMS时间,仓库：{}，时间：{}",warehouseCode,pattern.format(LocalDateTime.now()));
                wmsGoodsStockService.saveAllWmsStockToUs(allWmsStock);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        LocalDateTime endSync = LocalDateTime.now();
        log.info("结束时间："+pattern.format(endSync));
        log.info("总耗时：{}秒",Duration.between(start,endSync).getSeconds());
        return true;
    }

}
