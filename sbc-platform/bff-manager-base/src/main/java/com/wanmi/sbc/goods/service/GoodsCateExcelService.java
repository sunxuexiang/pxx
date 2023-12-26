package com.wanmi.sbc.goods.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Author: songhanlin
 * @Date: Created In 11:15 2018-12-18
 * @Description: 商品分类excel导入导出
 */
@Service
public class GoodsCateExcelService {
    /**
     * 操作日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsCateExcelService.class);

    @Value("classpath:goods_cate_template.xls")
    private Resource templateFile;

    /**
     * 商品类目模板下载
     *
     * @return base64位文件字符串
     */
    public void exportTemplate() {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        InputStream is = null;
        try {
            String fileName = URLEncoder.encode("商品类目导入模板.xls", "UTF-8");
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
     * @param file
     * @param userId
     * @return
     */
    public String upload(MultipartFile file, String userId) {
        if (file != null && !file.isEmpty()) {
            String fileExt =
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            if (!fileExt.equalsIgnoreCase("xls") && !fileExt.equalsIgnoreCase("xlsx")) {
                throw new SbcRuntimeException("K-030402");
            } else if (file.getSize() > (long) (Constants.IMPORT_GOODS_MAX_SIZE * 1024 * 1024)) {
                throw new SbcRuntimeException("K-030403", new Object[]{Constants.IMPORT_GOODS_MAX_SIZE});
            } else {
                String t_realPath = HttpUtil.getProjectRealPath().concat("/").concat("excel").concat("/").concat(
                        "goodsCate").concat("/");
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
                File newFile = new File(t_realPath.concat(newFileName));

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
    public void downErrExcel(String userId, String ext) {
        //图片存储地址
        String filePath = HttpUtil.getProjectRealPath().concat("/").concat("err_excel").concat("/").concat("goodaCate"
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


}
