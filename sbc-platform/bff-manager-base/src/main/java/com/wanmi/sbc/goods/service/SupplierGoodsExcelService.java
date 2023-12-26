package com.wanmi.sbc.goods.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.common.GoodsCommonProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattribute.GoodsAttributeQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateLeafByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchAddRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsDefaultByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsDefaultByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.bean.dto.*;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.GoodsSupplierExcelImportRequest;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadVideoResourceRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品EXCEL处理服务
 * Created by dyt on 2017/8/17.
 */
@Slf4j
@Service
public class SupplierGoodsExcelService {

    @Autowired
    private GoodsExcelService goodsExcelService;

    @Autowired
    private FreightTemplateGoodsQueryProvider freightTemplateGoodsQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsCommonProvider goodsCommonProvider;
    @Autowired
    private GoodsAttributeQueryProvider goodsAttributeQueryProvider;
    @Autowired
    private YunServiceProvider yunServiceProvider;
    /**
     * 导入模板
     *
     * @return
     */
    @Transactional
    public List<String> implGoods(GoodsSupplierExcelImportRequest goodsRequest) {
        String ext = goodsRequest.getExt();
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/")
                .concat(goodsRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        //运费模板未配置
        FreightTemplateGoodsDefaultByStoreIdResponse context = freightTemplateGoodsQueryProvider.getDefaultByStoreId(
                FreightTemplateGoodsDefaultByStoreIdRequest.builder().storeId(goodsRequest.getStoreId()).deliverWay(DeliverWay.EXPRESS.toValue()).build())
                .getContext();
        if (CollectionUtils.isEmpty(context.getDefultFreightTemplate())) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }

        //创建Workbook工作薄对象，表示整个excel
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            //检测文档正确性
            this.goodsExcelService.checkExcel(workbook);

            //获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            StoreCateListByStoreIdRequest storeCateQueryRequest = new StoreCateListByStoreIdRequest();
            storeCateQueryRequest.setStoreId(goodsRequest.getStoreId());
            List<StoreCateResponseVO> storeCateList = storeCateQueryProvider.listByStoreId(storeCateQueryRequest)
                    .getContext().getStoreCateResponseVOList();
            Long defaultStoreCateId = storeCateList.stream()
                            .filter(storeCate -> Objects.equals(DefaultFlag.YES, storeCate.getIsDefault()))
                            .map(StoreCateResponseVO::getStoreCateId).findFirst().orElseThrow(() -> new SbcRuntimeException
                            (GoodsCateErrorCode.DEFAULT_CATE_NOT_EXIST));
            Map<Long, Long> storeCateSet = storeCateList.stream().collect(Collectors.toMap(StoreCateResponseVO::getStoreCateId, StoreCateResponseVO::getCateParentId));

            //SkuMap<Spu编号,Spu对象>
            Map<String, BatchGoodsDTO> goodses = new LinkedHashMap<>();
            //SkuMap<Spu编号,Sku对象>
            Map<String, List<BatchGoodsInfoDTO>> skus = new LinkedHashMap<>();

            //SkuMap<Spu编号,图片对象>
            Map<String, List<String>> images = new LinkedHashMap<>();

            //存储编码以及单元格对象，验证重复
            Map<String, List<Cell>> spuNos = new HashMap<>();
            Map<String, List<Cell>> skuNos = new HashMap<>();

            //存储编码以及单元格对象，验证规格和规格值
            Map<String, Cell> spuNoSpecs = new HashMap<>();
            Map<String, Cell> spuNoSpecDetails = new HashMap<>();

            //规格值<Spu编号,规格项集合>
            Map<String, List<BatchGoodsSpecDTO>> allSpecs = new HashMap<>();

            //规格值<规格项模拟编号_规格值名称,规格值模拟编号>，保证在同一个Spu下、同一规格项下，不允许出现重复规格值
            Map<String, Long> specDetailMap = new HashMap<>();
            //规格值<Spu编号,规格值集合>
            Map<String, List<BatchGoodsSpecDetailDTO>> allSpecDetails = new HashMap<>();

            ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
            contractBrandQueryRequest.setStoreId(goodsRequest.getStoreId());
            Set<Long> brandSet = contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList()
                    .stream().filter(vo -> Objects.nonNull(vo.getGoodsBrand()))
                    .map(ContractBrandVO::getGoodsBrand).map(GoodsBrandVO::getBrandId).collect(Collectors.toSet());

            Map<Long, Boolean> goodsCateMap = goodsCateQueryProvider.listLeafByStoreId(
                    GoodsCateLeafByStoreIdRequest.builder().storeId(goodsRequest.getStoreId()).build()).getContext().getGoodsCateList()
                    .stream().collect(Collectors.toMap(GoodsCateVO::getCateId, c -> Boolean.TRUE));

            //规格列索引
            int[] specColNum = {11, 13, 15, 17, 19};
            int[] specDetailColNum = {12, 14, 16, 18, 20};
            StoreType type = goodsRequest.getType();
            int maxCell;
            if (StoreType.SUPPLIER.equals(type)) {
                maxCell = 27;
            }else{
                maxCell = 27;
            }

            boolean isError = false;

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
                    if (StringUtils.isNotEmpty(ExcelHelper.getValue(cell))) {
                        isNotEmpty = true;
                    }
                }
                //数据都为空，则跳过去
                if (!isNotEmpty) {
                    continue;
                }

                BatchGoodsDTO goods = new BatchGoodsDTO();
                if (StoreType.SUPPLIER.equals(type)) {
                    //默认销售类型 批发
                    goods.setSaleType(SaleType.WHOLESALE.toValue());
                    goods.setGoodsSource(StoreType.SUPPLIER.toValue());
                }else{
                    goods.setSaleType(SaleType.RETAIL.toValue());
                    goods.setGoodsSource(StoreType.PROVIDER.toValue());
                }
                //默认实体商品
                goods.setGoodsType(NumberUtils.INTEGER_ZERO);
                //允许独立设价
                goods.setAllowPriceSet(1);
                goods.setFreightTempId(context.getDefultFreightTemplate().get(0).getFreightTempId());
                goods.setGoodsName(ExcelHelper.getValue(cells[0]));
                goods.setGoodsNo(ExcelHelper.getValue(cells[1]));
                goods.setMockGoodsId(goods.getGoodsNo());
                goods.setStoreId(goodsRequest.getStoreId());
                goods.setCompanyInfoId(goodsRequest.getCompanyInfoId());
                goods.setSupplierName(goodsRequest.getSupplierName());
                goods.setCompanyType(goodsRequest.getCompanyType());
                if (StringUtils.isBlank(goods.getGoodsNo())) {
                    ExcelHelper.setError(workbook, cells[1], "此项必填");
                    isError = true;
                    goods.setGoodsNo("DEMO".concat(String.valueOf(rowNum)));
                } else if (!ValidateUtil.isBetweenLen(goods.getGoodsNo(), 1, 20)) {
                    ExcelHelper.setError(workbook, cells[1], "长度必须1-20个字");
                    isError = true;
                } else if (!ValidateUtil.isNotChs(goods.getGoodsNo())) {
                    ExcelHelper.setError(workbook, cells[1], "仅允许英文、数字、特殊字符");
                    isError = true;
                }

                //是否第一个第一个SPU商品
                boolean isFirstGoods = false;
                if (!goodses.containsKey(goods.getGoodsNo())){
                    isFirstGoods = true;
                }

