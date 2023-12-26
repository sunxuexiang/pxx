package com.wanmi.sbc.coupon;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoGetRecordRequest;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoGetRecordVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @Description: 优惠券领取记录
 * @author: jiangxin
 * @create: 2021-09-07 19:10
 */
@RestController
@Api(description = "S2B 平台端-优惠券领取记录api")
@RequestMapping("/coupon-code")
@Validated
@Slf4j
public class CouponCodeBossController {

    @Autowired
    private CouponCodeProvider couponCodeProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "优惠券领取记录")
    @RequestMapping(value = "/getRecord",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CouponInfoGetRecordVO>> getRecord(@Valid @RequestBody CouponInfoGetRecordRequest request){
        return BaseResponse.success(couponCodeProvider.getRecord(request).getContext());
    }

    @ApiOperation(value = "导出-优惠券领取记录")
    @RequestMapping(value = "/exportRecord/{encrypted}",method = RequestMethod.GET)
    public void exportRecord(@PathVariable String encrypted, HttpServletResponse response){
        try{
            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            CouponInfoGetRecordRequest request = JSON.parseObject(decrypted, CouponInfoGetRecordRequest.class);
            MicroServicePage<CouponInfoGetRecordVO> result = couponCodeProvider.getRecord(request).getContext();
            List<CouponInfoGetRecordVO> voList = result.getContent();
            List<String> headColumnNames = Arrays.asList("用户账号","优惠券ID","优惠券名称","优惠券面值 (元)","有效期","领取数量","优惠券状态");
            List<String> headColumnExp = Arrays.asList("customerAccount","couponId","couponName","denomination","validityStr","receiveCount","couponStatusStr");
            ExcelHelper.export("优惠券领取记录",headColumnNames,headColumnExp,voList,response);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        operateLogMQUtil.convertAndSend("优惠券领取记录", "导出-优惠券领取记录", "操作成功");
    }

}
