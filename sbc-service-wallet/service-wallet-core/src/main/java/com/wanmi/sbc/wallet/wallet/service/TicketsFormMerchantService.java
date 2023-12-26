package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.wallet.model.root.TicketsForm;
import com.wanmi.sbc.wallet.wallet.repository.TicketsFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 工单逻辑处理
 */
@Service
@Slf4j
@Transactional
public class TicketsFormMerchantService {

    @Autowired
    private TicketsFormRepository ticketsFormRepository;

    /**
     * 商户充值生成工单
     * */
    public BaseResponse<TicketsForm> addTicKets (TicketsFormQueryVO ticketsFormQueryVO) {
        try {
            TicketsForm convert = KsBeanUtil.convert(ticketsFormQueryVO, TicketsForm.class);
            convert.setApplyTime(LocalDateTime.now());
            TicketsForm save = ticketsFormRepository.save(convert);
            if (save != null) {
                return BaseResponse.success(save);
            }
        }catch (Exception e) {
            throw new SbcRuntimeException("K-111116","订单生成失败。请联系客服人员处理");
        }
        return null;
    }

    /**
     *  查询工单状态
     * */
    public TicketsForm queryTicketByRecordNo(TicketsFormQueryVO ticketsFormQueryVO){
        return ticketsFormRepository.findTicketsFormByRecordNo(ticketsFormQueryVO.getRecordNo());
    }

    public void updateTicketsForm(String recordNo,Integer status){
        ticketsFormRepository.updateTicketsStatus(recordNo,status);
    }
}
