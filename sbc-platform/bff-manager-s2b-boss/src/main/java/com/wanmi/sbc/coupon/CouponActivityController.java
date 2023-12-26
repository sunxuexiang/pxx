package com.wanmi.sbc.coupon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.ResultCode;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerListByConditionRequest;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.customer.NoDeleteCustomerGetByAccountResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityDisabledTimeRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityPageRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDisabledTimeResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.constant.CouponErrorCode;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityDisabledTimeVO;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import com.wanmi.sbc.quartz.service.TaskJobService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@RestController
@Api(tags = "CouponActivityController", description = "S2B 平台端-优惠券活动管理API")
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
    
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    


    /**
     * 新增活动
     *
     * @param couponActivityAddRequest
     * @return
     */
    @ApiOperation(value = "新增优惠券活动")
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
        couponActivityAddRequest.setWareId(-1l);
        couponActivityAddRequest.setPlatformFlag(DefaultFlag.YES);
        couponActivityAddRequest.setCreatePerson(commonUtil.getOperatorId());
        couponActivityAddRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
        //设置是否平台等级
        couponActivityAddRequest.setJoinLevelType(DefaultFlag.YES);
        BaseResponse<CouponActivityDetailResponse> response = couponActivityProvider.add(couponActivityAddRequest);
        //记录操作日志
        if (ResultCode.SUCCESSFUL.equals(response.getCode())) {
            operateLogMQUtil.convertAndSend("营销", "创建优惠券活动", "优惠券活动：" + couponActivityAddRequest.getActivityName());

            addOrModifyTaskJob(response.getContext().getCouponActivity(), null);
        }

        return response;
    }

    /**
     * 修改活动
     *
     * @param couponActivityModifyRequest
     * @return
     */
    @ApiOperation(value = "修改优惠券活动")
    @MultiSubmit
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody CouponActivityModifyRequest couponActivityModifyRequest) {
        couponActivityModifyRequest.setPlatformFlag(DefaultFlag.YES);
        couponActivityModifyRequest.setUpdatePerson(commonUtil.getOperatorId());
        couponActivityModifyRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
        couponActivityProvider.modify(couponActivityModifyRequest);

        //记录操作日志

        operateLogMQUtil.convertAndSend("营销", "编辑优惠券活动", "优惠券活动：" + couponActivityModifyRequest.getActivityName());
        addOrModifyTaskJob(null, couponActivityModifyRequest);
        return BaseResponse.SUCCESSFUL();
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
            couponActivityVO.setActivityId(couponActivityModifyRequest.getActivityId());
            couponActivityVO.setStartTime(couponActivityModifyRequest.getStartTime());
            couponActivityVO.setCouponActivityType(couponActivityModifyRequest.getCouponActivityType());

        }
        if (Objects.nonNull(couponActivityVO) && Objects.equals(CouponActivityType.SPECIFY_COUPON, couponActivityVO
                .getCouponActivityType())) {
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
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "couponActivityType", value =
                    "优惠券活动类型，0全场赠券，1指定赠券，2进店赠券，3注册赠券， 4权益赠券 9购买指定商品赠券 11订单满额赠券 12久未下单赠券",
                    required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String", name = "activityId", value = "优惠券活动Id",
                    required = true)
    })
    @RequestMapping(value = "/activity-disable-time/{couponActivityType}/{activityId}", method = RequestMethod.GET)
    public BaseResponse<List<CouponActivityDisabledTimeVO>> queryActivityEnableTime(@PathVariable @NotNull int
                                                                                                couponActivityType,
                                                                                    @PathVariable String activityId) {
        CouponActivityDisabledTimeRequest request = new CouponActivityDisabledTimeRequest();
        request.setCouponActivityType(CouponActivityType.fromValue(couponActivityType));
        if (!"-1".equals(activityId)) {
            request.setActivityId(activityId);
        }
        request.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
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
        //记录操作日志
        operateLogMQUtil.convertAndSend("营销", "客户账号导入模板下载", "下载成功");
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/excel/upload", method = RequestMethod.POST)
    public BaseResponse<List> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        //记录操作日志
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
    
    /**
     * 商家活动中心优惠券活动列表分页
     * @author zhangchen
     * @date 2023年6月29日 下午5:50:16
     * @param request
     * @return
     */
    @ApiOperation(value = "商家活动中心优惠券活动列表分页")
    @RequestMapping(value = "/supplier/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CouponActivityVO>> supplierPage(@RequestBody CouponActivityPageRequest request) {
		log.info("====进入/coupon-activity/supplier/page====");
		Assert.notNull(request.getStoreId(), "storeId不能为null");
		
        MicroServicePage<CouponActivityVO> response = couponActivityQueryProvider.page(request).getContext().getCouponActivityVOPage();
		// 商家ids
        Set<Long> storeIds = response.stream().map(CouponActivityVO::getStoreId).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(storeIds)) {
			// 一次性给商家名称赋值
			BaseResponse<ListStoreByIdsResponse> listByIds = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(storeIds)).build());
			response.forEach(m -> {
				Optional<StoreVO> findFirst = listByIds.getContext().getStoreVOList().stream().filter(s -> s.getStoreId().equals(m.getStoreId())).findFirst();
				findFirst.ifPresent(v -> {
					m.setSupplierName(v.getSupplierName());
					m.setStoreName(v.getStoreName());
				});
			});
			
		}
        return BaseResponse.success(response);
    }
}
