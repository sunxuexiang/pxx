package com.wanmi.sbc.job;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoCountByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.job.service.CheckInventoryHandlerService;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangchen
 */
@JobHandler(value = "checkInventoryHandlerNew")
@Component
@Slf4j
public class CheckInventoryHandlerNew extends IJobHandler {

	@Autowired
	private WareHouseQueryProvider wareHouseQueryProvider;

	@Autowired
	private GoodsInfoQueryProvider goodsInfoQueryProvider;

	@Autowired
	private RequestWMSInventoryProvider requestWMSInventoryProvider;

	@Autowired
	private CheckInventoryHandlerService checkInventoryHandlerService;

	@Autowired
	private ThreadPoolTaskExecutor checkInventoryTaskExecutor;

	@Value("${wms.inventory.reqSleepMillis}")
	private Long sleepMillis;
	
	@Autowired
	private StoreQueryProvider storeQueryProvider;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		log.info("新wms库存校对定时器开始工作........:::: {}", LocalDateTime.now());

		// 现在没有多仓，写死仓库id为1
		final long wareId = 1;
		BaseResponse<WareHouseByIdResponse> getByIdResp = wareHouseQueryProvider
				.getByWareId(WareHouseByIdRequest.builder().wareId(wareId).build());
		WareHouseVO wareHouseVO = getByIdResp.getContext().getWareHouseVO();

		List<Integer> notEqCompanyTypes = Arrays.asList(CompanyType.SUPPLIER.toValue());
		// 需要同步商品的店铺id
		BaseResponse<List<StoreSimpleResponse>> listSimple = storeQueryProvider.listSimple(StoreQueryRequest.builder()
				.delFlag(DeleteFlag.NO).storeState(StoreState.OPENING).notEqCompanyTypes(notEqCompanyTypes).build());
		List<Long> storeList = listSimple.getContext().stream().map(StoreSimpleResponse::getStoreId).collect(Collectors.toList());
		
		GoodsInfoCountByConditionResponse response = goodsInfoQueryProvider.countByCondition(GoodsInfoCountByConditionRequest
				.builder().goodsInfoType(0).delFlag(0).addedFlag(1).notEqCompanyTypes(notEqCompanyTypes).wareId(wareId).storeIds(storeList).build())
				.getContext();
		Long total = response.getCount();
		log.info("新wms库存校对定时器需要同步的商品总数[{}]", total);
		if (total == null || total == 0) {
			log.info("无商品，新wms库存校对定时器结束");
			return SUCCESS;
		}

		int pageSize = 100;
		int totalNum = total.intValue() / pageSize + 1;
		if (total.intValue() % pageSize == 0) {
			totalNum = total.intValue() / pageSize;
		}
		for (int i = 0; i < totalNum; i++) {
			final int pageNo = i;
//			Runnable task = () -> {
			String format = MessageFormat.format("wareId[{0}]pageNo[{1}]", wareHouseVO.getWareId(), pageNo);
			StopWatch stopWatch = new StopWatch(format);
			stopWatch.start();

			GoodsInfoPageRequest request = GoodsInfoPageRequest.builder().goodsInfoType(0).delFlag(0).addedFlag(1)
					.notEqCompanyTypes(notEqCompanyTypes).wareId(wareId).storeIds(storeList).build();
			request.setPageSize(pageSize);
			request.setPageNum(pageNo);
			// 设置仓库
			GoodsInfoPageResponse infoPageResponse = goodsInfoQueryProvider.page(request).getContext();
			List<GoodsInfoVO> goodsInfoVOS = infoPageResponse.getGoodsInfoPage().getContent();
			String prefix = Constants.ERP_NO_PREFIX.get(wareHouseVO.getWareId());
			goodsInfoVOS.forEach(g -> {
				g.setErpGoodsInfoNo(g.getErpGoodsInfoNo().replace(prefix, ""));
			});

			List<String> erpSkus = goodsInfoVOS.stream().map(GoodsInfoVO::getErpGoodsInfoNo).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(erpSkus)) {

				// 查询
				InventoryQueryResponse inventoryQueryResponse = requestWMSInventoryProvider
						.batchQueryInventory(BatchInventoryQueryRequest.builder().WarehouseID(wareHouseVO.getWareCode())
								.CustomerID(AbstractXYYConstant.CUSTOMER_ID).skuIds(erpSkus).Lotatt04(wareHouseVO.getSelfErpId())
								.build())
						.getContext();
				// 处理一下库存(除以步长)
				if (CollectionUtils.isNotEmpty(inventoryQueryResponse.getInventoryQueryReturnVO())) {
					inventoryQueryResponse.getInventoryQueryReturnVO().parallelStream().forEach(inventoryQueryReturnVO -> {
						Optional<GoodsInfoVO> optionalGoodsInfoVO = goodsInfoVOS.stream()
								.filter(g -> g.getErpGoodsInfoNo().equals(inventoryQueryReturnVO.getSku())).findFirst();
						if (optionalGoodsInfoVO.isPresent()) {
							BigDecimal addStep = optionalGoodsInfoVO.get().getAddStep().setScale(2, BigDecimal.ROUND_HALF_UP);
							if (Objects.nonNull(addStep) && addStep.compareTo(BigDecimal.ZERO) == 1) {
								BigDecimal stock = inventoryQueryReturnVO.getStockNum().divide(addStep, 2, BigDecimal.ROUND_DOWN);
								inventoryQueryReturnVO.setStockNum(stock);
							}
						}
					});
				}
				checkInventoryHandlerService.updateStockByWMS(inventoryQueryResponse.getInventoryQueryReturnVO(),
						wareHouseVO.getWareId(), goodsInfoVOS);
			}

			stopWatch.stop();
			log.info(stopWatch.toString());
//			};
//			checkInventoryTaskExecutor.execute(task);

			// WMS性能原因，调一次睡眠一段时间，睡眠时间过长多线程将无意义
			log.info("新wms库存定时器睡眠[{}]毫秒，当前pageNo[{}]", sleepMillis, pageNo);
			if (sleepMillis != null) {
				Thread.sleep(sleepMillis);
			}
		}

		log.info("新wms库存校对定时器结束........:::: {}", LocalDateTime.now());

		return SUCCESS;
	}
}
