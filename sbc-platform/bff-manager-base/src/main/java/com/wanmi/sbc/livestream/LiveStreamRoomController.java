package com.wanmi.sbc.livestream;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailModifyRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailListByConditionResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.live.api.request.room.liveRoomBaseRequest;
import com.wanmi.sbc.live.api.response.room.LiveAccountNumResponse;
import com.wanmi.sbc.live.api.response.room.LiveDetailExportVo;
import com.wanmi.sbc.live.bean.vo.LiveRoomVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.esotericsoftware.minlog.Log;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyQueryProvider;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyByIdRequest;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyByIdResponse;
import com.wanmi.sbc.live.api.provider.room.LiveStreamRoomProvider;
import com.wanmi.sbc.live.api.request.room.LiveRoomAddRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomModifyRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.response.room.LiveRoomInfoResponse;

import io.swagger.annotations.Api;

@Slf4j
@Api(description = "直播间管理", tags = "LiveStreamRoomController")
@RestController
@RequestMapping(value = "/liveStreamRoom")
public class LiveStreamRoomController {

    @Autowired
    LiveStreamRoomProvider liveRoomProvider;

    @Autowired
    GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    GoodsCompanyQueryProvider goodsCompanyQueryProvider;

    @Autowired
    CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerDetailProvider customerDetailProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    private AtomicInteger exportCount = new AtomicInteger(0);

    /**
     * 保存直播间
     * @param liveRoomAddRequest
     * @return
     */
    @PostMapping("/modify")
    public BaseResponse modify(@RequestBody @Valid LiveRoomModifyRequest liveRoomAddRequest) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播间管理", "保存直播间", "保存直播间:直播间名称" + (Objects.nonNull(liveRoomAddRequest) ? liveRoomAddRequest.getLiveRoomName() : ""));
        liveRoomAddRequest.setStoreId(commonUtil.getStoreId());
        List<CustomerDetailVO> newLiveCustomerIdList = new ArrayList<>();
        BaseResponse<Object> checkParamResponse = checkLiveRoomParam(liveRoomAddRequest, newLiveCustomerIdList, liveRoomAddRequest.getStoreId(), false);
        if (checkParamResponse != null) return checkParamResponse;

        BaseResponse<List<LiveAccountNumResponse>> baseResponse = liveRoomProvider.modify(liveRoomAddRequest);
        if (!ObjectUtils.isEmpty(baseResponse.getContext())) {
            /**
             * 收回账户开直播权限
             *  直播间绑定账户后，自动给账户开通直播权限（customer_detail.is_live = 1）
             *  当修改直播间，将账户解绑后，如果该账户没有绑定其他直播间，那该账户将不再有直播权限，应该收回
             *  zhouzhenguo 20230712
             */
            for (LiveAccountNumResponse account : baseResponse.getContext()) {
                if (StringUtils.isEmpty(account.getCustomerId())) {
                    continue;
                }
                // 如果该账户有超过1条绑定记录，则解绑该账户后，表面该账户还绑定有其他直播间，不能收回直播权限
                if (account.getQuantity() != null && account.getQuantity() > 1) {
                    continue;
                }
                // 是否回收账户直播权限标记， 初始标记需要回收账户直播权限，如果账户 customerId 还在该直播间新直播账户列表中，没有被解绑，有下面的逻辑判断回收标记为 false
                boolean isRecoveryLive = true;
                if (!ObjectUtils.isEmpty(liveRoomAddRequest.getAccountMap())) {
                    Iterator<String> iterator = liveRoomAddRequest.getAccountMap().keySet().iterator();
                    // 如果
                    while (iterator.hasNext()) {
                        String customerId = iterator.next();
                        if (account.getCustomerId().equals(customerId)) {
                            isRecoveryLive = false;
                            break;
                        }
                    }
                }
                // 回收账户直播权限
                if (isRecoveryLive) {
                    CustomerDetailModifyRequest customerDetailModifyRequest = new CustomerDetailModifyRequest();
                    customerDetailModifyRequest.setCustomerId(account.getCustomerId());
                    customerDetailModifyRequest.setIsLive(0);
                    customerDetailProvider.modifyCustomerLiveByCustomerId(customerDetailModifyRequest);
                }
            }
        }

