package com.wanmi.sbc.coupon;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.ResultCode;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerListByConditionRequest;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.response.customer.NoDeleteCustomerGetByAccountResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.api.request.market.ExistsSkuByMarketingTypeRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingBaseRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDisabledTimeResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingListResponse;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.marketing.bean.dto.CouponActivitySkuExistsDTO;
import com.wanmi.sbc.marketing.bean.dto.SkuExistsDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityDisabledTimeVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.quartz.service.TaskJobService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "CouponActivityController", description = "优惠券活动 API")
@RestController
@RequestMapping("/coupon-activity")
@Validated
@Slf4j
public class CouponActivityController {

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private CouponActivityQueryProvider couponActivityQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private TaskJobService taskJobService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Value("classpath:customer_account_import_template.xls")
    private Resource templateFile;

    /**
     * 查询在相同类型的营销活动中，skuList是否存在重复
     * @param skuExistsDTO {@link SkuExistsDTO}
     * @return
     */
    @ApiOperation(value = "查询在相同类型的优惠券活动中，skuList是否存在重复")
    @RequestMapping(value = "/sku/exists", method = RequestMethod.POST)
    public BaseResponse<List<String>> ifSkuExists(@RequestBody @Valid CouponActivitySkuExistsDTO skuExistsDTO) {
        return BaseResponse.success(this.checkGoods(skuExistsDTO.getSkuIds(),
                CouponActivityType.BUY_ASSIGN_GOODS_COUPON, commonUtil.getStoreId(), skuExistsDTO.getActivityId()));
    }