                //如果第一个SPU商品处理
                if (isFirstGoods) {
                    goods.setCateId(NumberUtils.toLong(ExcelHelper.getValue(cells[2]).split("_")[0]));
                    if (goods.getCateId() == 0 || (!goodsCateMap.containsKey(goods.getCateId()))) {
                        ExcelHelper.setError(workbook, cells[2], "请选择平台类目或平台类目不存在");
                        isError = true;
                    }

                    Long storeCateId = NumberUtils.toLong(ExcelHelper.getValue(cells[3]).split("_")[0]);
                    if (storeCateId == 0 || (!storeCateSet.containsKey(storeCateId))) {
                        storeCateId = defaultStoreCateId;
                    }
                    List<Long> storeCateIds = new ArrayList<>();
                    storeCateIds.add(storeCateId);
                    Long parentCateId = storeCateSet.get(storeCateId);
                    //获取店铺分类的父分类
                    if (Objects.nonNull(parentCateId) && parentCateId > 0) {
                        storeCateIds.add(parentCateId);
                    }
                    goods.setStoreCateIds(storeCateIds);

                    goods.setGoodsUnit(ExcelHelper.getValue(cells[4]));
                    if (StringUtils.isBlank(goods.getGoodsUnit())) {
                        ExcelHelper.setError(workbook, cells[4], "此项必填");
                        isError = true;
                    } else if (ValidateUtil.isOverLen(goods.getGoodsUnit(), 10)) {
                        ExcelHelper.setError(workbook, cells[4], "长度必须1-10个字");
                        isError = true;
                    } else if (!ValidateUtil.isChsEng(goods.getGoodsUnit())) {
                        ExcelHelper.setError(workbook, cells[4], "仅允许中文、英文");
                        isError = true;
                    }


                    Long brandId = NumberUtils.toLong(ExcelHelper.getValue(cells[5]).split("_")[0]);
                    if (brandId > 0 && brandSet.contains(brandId)) {
                        goods.setBrandId(brandId);
                    }
                }

                String imageUrlStr = ExcelHelper.getValue(cells[6]);
                if (StringUtils.isNoneBlank(imageUrlStr)){
                    String[] imageUrls = imageUrlStr.split("\\|");
                    List<String> imageArr = new ArrayList<>();
                    if (imageUrls.length > 0) {
                        if (!images.containsKey(goods.getGoodsNo())) {
                            if (imageUrls.length > 10) {
                                ExcelHelper.setError(workbook, cells[6], "最多传10张图片");
                                isError = true;
                            }
                            boolean isFirst = true;
                            for (int i = 0; i < imageUrls.length; i++) {
                                if(imageUrls[i].length() > 255){
                                    ExcelHelper.setError(workbook, cells[6], "商品图片链接长度不能超过255");
                                    isError = true;
                                }
                                if (WebUtil.isImage(imageUrls[i])) {
                                    if (isFirst) {
                                        goods.setGoodsImg(imageUrls[i]);
                                        isFirst = false;
                                    }
                                    imageArr.add(imageUrls[i]);
                                }
                            }
                            images.put(goods.getGoodsNo(), imageArr);
                        }
                    }
                }


                //SPU视频链接地址
                goods.setGoodsVideo(ExcelHelper.getValue(cells[7]));
                if (StringUtils.isNotEmpty(goods.getGoodsVideo()) && !goods.getGoodsVideo().endsWith("mp4")) {
                    ExcelHelper.setError(workbook, cells[7], "视频仅支持mp4格式");
                    isError = true;
                }

                if(goods.getGoodsVideo().length() > 255){
                    ExcelHelper.setError(workbook, cells[7], "商品视频链接长度不能超过255");
                    isError = true;
                }

                String details = ExcelHelper.getValue(cells[8]);
                if (StringUtils.isNotEmpty(details)) {
                    goods.setGoodsDetail(Arrays.stream(details.split("\\|")).map(s -> {
                        if (s.startsWith("http")) {
                            return String.format("<img src='%s'/><br/>", s);
                        }
                        return String.format("<p>%s</p>", s);
                    }).collect(Collectors.joining()));
                }

                BatchGoodsInfoDTO goodsInfo = new BatchGoodsInfoDTO();
                goodsInfo.setMockGoodsId(goods.getMockGoodsId());
                goodsInfo.setMockSpecIds(new ArrayList<>());
                goodsInfo.setMockSpecDetailIds(new ArrayList<>());

                if (StoreType.SUPPLIER.equals(type)) {
                    goodsInfo.setGoodsSource(StoreType.SUPPLIER.toValue());
                }else{
                    goodsInfo.setGoodsSource(StoreType.PROVIDER.toValue());
                }

                //SKU编码
                String skuNo = ExcelHelper.getValue(cells[9]);
                if (StringUtils.isNotEmpty(skuNo)) {
                    if (!ValidateUtil.isBetweenLen(skuNo, 1, 20)) {
                        ExcelHelper.setError(workbook, cells[9], "长度必须1-20个字");
                        isError = true;
                    } else if (!ValidateUtil.isNotChs(skuNo)) {
                        ExcelHelper.setError(workbook, cells[9], "仅允许英文、数字、特殊字符");
                        isError = true;
                    }
                } else {
                    skuNo = goodsExcelService.getSkuNo(skuNos);
                    cells[9].setCellValue(skuNo);
                }

                if (skuNos.containsKey(skuNo)) {
                    ExcelHelper.setError(workbook, cells[9], "文档中出现重复的SKU编码");
                    isError = true;
                }

                goodsInfo.setGoodsInfoNo(skuNo);

                //SKU图片
                String skuImage = ExcelHelper.getValue(cells[10]);
                if (StringUtils.isNoneBlank(skuImage)) {
                    //如果不是图片链接
                    if (!WebUtil.isImage(skuImage)) {
                        goodsInfo.setGoodsInfoImg(null);
                    } else {
                        goodsInfo.setGoodsInfoImg(skuImage);
                        if(skuImage.length() > 255){
                            ExcelHelper.setError(workbook, cells[10], "商品SKU图片链接长度不能超过255");
                            isError = true;
                        }
                    }
                } else {
                    goodsInfo.setGoodsInfoImg(null);
                }


