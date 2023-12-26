package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.request.wallet.TicketsFormImgRequest;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormImgResponse;
import com.wanmi.sbc.account.bean.vo.TicketsFormImgVO;
import com.wanmi.sbc.account.wallet.model.root.TicketsFormImg;
import com.wanmi.sbc.account.wallet.repository.TicketsFormImgRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketsFormImgService {

    @Autowired
    private TicketsFormImgRepository ticketsFormImgRepository;


    /**
     * 保存(新增)鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    public BaseResponse addTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        List<TicketsFormImgVO> addTicketsFormImgVOList = ticketsFormImgRequest.getAddTicketsFormImgVOList();
        if (CollectionUtils.isEmpty(addTicketsFormImgVOList)) {
            return BaseResponse.SUCCESSFUL();
        }
        List<TicketsFormImg> addTicketsFormImgList = KsBeanUtil.convert(addTicketsFormImgVOList, TicketsFormImg.class);
        for (TicketsFormImg ticketsFormImg : addTicketsFormImgList) {
            ticketsFormImg.setDelFlag(0);
            ticketsFormImg.setCreateTime(LocalDateTime.now());
            ticketsFormImg.setUpdateTime(LocalDateTime.now());
        }
        ticketsFormImgRepository.saveAll(addTicketsFormImgList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    public BaseResponse deleteTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        List<Long> deleteTicketsFormImgVOList = ticketsFormImgRequest.getDeleteTicketsFormImgVOList();
        return deleteTicketsImgByIds(deleteTicketsFormImgVOList);
    }

    public BaseResponse deleteTicketsImgByIds(List<Long> deleteTicketsFormImgVOList) {
        List<TicketsFormImg> ticketsFormImgsByTicketsFormImgIdIn = ticketsFormImgRepository.findTicketsFormImgsByTicketsFormImgIdIn(deleteTicketsFormImgVOList);
        for (TicketsFormImg ticketsFormImg : ticketsFormImgsByTicketsFormImgIdIn) {
            ticketsFormImg.setDelFlag(1);
            ticketsFormImg.setUpdateTime(LocalDateTime.now());
        }
        ticketsFormImgRepository.saveAll(ticketsFormImgsByTicketsFormImgIdIn);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查看鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    public TicketsFormImgResponse listTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        if (ticketsFormImgRequest.getFormId() == null) {
            return new TicketsFormImgResponse();
        }
        List<TicketsFormImg> ticketsFormImgsByDelFlagAndFormId = ticketsFormImgRepository.findTicketsFormImgsByDelFlagAndFormId(0, ticketsFormImgRequest.getFormId());
        TicketsFormImgResponse build = TicketsFormImgResponse.builder().ticketsFormImgVOList(KsBeanUtil.convert(ticketsFormImgsByDelFlagAndFormId, TicketsFormImgVO.class)).build();
        return build;
    }

    /**
     * 查看鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    public TicketsFormImgResponse listTicketsFormImgsByFormIds(TicketsFormImgRequest ticketsFormImgRequest) {
        if (ticketsFormImgRequest.getFormIdList() == null) {
            return new TicketsFormImgResponse();
        }
        List<TicketsFormImg> ticketsFormImgsByDelFlagAndFormId = ticketsFormImgRepository.findTicketsFormImgsByDelFlagAndFormIdIn(0, ticketsFormImgRequest.getFormIdList());
        TicketsFormImgResponse build = TicketsFormImgResponse.builder().ticketsFormImgVOList(KsBeanUtil.convert(ticketsFormImgsByDelFlagAndFormId, TicketsFormImgVO.class)).build();
        return build;
    }
}
