package com.wanmi.sbc.claims;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.api.request.manualrefund.RefundForClaimsApplyPageRequest;
import com.wanmi.sbc.order.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.order.api.response.manualrefund.RefundForClaimsApplyPageResponse;
import com.wanmi.sbc.order.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.response.ClaimsApplyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chenchang
 * @since 2023/04/17 15:42
 */
@Api(tags = "ClaimsApplyController", description = "Api")
@RestController
@RequestMapping("/claimsApply")
@Slf4j
@Validated
public class ClaimsApplyController {

    @Autowired
    RefundOrderProvider refundOrderProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @PostMapping("/refundForClaims")
    public BaseResponse refundForClaims(@RequestBody @Valid RefundForClaimsRequest request) {
        request.setOperator(commonUtil.getOperator());
        operateLogMQUtil.convertAndSend("理赔", "理赔申请", "订单理赔退款");
        return refundOrderProvider.refundForClaims(request);
    }

    @PostMapping("/page")
    @ApiOperation(value = "理赔申请分页查询")
    @EmployeeCheck
    public BaseResponse<RefundForClaimsApplyPageResponse> getApplyPage(@RequestBody @Valid RefundForClaimsApplyPageRequest request) {
        return refundOrderProvider.getRefundForClaimsApplyPage(request);
    }

    @GetMapping("/getApplyDetail/{applyNo}")
    @ApiOperation(value = "理赔申请详情")
    @EmployeeCheck
    public BaseResponse<RefundForClaimsApplyVO> getApplyDetail(@PathVariable(value = "applyNo") String applyNo) {
        return refundOrderProvider.getApplyDetail(applyNo);
    }

    @ApiOperation(value = "工单列表导出")
    @RequestMapping(value = "/claimsFormAllListExport/{encrypted}", method = RequestMethod.GET)
    public void claimsFormAllListExport(@PathVariable String encrypted, HttpServletResponse response) throws IOException {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));

        log.info("工单列表导出接受方法claimsFormAllListExport,参数：{}"+ decrypted);
        RefundForClaimsApplyPageRequest request = JSON.parseObject(decrypted, RefundForClaimsApplyPageRequest.class);
        List<RefundForClaimsApplyVO> voList = refundOrderProvider.exportChaimsApply(request).getContext();

        List<ClaimsApplyExcel> exportList = new ArrayList<>();
        if(Objects.nonNull(voList) && CollectionUtils.isNotEmpty(voList)){
            //DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (RefundForClaimsApplyVO formQueryVO : voList) {
                ClaimsApplyExcel formExcel = new ClaimsApplyExcel();
                KsBeanUtil.copyPropertiesThird(formQueryVO,formExcel);
                exportList.add(formExcel);
            }
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("鲸币充值数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ClaimsApplyExcel.class).sheet("模板").doWrite(exportList);
        operateLogMQUtil.convertAndSend("鲸币充值数据", "工单列表导出", "工单列表导出");
    }
}