    /**
     * 新增活动
     *
     * @param couponActivityAddRequest
     * @return
     */
    @ApiOperation(value = "新增活动")
    @MultiSubmit
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody CouponActivityAddRequest couponActivityAddRequest) {
        //如果是指定用户，验证指定用户中是否存在子账号
        if ("-2".equals(couponActivityAddRequest.getJoinLevel())
                &&CouponActivityType.SPECIFY_COUPON.equals(couponActivityAddRequest.getCouponActivityType())){
            CustomerListByConditionRequest customerRequest = new CustomerListByConditionRequest();
            customerRequest.setCustomerIds(couponActivityAddRequest.getCustomerScopeIds());
            List<CustomerVO> customerVOList = customerQueryProvider.listCustomerByCondition(customerRequest)
                    .getContext().getCustomerVOList();
            for (CustomerVO inner:customerVOList){
                if (StringUtils.isNotBlank(inner.getParentCustomerId())){
                    throw new SbcRuntimeException(CouponErrorCode.CHILD_CANT_GET,"优惠卷不可发给子账号");
                }
            }
        }
        couponActivityAddRequest.setPlatformFlag(DefaultFlag.NO);
        couponActivityAddRequest.setCreatePerson(commonUtil.getOperatorId());
        couponActivityAddRequest.setStoreId(commonUtil.getStoreId());
        if(Objects.isNull(couponActivityAddRequest.getWareId())){
            couponActivityAddRequest.setWareId(-1l);
        }
        //设置是否平台等级
        couponActivityAddRequest.setJoinLevelType(commonUtil.getCustomerLevelType());
        BaseResponse<CouponActivityDetailResponse> response =couponActivityProvider.add(couponActivityAddRequest);
        //记录操作日志
        if (ResultCode.SUCCESSFUL.equals(response.getCode())) {
            operateLogMQUtil.convertAndSend("营销", "创建优惠券活动", "优惠券活动：" + couponActivityAddRequest.getActivityName());
            if(couponActivityAddRequest.getSendNow() != 1){
                log.info("插入定时任务supplier"+response.getContext().getCouponActivity());
                addOrModifyTaskJob(response.getContext().getCouponActivity(), null);
            }else {
                //当sendNow == 1 时需要立即发券
                if(Objects.isNull(response.getContext()) ||
                        Objects.isNull(response.getContext().getCouponActivity()) ||
                        Objects.isNull(response.getContext().getCouponActivity().getActivityId())){
                    throw new RuntimeException("缺少活动id，无法立即发券！");
                }
                couponActivityAddRequest.setActivityId(response.getContext().getCouponActivity().getActivityId());
                couponActivityProvider.createActivitySendNow(couponActivityAddRequest);
            }
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改活动
     *
     * @param couponActivityModifyRequest
     * @return
     */
    @ApiOperation(value = "修改活动")
    @MultiSubmit
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody CouponActivityModifyRequest couponActivityModifyRequest) {
        if(couponActivityModifyRequest.getCouponActivityType().toValue() == CouponActivityType.SPECIFY_COUPON.toValue()
                && couponActivityModifyRequest.getSendNow() == 1){
            return BaseResponse.success("立即发券不可修改！");
        }
        couponActivityModifyRequest.setPlatformFlag(DefaultFlag.NO);
        couponActivityModifyRequest.setUpdatePerson(commonUtil.getOperatorId());
        couponActivityModifyRequest.setStoreId(commonUtil.getStoreId());
        BaseResponse response  =couponActivityProvider.modify
                (couponActivityModifyRequest);

        operateLogMQUtil.convertAndSend("营销", "编辑优惠券活动", "优惠券活动：" + couponActivityModifyRequest.getActivityName());
        addOrModifyTaskJob(null, couponActivityModifyRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 校验商品是否重复设置
     * @param skuIds
     * @param type
     * @param storeId
     * @param activityId
     * @return
     */
    private List<String> checkGoods(List<String> skuIds,CouponActivityType type,Long storeId,String activityId) {
        List<String> errorSkuIds = new ArrayList<>();
        //获取正在进行中、未开始的优惠券活动
        CouponActivityPageRequest couponActivityPageRequest = new CouponActivityPageRequest();
        couponActivityPageRequest.setCouponActivityType(type);
        couponActivityPageRequest.setStoreId(storeId);
        List<CouponActivityVO> couponActivities = KsBeanUtil.convert(couponActivityQueryProvider.listByActivityType(couponActivityPageRequest).getContext(),CouponActivityVO.class);
        if (CollectionUtils.isNotEmpty(couponActivities)) {
            //优惠券活动关联商品
            List<CouponActivityGoodsVO> couponActivityGoods = couponActivityQueryProvider.listCouponActivityGoods(CouponActivityGoodsRequest
                    .builder()
                    .activityIds(couponActivities.stream().map(CouponActivityVO::getActivityId).collect(Collectors.toList()))
                    .build()).getContext().getCouponActivityGoodsVOS();
            if (CollectionUtils.isNotEmpty(couponActivityGoods)) {
                //过滤出不是本身活动的关联商品信息
                List<CouponActivityGoodsVO> filterOwns = couponActivityGoods.stream()
                        .filter(item -> !item.getActivityId().equals(activityId)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(filterOwns)) {
                    List<String> filterSkuIds = filterOwns.stream().map(CouponActivityGoodsVO::getGoodsInfoId).collect(Collectors.toList());
                    errorSkuIds = skuIds.stream()
                            .filter(skuId -> filterSkuIds.stream().anyMatch(i -> i.equals(skuId)))
                            .collect(Collectors.toList());
                }
            }
        }
        return errorSkuIds;
    }

    /**
     * 指定赠券定时任务
     *
     * @param couponActivityVO
     */
    private void addOrModifyTaskJob(CouponActivityVO couponActivityVO, CouponActivityModifyRequest
            couponActivityModifyRequest) {

        //修改优惠券活动
        if (Objects.nonNull(couponActivityModifyRequest)) {
            couponActivityVO = new CouponActivityVO();
            couponActivityVO.setStartTime(couponActivityModifyRequest.getStartTime());
            couponActivityVO.setActivityId(couponActivityModifyRequest.getActivityId());
            couponActivityVO.setCouponActivityType(couponActivityModifyRequest.getCouponActivityType());

        }
        if (Objects.nonNull(couponActivityVO) && Objects.equals(CouponActivityType.SPECIFY_COUPON, couponActivityVO
                .getCouponActivityType())) {
            log.info("指定赠券定时任务"+couponActivityVO);
            //指定赠券定时任务
            taskJobService.addOrModifyTaskJob(couponActivityVO.getActivityId(), couponActivityVO.getStartTime());
        }

    }


    /**
     * 查询活动（注册赠券活动、进店赠券活动）不可用的时间范围
     *
     * @return
     */
    @ApiOperation(value = "查询活动（注册赠券活动、进店赠券活动）不可用的时间范围")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer",
                    name = "couponActivityType", value = "活动类型", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "activityId", value = "优惠券活动Id", required = true)
    })
    @RequestMapping(value = "/activity-disable-time/{couponActivityType}/{activityId}", method = RequestMethod.GET)
    public BaseResponse<List<CouponActivityDisabledTimeVO>> queryActivityEnableTime(@PathVariable @NotNull int
                                                                                                couponActivityType,
                                                                                    @PathVariable String activityId) {
        CouponActivityDisabledTimeRequest request = new CouponActivityDisabledTimeRequest();
        request.setStoreId(commonUtil.getStoreId());
        request.setCouponActivityType(CouponActivityType.fromValue(couponActivityType));
        if (!"-1".equals(activityId)) {
            request.setActivityId(activityId);
        }
        CouponActivityDisabledTimeResponse response = couponActivityQueryProvider.queryActivityEnableTime(request)
                .getContext();
        return BaseResponse.success(response.getCouponActivityDisabledTimeVOList());
    }

    /**
     * 客户账号导入模板下载
     */
    @ApiOperation(value = "客户账号导入模板下载")
    @RequestMapping(value = "/customer/downloadTemplate", method = RequestMethod.GET)
    public void export() {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            is = templateFile.getInputStream();
            Workbook wk = WorkbookFactory.create(is);
            wk.write(byteArrayOutputStream);
            String file = new BASE64Encoder().encode(byteArrayOutputStream.toByteArray());
            if (org.apache.commons.lang.StringUtils.isNotBlank(file)) {
                String fileName = URLEncoder.encode("客户账号导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            }
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                log.error("客户账号导入模板转Base64位异常", e);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("读取客户账号导入模板异常", e);
                }
            }
        }
        operateLogMQUtil.convertAndSend("营销", "客户账号导入模板下载", "操作成功");
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/excel/upload", method = RequestMethod.POST)
    public BaseResponse<List> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("营销", "上传文件", "上传文件");
        return BaseResponse.success(this.responseUpload(uploadFile));
    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @return 文件格式
     */
    private List<String> responseUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }

        String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if (!(fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx"))) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_EXT_ERROR);
        }

        if (file.getSize() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        ArrayList<String> customerList = new ArrayList<String>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            //检测文档正确性
            this.checkExcel(workbook);

            //获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }
            int maxCell = 2;
            boolean isError = false;
            String errorMessage = null;

            //循环除了第一行的所有行
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                //获得当前行
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                Cell[] cells = new Cell[maxCell];
                boolean isNotEmpty = false;
                for (int i = 0; i < maxCell; i++) {
                    Cell cell = row.getCell(i);
                    if (cell == null) {
                        cell = row.createCell(i);
                    }
                    cells[i] = cell;
                    if (StringUtils.isNotBlank(ExcelHelper.getValue(cell))) {
                        isNotEmpty = true;
                    }
                }
                //数据都为空，则跳过去
                if (!isNotEmpty) {
                    continue;
                }

                // 客户账号
                String customerAccount = ExcelHelper.getValue(cells[0]);
                //校验客户是否存在
                NoDeleteCustomerGetByAccountResponse customer = customerQueryProvider.getNoDeleteCustomerByAccount(new NoDeleteCustomerGetByAccountRequest(customerAccount)).getContext();
                if (null == customer) {
                    errorMessage = "客户号："+customerAccount+",不存在或已删除";
                    isError = true;
                } else {
                    customerList.add(customerAccount);
                }

            }

            if (isError) {
                throw new SbcRuntimeException("K-030404", new Object[]{errorMessage});
            }

        }catch (SbcRuntimeException e) {
            log.error("客户账号上传异常", e);
            throw e;
        } catch (Exception e) {
            log.error("客户账号上传异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }

        return customerList;
    }

    /**
     * 验证EXCEL
     *
     * @param workbook
     */
    private void checkExcel(Workbook workbook) {
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row row = sheet1.getRow(0);
            if (!row.getCell(0).getStringCellValue().contains("客户账号")) {
                throw new SbcRuntimeException("K-030406");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("K-030406");
        }
    }
}
