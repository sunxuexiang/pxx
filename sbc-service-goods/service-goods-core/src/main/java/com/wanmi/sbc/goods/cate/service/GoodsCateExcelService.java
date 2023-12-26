package com.wanmi.sbc.goods.cate.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.ValidateUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.request.GoodsCateSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 14:11 2018-12-12
 * @Description: 商品类目导入
 */
@Slf4j
@Service
public class GoodsCateExcelService {

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Transactional
    public Boolean importGoodsCate(String userId, String ext) {
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat("excel").concat("/").concat("goodsCate").concat("/").concat(userId).concat(".").concat(ext);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new SbcRuntimeException("K-000011");
        } else if (file.length() > (long) (Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024)) {
            throw new SbcRuntimeException("K-030403", new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        } else {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                Object workbook;
                if ("xls".equalsIgnoreCase(ext)) {
                    workbook = new HSSFWorkbook(is);
                } else {
                    if (!"xlsx".equalsIgnoreCase(ext)) {
                        throw new SbcRuntimeException("K-030402");
                    }

                    workbook = new XSSFWorkbook(is);
                }

                Sheet sheet = ((Workbook) workbook).getSheetAt(0);
                if (sheet == null) {
                    throw new SbcRuntimeException("K-030405");
                } else {
                    this.checkExcel((Workbook) workbook);
                    int firstRowNum = sheet.getFirstRowNum();
                    int lastRowNum = sheet.getLastRowNum();
                    if (lastRowNum < 1) {
                        throw new SbcRuntimeException("K-030405");
                    } else {
                        List<GoodsCate> goodsCateList = goodsCateRepository.findAll(GoodsCateQueryRequest.builder().delFlag(DeleteFlag.NO.toValue()).build().getWhereCriteria());
                        if (CollectionUtils.isNotEmpty(goodsCateList)) {
                            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                        }
                        int maxCell = 6;
                        long cateId = 1;
                        Boolean isError = false;
                        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; ++rowNum) {
                            Row row = sheet.getRow(rowNum);
                            if (row != null) {
                                Cell[] cells = new Cell[maxCell];
                                for (int i = 0; i < maxCell; i++) {
                                    Cell cell = row.getCell(i);
                                    if (cell == null) {
                                        cell = row.createCell(i);
                                    }
                                    cells[i] = cell;
                                }
                                // 一级类目
                                GoodsCate goodsCate = new GoodsCate();
                                isError = this.checkCells((Workbook) workbook, cells, 0, goodsCate, isError);
                                isError = this.checkCells((Workbook) workbook, cells, 1, goodsCate, isError);
                                goodsCate.setCateId(cateId);
                                goodsCate.setCateParentId(0L);
                                goodsCateList.stream().filter(cate -> cate.getCateParentId().intValue() == 0 && StringUtils.equals(goodsCate.getCateName(), cate.getCateName())).
                                        findFirst().ifPresent(cate -> {
                                    goodsCate.setCateId(null);
                                });
                                if (goodsCate.getCateId() != null) {
                                    goodsCateList.add(goodsCate);
                                    cateId++;
                                }

                                //二级类目
                                GoodsCate goodsCate1 = new GoodsCate();
                                isError = this.checkCells((Workbook) workbook, cells, 2, goodsCate1, isError);
                                isError = this.checkCells((Workbook) workbook, cells, 3, goodsCate1, isError);
                                GoodsCate parentGoodsCate = goodsCate.getCateId() == null
                                        ? goodsCateList.stream().filter(cate -> cate.getCateParentId().intValue() == 0
                                        && StringUtils.equals(cate.getCateName(), goodsCate.getCateName())).findFirst().get()
                                        : goodsCate;
                                goodsCate1.setCateId(cateId);
                                goodsCate1.setCateParentId(parentGoodsCate.getCateId());
                                goodsCateList.stream().filter(cate -> cate.getCateParentId().equals(parentGoodsCate.getCateId()) && StringUtils.equals(goodsCate1.getCateName(), cate.getCateName())).
                                        findFirst().ifPresent(cate -> {
                                    goodsCate1.setCateId(null);
                                });
                                if (goodsCate1.getCateId() != null) {
                                    goodsCateList.add(goodsCate1);
                                    cateId++;
                                }
                                // 三级类目
                                GoodsCate goodsCate2 = new GoodsCate();
                                isError = this.checkCells((Workbook) workbook, cells, 4, goodsCate2, isError);
                                isError = this.checkCells((Workbook) workbook, cells, 5, goodsCate2, isError);
                                GoodsCate parentGoodsCate1 = goodsCate1.getCateId() == null
                                        ? goodsCateList.stream().filter(cate -> cate.getCateParentId().equals(parentGoodsCate.getCateId())
                                        && StringUtils.equals(cate.getCateName(), goodsCate1.getCateName())).findFirst().get()
                                        : goodsCate1;
                                goodsCate2.setCateId(cateId);
                                goodsCate2.setCateParentId(parentGoodsCate1.getCateId());
                                goodsCateList.stream().filter(cate -> cate.getCateParentId().equals(parentGoodsCate1.getCateId()) && StringUtils.equals(goodsCate2.getCateName(), cate.getCateName()))
                                        .findFirst().ifPresent(cate -> goodsCate2.setCateId(null));
                                if (goodsCate2.getCateId() != null) {
                                    goodsCateList.add(goodsCate2);
                                    cateId++;
                                }

                            }
                        }
                        //上传文件有错误内容
                        if (isError) {
                            this.errorExcel(userId.concat(".").concat(ext), (Workbook) workbook);
                            throw new SbcRuntimeException("K-030404", new Object[]{ext});
                        }
                        //处理层级结构
                        List<GoodsCate> list = goodsCateList.stream().filter(cate -> cate.getCateParentId().intValue() == 0)
                                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(c -> c.getCateName()))), ArrayList::new)).stream().map(cate -> {
                                    cate.setGoodsCateList(goodsCateList.stream().filter(c -> c.getCateParentId().equals(cate.getCateId()))
                                            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(c -> c.getCateName()))), ArrayList::new)).stream().map(c -> {
                                                c.setGoodsCateList(goodsCateList.stream().filter(c1 -> c1.getCateParentId().equals(c.getCateId())).collect(Collectors.toList()));
                                                return c;
                                            }).collect(Collectors.toList()));
                                    return cate;
                                }).collect(Collectors.toList());
                        //保存类目
                        list.forEach(cate -> {
                            GoodsCateSaveRequest saveRequest = new GoodsCateSaveRequest();
                            saveRequest.setGoodsCate(cate);
                            GoodsCateQueryRequest goodsCateQueryRequest = GoodsCateQueryRequest.builder().delFlag(DeleteFlag.NO.toValue()).cateParentId(0L).cateName(cate.getCateName()).build();
                            GoodsCate newGoodsCate = goodsCateRepository.count(goodsCateQueryRequest.getWhereCriteria()) > 0
                                    ? goodsCateRepository.findOne(goodsCateQueryRequest.getWhereCriteria()).orElse(null)
                                    : goodsCateService.add(saveRequest);
                            if (CollectionUtils.isNotEmpty(cate.getGoodsCateList())) {
                                cate.getGoodsCateList().forEach(cate1 -> {
                                    cate1.setCateParentId(newGoodsCate.getCateId());
                                    GoodsCateSaveRequest saveRequest1 = new GoodsCateSaveRequest();
                                    saveRequest1.setGoodsCate(cate1);
                                    GoodsCateQueryRequest goodsCateQueryRequest1 = GoodsCateQueryRequest.builder().delFlag(DeleteFlag.NO.toValue())
                                            .cateParentId(newGoodsCate.getCateId()).cateName(cate1.getCateName()).build();
                                    GoodsCate goodsCate1 = goodsCateRepository.count(goodsCateQueryRequest1.getWhereCriteria()) > 0
                                            ? goodsCateRepository.findOne(goodsCateQueryRequest1.getWhereCriteria()).orElse(null)
                                            : goodsCateService.add(saveRequest1);
                                    if (CollectionUtils.isNotEmpty(cate1.getGoodsCateList())) {
                                        cate1.getGoodsCateList().forEach(cate2 -> {
                                            cate2.setCateParentId(goodsCate1.getCateId());
                                            GoodsCateSaveRequest saveRequest2 = new GoodsCateSaveRequest();
                                            saveRequest2.setGoodsCate(cate2);
                                            goodsCateService.add(saveRequest2);
                                        });
                                    }
                                });
                            }

                        });

                    }
                }
            } catch (SbcRuntimeException var52) {
                log.error("商品类目导入异常", var52);
                throw var52;
            } catch (Exception var53) {
                log.error("商品类目导入异常", var53);
                throw new SbcRuntimeException("K-000001", var53);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var51) {
                        log.error("商品类目导入关闭IO异常", var51);
                    }
                }

            }
        }
        return true;
    }

    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat("err_excel").concat("/").concat("goodaCate").concat("/");
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception var18) {
                log.error("创建文件路径失败->".concat(t_realPath), var18);
                throw new SbcRuntimeException("K-000011");
            }
        }
        System.err.println("--------------------------->" + t_realPath.concat(newFileName));
        File newFile = new File(t_realPath.concat(newFileName));
        if (newFile.exists()) {
            newFile.delete();
        }

        FileOutputStream fos = null;

        String var7;
        try {
            newFile.createNewFile();
            fos = new FileOutputStream(newFile);
            wk.write(fos);
            var7 = newFileName;
        } catch (IOException var17) {
            log.error("生成文件失败->".concat(t_realPath.concat(newFileName)), var17);
            throw new SbcRuntimeException("K-000011");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException var16) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), var16);
                }
            }

        }

        return var7;
    }


    public void checkExcel(Workbook workbook) {
        try {
            Sheet sheet1 = workbook.getSheetAt(0);
            Row row = sheet1.getRow(0);
            Sheet sheet2 = workbook.getSheetAt(1);
            if (!row.getCell(0).getStringCellValue().contains("一级类目名称")) {
                throw new SbcRuntimeException("K-030406");
            }
        } catch (Exception var5) {
            throw new SbcRuntimeException("K-030406");
        }
    }

    private boolean checkCells(Workbook workbook, Cell[] cells, int num, GoodsCate goodsCate, boolean isError) {
        if (goodsCate.getCateName() == null) {
            Double grade = Math.ceil((double) (num + 1) / 2);
            goodsCate.setCateGrade(grade.intValue());
        }
        if (num == 0 || num == 2 || num == 4) {
            if (StringUtils.isBlank(ExcelHelper.getValue(cells[num]))) {
                isError = true;
                ExcelHelper.setError((Workbook) workbook, cells[num], "此项必填");
            } else if (ExcelHelper.getValue(cells[num]).trim().length() > 20) {
                isError = true;
                ExcelHelper.setError((Workbook) workbook, cells[num], "长度必须1-20个字");
            } else if (ValidateUtil.containsEmoji(ExcelHelper.getValue(cells[num]))) {
                isError = true;
                ExcelHelper.setError((Workbook) workbook, cells[num], "含有非法字符");
            }
            goodsCate.setCateName(ExcelHelper.getValue(cells[num]).trim());
        } else {
            if (StringUtils.isNotBlank(ExcelHelper.getValue(cells[num]))) {
                BigDecimal cateRate = null;
                try {
                    cateRate = new BigDecimal(ExcelHelper.getValue(cells[num]).trim());
                    if (cateRate.compareTo(new BigDecimal("0")) == -1 || cateRate.compareTo(new BigDecimal("100")) == 1) {
                        isError = true;
                        ExcelHelper.setError((Workbook) workbook, cells[num], "请填写0-100的整数");
                    }
                } catch (Exception e) {
                    isError = true;
                    ExcelHelper.setError((Workbook) workbook, cells[num], "请填写0-100的整数");
                    cateRate = null;
                }
                if (cateRate == null) {
                    goodsCate.setIsParentCateRate(DefaultFlag.YES);
                }
                goodsCate.setCateRate(cateRate == null ? BigDecimal.ZERO : cateRate);
            } else {
                if (num == 5) {
                    isError = true;
                    ExcelHelper.setError((Workbook) workbook, cells[num], "此项必填");
                }
            }
        }
        return isError;
    }
}
