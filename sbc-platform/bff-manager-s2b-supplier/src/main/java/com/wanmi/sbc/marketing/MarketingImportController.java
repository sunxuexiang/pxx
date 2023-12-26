package com.wanmi.sbc.marketing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import com.wanmi.sbc.common.enums.BoolFlag;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNosRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByNoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.market.AddActivitGoodsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingPageRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingPageResponse;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.marketing.bean.dto.MarketingPageDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.request.MarketingImportVO;
import com.wanmi.sbc.marketing.request.MarketingPageListRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Api(tags = "MarketingController", description = "营销服务API")
@RestController
@RequestMapping("/marketing")
@Slf4j
public class MarketingImportController {

	@Autowired
	private CommonUtil commonUtil;
	@Autowired
	private GoodsInfoQueryProvider goodsInfoQueryProvider;
	@Autowired
	private OperateLogMQUtil operateLogMQUtil;
	@Autowired
	private MarketingQueryProvider marketingQueryProvider;
	@Autowired
	private WareHouseQueryProvider wareHouseQueryProvider;

	@Value("classpath:marketing_import_template.xlsx")
	private Resource templateFile;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value = "促销活动导入商品")
	@PostMapping(value = "/importGoods/{wareId}")
	public BaseResponse importGoodsInfos(@RequestParam(value = "file", required = true) MultipartFile file,
			@PathVariable("wareId") Long wareId, HttpServletResponse response) {
		// 校验文件格式
		String originalFilename = file.getOriginalFilename();
		assert originalFilename != null;
		String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));

		if (!("xls".equalsIgnoreCase(fileSuffix) || !"xlsx".equalsIgnoreCase(fileSuffix))) {
			throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
		}
