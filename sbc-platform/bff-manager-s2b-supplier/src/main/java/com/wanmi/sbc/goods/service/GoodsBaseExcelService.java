package com.wanmi.sbc.goods.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateLeafByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchUpdateRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateLeafByStoreIdResponse;
import com.wanmi.sbc.goods.bean.dto.BatchGoodsUpdateDTO;
import com.wanmi.sbc.goods.bean.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 11:15 2018-12-18
 * @Description: 商品分类excel导入导出
 */
@Service
@Slf4j
public class GoodsBaseExcelService {
    /**
     * 操作日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsBaseExcelService.class);

    @Value("classpath:goods_modify_template.xls")
    private Resource templateFile;

    @Value("classpath:goods_batch_import_template.xlsx")
    private Resource goodsBatchImportTemplateFile;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private GoodsProvider goodsProvider;
    /**
     * 商品更新模板下载
     *
     * @return base64位文件字符串
     */
    public void exportTemplate() {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        InputStream is = null;
        try {
            String fileName = URLEncoder.encode("商品更新导入模板.xls", "UTF-8");
            is = templateFile.getInputStream();
            HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                    "filename*=\"utf-8''%s\"", fileName, fileName));
            Workbook wk = WorkbookFactory.create(is);
            wk.write(HttpUtil.getResponse().getOutputStream());
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("读取导入模板异常", e);
                }
            }
        }
    }


    /**
     * 上传商品类目模板
     *
     * @param file（goodsModify，）
     * @param type
     * @param userId
     * @return
     */
    public String upload(MultipartFile file, String userId,String type) {
        if (file != null && !file.isEmpty()) {
            String fileExt =
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            if (!fileExt.equalsIgnoreCase("xls") && !fileExt.equalsIgnoreCase("xlsx")) {
                throw new SbcRuntimeException("K-030402");
            } else if (file.getSize() > (long) (Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024)) {
                throw new SbcRuntimeException("K-030403", new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
            } else {
                String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat("excel").concat("/").concat(
                        type).concat("/");
                log.info("上传文件信息"+t_realPath);
                File picSaveFile = new File(t_realPath);
                if (!picSaveFile.exists()) {
                    try {
                        picSaveFile.mkdirs();
                    } catch (Exception var10) {
                        LOGGER.error("创建文件路径失败->".concat(t_realPath), var10);
                        throw new SbcRuntimeException("K-000011");
                    }
                }

                String newFileName = userId.concat(".").concat(fileExt);
                log.info("上传文件信息newFileName"+newFileName);
                File newFile = new File(t_realPath.concat(newFileName));
                log.info("上传文件信息newFileNamenewFile.getPath()"+newFile.getPath());
                log.info("上传文件信息fileExt"+fileExt);
                try {
                    newFile.deleteOnExit();
                    file.transferTo(newFile);
                    return fileExt;
                } catch (IOException var9) {
                    LOGGER.error("上传Excel文件失败->".concat(newFile.getPath()), var9);
                    throw new SbcRuntimeException("K-000011");
                }
            }
        } else {
            throw new SbcRuntimeException("K-000011");
        }
    }

    /**
     * 下载Excel错误文档
     *
     * @param userId 用户Id
     * @param ext    文件扩展名
     */
    public void downErrExcel(String userId, String ext,String type) {
        //图片存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat("err_excel").concat("/").concat(type
        ).concat("/").concat(userId).concat(".").concat(ext);
        File picSaveFile = new File(filePath);
        FileInputStream is = null;
        ServletOutputStream os = null;
        try {
            if (picSaveFile.exists()) {
                is = new FileInputStream(picSaveFile);
                os = HttpUtil.getResponse().getOutputStream();
                String fileName = URLEncoder.encode("错误表格.".concat(ext), "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));

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
            LOGGER.error("下载EXCEL文件异常->", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("下载EXCEL文件关闭IO失败->", e);
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOGGER.error("下载EXCEL文件关闭IO失败->", e);
                }
            }
        }

    }


    @Transactional
    public List<String> importGoodsModify(String userId, String ext, Long storeId) {
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat("excel").concat("/").concat("goodsModify").concat("/").concat(userId).concat(".").concat(ext);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new SbcRuntimeException("K-000011");
        } else if (file.length() > (long) (Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024)) {
            throw new SbcRuntimeException("K-030403", new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        } else {
            //根据店铺获取品牌
            ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
            contractBrandQueryRequest.setStoreId(storeId);
            List<GoodsBrandVO> collect = contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList()
                    .stream().filter(contractBrand -> contractBrand.getGoodsBrand() != null && StringUtils.isNotBlank(contractBrand.getGoodsBrand().getBrandName()))
                    .map(ContractBrandVO::getGoodsBrand).collect(Collectors.toList());

            Map<String, GoodsBrandVO> goodsBrandVOMap = collect.stream().collect(Collectors.toMap(GoodsBrandVO::getBrandName, Function.identity(),(k1,k2) ->k1));
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
                    int firstRowNum = sheet.getFirstRowNum();
                    int lastRowNum = sheet.getLastRowNum();
                    if (lastRowNum < 1) {
                        throw new SbcRuntimeException("K-030405");
                    } else {
                        Boolean isError = false;
                        GoodsCommonBatchUpdateRequest request=new GoodsCommonBatchUpdateRequest();
                        List<BatchGoodsUpdateDTO> list=new ArrayList<>();
                        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; ++rowNum) {
                            Row row = sheet.getRow(rowNum);
                            //物料编码
                            Cell cell1 = row.getCell(0);
//                            //商品名称
//                            Cell cell2 = row.getCell(1);
                            //商品品牌
                            Cell cell3 = row.getCell(2);
                            //上下架
                            Cell cell4 = row.getCell(3);
                            //销售类型
                            Cell cell5 = row.getCell(4);
                            //店铺分类
                            Cell cell6 = row.getCell(5);
                            log.info("上传文件获取分类：{}",cell6);
                            BatchGoodsUpdateDTO batchGoodsUpdateDTO=new BatchGoodsUpdateDTO();
                            if(Objects.nonNull(cell1)
                                    && Objects.nonNull(cell5)
//                                    && Objects.nonNull(cell2)
                                    && Objects.nonNull(cell3)
                                    && Objects.nonNull(cell4)){

                                batchGoodsUpdateDTO.setErpId(ExcelHelper.getValue(cell1));
                                String value = ExcelHelper.getValue(cell4);
                                if ("上架".equals(value)){
                                    batchGoodsUpdateDTO.setAddFlag(1);
                                }else if ("下架".equals(value)){
                                    batchGoodsUpdateDTO.setAddFlag(0);
                                }else{
                                    isError = true;
                                }
                                String saleType = ExcelHelper.getValue(cell5);
                                if ("零售".equals(saleType)){
                                    batchGoodsUpdateDTO.setSaleType(1);
                                }else if ("批发".equals(saleType)){
                                    batchGoodsUpdateDTO.setSaleType(0);
                                }else {
                                    isError = true;
                                }
                                String cell3Str = ExcelHelper.getValue(cell3);
                                if (StringUtils.isNotBlank(cell3Str)){
                                    if (Objects.nonNull(goodsBrandVOMap.get(cell3Str.trim()))){
                                        batchGoodsUpdateDTO.setBrandId(goodsBrandVOMap.get(cell3Str.trim()).getBrandId());
                                    }
                                }

                            }else {
                                isError=true;
                            }
                            //当必填项没有错误并且店铺分类项有值时匹配分类
                            if(!isError && Objects.nonNull(cell6)){
                                StoreCateListByStoreIdRequest storeCateListByStoreIdRequest = new StoreCateListByStoreIdRequest();
                                storeCateListByStoreIdRequest.setStoreId(storeId);
                                List<StoreCateResponseVO> storeCateResponseVOList = storeCateQueryProvider.listByStoreId(storeCateListByStoreIdRequest).getContext().getStoreCateResponseVOList()
                                        .stream().filter(storeCateVO -> storeCateVO != null && StringUtils.isNotBlank(storeCateVO.getCateName()) && storeCateVO.getCateGrade() == 2)
                                        .collect(Collectors.toList());
                                Map<String, StoreCateResponseVO> storeCateVOMap = storeCateResponseVOList.stream().collect(Collectors.toMap(StoreCateResponseVO::getCateName,Function.identity(),(k1,k2)->k1));
                                log.info("查询获取分类map:{}",storeCateVOMap);
                                String cell6Str = ExcelHelper.getValue(cell6);
                                if (StringUtils.isNotBlank(cell6Str)){
                                    if (Objects.nonNull(storeCateVOMap.get(cell6Str.trim()))){
                                        batchGoodsUpdateDTO.setCateId(storeCateVOMap.get(cell6Str.trim()).getStoreCateId());
                                    }
                                }
                            }
                            if(!isError){
                                list.add(batchGoodsUpdateDTO);
                            }

                        }
                        //上传文件有错误内容
                        if (isError) {
                            this.errorExcel(userId.concat(".").concat(ext), (Workbook) workbook);
                            throw new SbcRuntimeException("K-030404", new Object[]{ext});
                        }
                        if (CollectionUtils.isNotEmpty(list)){
                            request.setGoodsUpdateList(list);
                            request.setStoreId(storeId);
                            log.info("list参数：{}",list);
                            return goodsProvider.batchUpdate(request).getContext().getSkuIds();
                        }
                    }
                }
            } catch (SbcRuntimeException var52) {
                LOGGER.error("商品修改导入异常", var52);
                throw var52;
            } catch (Exception var53) {
                LOGGER.error("商品修改导入异常", var53);
                throw new SbcRuntimeException("K-000001", var53);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var51) {
                        LOGGER.error("商品修改导入关闭IO异常", var51);
                    }
                }

            }
        }
        return new ArrayList<>();
    }

    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        String t_realPath = HttpUtil.getProjectRealPath().concat("err_excel").concat("/").concat("goodaModify").concat("/");
        File picSaveFile = new File(t_realPath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception var18) {
                LOGGER.error("创建文件路径失败->".concat(t_realPath), var18);
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
            LOGGER.error("生成文件失败->".concat(t_realPath.concat(newFileName)), var17);
            throw new SbcRuntimeException("K-000011");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException var16) {
                    LOGGER.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), var16);
                }
            }

        }
        return var7;
    }

    /**
     * 商品更新模板下载
     *
     * @return base64位文件字符串
     */
    public void goodsBatchImportTemplate() {
        if (goodsBatchImportTemplateFile == null || !goodsBatchImportTemplateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        InputStream is = null;
        try {
            String fileName = URLEncoder.encode("代客下单商品导入模板.xlsx", "UTF-8");
            is = goodsBatchImportTemplateFile.getInputStream();
            HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                    "filename*=\"utf-8''%s\"", fileName, fileName));
            Workbook wk = WorkbookFactory.create(is);
            wk.write(HttpUtil.getResponse().getOutputStream());
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("读取导入模板异常", e);
                }
            }
        }
    }

}