                //处理同一SPU下第一条的规格项
                if (isFirstGoods) {
                    for (int i : specColNum) {
                        BatchGoodsSpecDTO spec = new BatchGoodsSpecDTO();
                        spec.setMockGoodsId(goods.getMockGoodsId());
                        spec.setMockSpecId(NumberUtils.toLong(String.valueOf(rowNum).concat(String.valueOf(i))));
                        spec.setSpecName(ExcelHelper.getValue(cells[i]));
                        if (StringUtils.isNotEmpty(ExcelHelper.getValue(cells[i + 1]))) {
                            if (StringUtils.isBlank(spec.getSpecName())) {
                                ExcelHelper.setError(workbook, cells[i], "此项必填");
                                isError = true;
                            } else if (ValidateUtil.isOverLen(spec.getSpecName(), 10)) {
                                ExcelHelper.setError(workbook, cells[i], "长度必须0-10个字");
                                isError = true;
                            } else if (ValidateUtil.containsEmoji(spec.getSpecName())) {
                                ExcelHelper.setError(workbook, cells[i], "含有非法字符");
                                isError = true;
                            }
                        }

                        allSpecs.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(spec)), (s1, s2) -> {
                            s1.addAll(s2);
                            return s1;
                        });
                    }
                    spuNoSpecs.put(goods.getGoodsNo(), cells[specColNum[0]]);
                } else {
                    Arrays.stream(specColNum).forEach(i -> cells[i].setCellValue(""));
                }

                //处理规格值
                for (int i = 0; i < specDetailColNum.length; i++) {
                    BatchGoodsSpecDTO spec = allSpecs.get(goods.getGoodsNo()).get(i);
                    BatchGoodsSpecDetailDTO specDetail = new BatchGoodsSpecDetailDTO();
                    specDetail.setDetailName(ExcelHelper.getValue(cells[specDetailColNum[i]]));
                    specDetail.setMockGoodsId(goods.getMockGoodsId());
                    specDetail.setMockSpecId(spec.getMockSpecId());
                    specDetail.setMockSpecDetailId(NumberUtils.toLong(String.valueOf(rowNum).concat(String.valueOf(i))));

                    //设置SKU与规格的扁平数据
                    goodsInfo.getMockSpecIds().add(specDetail.getMockSpecId());

                    spuNoSpecDetails.put(goodsInfo.getGoodsInfoNo(), cells[specDetailColNum[0]]);

                    //不为空的规格项相应的规格值必须填写
                    if (StringUtils.isNotEmpty(spec.getSpecName()) && StringUtils.isBlank(specDetail.getDetailName())) {
                        ExcelHelper.setError(workbook, cells[specDetailColNum[i]], "此项必填");
                        isError = true;
                    }

                    //明细不为空
                    if (StringUtils.isNotEmpty(specDetail.getDetailName())) {
                        if (ValidateUtil.isOverLen(specDetail.getDetailName(), 20)) {
                            ExcelHelper.setError(workbook, cells[specDetailColNum[i]], "长度必须0-20个字");
                            isError = true;
                        } else if (ValidateUtil.containsEmoji(specDetail.getDetailName())) {
                            ExcelHelper.setError(workbook, cells[specDetailColNum[i]], "含有非法字符");
                            isError = true;
                        }

                        String key = String.valueOf(specDetail.getMockSpecId()).concat("_").concat(specDetail.getDetailName());
                        //保证在同一个Spu下、同一规格项下，不允许出现重复规格值
                        //存在同一个规格值，取其模拟Id
                        if (specDetailMap.containsKey(key)) {
                            specDetail.setMockSpecDetailId(specDetailMap.get(key));
                        } else {
                            //不存在，则放明细模拟ID和明细
                            specDetailMap.put(key, specDetail.getMockSpecDetailId());
                            allSpecDetails.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(specDetail)), (s1, s2) -> {
                                s1.addAll(s2);
                                return s1;
                            });
                        }

                        //设置SKU与规格值的扁平数据
                        goodsInfo.getMockSpecDetailIds().add(specDetail.getMockSpecDetailId());

                        if (allSpecDetails.get(goods.getGoodsNo()).stream().filter(goodsSpecDetail -> specDetail.getMockSpecId().equals(goodsSpecDetail.getMockSpecId())).count() > 20) {
                            ExcelHelper.setError(workbook, cells[specDetailColNum[i]], "在同一规格项内，不同的规格值不允许超过20个");
                            isError = true;
                        }
                    }
                }

                 if (CollectionUtils.isNotEmpty(goodsInfo.getMockSpecDetailIds())
                        && skus.getOrDefault(goods.getGoodsNo(), new ArrayList<>()).stream()
                        .map(BatchGoodsInfoDTO::getMockSpecDetailIds)
                        .filter(r -> r.size() == goodsInfo.getMockSpecDetailIds().size()
                                && r.containsAll(goodsInfo.getMockSpecDetailIds())).count() > 0){
                    Arrays.stream(specDetailColNum).forEach(i -> {
                        ExcelHelper.setError(workbook, cells[i], "不允许出现所有规格值完全一致的商品");
                    });
                    isError = true;
                }


                //库存
                String stock = ExcelHelper.getValue(cells[21]);
                goodsInfo.setStock(StringUtils.isBlank(stock) ? BigDecimal.ZERO : new BigDecimal(stock));
                if(goodsInfo.getCostPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getCostPrice().compareTo(new BigDecimal("9999999.99")) > 0){
                    ExcelHelper.setError(workbook, cells[22], "必须在0-9999999.99范围内");
                    isError = true;
                }

                //条形码
                goodsInfo.setGoodsInfoBarcode(ExcelHelper.getValue(cells[22]));
                if (StringUtils.isNotEmpty(goodsInfo.getGoodsInfoBarcode()) && ValidateUtil.isOverLen(goodsInfo
                        .getGoodsInfoBarcode(), 20)) {
                    ExcelHelper.setError(workbook, cells[22], "长度必须0-20个字");
                    isError = true;
                }

                if (StoreType.SUPPLIER.equals(type)) {
                    //门店价
                    String marketPrice = ExcelHelper.getValue(cells[23]);
                    goodsInfo.setMarketPrice(StringUtils.isBlank(marketPrice) ? BigDecimal.ZERO : new BigDecimal
                            (marketPrice));
//                goods.setMarketPrice(goodsInfo.getMarketPrice());
                    if (StringUtils.isNotEmpty(marketPrice)) {
                        if (goodsInfo.getMarketPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getMarketPrice()
                                .compareTo(new BigDecimal("9999999.99")) > 0) {
                            ExcelHelper.setError(workbook, cells[23], "必须在0-9999999.99范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[23], "此项必填");
                        isError = true;
                    }

                }else{
                    //供货价
                    String supplyPrice = ExcelHelper.getValue(cells[23]);
                    goodsInfo.setSupplyPrice(StringUtils.isBlank(supplyPrice) ? BigDecimal.ZERO : new BigDecimal
                            (supplyPrice));
                    if (StringUtils.isNotEmpty(supplyPrice)) {
                        if (goodsInfo.getSupplyPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getSupplyPrice()
                                .compareTo(new BigDecimal("9999999.99")) > 0) {
                            ExcelHelper.setError(workbook, cells[23], "必须在0-9999999.99范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[23], "此项必填");
                        isError = true;
                    }

                }


                //大客户价
                String vipPrice = ExcelHelper.getValue(cells[24]);
                goodsInfo.setVipPrice(StringUtils.isBlank(vipPrice) ? BigDecimal.ZERO : new BigDecimal
                        (vipPrice));
//                goods.setMarketPrice(goodsInfo.getMarketPrice());
                if (StringUtils.isNotEmpty(vipPrice)) {
                    if (goodsInfo.getMarketPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getMarketPrice()
                            .compareTo(new BigDecimal("9999999.99")) > 0) {
                        ExcelHelper.setError(workbook, cells[24], "必须在0-9999999.99范围内");
                        isError = true;
                    }
                } else {
                    ExcelHelper.setError(workbook, cells[24], "此项必填");
                    isError = true;
                }


                //上下架
                String addedFlagStr = ExcelHelper.getValue(cells[25]);

                if ("上架".equals(addedFlagStr.trim())) {
                    goodsInfo.setAddedFlag(AddedFlag.YES.toValue());
                } else if ("下架".equals(addedFlagStr.trim())) {
                    goodsInfo.setAddedFlag(AddedFlag.NO.toValue());
                } else {
                    ExcelHelper.setError(workbook, cells[25], "必须在[上架、下架]范围内");
                    isError = true;
                }

                if (isFirstGoods) {
                    //重量
                    String weightStr = ExcelHelper.getValue(cells[26]);
                    if (StringUtils.isNotEmpty(weightStr)) {
                        goods.setGoodsWeight(new BigDecimal(weightStr).setScale(3, BigDecimal.ROUND_HALF_UP));
                        if (goods.getGoodsWeight().compareTo(new BigDecimal("0.001")) < 0 || goods.getGoodsWeight()
                                .compareTo(new BigDecimal("9999.999")) > 0) {
                            ExcelHelper.setError(workbook, cells[26], "必须在0.001-9999.999范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[26], "此项必填");
                        isError = true;
                    }

                    //体积
                    String cubageStr = ExcelHelper.getValue(cells[27]);
                    if (StringUtils.isNotEmpty(cubageStr)) {
                        goods.setGoodsCubage(new BigDecimal(cubageStr).setScale(6, BigDecimal.ROUND_HALF_UP));
                        if (goods.getGoodsCubage().compareTo(new BigDecimal("0.000001")) < 0 || goods.getGoodsCubage()
                                .compareTo(new BigDecimal("999.999999")) > 0) {
                            ExcelHelper.setError(workbook, cells[27], "必须在0.000001-999.999999范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[27], "此项必填");
                        isError = true;
                    }

                    //商品名称
                    if (StringUtils.isBlank(goods.getGoodsName())) {
                        ExcelHelper.setError(workbook, cells[0], "此项必填");
                        isError = true;
                    } else if (!ValidateUtil.isBetweenLen(goods.getGoodsName(), 1, 40)) {
                        ExcelHelper.setError(workbook, cells[0], "长度必须1-40个字");
                        isError = true;
                    } else if (ValidateUtil.containsEmoji(goods.getGoodsName())) {
                        ExcelHelper.setError(workbook, cells[0], "含有非法字符");
                        isError = true;
                    }

                    goodses.put(goods.getGoodsNo(), goods);
                } else {
                    if (skus.getOrDefault(goods.getGoodsNo(), new ArrayList<>()).size() >= 50) {
                        ExcelHelper.setError(workbook, cells[1], "同一SPU的商品不允许超过50条");
                        isError = true;
                    }
                }
                skus.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(goodsInfo)), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });

                spuNos.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(cells[1])), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });

                skuNos.merge(goodsInfo.getGoodsInfoNo(), new ArrayList(Arrays.asList(cells[9])), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });
            }

            List<GoodsVO> goodsList = goodsQueryProvider.listByCondition(GoodsByConditionRequest.builder()
                    .delFlag(DeleteFlag.NO.toValue()).goodsNos(new ArrayList<>(spuNos.keySet())).build()).getContext().getGoodsVOList();

            //设置重复错误提示
            if (CollectionUtils.isNotEmpty(goodsList)) {
                goodsList.forEach(goods -> {
                    spuNos.get(goods.getGoodsNo()).forEach(cell -> {
                        //为单元格设置重复错误提示
                        ExcelHelper.setError(workbook, cell, "该编码重复");
                    });
                });
                isError = true;
            }

            List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder().delFlag
                    (DeleteFlag.NO.toValue()).goodsInfoNos(new ArrayList<>(skuNos.keySet()))
                    .build()).getContext().getGoodsInfos();

            //设置重复错误提示
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                goodsInfos.forEach(sku -> {
                    skuNos.get(sku.getGoodsInfoNo()).forEach(cell -> {
                        //为单元格设置重复错误提示
                        ExcelHelper.setError(workbook, cell, "该编码重复");
                    });
                });
                isError = true;
            }

            //针对多个SKU验证规格或规格值是否为空
            for (BatchGoodsDTO goods : goodses.values()) {
                List<BatchGoodsInfoDTO> goodsInfoList = skus.get(goods.getGoodsNo());
                if (goodsInfoList.size() > 1) {
                    boolean isFirst = false;
                    for (BatchGoodsInfoDTO goodsInfo : goodsInfoList) {
                        if (CollectionUtils.isEmpty(goodsInfo.getMockSpecIds()) && (!isFirst)) {
                            ExcelHelper.setError(workbook, spuNoSpecs.get(goods.getGoodsNo()), "规格不允许为空");
                            isFirst = true;
                            isError = true;
                        }

                        if (CollectionUtils.isEmpty(goodsInfo.getMockSpecDetailIds())) {
                            //为单元格设置重复错误提示
                            ExcelHelper.setError(workbook, spuNoSpecDetails.get(goodsInfo.getGoodsInfoNo()), "规格值不允许为空");
                            isError = true;
                        }
                    }
                }
            }

            if (isError) {
                goodsExcelService.errorExcel(goodsRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{ext});
            }

            //指定图片与goods的mockGoodsId关系
            List<BatchGoodsImageDTO> imageDTOList = new ArrayList<>();
            goodses.values().forEach(goods -> {
                if (images.containsKey(goods.getGoodsNo())) {
                    imageDTOList.addAll(images.get(goods.getGoodsNo()).stream().filter(StringUtils::isNotEmpty).map(s -> {
                        BatchGoodsImageDTO image = new BatchGoodsImageDTO();
                        image.setMockGoodsId(goods.getMockGoodsId());
                        image.setArtworkUrl(s);
                        return image;
                    }).collect(Collectors.toList()));
                }
            });

            //批量保存
            return goodsCommonProvider.batchAdd(GoodsCommonBatchAddRequest.builder()
                    .goodsList(new ArrayList<>(goodses.values()))
                    .goodsInfoList(skus.values().stream().flatMap(Collection::stream).collect(Collectors.toList()))
                    .specList(allSpecs.values().stream().flatMap(Collection::stream).collect(Collectors.toList()))
                    .specDetailList(allSpecDetails.values().stream().flatMap(Collection::stream).collect(Collectors.toList()))
                    .imageList(imageDTOList).build()).getContext().getSkuNoList();
        } catch (SbcRuntimeException e) {
            log.error("商品导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("商品导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }
    /**
     * 导入模板
     *“/tmp/tomcat-docbase.4258734741356701857.8390/excel/goodsAdd/2c97d6e28947f864018948fc98fe0000.xls”*
     * @return
     */
    @Transactional
    public List<String> storeImplGoods(GoodsSupplierExcelImportRequest goodsRequest) {
        String extf = goodsRequest.getExt();
        InputStream inputStream = PoiExcel.CrmExcelReadHelper(extf);
        String filePath =  goodsRequest.getExt();
        Workbook sheets;
        try {
            sheets = PoiExcel.readExcelTitle(inputStream, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //运费模板未配置
        FreightTemplateGoodsDefaultByStoreIdResponse context = freightTemplateGoodsQueryProvider.getDefaultByStoreId(
                        FreightTemplateGoodsDefaultByStoreIdRequest.builder().storeId(goodsRequest.getStoreId())
                                .deliverWay(DeliverWay.EXPRESS.toValue()).build())
                .getContext();
        if (CollectionUtils.isEmpty(context.getDefultFreightTemplate())) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }

        //创建Workbook工作薄对象，表示整个excel
        try (Workbook workbook = sheets) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            //检测文档正确性
            this.goodsExcelService.checkExcel(workbook);

            //获得当前sheet的开始行
            int firstRowNum = sheet.getFirstRowNum();
            //获得当前sheet的结束行
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            StoreCateListByStoreIdRequest storeCateQueryRequest = new StoreCateListByStoreIdRequest();
            storeCateQueryRequest.setStoreId(goodsRequest.getStoreId());
            List<StoreCateResponseVO> storeCateList = storeCateQueryProvider.listByStoreId(storeCateQueryRequest)
                    .getContext().getStoreCateResponseVOList();
            Long defaultStoreCateId = storeCateList.stream()
                    .filter(storeCate -> Objects.equals(DefaultFlag.YES, storeCate.getIsDefault()))
                    .map(StoreCateResponseVO::getStoreCateId).findFirst().orElseThrow(() -> new SbcRuntimeException
                            (GoodsCateErrorCode.DEFAULT_CATE_NOT_EXIST));
            Map<Long, Long> storeCateSet = storeCateList.stream().collect(Collectors.toMap(StoreCateResponseVO::getStoreCateId, StoreCateResponseVO::getCateParentId));

            //SkuMap<Spu编号,Spu对象>
            Map<String, BatchGoodsDTO> goodses = new LinkedHashMap<>();
            //SkuMap<Spu编号,Sku对象>
            Map<String, List<BatchGoodsInfoDTO>> skus = new LinkedHashMap<>();

            //SkuMap<Spu编号,图片对象>
            Map<String, List<String>> images = new LinkedHashMap<>();

            //存储编码以及单元格对象，验证重复
            Map<String, List<Cell>> spuNos = new HashMap<>();
            Map<String, List<Cell>> skuNos = new HashMap<>();

            //存储编码以及单元格对象，验证规格和规格值
           // Map<String, Cell> spuNoSpecs = new HashMap<>();
          //  Map<String, Cell> spuNoSpecDetails = new HashMap<>();

            //规格值<Spu编号,规格项集合>
          //  Map<String, List<BatchGoodsSpecDTO>> allSpecs = new HashMap<>();

            //规格值<规格项模拟编号_规格值名称,规格值模拟编号>，保证在同一个Spu下、同一规格项下，不允许出现重复规格值
         //   Map<String, Long> specDetailMap = new HashMap<>();
            //规格值<Spu编号,规格值集合>
          //  Map<String, List<BatchGoodsSpecDetailDTO>> allSpecDetails = new HashMap<>();

            ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
            contractBrandQueryRequest.setStoreId(goodsRequest.getStoreId());
            Set<Long> brandSet = contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList()
                    .stream().filter(vo -> Objects.nonNull(vo.getGoodsBrand()))
                    .map(ContractBrandVO::getGoodsBrand).map(GoodsBrandVO::getBrandId).collect(Collectors.toSet());

            Map<Long, Boolean> goodsCateMap = goodsCateQueryProvider.listLeafByStoreId(
                            GoodsCateLeafByStoreIdRequest.builder().storeId(goodsRequest.getStoreId()).build()).getContext().getGoodsCateList()
                    .stream().collect(Collectors.toMap(GoodsCateVO::getCateId, c -> Boolean.TRUE));

            //规格列索引
         //   int[] specColNum = {11, 13, 15, 17, 19};
            //int[] specDetailColNum = {12, 14, 16, 18, 20};
            StoreType type = goodsRequest.getType();
            int maxCell;
            if (StoreType.SUPPLIER.equals(type)) {
                maxCell = 38;
            }else{
                maxCell = 38;
            }

            boolean isError = false;

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
                    if (StringUtils.isNotEmpty(ExcelHelper.getValue(cell))) {
                        isNotEmpty = true;
                    }
                }
                //数据都为空，则跳过去
                if (!isNotEmpty) {
                    continue;
                }

                BatchGoodsDTO goods = new BatchGoodsDTO();
                if (StoreType.SUPPLIER.equals(type)) {
                    //默认销售类型 批发
                    goods.setSaleType(SaleType.WHOLESALE.toValue());
                    goods.setGoodsSource(StoreType.SUPPLIER.toValue());
                }else{
                    goods.setSaleType(SaleType.RETAIL.toValue());
                    goods.setGoodsSource(StoreType.PROVIDER.toValue());
                }
                //默认实体商品
                goods.setGoodsType(NumberUtils.INTEGER_ZERO);
                //允许独立设价
                goods.setAllowPriceSet(1);
                goods.setFreightTempId(context.getDefultFreightTemplate().get(0).getFreightTempId());
                goods.setGoodsName(ExcelHelper.getValue(cells[0]));
                goods.setGoodsNo(ExcelHelper.getValue(cells[1]));
                goods.setMockGoodsId(goods.getGoodsNo());
                goods.setStoreId(goodsRequest.getStoreId());
                goods.setCompanyInfoId(goodsRequest.getCompanyInfoId());
                goods.setSupplierName(goodsRequest.getSupplierName());
                goods.setCompanyType(goodsRequest.getCompanyType());
                if (StringUtils.isBlank(goods.getGoodsNo())) {
                    ExcelHelper.setError(workbook, cells[1], "此项必填");
                    isError = true;
                    goods.setGoodsNo("DEMO".concat(String.valueOf(rowNum)));
                } else if (!ValidateUtil.isBetweenLen(goods.getGoodsNo(), 1, 20)) {
                    ExcelHelper.setError(workbook, cells[1], "长度必须1-20个字");
                    isError = true;
                } else if (!ValidateUtil.isNotChs(goods.getGoodsNo())) {
                    ExcelHelper.setError(workbook, cells[1], "仅允许英文、数字、特殊字符");
                    isError = true;
                }

                goods.setCateId(NumberUtils.toLong(ExcelHelper.getValue(cells[2]).split("_")[0]));
                if (goods.getCateId() == 0 || (!goodsCateMap.containsKey(goods.getCateId()))) {
                    ExcelHelper.setError(workbook, cells[2], "请选择平台类目或平台类目不存在");
                    isError = true;
                }

                Long storeCateId = NumberUtils.toLong(ExcelHelper.getValue(cells[3]).split("_")[0]);
                if (storeCateId == 0 || (!storeCateSet.containsKey(storeCateId))) {
                    storeCateId = defaultStoreCateId;
                }
                List<Long> storeCateIds = new ArrayList<>();
                storeCateIds.add(storeCateId);
//                Long parentCateId = storeCateSet.get(storeCateId);
//                //获取店铺分类的父分类
//                if (Objects.nonNull(parentCateId) && parentCateId > 0) {
//                    storeCateIds.add(parentCateId);
//                }
                   goods.setStoreCateIds(storeCateIds);

                    if (CollectionUtils.isEmpty(goods.getStoreCateIds())){
                        ExcelHelper.setError(workbook, cells[3], "请选择平台类目或设置默认类目");
                        isError = true;
                    }

                    Long brandId = NumberUtils.toLong(ExcelHelper.getValue(cells[4]).split("_")[0]);
                    if (brandId > 0 && brandSet.contains(brandId)) {
                        goods.setBrandId(brandId);
                    }

                    //生产日期
                    String goodsDate = ExcelHelper.getValue(cells[5]);
                    if (StringUtils.isNotEmpty(goodsDate)) {
                        goods.setGoodDate(DateUtil.parseDay(goodsDate));
                        goods.setGoodsInfoBatchNo(goodsDate);
                    }
                String bzq = ExcelHelper.getValue(cells[6]);
                if (StringUtils.isNotEmpty(bzq)) {
                    Long l = new Double(bzq).longValue();
                    goods.setShelflife(l);
                }

                    String isScatteredQuantitative = ExcelHelper.getValue(cells[7]);
                    if (StringUtils.isNotEmpty(isScatteredQuantitative)) {
                            //商品分类 0,散装 1，定量
                            if (isScatteredQuantitative.equals("定量")){
                                isScatteredQuantitative="1";
                            }else if (isScatteredQuantitative.equals("散称")){
                                isScatteredQuantitative="0";
                            }else  if (isScatteredQuantitative.equals("其他")){
                                isScatteredQuantitative="2";
                            }else {
                                isScatteredQuantitative="1";
                            }

                        goods.setIsScatteredQuantitative(Integer.parseInt(isScatteredQuantitative));
                    }
                    //上下架
                    String addedFlagStr = ExcelHelper.getValue(cells[8]);

                    if ("上架".equals(addedFlagStr.trim())) {
                        goods.setAddedFlag(AddedFlag.YES.toValue());
                    } else if ("下架".equals(addedFlagStr.trim())) {
                        goods.setAddedFlag(AddedFlag.NO.toValue());
                    } else {
                        ExcelHelper.setError(workbook, cells[8], "必须在[上架、下架]范围内");
                        isError = true;
                    }


                String imageUrlStr = ExcelHelper.getValue(cells[9]);
                if (StringUtils.isNoneBlank(imageUrlStr)){
                    String[] imageUrls = imageUrlStr.split("\\|");
                    List<String> imageArr = new ArrayList<>();
                    if (imageUrls.length > 0) {
                        if (!images.containsKey(goods.getGoodsNo())) {
                            if (imageUrls.length > 10) {
                                ExcelHelper.setError(workbook, cells[9], "最多传10张图片");
                                isError = true;
                            }
                            boolean isFirst = true;
                            for (int i = 0; i < imageUrls.length; i++) {
                                if(imageUrls[i].length() > 255){
                                    ExcelHelper.setError(workbook, cells[9], "商品图片链接长度不能超过255");
                                    isError = true;
                                }
                                if (WebUtil.isImage(imageUrls[i])) {
                                    if (isFirst) {
                                        goods.setGoodsImg(imageUrls[i]);
                                        isFirst = false;
                                    }
                                    imageArr.add(imageUrls[i]);
                                }
                            }
                            images.put(goods.getGoodsNo(), imageArr);
                        }
                    }
                }


                //SPU视频链接地址
                goods.setGoodsVideo(ExcelHelper.getValue(cells[10]));
                if (StringUtils.isNotEmpty(goods.getGoodsVideo()) && !goods.getGoodsVideo().endsWith("mp4")) {
                    ExcelHelper.setError(workbook, cells[10], "视频仅支持mp4格式");
                    isError = true;
                }

                if(goods.getGoodsVideo().length() > 255){
                    ExcelHelper.setError(workbook, cells[10], "商品视频链接长度不能超过255");
                    isError = true;
                }

                String details = ExcelHelper.getValue(cells[11]);
                if (StringUtils.isNotEmpty(details)) {
                    goods.setGoodsDetail(Arrays.stream(details.split("\\|")).map(s -> {
                        if (s.startsWith("http")) {
                            return String.format("<p> <img src='%s'/></p>", s);
                        }
                        return String.format("<p>%s</p>", s);
                    }).collect(Collectors.joining()));
                }

                BatchGoodsInfoDTO goodsInfo = new BatchGoodsInfoDTO();
                goodsInfo.setIsScatteredQuantitative(goods.getIsScatteredQuantitative());
                goodsInfo.setMockGoodsId(goods.getMockGoodsId());
                goodsInfo.setMockSpecIds(new ArrayList<>());
                //goodsInfo.setMockSpecDetailIds(new ArrayList<>());
                String goodsDate1 = ExcelHelper.getValue(cells[7]);
                if (StringUtils.isNotEmpty(goodsDate1)) {
                  //  goodsInfo.setGoodDate(DateUtil.parseDay(goodsDate1));
                    goodsInfo.setGoodsInfoBatchNo(goodsDate1);
                }
                String bzq1 = ExcelHelper.getValue(cells[6]);
                if (StringUtils.isNotEmpty(bzq1)) {
                    Long l = new Double(bzq1).longValue();
                    goodsInfo.setShelflife(l);
                }
                if (StoreType.SUPPLIER.equals(type)) {
                    goodsInfo.setGoodsSource(StoreType.SUPPLIER.toValue());
                }else{
                    goodsInfo.setGoodsSource(StoreType.PROVIDER.toValue());
                }

                //SKU编码
                String skuNo = ExcelHelper.getValue(cells[12]);
                if (StringUtils.isNotEmpty(skuNo)) {
                    if (!ValidateUtil.isBetweenLen(skuNo, 1, 20)) {
                        ExcelHelper.setError(workbook, cells[12], "长度必须1-20个字");
                        isError = true;
                    } else if (!ValidateUtil.isNotChs(skuNo)) {
                        ExcelHelper.setError(workbook, cells[12], "仅允许英文、数字、特殊字符");
                        isError = true;
                    }
                } else {
                    skuNo = goodsExcelService.getSkuNo(skuNos);
                    cells[12].setCellValue(skuNo);
                }

                if (skuNos.containsKey(skuNo)) {
                    ExcelHelper.setError(workbook, cells[12], "文档中出现重复的SKU编码");
                    isError = true;
                }


                goodsInfo.setGoodsInfoNo(skuNo);

                //SKU图片
                String skuImage = ExcelHelper.getValue(cells[13]);
                if (StringUtils.isNoneBlank(skuImage)) {
                    //如果不是图片链接
                    if (!WebUtil.isImage(skuImage)) {
                        goodsInfo.setGoodsInfoImg(null);
                    } else {
                        goodsInfo.setGoodsInfoImg(skuImage);
                        if(skuImage.length() > 255){
                            ExcelHelper.setError(workbook, cells[13], "商品SKU图片链接长度不能超过255");
                            isError = true;
                        }
                    }
                } else {
                    goodsInfo.setGoodsInfoImg(null);
                }
                List<GoodsAttributeKeyDTO> goodsAttributeKeys =new ArrayList<>();

                //处理同一SPU下第一条的规格项
                String gz1 = ExcelHelper.getValue(cells[14]);
                String gzs1 = ExcelHelper.getValue(cells[15]);

                if (StringUtils.isNotEmpty(gz1)&& StringUtils.isNotEmpty(gzs1)){
                    GoodsAttributeKeyDTO goodsAttributeKeyDTO =new GoodsAttributeKeyDTO();
                    goodsAttributeKeyDTO.setAttributeName(gz1);
                    goodsAttributeKeyDTO.setGoodsAttributeValue(gzs1);
                    goodsAttributeKeys.add(goodsAttributeKeyDTO);
                    BaseResponse<GoodsAttributeListResponse> list = goodsAttributeQueryProvider.getList(GoodsAttributeQueryRequest.builder().attribute(gz1).build());
                    if (CollectionUtils.isEmpty(list.getContext().getAttributeVos())){
                        ExcelHelper.setError(workbook, cells[15], "此规格属性在系统无法找到");
                        isError = true;
                    }else {
                        goodsAttributeKeyDTO.setAttributeId(list.getContext().getAttributeVos().get(Constants.no).getAttributeId());
                    }

                }


                String gz2 = ExcelHelper.getValue(cells[16]);
                String gzs2 = ExcelHelper.getValue(cells[17]);
                if (StringUtils.isNotEmpty(gz2)&& StringUtils.isNotEmpty(gzs2)){
                    GoodsAttributeKeyDTO goodsAttributeKeyDTO =new GoodsAttributeKeyDTO();
                    goodsAttributeKeyDTO.setAttributeName(gz2);
                    goodsAttributeKeyDTO.setGoodsAttributeValue(gzs2);
                    goodsAttributeKeys.add(goodsAttributeKeyDTO);
                    BaseResponse<GoodsAttributeListResponse> list = goodsAttributeQueryProvider.getList(GoodsAttributeQueryRequest.builder().attribute(gz2).build());
                    if (CollectionUtils.isEmpty(list.getContext().getAttributeVos())){
                        ExcelHelper.setError(workbook, cells[17], "此规格属性在系统无法找到");
                        isError = true;
                    }else {
                        goodsAttributeKeyDTO.setAttributeId(list.getContext().getAttributeVos().get(Constants.no).getAttributeId());
                    }
                }
                String gz3 = ExcelHelper.getValue(cells[18]);
                String gzs3 = ExcelHelper.getValue(cells[19]);
                if (StringUtils.isNotEmpty(gz3)&& StringUtils.isNotEmpty(gzs3)){
                    GoodsAttributeKeyDTO goodsAttributeKeyDTO =new GoodsAttributeKeyDTO();
                    goodsAttributeKeyDTO.setAttributeName(gz3);
                    goodsAttributeKeyDTO.setGoodsAttributeValue(gzs3);
                    goodsAttributeKeys.add(goodsAttributeKeyDTO);
                    BaseResponse<GoodsAttributeListResponse> list = goodsAttributeQueryProvider.getList(GoodsAttributeQueryRequest.builder().attribute(gz3).build());
                    if (CollectionUtils.isEmpty(list.getContext().getAttributeVos())){
                        ExcelHelper.setError(workbook, cells[19], "此规格属性在系统无法找到");
                        isError = true;
                    }else {
                        goodsAttributeKeyDTO.setAttributeId(list.getContext().getAttributeVos().get(Constants.no).getAttributeId());
                    }
                }
                String gz4 = ExcelHelper.getValue(cells[20]);
                String gzs4 = ExcelHelper.getValue(cells[21]);
                if (StringUtils.isNotEmpty(gz4)&& StringUtils.isNotEmpty(gzs4)){
                    GoodsAttributeKeyDTO goodsAttributeKeyDTO =new GoodsAttributeKeyDTO();
                    goodsAttributeKeyDTO.setAttributeName(gz4);
                    goodsAttributeKeyDTO.setGoodsAttributeValue(gzs4);
                    goodsAttributeKeys.add(goodsAttributeKeyDTO);
                    BaseResponse<GoodsAttributeListResponse> list = goodsAttributeQueryProvider.getList(GoodsAttributeQueryRequest.builder().attribute(gz4).build());
                    if (CollectionUtils.isEmpty(list.getContext().getAttributeVos())){
                        ExcelHelper.setError(workbook, cells[21], "此规格属性在系统无法找到");
                        isError = true;
                    }else {
                        goodsAttributeKeyDTO.setAttributeId(list.getContext().getAttributeVos().get(Constants.no).getAttributeId());
                    }
                }
                String gz5 = ExcelHelper.getValue(cells[22]);
                String gzs5 = ExcelHelper.getValue(cells[23]);
                if (StringUtils.isNotEmpty(gz5)&& StringUtils.isNotEmpty(gzs5)){
                    GoodsAttributeKeyDTO goodsAttributeKeyDTO =new GoodsAttributeKeyDTO();
                    goodsAttributeKeyDTO.setAttributeName(gz5);
                    goodsAttributeKeyDTO.setGoodsAttributeValue(gzs5);
                    goodsAttributeKeys.add(goodsAttributeKeyDTO);
                    BaseResponse<GoodsAttributeListResponse> list = goodsAttributeQueryProvider.getList(GoodsAttributeQueryRequest.builder().attribute(gz5).build());
                    if (CollectionUtils.isEmpty(list.getContext().getAttributeVos())){
                        ExcelHelper.setError(workbook, cells[23], "此规格属性在系统无法找到");
                        isError = true;
                    }else {
                        goodsAttributeKeyDTO.setAttributeId(list.getContext().getAttributeVos().get(Constants.no).getAttributeId());
                    }
                }
                //加入规格
                goodsInfo.setGoodsAttributeKeys(goodsAttributeKeys);

                //库存
                String stock = ExcelHelper.getValue(cells[24]);
                if (StringUtils.isEmpty(stock)){
                    ExcelHelper.setError(workbook, cells[24], "库存不能为空");
                    isError = true;
                }else{
                    goodsInfo.setStock(StringUtils.isEmpty(stock) ? BigDecimal.ZERO : new BigDecimal(stock));

                }



                //条形码
                goodsInfo.setGoodsInfoBarcode(ExcelHelper.getValue(cells[25]));
                if (StringUtils.isNotEmpty(goodsInfo.getGoodsInfoBarcode()) && ValidateUtil.isOverLen(goodsInfo
                        .getGoodsInfoBarcode(), 20)) {
                    ExcelHelper.setError(workbook, cells[25], "长度必须0-20个字");
                    isError = true;
                }

                if (StoreType.SUPPLIER.equals(type)) {
                    //门店价
                    String marketPrice1 = ExcelHelper.getValue(cells[26]);
                    goodsInfo.setMarketPrice(StringUtils.isEmpty(marketPrice1) ? BigDecimal.ZERO : new BigDecimal
                            (marketPrice1));;
                    if (StringUtils.isNotEmpty(marketPrice1)) {
                        if (goodsInfo.getMarketPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getMarketPrice()
                                .compareTo(new BigDecimal("9999999.99")) > 0) {
                            ExcelHelper.setError(workbook, cells[26], "必须在0-9999999.99范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[26], "此项必填");
                        isError = true;
                    }
                    goodsInfo.setMarketPrice(StringUtils.isEmpty(marketPrice1) ? BigDecimal.ZERO : new BigDecimal
                            (marketPrice1));
                }else{
                    //供货价
                    String supplyPrice = ExcelHelper.getValue(cells[26]);
                    goodsInfo.setSupplyPrice(StringUtils.isEmpty(supplyPrice) ? BigDecimal.ZERO : new BigDecimal
                            (supplyPrice));
                    if (StringUtils.isNotEmpty(supplyPrice)) {
                        if (goodsInfo.getSupplyPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getSupplyPrice()
                                .compareTo(new BigDecimal("9999999.99")) > 0) {
                            ExcelHelper.setError(workbook, cells[26], "必须在0-9999999.99范围内");
                            isError = true;
                        }
                    } else {
                        ExcelHelper.setError(workbook, cells[26], "此项必填");
                        isError = true;
                    }

                }



                goodsInfo.setVipPrice(goodsInfo.getMarketPrice());
                //成本价
                String costPrice = ExcelHelper.getValue(cells[27]);
                if (StringUtils.isEmpty(costPrice)){
                    goodsInfo.setCostPrice(goodsInfo.getMarketPrice());
                }else{
                    goodsInfo.setCostPrice(StringUtils.isEmpty(costPrice) ? BigDecimal.ZERO : new BigDecimal
                            (costPrice));
                }
                if (StringUtils.isNotEmpty(costPrice)) {
                    if (goodsInfo.getCostPrice().compareTo(BigDecimal.ZERO) < 0 || goodsInfo.getCostPrice()
                            .compareTo(new BigDecimal("9999999.99")) > 0) {
                        ExcelHelper.setError(workbook, cells[27], "必须在0-9999999.99范围内");
                        isError = true;
                    }
                }


                //上下架
                String addedFlagStr1 = ExcelHelper.getValue(cells[28]);

                if ("上架".equals(addedFlagStr1.trim())) {
                    goodsInfo.setAddedFlag(AddedFlag.YES.toValue());
                } else if ("下架".equals(addedFlagStr1.trim())) {
                    goodsInfo.setAddedFlag(AddedFlag.NO.toValue());
                } else {
                    ExcelHelper.setError(workbook, cells[28], "必须在[上架、下架]范围内");
                    isError = true;
                }


                //重量
                String weightStr = ExcelHelper.getValue(cells[29]);
                if (StringUtils.isNotEmpty(weightStr)) {
                    goodsInfo.setGoodsInfoWeight(new BigDecimal(weightStr).setScale(3, BigDecimal.ROUND_HALF_UP));
                    if (goodsInfo.getGoodsInfoWeight().compareTo(new BigDecimal("0.001")) < 0 || goodsInfo.getGoodsInfoWeight()
                            .compareTo(new BigDecimal("9999.999")) > 0) {
                        ExcelHelper.setError(workbook, cells[29], "必须在0.001-9999.999范围内");
                        isError = true;
                    }
                } else {
                    ExcelHelper.setError(workbook, cells[29], "此项必填");
                    isError = true;
                }

                //体积
                String cubageStr = ExcelHelper.getValue(cells[30]);
                if (StringUtils.isNotEmpty(cubageStr)) {
                    goodsInfo.setGoodsInfoCubage(new BigDecimal(cubageStr).setScale(6, BigDecimal.ROUND_HALF_UP));
                    if (goodsInfo.getGoodsInfoCubage().compareTo(new BigDecimal("0.000001")) < 0 || goodsInfo.getGoodsInfoCubage()
                            .compareTo(new BigDecimal("999.999999")) > 0) {
                        ExcelHelper.setError(workbook, cells[30], "必须在0.000001-999.999999范围内");
                        isError = true;
                    }
                } else {
                    ExcelHelper.setError(workbook, cells[30], "此项必填");
                    isError = true;
                }

                //销售单位
                String goodsInfoUnit = ExcelHelper.getValue(cells[31]);
                if (StringUtils.isNotEmpty(goodsInfoUnit)) {
                    goodsInfo.setGoodsInfoUnit(goodsInfoUnit);
                } else {
                    ExcelHelper.setError(workbook, cells[31], "此项必填");
                    isError = true;
                }
                //销售步长
                String addStep = ExcelHelper.getValue(cells[32]);
                if (StringUtils.isNotEmpty(addStep)) {
                    goodsInfo.setAddStep(new BigDecimal(addStep).setScale(2, BigDecimal.ROUND_HALF_UP));
                    if (goodsInfo.getAddStep().compareTo(new BigDecimal("0.000001")) < 0 || goodsInfo.getAddStep()
                            .compareTo(new BigDecimal("999.999999")) > 0) {
                        ExcelHelper.setError(workbook, cells[32], "必须在0.000001-999.999999范围内");
                        isError = true;
                    }
                } else {
                    ExcelHelper.setError(workbook, cells[32], "此项必填");
                    isError = true;
                }

                //销售单位
                String deGoodsInfoUnit = ExcelHelper.getValue(cells[33]);
                if (StringUtils.isNotEmpty(deGoodsInfoUnit)) {
                    goodsInfo.setDevanningUnit(deGoodsInfoUnit);
                } else {
                    ExcelHelper.setError(workbook, cells[33], "此项必填");
                    isError = true;
                }
                //主Hsu
                String host = ExcelHelper.getValue(cells[34]);
                if (StringUtils.isNotEmpty(host)) {
                    String[] s = host.split("_");
                    goodsInfo.setHostSku(Integer.parseInt(s[0]));
                } else {
                    goodsInfo.setHostSku(0);
                }
                // 排序
                final String setNumStr = ExcelHelper.getValue(cells[35]);
                if (StringUtils.isNotBlank(setNumStr) && ValidateUtil.isInteger(setNumStr)) {
                    goods.setStoreGoodsSeqNum(Integer.parseInt(setNumStr));
                }

                //商品名称
                if (StringUtils.isBlank(goods.getGoodsName())) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                } else if (!ValidateUtil.isBetweenLen(goods.getGoodsName(), 1, 40)) {
                    ExcelHelper.setError(workbook, cells[0], "长度必须1-40个字");
                    isError = true;
                } else if (ValidateUtil.containsEmoji(goods.getGoodsName())) {
                    ExcelHelper.setError(workbook, cells[0], "含有非法字符");
                    isError = true;
                }

                goodses.put(goods.getGoodsNo(), goods);

                skus.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(goodsInfo)), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });

                spuNos.merge(goods.getGoodsNo(), new ArrayList(Arrays.asList(cells[1])), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });

                skuNos.merge(goodsInfo.getGoodsInfoNo(), new ArrayList(Arrays.asList(cells[14])), (s1, s2) -> {
                    s1.addAll(s2);
                    return s1;
                });
            }

            List<GoodsVO> goodsList = goodsQueryProvider.listByCondition(GoodsByConditionRequest.builder()
                    .delFlag(DeleteFlag.NO.toValue()).goodsNos(new ArrayList<>(spuNos.keySet())).build()).getContext().getGoodsVOList();

            //设置重复错误提示
            if (CollectionUtils.isNotEmpty(goodsList)) {
                goodsList.forEach(goods -> {
                    spuNos.get(goods.getGoodsNo()).forEach(cell -> {
                        //为单元格设置重复错误提示
                        ExcelHelper.setError(workbook, cell, "该编码重复");
                    });
                });
                isError = true;
            }

            List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder().delFlag
                            (DeleteFlag.NO.toValue()).goodsInfoNos(new ArrayList<>(skuNos.keySet()))
                    .build()).getContext().getGoodsInfos();

            //设置重复错误提示
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                goodsInfos.forEach(sku -> {
                    skuNos.get(sku.getGoodsInfoNo()).forEach(cell -> {
                        //为单元格设置重复错误提示
                        ExcelHelper.setError(workbook, cell, "该编码重复");
                    });
                });
                isError = true;
            }
            if (isError) {

                String fileKey = "error"+goodsRequest.getCompanyInfoId() + goodsRequest.getStoreId() + "/" + goodsRequest.getUserId()+ ".xls";
                // 上传

                    String contentType = "text/plain";
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    workbook.write(bos);
                    //Workbook 转 byte[]
                    byte[] barray = bos.toByteArray();
                    //byte[] 转 InputStream
                    InputStream is = new ByteArrayInputStream(barray);
                    //InputStream 转 MultipartFile
                    MultipartFile multipartFile = new MockMultipartFile(fileKey, fileKey, contentType, is);
                    String  resourceUrl = yunServiceProvider.uploadExclFile(YunUploadVideoResourceRequest.builder()
                            .storeId(goodsRequest.getStoreId())
                            .companyInfoId(goodsRequest.getCompanyInfoId())
                            .resourceType(ResourceType.EXCEL)
                            .resourceName(multipartFile.getOriginalFilename())
                            .resourceKey(fileKey)
                            .content(multipartFile.getBytes())
                            .build()).getContext();
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{resourceUrl});


            }

            //指定图片与goods的mockGoodsId关系
            List<BatchGoodsImageDTO> imageDTOList = new ArrayList<>();
            goodses.values().forEach(goods -> {
                if (images.containsKey(goods.getGoodsNo())) {
                    imageDTOList.addAll(images.get(goods.getGoodsNo()).stream().filter(StringUtils::isNotEmpty).map(s -> {
                        BatchGoodsImageDTO image = new BatchGoodsImageDTO();
                        image.setMockGoodsId(goods.getMockGoodsId());
                        image.setArtworkUrl(s);
                        return image;
                    }).collect(Collectors.toList()));
                }
            });

            //批量保存
            return goodsCommonProvider.storeBatchAdd(GoodsCommonBatchAddRequest.builder()
                    .goodsList(new ArrayList<>(goodses.values()))
                    .goodsInfoList(skus.values().stream().flatMap(Collection::stream).collect(Collectors.toList()))
                    .imageList(imageDTOList).build()).getContext().getSkuNoList();

        } catch (SbcRuntimeException e) {
            log.error("商品导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("商品导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }


}