//		String classPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/")
//				.concat(commonUtil.getOperatorId()).concat(".").concat(fileSuffix);
		File files = transferToFile(file);
		try (Workbook workbook = WorkbookFactory.create(files)) {
			// 创建Workbook工作薄对象，表示整个excel
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
			}
			// 获得当前sheet的开始行
			int firstRowNum = sheet.getFirstRowNum();
			// 获得当前sheet的结束行
			int lastRowNum = sheet.getLastRowNum();
			if (lastRowNum < 1) {
				throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
			}
			int maxCell = 4;
			boolean isError = false;
			HashMap<String, Integer> map_exist = new HashMap<>();
			List<Map> reasonList = new ArrayList<>();
			List<String> goodsInfoIdsAll = new ArrayList<>();// 需要提取的商品id
			List<GoodsInfoVO> contentAll = new ArrayList<>();
			// 循环除了第一行的所有行
			for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
				// 获得当前行
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				Cell[] cells = new Cell[maxCell];
				boolean isNotEmpty = false;
				for (int i = 0; i < 4; i++) {
					Cell cell = row.getCell(i);
					if (cell == null) {
						cell = row.createCell(i);
					}
					cells[i] = cell;
					if (StringUtils.isNotBlank(ExcelHelper.getValue(cell))) {
						isNotEmpty = true;
					}
				}
				// 数据都为空，则跳过去
				if (!isNotEmpty) {
					continue;
				}

				String erpName = ExcelHelper.getValue(cells[0]);// ERP编码

				String goodsName = ExcelHelper.getValue(cells[1]);// 商品名称
				String purchaseNum = null;
				if (Objects.nonNull(cells[2])) {
					String str = ExcelHelper.getValue(cells[2]);
					if (StringUtils.isNotEmpty(str)) {
						purchaseNum = "" + (long) Double.parseDouble(str);// 总限购 临时这样处理下
					}
				}
				String perUserPurchaseNum = null;
				if (Objects.nonNull(cells[3])) {
					String str = ExcelHelper.getValue(cells[3]);
					if (StringUtils.isNotEmpty(str)) {
						perUserPurchaseNum = "" + (long) Double.parseDouble(str);// 单用户限购 临时这样处理下
					}
				}

				if (StringUtils.isEmpty(erpName)) {
					Map map = new HashMap();
					map.put("erpName", erpName);
					map.put("goodsName", goodsName);
					map.put("reason", "ERP编码为空");
					reasonList.add(map);
					contentAll.clear();
					break;
				}
				if (map_exist.containsKey(erpName)) {
					Map map = new HashMap();
					map.put("erpName", erpName);
					map.put("goodsName", goodsName);
					map.put("reason", "商品在表格中存在重复项");
					reasonList.add(map);
					contentAll.clear();
					break;
				}
				map_exist.put(erpName, 1);

				if (Objects.nonNull(purchaseNum) && Objects.nonNull(perUserPurchaseNum)) {
					if (Long.parseLong(perUserPurchaseNum) > Long.parseLong(purchaseNum)) {
						// 导入数据单用户限购大于总限购提示
						Map map = new HashMap();
						map.put("erpName", erpName);
						map.put("goodsName", goodsName);
						map.put("reason", "导入数据单用户限购大于总限购");
						reasonList.add(map);
						continue;
					}
				}
				BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider.findAllGoodsByErpNos(
						GoodsInfoByErpNosRequest.builder().erpGoodsInfoNos(Arrays.asList(erpName)).build());
				GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
				
				// 由于有统仓统配商家，一个erpNo可能对应多个商品，需要排除其他商家的商品
				List<GoodsInfoVO> collect = context.getGoodsInfo().stream().filter(e -> e.getStoreId().equals(commonUtil.getStoreId())).collect(Collectors.toList());
				context.setGoodsInfo(collect);
				
				if (CollectionUtils.isNotEmpty(context.getGoodsInfo())) {
					// 营销不分仓库，这段代码注释掉
//					if (context.getGoodsInfo().get(0).getWareId() != wareId) {
//						// 导入的商品与所选的“适用区域”不属于属同一仓库
//						Map map = new HashMap();
//						map.put("erpName", erpName);
//						map.put("goodsName", goodsName);
//						map.put("reason", "导入的商品与所选的“适用区域”不属于属同一仓库");
//						reasonList.add(map);
//						continue;
//					}
					String finalPurchaseNum = purchaseNum;
					String finalPerUserPurchaseNum = perUserPurchaseNum;
					context.getGoodsInfo().forEach(goodsInfoVO -> {
						if (Objects.nonNull(finalPurchaseNum)) {
							Long max = Long.max(Long.parseLong(finalPurchaseNum), 0L);
							goodsInfoVO.setPurchaseNum(max);
						}

						if (Objects.nonNull(finalPerUserPurchaseNum)) {
							Long max = Long.max(Long.parseLong(finalPerUserPurchaseNum), 0L);
							goodsInfoVO.setPerUserPurchaseNum(max);
						}
						contentAll.add(goodsInfoVO);
					});
					List<String> goodsInfoIds = context.getGoodsInfo().stream().map(o -> o.getGoodsInfoId())
							.collect(Collectors.toList());
					/*
					 * if(Objects.nonNull(haveGoodsInfoIds)) { if(haveGoodsInfoIds.size()>0) { if
					 * (haveGoodsInfoIds.containsAll(goodsInfoIds)) { Map map = new HashMap();
					 * map.put("erpName", erpName); map.put("goodsName", goodsName);
					 * map.put("reason", "导入的商品已存在列表中"); reasonList.add(map); continue; } } }
					 */
					goodsInfoIdsAll.addAll(goodsInfoIds);
				}
			}
			/*
			 * GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
			 * queryRequest.setPageSize(10000);
			 * queryRequest.setGoodsInfoIds(goodsInfoIdsAll); GoodsInfoViewPageResponse
			 * pageGoodsResponse =
			 * goodsInfoQueryProvider.pageView(queryRequest).getContext(); List<GoodsInfoVO>
			 * content = pageGoodsResponse.getGoodsInfoPage().getContent();
			 */