        // 直播间绑定客户账号，设置直播标记未 customer_detail.is_live
        if (!ObjectUtils.isEmpty(newLiveCustomerIdList)) {
            for (CustomerDetailVO account : newLiveCustomerIdList) {
                CustomerDetailModifyRequest customerDetailModifyRequest = new CustomerDetailModifyRequest();
                customerDetailModifyRequest.setCustomerId(account.getCustomerId());
                customerDetailModifyRequest.setIsLive(1);
                customerDetailProvider.modifyCustomerLiveByCustomerId(customerDetailModifyRequest);
            }
        }

        return baseResponse;
    }


    private BaseResponse<Object> checkLiveRoomParam(liveRoomBaseRequest liveRequest, List<CustomerDetailVO> newLiveCustomerIdList, Long storeId, boolean isAddRoom) {
//        if (ObjectUtils.isEmpty(liveRequest.getAccountMap()) && ObjectUtils.isEmpty(liveRequest.getLivePhoneList())) {
//            return BaseResponse.error("直播账号不能为空");
//        }

        // 根据店铺类型，判断是自营直播间、还是非自营直播间
        StoreByIdRequest storeRequest = new StoreByIdRequest(storeId);
        BaseResponse<StoreByIdResponse> response = storeQueryProvider.getById(storeRequest);
        if (response == null || response.getContext() == null || response.getContext().getStoreVO() == null
                || response.getContext().getStoreVO().getCompanyType() == null) {
            // 如果没查询到，默认为非自营直播间
            return BaseResponse.error("查询店铺类型失败");
        }
        StoreVO storeVO = response.getContext().getStoreVO();
        if (CompanyType.PLATFORM.equals(storeVO.getCompanyType())) {
            liveRequest.setRoomType(1);
        }
        else {
            liveRequest.setRoomType(0);
            // 如果新增直播间，第三方商家只能创建一个直播间判断
            if (isAddRoom) {
                BaseResponse<List<LiveRoomVO>> liveResponse = liveRoomProvider.getLiveRoomByStore(storeId);
                if (!ObjectUtils.isEmpty(liveResponse.getContext()) && liveResponse.getContext().size() > 0) {
                    return BaseResponse.error("店铺只能创建一个直播间");
                }
            }
        }

        /**
         * 直播间绑定直播账号，前端输入手机号码，后端将手机号码绑定为 customer.customer_id
         */
        if (!ObjectUtils.isEmpty(liveRequest.getLivePhoneList())) {
            CustomerDetailListByConditionRequest customerRequest = new CustomerDetailListByConditionRequest();
            customerRequest.setCustomerAccountList(liveRequest.getLivePhoneList());
            // 根据手机号码查询 customer 信息
            BaseResponse<CustomerDetailListByConditionResponse> customerResponse = customerQueryProvider.listCustomerDetailByCondition(customerRequest);
            log.info("查询用户结果 {}", JSON.toJSONString(customerResponse));
            if (!ObjectUtils.isEmpty(customerResponse.getContext()) && !ObjectUtils.isEmpty(customerResponse.getContext().getDetailResponseList())) {
                List<CustomerDetailVO> detailResponseList = customerResponse.getContext().getDetailResponseList();
                if (liveRequest.getAccountMap() == null) {
                    liveRequest.setAccountMap(new HashMap<>());
                }
                for (String phone : liveRequest.getLivePhoneList()) {
                    CustomerDetailVO customerDetailVO = null;
                    for (CustomerDetailVO tempCustomer : detailResponseList) {
                        if (phone.equals(tempCustomer.getContactPhone()) || (tempCustomer.getCustomerVO() != null && phone.equals(tempCustomer.getCustomerVO().getCustomerAccount()))) {
                            customerDetailVO = tempCustomer;
                            break;
                        }
                    }
                    if (customerDetailVO == null) {
                        log.info("关联直播账号不存在 {}", phone);
                        return BaseResponse.error("直播账号不存在");
                    }
                    liveRequest.getAccountMap().put(customerDetailVO.getCustomerId(), customerDetailVO.getContactPhone());
                    if (ObjectUtils.isEmpty(customerDetailVO.getIsLive()) || ((Integer)0).equals(customerDetailVO.getIsLive())) {
                        newLiveCustomerIdList.add(customerDetailVO);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据ID查询直播间
     * @param liveRoomId
     * @return
     */
    @PostMapping("/getInfo")
    public BaseResponse getInfo(@RequestBody Long liveRoomId) {
        BaseResponse<LiveRoomInfoResponse> liveRoom = liveRoomProvider.getInfo(liveRoomId);
        LiveRoomInfoResponse liveRoomInfoResponse = liveRoom.getContext();
        if (null != liveRoomInfoResponse) {
            GoodsCompanyByIdRequest goodsCompanyByIdRequest = new GoodsCompanyByIdRequest();
            goodsCompanyByIdRequest.setCompanyId(liveRoomInfoResponse.getCompanyId());
            BaseResponse<GoodsCompanyByIdResponse> company = goodsCompanyQueryProvider.getById(goodsCompanyByIdRequest);
            if (null != company && null != company.getContext()) {
                liveRoomInfoResponse.setCompanyName(company.getContext().getCompanyName());
            }

        }
        return BaseResponse.success(liveRoomInfoResponse);
    }


    /**
     * 查询直播间分页
     * @param pageRequest
     * @return
     */
    @PostMapping("/getPage")
    public BaseResponse getPage(@RequestBody @Valid LiveRoomPageRequest pageRequest) {
        Operator operator = commonUtil.getOperator();
        /**
         * 20230712 zzg
         * 需求：
         *  1、运营后台可指定按 storeId 搜索，请求参数里会携带 storeId
         *  2、商家后台需要根据登录用户获取所属店铺，只能查找自己店铺的直播间列表
         * 逻辑：
         *  1、pageRequest.getStoreId() 不为空，为运营后台根据店铺ID查找直播间列表
         */
        if (ObjectUtils.isEmpty(pageRequest.getStoreId()) && !ObjectUtils.isEmpty(commonUtil.getStoreId())) {
            pageRequest.setStoreId(commonUtil.getStoreId());
        }
        BaseResponse baseResponse = liveRoomProvider.getPage(pageRequest);
        if (null != baseResponse && null != baseResponse.getContext()) {
        	Map mLiveRoomPageResponse = (Map) baseResponse.getContext();
        	List<Map> list = (List<Map>) mLiveRoomPageResponse.get("content");
        	if (CollectionUtils.isNotEmpty(list)) {
        		Map<Object, String> map = new HashMap<>();
	        	list.forEach(k -> {
	        		GoodsCompanyByIdRequest goodsCompanyByIdRequest = new GoodsCompanyByIdRequest();
	        		Object companyId = k.get("companyId");
	        		if (null == companyId) {
	        			return;
	        		}
	        		String companyName = map.get(companyId);
	        		if (null == companyName) {
	        			companyName = "";
		                goodsCompanyByIdRequest.setCompanyId(Long.valueOf(companyId.toString()));
		                try {
			                BaseResponse<GoodsCompanyByIdResponse> company = goodsCompanyQueryProvider.getById(goodsCompanyByIdRequest);
			                if (null != company && null != company.getContext()) {
			                	companyName = company.getContext().getCompanyName();
			                }
		                } catch (Exception e) {
		                	Log.error("queryCompanyNameErr: " + e.getMessage()  + ","+ companyId);
		                }
		                map.put(companyId, companyName);
	        		}
                    k.put("companyName", companyName);
	        	});
        	}
        }
        return baseResponse;
    }
    /**
     * 新增直播间
     * @param liveRoomAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse add(@RequestBody @Valid LiveRoomAddRequest liveRoomAddRequest) {
        liveRoomAddRequest.setStoreId(commonUtil.getStoreId());

        List<CustomerDetailVO> newLiveCustomerIdList = new ArrayList<>();
        BaseResponse<Object> checkParamResponse = checkLiveRoomParam(liveRoomAddRequest, newLiveCustomerIdList, liveRoomAddRequest.getStoreId(), true);
        if (checkParamResponse != null) return checkParamResponse;
        // 添加直播间，入库
        liveRoomProvider.add(liveRoomAddRequest);

        // 直播间绑定客户账号，设置直播标记未 customer_detail.is_live
        if (!ObjectUtils.isEmpty(newLiveCustomerIdList)) {
            for (CustomerDetailVO account : newLiveCustomerIdList) {
                CustomerDetailModifyRequest customerDetailModifyRequest = new CustomerDetailModifyRequest();
                customerDetailModifyRequest.setCustomerId(account.getCustomerId());
                customerDetailModifyRequest.setIsLive(1);
                customerDetailProvider.modifyCustomerLiveByCustomerId(customerDetailModifyRequest);
            }
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("直播间管理", "新增直播间", "新增直播间:直播间名称" + (Objects.nonNull(liveRoomAddRequest) ? liveRoomAddRequest.getLiveRoomName() : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "导出直播列表")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params/{encrypted}", method = RequestMethod.GET)
    public void export(@PathVariable String encrypted, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            LiveRoomPageRequest request = JSON.parseObject(decrypted, LiveRoomPageRequest.class);
            BaseResponse<List<LiveDetailExportVo>> listBaseResponse = liveRoomProvider.getExportData(request);
            List<LiveDetailExportVo> list = listBaseResponse.getContext();

            if (CollectionUtils.isNotEmpty(list)) {
                List<Long> storeIds = list.stream().map(LiveDetailExportVo::getStoreId).collect(Collectors.toList());
                storeIds = storeIds.stream().distinct().collect(Collectors.toList());
                storeIds.remove(null);
                ListStoreByIdsRequest idsRequest = ListStoreByIdsRequest.builder().storeIds(storeIds).build();
                BaseResponse<ListStoreByIdsResponse> storeResponse = storeQueryProvider.listByIds(idsRequest);
                List<StoreVO> storeVOList = storeResponse.getContext().getStoreVOList();
                Map<Object, String> map = new HashMap<>();
                list.remove(null);

                list.forEach(stream -> {
                    if (!ObjectUtils.isEmpty(storeVOList)) {
                        for (StoreVO storeVO : storeVOList) {
                            if (stream.getStoreId() != null && stream.getStoreId().equals(storeVO.getStoreId())) {
                                stream.setStoreName(storeVO.getStoreName());
                                break;
                            }
                        }
                    }

                    if (null == stream.getCompanyId()) {
                        return;
                    }
                    Long companyId = stream.getCompanyId();
                    GoodsCompanyByIdRequest goodsCompanyByIdRequest = new GoodsCompanyByIdRequest();
                    String companyName = map.get(companyId);
                    if (null == companyName) {
                        companyName = "";
                        goodsCompanyByIdRequest.setCompanyId(Long.valueOf(companyId.toString()));
                        try {
                            BaseResponse<GoodsCompanyByIdResponse> company = goodsCompanyQueryProvider.getById(goodsCompanyByIdRequest);
                            if (null != company && null != company.getContext()) {
                                companyName = company.getContext().getCompanyName();
                            }
                        } catch (Exception e) {
                        }
                        map.put(companyId, companyName);
                    }
                    stream.setCompanyName(companyName);
                });
            }

            String headerKey = "Content-Disposition";
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String fileName = String.format("导出直播记录_%s.xls", dateTime.format(formatter));
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("/return/export/params, fileName={}, error={}", fileName, e);
            }
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);

            ExcelHelper excelHelper = new ExcelHelper();
            Column[] columns = {
                    new Column("店铺", new SpelColumnRender<LiveDetailExportVo>("storeName")),
                    new Column("直播间名称", new SpelColumnRender<LiveDetailExportVo>("roomName")),
                    new Column("直播账号", new SpelColumnRender<LiveDetailExportVo>("customerAccount")),
                    new Column("开播日期（年月日）", new SpelColumnRender<LiveDetailExportVo>("liveDate")),
                    new Column("开播时段（时分秒）", new SpelColumnRender<LiveDetailExportVo>("liveHour")),
                    new Column("直播时长(分钟)", new SpelColumnRender<LiveDetailExportVo>("liveDuration")),
                    new Column("厂商名称", new SpelColumnRender<LiveDetailExportVo>("companyName")),
                    new Column("品牌名称", new SpelColumnRender<LiveDetailExportVo>("brandName")),
                    new Column("直播回放地址", new SpelColumnRender<LiveDetailExportVo>("mediaUrl"))
            };
            excelHelper.addSheet(
                    "直播详情导出",
                    columns,
                    list
            );
            excelHelper.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("/liveStreamRoom/export/params/params error: ", e);
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        } finally {
            exportCount.set(0);
        }
    }
}
