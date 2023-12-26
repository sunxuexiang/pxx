package com.wanmi.sbc.goods.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.es.elastic.EsBulkGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.provider.goods.*;
import com.wanmi.sbc.goods.api.request.goods.GoodsBatchModifySeqNumRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySeqNumRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.request.GoodsSortImportExcelRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品排序Excel处理服务
 *
 * @author yang
 * @since 2020/12/31
 */
@Slf4j
@Service
public class GoodsSortImportExcelService {

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private RetailGoodsProvider retailGoodsProvider;

    @Autowired
    private BulkGoodsProvider bulkGoodsProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private RetailGoodsQueryProvider retailGoodsQueryProvider;

    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private EsBulkGoodsInfoElasticService esBulkGoodsInfoElasticService;

    /**
     * 导入模板
     *
     * @author yang
     * @since 2019/5/21
     */
    @Transactional
    public void implGoods(GoodsSortImportExcelRequest goodsRequest) {
        String ext = goodsRequest.getExt();
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(goodsRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        try (Workbook workbook = WorkbookFactory.create(file)) {
            //创建Workbook工作薄对象，表示整个excel
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
            List<GoodsModifySeqNumRequest> modifySeqNumRequests = new ArrayList<>();

            List<Map<String, Object>> goodsErps = new ArrayList<>();
            List<Integer> sorts = new ArrayList<>();

            // 查询erp不为空的商品
            List<GoodsVO> goodsVOList = goodsQueryProvider.listByErp().getContext().getGoodsVOList();
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

                GoodsModifySeqNumRequest goodsModifySeqNumRequest = new GoodsModifySeqNumRequest();
                String goodsErp = ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(goodsErp)) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                } else {
                    // 验证数据是否重复
                    List<Map<String, Object>> collect = goodsErps.stream()
                            .filter(map -> goodsErp.equals(map.get("erp")))
                            .collect(Collectors.toList());
                    if (collect.size() > 1) {
                        ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                        isError = true;
                    }
                    GoodsVO goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo())).findFirst().orElse(null);
                    if (collect.size() > 0) {
                        goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo()) && x.getGoodsInfoType() != collect.get(0).get("goodsInfoType")).findFirst().orElse(null);
                        if (Objects.isNull(goodsVO)) {
                            ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                            isError = true;
                        }
                    }
                    if (Objects.isNull(goodsVO)) {
                        ExcelHelper.setError(workbook, cells[0], "erp编码不存在");
                        isError = true;
                    } else {
                        Map<String, Object> erpMap = new HashMap<>();
                        erpMap.put("erp", goodsErp);
                        erpMap.put("goodsInfoType", goodsVO.getGoodsInfoType());
                        goodsErps.add(erpMap);
//                        if (Objects.isNull(goodsVO.getBrandId())) {
//                            ExcelHelper.setError(workbook, cells[0], "商品未关联品牌");
//                            isError = true;
//                        } else if (Objects.isNull(goodsVO.getBrandSeqNum())) {
//                            ExcelHelper.setError(workbook, cells[0], "品牌排序不存在");
//                            isError = true;
//                        }
                        // 商品排序
                        String sort = ExcelHelper.getValue(cells[1]);
                        if (StringUtils.isBlank(sort)) {
                            ExcelHelper.setError(workbook, cells[1], "此项必填");
                            isError = true;
                        } else {
                            Integer goodsSeqNum = Integer.valueOf(ExcelHelper.getValue(cells[1]).split("\\.")[0]);
                            if (goodsSeqNum != 0) {
                                // 验证数据重复
                                List<Integer> sortList = sorts.stream()
                                        .filter(goodsSeqNum::equals)
                                        .collect(Collectors.toList());
                                if (sortList.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "排序数据重复导入");
                                    isError = true;
                                }
                                sorts.add(goodsSeqNum);
                                GoodsVO finalGoodsVO = goodsVO;
                                List<GoodsVO> filterGoodsVO = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                !vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (filterGoodsVO.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "已有商品是此排序");
                                    isError = true;
                                }
                                // 商品及排序没有改变不处理
                                List<GoodsVO> goodsVOS = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (goodsVOS.size() > 0) {
                                    continue;
                                }
                                goodsModifySeqNumRequest.setGoodsSeqNum(goodsSeqNum);
                            } else {
                                goodsModifySeqNumRequest.setGoodsSeqNum(null);
                            }
                        }
                        goodsModifySeqNumRequest.setGoodsId(goodsVO.getGoodsId());
                    }
                }

                modifySeqNumRequests.add(goodsModifySeqNumRequest);
            }

            if (isError) {
                errorExcel(goodsRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{ext});
            }
            goodsProvider.modifyBatchGoodsSeqNum(GoodsBatchModifySeqNumRequest.builder()
                    .batchRequest(modifySeqNumRequests)
                    .build());
            // 刷es
            List<GoodsVO> goodsVOS = modifySeqNumRequests.stream()
                    .map(request -> KsBeanUtil.convert(request, GoodsVO.class))
                    .collect(Collectors.toList());
            if (goodsVOS.size() > 0) {
                esGoodsInfoElasticService.batchGoodsSeqNum(goodsVOS);
            }
        } catch (SbcRuntimeException e) {
            log.error("商品排序导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("商品排序导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }

    }

    /**
     * 导入模板
     *
     * @author yang
     * @since 2019/5/21
     */
    @Transactional
    public void implRetailGoods(GoodsSortImportExcelRequest goodsRequest) {
        String ext = goodsRequest.getExt();
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(goodsRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        try (Workbook workbook = WorkbookFactory.create(file)) {
            //创建Workbook工作薄对象，表示整个excel
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
            List<GoodsModifySeqNumRequest> modifySeqNumRequests = new ArrayList<>();

            List<Map<String, Object>> goodsErps = new ArrayList<>();
            List<Integer> sorts = new ArrayList<>();

            // 查询erp不为空的商品
            List<GoodsVO> goodsVOList = retailGoodsQueryProvider.listByErp().getContext().getGoodsVOList();
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

                GoodsModifySeqNumRequest goodsModifySeqNumRequest = new GoodsModifySeqNumRequest();
                String goodsErp = ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(goodsErp)) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                } else {
                    // 验证数据是否重复
                    List<Map<String, Object>> collect = goodsErps.stream()
                            .filter(map -> goodsErp.equals(map.get("erp")))
                            .collect(Collectors.toList());
                    if (collect.size() > 1) {
                        ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                        isError = true;
                    }
                    GoodsVO goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo())).findFirst().orElse(null);
                    if (collect.size() > 0) {
                        goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo()) && x.getGoodsInfoType() != collect.get(0).get("goodsInfoType")).findFirst().orElse(null);
                        if (Objects.isNull(goodsVO)) {
                            ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                            isError = true;
                        }
                    }
                    if (Objects.isNull(goodsVO)) {
                        ExcelHelper.setError(workbook, cells[0], "erp编码不存在");
                        isError = true;
                    } else {
                        Map<String, Object> erpMap = new HashMap<>();
                        erpMap.put("erp", goodsErp);
                        erpMap.put("goodsInfoType", goodsVO.getGoodsInfoType());
                        goodsErps.add(erpMap);
//                        if (Objects.isNull(goodsVO.getBrandId())) {
//                            ExcelHelper.setError(workbook, cells[0], "商品未关联品牌");
//                            isError = true;
//                        } else if (Objects.isNull(goodsVO.getBrandSeqNum())) {
//                            ExcelHelper.setError(workbook, cells[0], "品牌排序不存在");
//                            isError = true;
//                        }
                        // 商品排序
                        String sort = ExcelHelper.getValue(cells[1]);
                        if (StringUtils.isBlank(sort)) {
                            ExcelHelper.setError(workbook, cells[1], "此项必填");
                            isError = true;
                        } else {
                            Integer goodsSeqNum = Integer.valueOf(ExcelHelper.getValue(cells[1]).split("\\.")[0]);
                            if (goodsSeqNum != 0) {
                                // 验证数据重复
                                List<Integer> sortList = sorts.stream()
                                        .filter(goodsSeqNum::equals)
                                        .collect(Collectors.toList());
                                if (sortList.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "排序数据重复导入");
                                    isError = true;
                                }
                                sorts.add(goodsSeqNum);
                                GoodsVO finalGoodsVO = goodsVO;
                                List<GoodsVO> filterGoodsVO = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                !vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (filterGoodsVO.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "已有商品是此排序");
                                    isError = true;
                                }
                                // 商品及排序没有改变不处理
                                List<GoodsVO> goodsVOS = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (goodsVOS.size() > 0) {
                                    continue;
                                }
                                goodsModifySeqNumRequest.setGoodsSeqNum(goodsSeqNum);
                            } else {
                                goodsModifySeqNumRequest.setGoodsSeqNum(null);
                            }
                        }
                        goodsModifySeqNumRequest.setGoodsId(goodsVO.getGoodsId());
                    }
                }

                modifySeqNumRequests.add(goodsModifySeqNumRequest);
            }

            if (isError) {
                errorExcel(goodsRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{ext});
            }
            retailGoodsProvider.modifyBatchGoodsSeqNum(GoodsBatchModifySeqNumRequest.builder()
                    .batchRequest(modifySeqNumRequests)
                    .build());
            // 刷es
            List<GoodsVO> goodsVOS = modifySeqNumRequests.stream()
                    .map(request -> KsBeanUtil.convert(request, GoodsVO.class))
                    .collect(Collectors.toList());
            if (goodsVOS.size() > 0) {
                esRetailGoodsInfoElasticService.batchGoodsSeqNum(goodsVOS);
            }
        } catch (SbcRuntimeException e) {
            log.error("散批商品排序导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("散批商品排序导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }

    }


    /**
     * 导入模板
     *
     * @author yang
     * @since 2019/5/21
     */
    @Transactional
    public void implBulkGoods(GoodsSortImportExcelRequest goodsRequest) {
        String ext = goodsRequest.getExt();
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/").concat(goodsRequest.getUserId()).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        try (Workbook workbook = WorkbookFactory.create(file)) {
            //创建Workbook工作薄对象，表示整个excel
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
            List<GoodsModifySeqNumRequest> modifySeqNumRequests = new ArrayList<>();

            List<Map<String, Object>> goodsErps = new ArrayList<>();
            List<Integer> sorts = new ArrayList<>();

            // 查询erp不为空的商品
            List<GoodsVO> goodsVOList = bulkGoodsQueryProvider.listByErp().getContext().getGoodsVOList();
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

                GoodsModifySeqNumRequest goodsModifySeqNumRequest = new GoodsModifySeqNumRequest();
                String goodsErp = ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(goodsErp)) {
                    ExcelHelper.setError(workbook, cells[0], "此项必填");
                    isError = true;
                } else {
                    // 验证数据是否重复
                    List<Map<String, Object>> collect = goodsErps.stream()
                            .filter(map -> goodsErp.equals(map.get("erp")))
                            .collect(Collectors.toList());
                    if (collect.size() > 1) {
                        ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                        isError = true;
                    }
                    GoodsVO goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo())).findFirst().orElse(null);
                    if (collect.size() > 0) {
                        goodsVO = goodsVOList.stream().filter(x -> goodsErp.equals(x.getErpNo()) && x.getGoodsInfoType() != collect.get(0).get("goodsInfoType")).findFirst().orElse(null);
                        if (Objects.isNull(goodsVO)) {
                            ExcelHelper.setError(workbook, cells[0], "商品数据重复导入");
                            isError = true;
                        }
                    }
                    if (Objects.isNull(goodsVO)) {
                        ExcelHelper.setError(workbook, cells[0], "erp编码不存在");
                        isError = true;
                    } else {
                        Map<String, Object> erpMap = new HashMap<>();
                        erpMap.put("erp", goodsErp);
                        erpMap.put("goodsInfoType", goodsVO.getGoodsInfoType());
                        goodsErps.add(erpMap);
//                        if (Objects.isNull(goodsVO.getBrandId())) {
//                            ExcelHelper.setError(workbook, cells[0], "商品未关联品牌");
//                            isError = true;
//                        } else if (Objects.isNull(goodsVO.getBrandSeqNum())) {
//                            ExcelHelper.setError(workbook, cells[0], "品牌排序不存在");
//                            isError = true;
//                        }
                        // 商品排序
                        String sort = ExcelHelper.getValue(cells[1]);
                        if (StringUtils.isBlank(sort)) {
                            ExcelHelper.setError(workbook, cells[1], "此项必填");
                            isError = true;
                        } else {
                            Integer goodsSeqNum = Integer.valueOf(ExcelHelper.getValue(cells[1]).split("\\.")[0]);
                            if (goodsSeqNum != 0) {
                                // 验证数据重复
                                List<Integer> sortList = sorts.stream()
                                        .filter(goodsSeqNum::equals)
                                        .collect(Collectors.toList());
                                if (sortList.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "排序数据重复导入");
                                    isError = true;
                                }
                                sorts.add(goodsSeqNum);
                                GoodsVO finalGoodsVO = goodsVO;
                                List<GoodsVO> filterGoodsVO = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                !vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (filterGoodsVO.size() > 0) {
                                    ExcelHelper.setError(workbook, cells[1], "已有商品是此排序");
                                    isError = true;
                                }
                                // 商品及排序没有改变不处理
                                List<GoodsVO> goodsVOS = goodsVOList.stream()
                                        .filter(vo -> goodsSeqNum.equals(vo.getGoodsSeqNum()) &&
                                                vo.getGoodsId().equals(finalGoodsVO.getGoodsId()))
                                        .collect(Collectors.toList());
                                if (goodsVOS.size() > 0) {
                                    continue;
                                }
                                goodsModifySeqNumRequest.setGoodsSeqNum(goodsSeqNum);
                            } else {
                                goodsModifySeqNumRequest.setGoodsSeqNum(null);
                            }
                        }
                        goodsModifySeqNumRequest.setGoodsId(goodsVO.getGoodsId());
                    }
                }

                modifySeqNumRequests.add(goodsModifySeqNumRequest);
            }

            if (isError) {
                errorExcel(goodsRequest.getUserId().concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{ext});
            }
            bulkGoodsProvider.modifyBatchGoodsSeqNum(GoodsBatchModifySeqNumRequest.builder()
                    .batchRequest(modifySeqNumRequests)
                    .build());
            // 刷es
            List<GoodsVO> goodsVOS = modifySeqNumRequests.stream()
                    .map(request -> KsBeanUtil.convert(request, GoodsVO.class))
                    .collect(Collectors.toList());
            if (goodsVOS.size() > 0) {
                esBulkGoodsInfoElasticService.batchGoodsSeqNum(goodsVOS);
            }
        } catch (SbcRuntimeException e) {
            log.error("散批商品排序导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("散批商品排序导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }

    }



    /**
     * 下载Excel错误文档
     *
     * @param userId 用户Id
     * @param ext    文件扩展名
     */
    public void downErrExcel(String userId, String ext) {
        //图片存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
        File picSaveFile = new File(filePath);
        FileInputStream is = null;
        ServletOutputStream os = null;
        try {
            if (picSaveFile.exists()) {
                is = new FileInputStream(picSaveFile);
                os = HttpUtil.getResponse().getOutputStream();
                String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));

                byte b[] = new byte[1024];
                //读取文件，存入字节数组b，返回读取到的字符数，存入read,默认每次将b数组装满
                int read = is.read(b);
                while (read != -1) {
                    os.write(b, 0, read);
                    read = is.read(b);
                }
                HttpUtil.getResponse().flushBuffer();
            }
        } catch (Exception e) {
            log.error("下载EXCEL文件异常->", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("下载EXCEL文件关闭IO失败->", e);
                }
            }
        }

    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @param userId 操作员id
     * @return 文件格式
     */
    public String upload(MultipartFile file, String userId) {
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

        //上传存储地址
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.EXCEL_DIR).concat("/");
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception e) {
                log.error("创建文件路径失败->".concat(t_realPath), e);
                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
            }
        }

        String newFileName = userId.concat(".").concat(fileExt);
        File newFile = new File(t_realPath.concat(newFileName));
        try {
            newFile.deleteOnExit();
            file.transferTo(newFile);
        } catch (IOException e) {
            log.error("上传Excel文件失败->".concat(newFile.getPath()), e);
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        return fileExt;
    }

    /**
     * EXCEL错误文件-本地生成
     *
     * @param newFileName 新文件名
     * @param wk          Excel对象
     * @return 新文件名
     * @throws SbcRuntimeException
     */
    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        //图片存储地址
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_EXCEL_DIR).concat("/");

        // 根据真实路径创建目录文件
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception e) {
                log.error("创建文件路径失败->".concat(t_realPath), e);
                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
            }
        }

        //获取扩展名
        File newFile = new File(t_realPath.concat(newFileName));
        if (newFile.exists()) {
            newFile.delete();
        }
        FileOutputStream fos = null;
        try {
            newFile.createNewFile();
            fos = new FileOutputStream(newFile);
            wk.write(fos);
            return newFileName;
        } catch (IOException e) {
            log.error("生成文件失败->".concat(t_realPath.concat(newFileName)), e);
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), e);
                }
            }
        }
    }

    /**
     * 验证EXCEL
     *
     * @param workbook
     */
    public void checkExcel(Workbook workbook) {
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row row = sheet1.getRow(0);
            if (!(row.getCell(0).getStringCellValue().contains("ERP编码"))) {
                throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
            }
        } catch (Exception e) {
            throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
        }
    }
}