//            List<GoodsInfoVO> content=getDuplicateElements(contentAll.stream());
//            if(Objects.nonNull(content)) {
//                if(content.size()>0) {
//                    content.forEach(goodsInfoVO -> {
//                        Map map = new HashMap();
//                        map.put("erpName", goodsInfoVO.getErpGoodsInfoNo());
//                        map.put("goodsName", goodsInfoVO.getGoodsInfoName());
//                        map.put("reason", "商品在表格中存在重复项");
//                        reasonList.add(map);
//                    });
//                    contentAll.removeAll(content);
//                }
//            }
			Map data = new HashMap();
			data.put("goods", contentAll);
			data.put("reason", reasonList);
			operateLogMQUtil.convertAndSend("营销", "促销活动导入商品", "操作成功");
			return BaseResponse.success(data);
		} catch (SbcRuntimeException e) {
			log.error("促销活动商品导入异常", e);
			throw e;
		} catch (Exception e) {
			log.error("促销活动商品导入异常", e);
			throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
		}

	}

	/**
	 * MultipartFile 转换为 File 文件
	 *
	 * @param multipartFile
	 * @return
	 */
	public final static File transferToFile(MultipartFile multipartFile) {
		// 选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
		File file = null;
		try {
			String originalFilename = multipartFile.getOriginalFilename();
			// 获取文件后缀
			String prefix = originalFilename.substring(originalFilename.lastIndexOf("."));
			// String[] filename = originalFilename.split("\\.");
			// file = File.createTempFile(filename[0], filename[1]); //注意下面的 特别注意！！！
			file = File.createTempFile(originalFilename, prefix); // 创建零食文件
			multipartFile.transferTo(file);
			// 删除
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static <T> List<T> getDuplicateElements(Stream<T> stream) {
		return stream.collect(Collectors.groupingBy(p -> p, Collectors.counting())).entrySet().stream() // Set<Entry>转换为Stream<Entry>
				.filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
				.map(entry -> entry.getKey()) // 获得 entry 的键（重复元素）对应的 Stream
				.collect(Collectors.toList()); // 转化为 List
	}

	@Transactional
	public BaseResponse addActivityGoods(MarketingVO marketing, AddActivitGoodsRequest request, List<Map> reasonList) {

		if (Objects.nonNull(marketing)) {
			// 活动正在进行中
			if (LocalDateTime.now().isAfter(marketing.getBeginTime())
					&& LocalDateTime.now().isBefore(marketing.getEndTime())) {
				BaseResponse<List<String>> listBaseResponse = marketingQueryProvider.addActivityGoods(request);
				if (CollectionUtils.isNotEmpty(listBaseResponse.getContext())) {
					throw new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT,
							new Object[] { listBaseResponse.getContext().size(), listBaseResponse.getContext() });
				}
				operateLogMQUtil.convertAndSend("营销", "增加参与活动商品", "增加参与活动商品："
						+ (Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " "));
			} else {
				return BaseResponse.error("活动未开始不可使用");
			}
		} else {
			throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
		}
		return BaseResponse.success(reasonList);
	}

	/**
	 * 导出营销活动excel
	 * 
	 * @param marketingPageListRequest {@link MarketingPageRequest}
	 * @return
	 */
	@ApiOperation(value = "导出营销活动excel")
	@RequestMapping(value = "/export/marketing", method = RequestMethod.POST)
	public void getMarketingList(@RequestBody MarketingPageListRequest marketingPageListRequest,
			HttpServletResponse response) {
		log.info("====营销活动列表导出=============");
		List<MarketingImportVO> marketingImportVOS = new ArrayList<>();
		if (Objects.isNull(marketingPageListRequest.getMarketingIds())) {
			MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
			marketingPageRequest.setStoreId(commonUtil.getStoreId());
			marketingPageListRequest.setPageSize(3000);
			MarketingPageDTO request = KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class);
			if (!StringUtils.isEmpty(marketingPageListRequest.getErpGoodsInfoNo())) {
				BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider
						.findAllGoodsByErpNos(GoodsInfoByErpNosRequest.builder()
								.erpGoodsInfoNos(Arrays.asList(marketingPageListRequest.getErpGoodsInfoNo())).build());
				GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
				if (CollectionUtils.isNotEmpty(context.getGoodsInfo())) {
					List<String> goodsInfoIds = context.getGoodsInfo().stream().map(o -> o.getGoodsInfoId())
							.collect(Collectors.toList());
					request.setGoodsInfoIds(goodsInfoIds);
				} else {
					request.setGoodsInfoIds(Arrays.asList("*****"));
				}
			}
			marketingPageRequest.setMarketingPageDTO(request);
			BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.page(marketingPageRequest);
			
			pageResponse.getContext().getMarketingVOS().getContent().forEach(marketingPageVO -> {
				MarketingGetByIdByIdRequest marketingGetByIdRequest = new MarketingGetByIdByIdRequest();
				marketingGetByIdRequest.setMarketingId(marketingPageVO.getMarketingId());
				MarketingForEndVO marketingResponse = marketingQueryProvider.getByIdForSupplier(marketingGetByIdRequest)
						.getContext().getMarketingForEndVO();
				fillImportVOList(marketingImportVOS, marketingResponse);
			});
		} else {
			marketingPageListRequest.getMarketingIds().forEach(marketingId -> {
				MarketingGetByIdByIdRequest marketingGetByIdRequest = new MarketingGetByIdByIdRequest();
				marketingGetByIdRequest.setMarketingId(marketingId);
				MarketingForEndVO marketingResponse = marketingQueryProvider.getByIdForSupplier(marketingGetByIdRequest)
						.getContext().getMarketingForEndVO();
				fillImportVOList(marketingImportVOS, marketingResponse);
			});
		}

		String headerKey = "Content-Disposition";
		LocalDateTime dateTime = LocalDateTime.now();
		String fileName = String.format("导出促销活动商品记录_%s.xls",
				dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
		String fileNameNew = fileName;
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("/funds/export/params, fileName={},", fileName, e);
		}
		String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
		response.setHeader(headerKey, headerValue);

		try {
			export(marketingImportVOS, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			throw new SbcRuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		operateLogMQUtil.convertAndSend("营销", "导出促销活动商品记录", fileNameNew);
	}

	private void fillImportVOList(List<MarketingImportVO> marketingImportVOS, MarketingForEndVO marketingResponse) {
		MarketingVO marketingVO = KsBeanUtil.convert(marketingResponse, MarketingVO.class);
		StringBuffer stringBuffer = new StringBuffer("");
		if (MarketingType.REDUCTION.name().equals(marketingVO.getMarketingType().name())) {
			List<MarketingFullReductionLevelVO> fullReductionLevelList = marketingResponse
					.getFullReductionLevelList();
			for (MarketingFullReductionLevelVO levelVO : fullReductionLevelList) {
				if (MarketingSubType.REDUCTION_FULL_AMOUNT.name().equals(marketingVO.getSubType().name())) {
					stringBuffer
							.append("满：" + levelVO.getFullAmount() + "元 减：" + levelVO.getReduction() + "元;");
				} else if (MarketingSubType.REDUCTION_FULL_COUNT.name()
						.equals(marketingVO.getSubType().name())) {
					stringBuffer.append("满：" + levelVO.getFullCount() + "件 减：" + levelVO.getReduction() + "元;");
				}
			}
		} else if (MarketingType.DISCOUNT.name().equals(marketingVO.getMarketingType().name())) {
			List<MarketingFullDiscountLevelVO> fullDiscountLevelList = marketingResponse
					.getFullDiscountLevelList();
			for (MarketingFullDiscountLevelVO levelVO : fullDiscountLevelList) {
				if (MarketingSubType.DISCOUNT_FULL_AMOUNT.name().equals(marketingVO.getSubType().name())) {
					stringBuffer.append("满：" + levelVO.getFullAmount() + "元 打：" + levelVO.getDiscount() + "折;");
				} else if (MarketingSubType.DISCOUNT_FULL_COUNT.name()
						.equals(marketingVO.getSubType().name())) {
					stringBuffer.append("满：" + levelVO.getFullCount() + "件 打：" + levelVO.getDiscount() + "折;");
				}
			}
		}
		List<GoodsInfoVO> goodsInfos = marketingResponse.getGoodsList().getGoodsInfoPage().getContent();
		Map<Long, GoodsBrandVO> brandsMap = marketingResponse.getGoodsList().getBrands().stream()
				.collect(Collectors.toMap(GoodsBrandVO::getBrandId, g -> g));
		Map<Long, GoodsCateVO> catesMap = marketingResponse.getGoodsList().getCates().stream()
				.collect(Collectors.toMap(GoodsCateVO::getCateId, g -> g));
		List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider
				.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext()
				.getWareHouseVOList();
		String wareName = wareHouseVOList.stream()
				.filter(wareHouseVO -> wareHouseVO.getWareId().equals(marketingResponse.getWareId()))
//				.findFirst().get().getWareName();
				.findFirst().orElse(new WareHouseVO()).getWareName();

		Map<String, BoolFlag> terminalFlagBySkuIdMap = marketingVO.getMarketingScopeList().stream()
				.collect(Collectors.toMap(MarketingScopeVO::getScopeId, MarketingScopeVO::getTerminationFlag, (k1, k2) -> k1));

		goodsInfos.forEach(goodsInfoVO -> {
			goodsInfoVO.setTerminationFlag(terminalFlagBySkuIdMap.get(goodsInfoVO.getGoodsInfoId()));

			GoodsBrandVO goodsBrandVO = brandsMap.get(goodsInfoVO.getBrandId());
			GoodsCateVO goodsCateVO = catesMap.get(goodsInfoVO.getCateId());
			MarketingImportVO marketingImportVO = new MarketingImportVO();
			marketingImportVO.setGoodsInfoVO(goodsInfoVO);
			marketingImportVO.setMarketingVO(marketingVO);
			if (Objects.nonNull(goodsBrandVO)) {
				marketingImportVO.setBrandName(goodsBrandVO.getBrandName());
			}
			if (Objects.nonNull(goodsCateVO)) {
				marketingImportVO.setCateName(goodsCateVO.getCateName());
			}
			marketingImportVO.setWareName(wareName);
			marketingImportVO.setActivityName(stringBuffer.toString());
			marketingImportVOS.add(marketingImportVO);
		});
	}

	public void export(List<MarketingImportVO> marketingImportVOList, OutputStream outputStream) {
		ExcelHelper excelHelper = new ExcelHelper();
		Column[] columns = { new Column("活动名称", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getMarketingVO().getMarketingName());
		}), new Column("活动类型", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			String subType = "";
			if (Objects.nonNull(marketingImportVO.getMarketingVO().getSubType())) {
				// 0：满金额减 1：满数量减 2:满金额折 3:满数量折
				if (MarketingSubType.REDUCTION_FULL_AMOUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满金额减";
				} else if (MarketingSubType.REDUCTION_FULL_COUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满数量减";
				} else if (MarketingSubType.DISCOUNT_FULL_AMOUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满金额折";
				} else if (MarketingSubType.DISCOUNT_FULL_COUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满数量折";
				} else if (MarketingSubType.GIFT_FULL_AMOUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满金额赠";
				} else if (MarketingSubType.GIFT_FULL_COUNT.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满数量赠";
				} else if (MarketingSubType.GIFT_FULL_ORDER.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满订单赠";
				} else if (MarketingSubType.REDUCTION_FULL_ORDER.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满订单减";
				} else if (MarketingSubType.DISCOUNT_FULL_ORDER.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "满订单折";
				} else if (MarketingSubType.SUIT_TO_BUY.name()
						.equals(marketingImportVO.getMarketingVO().getSubType().name())) {
					subType = "套装购买";
				}
			}
			cell.setCellValue(subType);
		}), new Column("活动时间", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getMarketingVO().getBeginTime().toString() + "~~"
					+ marketingImportVO.getMarketingVO().getEndTime().toString());
		}), new Column("活动状态", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getMarketingVO().getMarketingStatus());
		}), new Column("适用区域", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getWareName());
		}), new Column("是否叠加", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			if (Objects.nonNull(marketingImportVO.getMarketingVO().getIsOverlap())) {
				cell.setCellValue(marketingImportVO.getMarketingVO().getIsOverlap().toValue() == 0 ? "否" : "是");
			} else {
				cell.setCellValue("否");
			}
		}), new Column("是否可跨单品", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			if (Objects.nonNull(marketingImportVO.getMarketingVO().getIsAddMarketingName())) {
				cell.setCellValue(
						marketingImportVO.getMarketingVO().getIsAddMarketingName().toValue() == 0 ? "否" : "是");
			} else {
				cell.setCellValue("否");
			}
		}), new Column("活动力度", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getActivityName());
		}), new Column("ERP编号", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getGoodsInfoVO().getErpGoodsInfoNo());
		}), new Column("商品名称", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getGoodsInfoVO().getGoodsInfoName());
		}), new Column("类目", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getCateName());
		}), new Column("品牌", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getBrandName());
		}), new Column("单价", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			cell.setCellValue(marketingImportVO.getGoodsInfoVO().getMarketPrice().doubleValue());
		}), new Column("总限购", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			// 首先在保存时， 就应该要把 marketing_scope 排重， 然后 这里也需要把 marketing_scope 扁平化结构， 或者要传递个索引进来， 这样就可以不用便利找了。
			String str_val = "无";
			List<MarketingScopeVO> list_marketingScopeList = marketingImportVO.getMarketingVO().getMarketingScopeList();
			if (CollectionUtils.isNotEmpty(list_marketingScopeList)) {
				String str_goodsInfoId = marketingImportVO.getGoodsInfoVO().getGoodsInfoId();
				MarketingScopeVO marketingScopeVO = list_marketingScopeList.stream()
						.filter(obj -> StringUtils.isNotEmpty(obj.getScopeId()) && obj.getScopeId().equals(str_goodsInfoId)).findFirst()
						.orElseGet(MarketingScopeVO::new);
				if (marketingScopeVO.getPurchaseNum() != null) {
					str_val = marketingScopeVO.getPurchaseNum().toString();
				}
			}
			cell.setCellValue(str_val);
		}), new Column("单用户限购", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			// 首先在保存时， 就应该要把 marketing_scope 排重， 然后 这里也需要把 marketing_scope 扁平化结构， 或者要传递个索引进来， 这样就可以不用便利找了。
			String str_val = "无";
			List<MarketingScopeVO> list_marketingScopeList = marketingImportVO.getMarketingVO().getMarketingScopeList();
			if (CollectionUtils.isNotEmpty(list_marketingScopeList)) {
				String str_goodsInfoId = marketingImportVO.getGoodsInfoVO().getGoodsInfoId();
				MarketingScopeVO marketingScopeVO = list_marketingScopeList.stream()
						.filter(obj -> StringUtils.isNotEmpty(obj.getScopeId()) && obj.getScopeId().equals(str_goodsInfoId)).findFirst()
						.orElseGet(MarketingScopeVO::new);
				if (marketingScopeVO.getPerUserPurchaseNum() != null) {
					str_val = marketingScopeVO.getPerUserPurchaseNum().toString();
				}
			}
			cell.setCellValue(str_val);
		}), new Column("商品参与状态", (cell, object) -> {
			MarketingImportVO marketingImportVO = (MarketingImportVO) object;
			BoolFlag terminationFlag = marketingImportVO.getGoodsInfoVO().getTerminationFlag();
			cell.setCellValue(terminationFlag == null || terminationFlag.toValue() == 0 ? "参与中" : "已终止");
		}), };
		excelHelper.addSheet("促销活动商品导出", columns, marketingImportVOList);
		excelHelper.write(outputStream);
	}

	/**
	 * 会员导入模板下载
	 *
	 */
	@ApiOperation(value = "促销活动商品导入模板下载")
	@RequestMapping(value = "/downloadTemplate/{encrypted}", method = RequestMethod.GET)
	public void export(@PathVariable String encrypted) {
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
				String fileName = URLEncoder.encode("促销活动商品导入模板.xls", "UTF-8");
				HttpUtil.getResponse().setHeader("Content-Disposition",
						String.format("attachment;filename=\"%s\";" + "filename*=\"utf-8''%s\"", fileName, fileName));
				HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
			}
		} catch (Exception e) {
			throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				log.error("促销活动商品导入模板转Base64位异常", e);
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("读取促销活动商品导入模板异常", e);
				}
			}
		}
		operateLogMQUtil.convertAndSend("营销", "促销活动商品导入模板下载", "操作成功");
	}

}
