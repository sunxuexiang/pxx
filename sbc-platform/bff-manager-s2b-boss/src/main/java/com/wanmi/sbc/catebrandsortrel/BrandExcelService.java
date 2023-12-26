package com.wanmi.sbc.catebrandsortrel;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.ValidateUtil;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelBatchAddRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelDelByIdRequest;
import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 品牌导入类
 */
@Slf4j
@Service
public class BrandExcelService {

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private CateBrandSortRelProvider cateBrandSortRelProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    /**
     * 上传文件
     * @param file 文件
     * @param userId 操作员id
     * @return 文件格式
     */
    public String upload(MultipartFile file, String userId){
        if (file == null || file.isEmpty()) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if(!(fileExt.equalsIgnoreCase("xls") || fileExt.equalsIgnoreCase("xlsx"))){
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_EXT_ERROR);
        }

        if (file.getSize() > Constants.BRAND_EXCEL_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
        }

        //上传存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.BRAND_EXCEL_DIR).concat("/");
        File picSaveFile = new File(filePath);
        if (!picSaveFile.exists()) {
            try {
                picSaveFile.mkdirs();
            } catch (Exception e) {
                log.error("创建文件路径失败->".concat(filePath), e);
                throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
            }
        }

        String newFileName = userId.concat(".").concat(fileExt);
        File newFile = new File(filePath.concat(newFileName));
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
     * 导入品牌
     * @param cateId
     */
    @Transactional( rollbackFor = SbcRuntimeException.class)
    public void implBrands(Long cateId,String userId,String ext) {
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.BRAND_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
        File file = new File(filePath);
        if ((!file.exists())) {
            throw new SbcRuntimeException(CommonErrorCode.UPLOAD_FILE_ERROR);
        }
        if (file.length() > Constants.BRAND_EXCEL_MAX_SIZE * 1024 * 1024) {
            throw new SbcRuntimeException(GoodsImportErrorCode.FILE_MAX_SIZE, new Object[]{Constants.BRAND_EXCEL_MAX_SIZE});
        }


        try (Workbook workbook = WorkbookFactory.create(file)) {
            // 查询品牌信息
            List<GoodsBrandVO> goodsBrandVOList = goodsBrandQueryProvider.list(GoodsBrandListRequest.builder()
                    .delFlag(0)
                    .build()).getContext().getGoodsBrandVOList();
            Map<String, GoodsBrandVO> brandMap = goodsBrandVOList.stream()
                    .collect(Collectors.toMap(GoodsBrandVO::getBrandName, Function.identity()));

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
            int maxCell = 2;
            if(lastRowNum < 1){
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR);
            }

            boolean isError = false;
            List<String> brandNames = new ArrayList<>();
            List<Long> brandSorts = new ArrayList<>();
            List<CateBrandSortRelVO> cateBrandSortRelS = new ArrayList<>();
            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
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
                if(!isNotEmpty){
                    continue;
                }

                GoodsBrandVO goodsBrand = null;
                //品牌名称
                String brandName = ExcelHelper.getValue(cells[0]);
                if (StringUtils.isBlank(brandName)) {
                    ExcelHelper.setError(workbook, cells[0], "品牌名称未填写");
                    isError = true;
                }else {
                    goodsBrand = brandMap.get(brandName);
                    if (Objects.isNull(goodsBrand)) {
                        ExcelHelper.setError(workbook, cells[0], "品牌名称不存在");
                        isError = true;
                        continue;
                    }

                    if (ValidateUtil.isOverLen(brandName, 30)) {
                        ExcelHelper.setError(workbook, cells[0], "品牌名称不超过30字符");
                        isError = true;
                    }

                    if (brandNames.contains(goodsBrand.getBrandName())) {
                        ExcelHelper.setError(workbook, cells[0], "品牌重复导入");
                        isError = true;
                    }

                    brandNames.add(goodsBrand.getBrandName());
                }


                Long brandSort = null;
                //品牌排序
                cells[1].setCellType(CellType.STRING);
                String brandSortStr = ExcelHelper.getValue(cells[1]);
                if (StringUtils.isBlank(brandSortStr)) {
                    ExcelHelper.setError(workbook, cells[1], "品牌排序未填写");
                    isError = true;
                }else {
                    try {
                        brandSort = Long.parseLong(brandSortStr);
                    } catch (NumberFormatException e) {
                        ExcelHelper.setError(workbook, cells[1], "品牌排序仅支持1-9999数字");
                        isError = true;
                    }

                    if (brandSort!=0 && brandSorts.contains(brandSort)) {
                        ExcelHelper.setError(workbook, cells[1], "排序已存在”");
                        isError = true;
                    }
                    brandSorts.add(brandSort);
                }

                if (isError) {
                    continue;
                }
                CateBrandSortRelVO cateBrandSortRelVO = new CateBrandSortRelVO();
                cateBrandSortRelVO.setBrandId(goodsBrand.getBrandId());
                cateBrandSortRelVO.setCateId(cateId);
                cateBrandSortRelVO.setName(brandName);
                cateBrandSortRelVO.setSerialNo(brandSort);
                cateBrandSortRelVO.setCreatePerson(userId);
                cateBrandSortRelVO.setCreateTime(LocalDateTime.now());
                cateBrandSortRelVO.setDelFlag(DeleteFlag.NO);
                cateBrandSortRelS.add(cateBrandSortRelVO);

            }
            if (isError) {
                this.errorExcel(userId.concat(".").concat(ext), workbook);
                throw new SbcRuntimeException(GoodsImportErrorCode.CUSTOM_ERROR, new Object[]{"上传失败"});
            }
            //删除该品类下所有的品牌数据
            CateBrandSortRelDelByIdRequest cateBrandSortRelDelByIdRequest = new CateBrandSortRelDelByIdRequest();
            cateBrandSortRelDelByIdRequest.setCateId(cateId);
            cateBrandSortRelProvider.deleteById(cateBrandSortRelDelByIdRequest);
            CateBrandSortRelBatchAddRequest cateBrandSortRelBatchAddRequest = new CateBrandSortRelBatchAddRequest();
            cateBrandSortRelBatchAddRequest.setCateBrandSortRelVO(cateBrandSortRelS);
            cateBrandSortRelProvider.batchAdd(cateBrandSortRelBatchAddRequest);
            // 修改品牌的es排序
            if (Objects.nonNull(cateBrandSortRelS) && cateBrandSortRelS.size() > 0) {
                // 品牌同步es操作
                // 根据商品的品牌id集合，修改所有的商品品牌排序序号
//                esGoodsInfoElasticService.updateBrandSerialNoList(cateId,cateBrandSortRelS);
                esGoodsInfoElasticService.updateBrandSerialNoByCateId(cateId,null);
                esRetailGoodsInfoElasticService.updateBrandSerialNoByCateId(cateId, null);
                int size = cateBrandSortRelS.size();
//                cateBrandSortRelS = this.rightMove(cateBrandSortRelS,size+1);
                // 根据三级类目id和品牌id，设置排序
                GoodsBrandVO goodsBrandVOCopy = null;
                for (int i = 0; i < cateBrandSortRelS.size(); i++) {
                    CateBrandSortRelVO cateBrandSortRelVO = cateBrandSortRelS.get(i);
                    GoodsBrandVO goodsBrandVO = new GoodsBrandVO();
                    goodsBrandVO.setBrandId(cateBrandSortRelVO.getBrandId());
                    goodsBrandVO.setBrandName(cateBrandSortRelVO.getName());
                    Integer brandSeqNum = cateBrandSortRelVO.getSerialNo().intValue();
                    goodsBrandVO.setBrandSeqNum(brandSeqNum);
                    if(i == 0){
                        goodsBrandVOCopy = goodsBrandVO;
                    }
                    esGoodsInfoElasticService.updateBrandSerialNo(goodsBrandVO, cateId);
                }
                if(Objects.nonNull(goodsBrandVOCopy)){
                    esGoodsInfoElasticService.updateBrandSerialNo(goodsBrandVOCopy, cateId);
                }
            }

        }catch (SbcRuntimeException e) {
            log.error("商品导入异常", e);
            throw e;
        } catch (Exception e) {
            log.error("商品导入异常", e);
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }

    /**
     * 数组向右移动
     * @param list 原数组
     * @param b 移动的位数
     * @return 移动后b位后得到的数组
     */
    public static List rightMove(List list, int b) {
        int originSize = list.size();
        b = originSize - b % originSize;
        for (int i = 0; i < b; i++) {
            list.add(list.get(i));
        }
        return list.subList(list.size() - originSize, list.size());
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
            if (!(row.getCell(0).getStringCellValue().contains("品牌名称"))) {
                throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
            }
        } catch (Exception e) {
            throw new SbcRuntimeException(GoodsImportErrorCode.DATA_FILE_FAILD);
        }
    }

    /**
     * EXCEL错误文件-本地生成
     * @param newFileName 新文件名
     * @param wk Excel对象
     * @return 新文件名
     * @throws SbcRuntimeException
     */
    public String errorExcel(String newFileName, Workbook wk) throws SbcRuntimeException {
        //图片存储地址
        String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_BRAND_EXCEL_DIR).concat("/");

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
        if(newFile.exists()){
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
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("生成文件关闭IO失败->".concat(t_realPath.concat(newFileName)), e);
                }
            }
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
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat(Constants.ERR_BRAND_EXCEL_DIR).concat("/").concat(userId).concat(".").concat(ext);
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
}
