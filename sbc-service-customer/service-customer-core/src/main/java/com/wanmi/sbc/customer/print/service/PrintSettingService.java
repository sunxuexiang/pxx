package com.wanmi.sbc.customer.print.service;


import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.enums.PrintDeliveryItem;
import com.wanmi.sbc.customer.bean.enums.PrintSize;
import com.wanmi.sbc.customer.bean.enums.PrintTradeItem;
import com.wanmi.sbc.customer.print.repository.PrintSettingRepository;
import com.wanmi.sbc.customer.print.model.root.PrintSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PrintSettingService {
    @Autowired
    private PrintSettingRepository printSettingRepository;

    /**
     * 按照店铺Id查询打印设置
     *
     * @param storeId
     * @return
     */
    public PrintSetting findByStoreId(Long storeId) {
        PrintSetting printSetting = printSettingRepository.queryByStoreId(storeId);
        printSetting = initSetting(printSetting);
        return printSetting;
    }

    /**
     * 更新打印设置
     *
     * @param printSetting
     */
    public void updateSettings(PrintSetting printSetting) {
        PrintSetting oldSettings = this.findByStoreId(printSetting.getStoreId());
        KsBeanUtil.copyProperties(printSetting, oldSettings);
        printSettingRepository.save(oldSettings);
    }


    /**
     * 初始化打印设置，打印尺寸为58mm，设置都是全选
     *
     * @param printSetting
     */
    private PrintSetting initSetting(PrintSetting printSetting) {
        if (printSetting == null) {
            printSetting = new PrintSetting();
        }
        if (printSetting.getPrintSize() == null) {
            printSetting.setPrintSize(PrintSize.FIFTY_EIGHT);
        }
        if (printSetting.getTradeSettings() == null) {
            printSetting.setTradeSettings(Arrays.asList(PrintTradeItem.values()));
        }
        if (printSetting.getDeliverySettings() == null) {
            printSetting.setDeliverySettings(Arrays.asList(PrintDeliveryItem.values()));
        }
        return printSetting;
    }


}
