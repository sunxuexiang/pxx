package com.wanmi.sbc.returnorder.manualrefund.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundImgRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundImgResponse;
import com.wanmi.sbc.returnorder.bean.vo.ManualRefundImgVO;
import com.wanmi.sbc.returnorder.constant.AbstractOrderConstant;
import com.wanmi.sbc.returnorder.enums.RefundBillTypeEnum;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefund;
import com.wanmi.sbc.returnorder.manualrefund.model.root.ManualRefundImg;
import com.wanmi.sbc.returnorder.manualrefund.repository.ManualRefundImgRepository;
import com.wanmi.sbc.returnorder.manualrefund.repository.ManualRefundRepository;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManualRefundImgService {

    @Autowired
    private ManualRefundImgRepository manualRefundImgRepository;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ManualRefundRepository manualRefundRepository;

    @Autowired
    private TradeService tradeService;

    /**
     * 保存(新增)退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    public ManualRefundImgResponse addManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        List<ManualRefundImgVO> addManualRefundImgVOList = manualRefundImgRequest.getAddManualRefundImgVOList();
        if(Objects.isNull(manualRefundImgRequest.getRefundBillType())){
            manualRefundImgRequest.setRefundBillType(RefundBillTypeEnum.ORDER.toStatus());
        }
        //先删除以前的图片 前端改了控件，邓开元说要覆盖上传
        List<ManualRefundImg> deleteingManualRefundImgList = manualRefundImgRepository.findManualRefundImgsByRefundId(0, manualRefundImgRequest.getRefundId(), manualRefundImgRequest.getRefundBillType());
        for (ManualRefundImg ManualRefundImg : deleteingManualRefundImgList) {
            ManualRefundImg.setDelFlag(1);
            ManualRefundImg.setUpdateTime(LocalDateTime.now());
        }
        manualRefundImgRepository.saveAll(deleteingManualRefundImgList);
        //如果数组没有图片地址，说明没有图片
        if (CollectionUtils.isEmpty(addManualRefundImgVOList)) {
            extracted(manualRefundImgRequest,AbstractOrderConstant.NO_INT);
            return ManualRefundImgResponse.builder().build();
        }
        List<ManualRefundImg> addManualRefundImgList = KsBeanUtil.convert(addManualRefundImgVOList, ManualRefundImg.class);
        for (ManualRefundImg manualRefundImg : addManualRefundImgList) {
            manualRefundImg.setRefundBillType(manualRefundImgRequest.getRefundBillType());
            manualRefundImg.setRefundId(manualRefundImgRequest.getRefundId());
            manualRefundImg.setDelFlag(0);
            manualRefundImg.setCreateTime(LocalDateTime.now());
            manualRefundImg.setUpdateTime(LocalDateTime.now());
        }
        manualRefundImgRepository.saveAll(addManualRefundImgList);
        extracted(manualRefundImgRequest,AbstractOrderConstant.YES_INT);
        return ManualRefundImgResponse.builder().manualRefundImgVOList(KsBeanUtil.convert(addManualRefundImgList, ManualRefundImgVO.class)).build();
    }

    private void extracted(ManualRefundImgRequest manualRefundImgRequest,String flag) {
        if(RefundBillTypeEnum.RETURN.toStatus()== manualRefundImgRequest.getRefundBillType()){
            returnOrderService.updateVoucherImagesFlagById(manualRefundImgRequest.getRefundBelongBillId(), flag);
        }
        if(RefundBillTypeEnum.ORDER.toStatus()== manualRefundImgRequest.getRefundBillType()){
            tradeService.updateVoucherImagesFlagById(manualRefundImgRequest.getRefundBelongBillId(), flag);
        }
    }

    private void deleteImgByFundIdAndBillType(ManualRefundImgRequest manualRefundImgRequest) {
        ManualRefundImgResponse refundImgResponse = listManualRefundImgs(manualRefundImgRequest);
        List<ManualRefundImgVO> manualRefundImgVOList = refundImgResponse.getManualRefundImgVOList();
        if (CollectionUtils.isEmpty(manualRefundImgVOList)) {
            return;
        }
        List<Long> imgIdList = manualRefundImgVOList.stream().map(ManualRefundImgVO::getManualRefundImgId).collect(Collectors.toList());
        ManualRefundImgRequest deleteRequest = new ManualRefundImgRequest();
        deleteRequest.setDeleteManualRefundImgVOList(imgIdList);
        deleteRequest.setRefundBillType(manualRefundImgRequest.getRefundBillType());
        deleteRequest.setRefundBelongBillId(manualRefundImgRequest.getRefundBelongBillId());
        deleteManualRefundImgs(deleteRequest);
    }

    /**
     * 删除退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    public BaseResponse deleteManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        List<Long> deleteManualRefundImgVOList = manualRefundImgRequest.getDeleteManualRefundImgVOList();
        List<ManualRefundImg> ManualRefundImgsByManualRefundImgIdIn = manualRefundImgRepository.findManualRefundImgsByManualRefundImgIdIn(deleteManualRefundImgVOList);
        for (ManualRefundImg ManualRefundImg : ManualRefundImgsByManualRefundImgIdIn) {
            ManualRefundImg.setDelFlag(1);
            ManualRefundImg.setUpdateTime(LocalDateTime.now());
        }
        manualRefundImgRepository.saveAll(ManualRefundImgsByManualRefundImgIdIn);
        if(Objects.isNull(manualRefundImgRequest.getRefundBillType())){
            manualRefundImgRequest.setRefundBillType(RefundBillTypeEnum.ORDER.toStatus());
        }
        List<ManualRefundImgVO> tmpList = listManualRefundImgs(manualRefundImgRequest).getManualRefundImgVOList();
        int imagesCount = CollectionUtils.isEmpty(tmpList)?0: tmpList.size();
        if(imagesCount==0){
            if(RefundBillTypeEnum.RETURN.toStatus()==manualRefundImgRequest.getRefundBillType()){
                returnOrderService.updateVoucherImagesFlagById(manualRefundImgRequest.getRefundBelongBillId(), AbstractOrderConstant.NO_INT);
            }
            if(RefundBillTypeEnum.ORDER.toStatus()==manualRefundImgRequest.getRefundBillType()){
                tradeService.updateVoucherImagesFlagById(manualRefundImgRequest.getRefundBelongBillId(), AbstractOrderConstant.NO_INT);
            }
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查看退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    public ManualRefundImgResponse listManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        if (manualRefundImgRequest.getRefundId() == null) {
            return new ManualRefundImgResponse();
        }
        if(Objects.isNull(manualRefundImgRequest.getRefundBillType())){
            manualRefundImgRequest.setRefundBillType(RefundBillTypeEnum.ORDER.toStatus());
        }
        ManualRefundImgResponse build = getManualRefundImgResponse(manualRefundImgRequest.getRefundId(),manualRefundImgRequest.getRefundBillType());
        return build;
    }

    public ManualRefundImgResponse getManualRefundImgResponse(String refundId, Integer refundBillType) {
        List<ManualRefundImg> manualRefundImgsByDelFlagAndRefundId = manualRefundImgRepository.findManualRefundImgsByRefundId(0, refundId, refundBillType);
        ManualRefundImgResponse build = ManualRefundImgResponse.builder().manualRefundImgVOList(KsBeanUtil.convert(manualRefundImgsByDelFlagAndRefundId, ManualRefundImgVO.class)).build();
        return build;
    }


    /**
     * 查看退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    public ManualRefundImgResponse listManualRefundImgsByrefundIds(ManualRefundImgRequest manualRefundImgRequest) {
        if (manualRefundImgRequest.getRefundIdList() == null) {
            return new ManualRefundImgResponse();
        }
        List<ManualRefundImg> manualRefundImgsByDelFlagAndRefundId = manualRefundImgRepository.findManualRefundImgsByDelFlagAndRefundIdIn(0, manualRefundImgRequest.getRefundIdList());
        ManualRefundImgResponse build = ManualRefundImgResponse.builder().manualRefundImgVOList(KsBeanUtil.convert(manualRefundImgsByDelFlagAndRefundId, ManualRefundImgVO.class)).build();
        return build;
    }

    public BaseResponse initOrderRefundImgsFlag() {
        List<ManualRefundImg> unDelFlagImageList = manualRefundImgRepository.findManualRefundImgsUnDelFlag();
        if (CollectionUtils.isEmpty(unDelFlagImageList)) {
            return null;
        }
        List<String> fundIdList = unDelFlagImageList.stream().map(ManualRefundImg::getRefundId).distinct().collect(Collectors.toList());
        List<ManualRefund> manualRefundList = manualRefundRepository.findListByRefundId(fundIdList);
        if (CollectionUtils.isEmpty(manualRefundList)) {
            return null;
        }
        List<String> orderIdList = manualRefundList.stream().map(ManualRefund::getOrderCode).distinct().collect(Collectors.toList());
        tradeService.updateVoucherImagesFlagByIdList(orderIdList);
        return BaseResponse.SUCCESSFUL();
    }
}
