package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.response.PayWithDrawListResponse;
import com.wanmi.sbc.pay.api.response.PayWithDrawResponse;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawDTO;
import com.wanmi.sbc.pay.bean.vo.PayWithDrawVO;
import com.wanmi.sbc.pay.model.root.PayWithDraw;
import com.wanmi.sbc.pay.repository.PayWithDrawRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lm
 * @date 2022/10/21 10:19
 */
@Service
@Slf4j
public class PayWithDrawService {

    @Autowired
    private PayWithDrawRepository payWithDrawRepository;

    public PayWithDrawListResponse listWithDraw() {
        List<PayWithDraw> payWithDrawList = payWithDrawRepository.findAllWithDraw();
        List<PayWithDrawVO> convert = KsBeanUtil.convert(payWithDrawList, PayWithDrawVO.class);
        return PayWithDrawListResponse.builder().payWithDrawDTOList(convert).build();
    }

    /**
     * 添加鲸币提现收款账户
     * @param payWithDrawDTO
     * @return
     */
    public void addWithDraw(PayWithDrawDTO payWithDrawDTO) {
        PayWithDraw payWithDraw = KsBeanUtil.convert(payWithDrawDTO, PayWithDraw.class);
        // 设置默认值
        payWithDraw.setEnableStatus(EnableStatus.ENABLE);
        payWithDraw.setDelFlag(DeleteFlag.NO);
        LocalDateTime now = LocalDateTime.now();
        payWithDraw.setUpdateTime(now);
        payWithDraw.setCreateTime(now);
        payWithDrawRepository.save(payWithDraw);
    }

    /**
     * 修改鲸币提现收款账户
     * @param payWithDrawDTO
     * @return
     */
    public void updateWithDraw(PayWithDrawDTO payWithDrawDTO) {
        PayWithDrawVO oldPayWithDraw = this.findWithDrawById(payWithDrawDTO.getWithdrawId()).getPayWithDrawVO();
        oldPayWithDraw.setUpdateTime(LocalDateTime.now());
        oldPayWithDraw.setAccountName(payWithDrawDTO.getAccountName());
        oldPayWithDraw.setBankName(payWithDrawDTO.getBankName());
        oldPayWithDraw.setBankAccount(payWithDrawDTO.getBankAccount());
        oldPayWithDraw.setBankNo(payWithDrawDTO.getBankNo());
        oldPayWithDraw.setEnableStatus(payWithDrawDTO.getEnableStatus());
        PayWithDraw payWithDraw = KsBeanUtil.convert(oldPayWithDraw, PayWithDraw.class);
        payWithDraw.setDelFlag(DeleteFlag.NO);
        payWithDrawRepository.saveAndFlush(payWithDraw);
    }


    public PayWithDrawResponse findWithDrawById(Integer withdrawId) {
        PayWithDraw payWithDraw = payWithDrawRepository.findById(withdrawId).orElse(null);
        PayWithDrawVO convert = KsBeanUtil.convert(payWithDraw, PayWithDrawVO.class);
        return PayWithDrawResponse.builder().payWithDrawVO(convert).build();
    }

    public void deletePayWithDraw(Integer withdrawId) {
        PayWithDrawVO payWithDrawVO = this.findWithDrawById(withdrawId).getPayWithDrawVO();
        if(payWithDrawVO == null){
            return;
        }
        PayWithDraw convert = KsBeanUtil.convert(payWithDrawVO, PayWithDraw.class);
        convert.setDelFlag(DeleteFlag.YES);
        convert.setUpdateTime(LocalDateTime.now());
        payWithDrawRepository.saveAndFlush(convert);
    }
}
