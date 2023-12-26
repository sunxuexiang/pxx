package com.wanmi.sbc.setting.print;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.PrintConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by feitingting on 2019/11/6.
 */

@Service
/**
 * 系统配置类
 */
public class PrintConfigService {

    @Autowired
    private PrintConfigRepository printConfigRepository;

    /**
     * 查询
     * @return
     */
    public PrintConfigVO findFirst(){
        List<PrintConfig> printConfigList = printConfigRepository.findAll();
        PrintConfig printConfig = printConfigList.get(0);
        return KsBeanUtil.convert(printConfig, PrintConfigVO.class);

    }

    /**
     * 更新
     * @param printConfigVO
     */
    @Transactional(rollbackFor = Exception.class)
    public PrintConfigVO modify(PrintConfigVO printConfigVO){
        PrintConfig printConfig = KsBeanUtil.convert(printConfigVO,PrintConfig.class);
        return KsBeanUtil.convert(printConfigRepository.saveAndFlush(printConfig),PrintConfigVO.class);
    }

}
