package com.wanmi.sbc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author shiGuangYi
 * @createDate 2023-08-31 10:35
 * @Description: TODO
 * @Version 1.0
 */

@Slf4j
public class PoiExcel {
    public PoiExcel(){
    }
    public PoiExcel(String filepath){

        InputStream is=CrmExcelReadHelper(filepath);
        Workbook wb=null;
        try {
            wb=readExcelTitle(is, filepath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            try {
                wb.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取文件流，如果是本地文件，请修改try catch内的读取方法
     * @param filepath
     * @return
     */
    public static InputStream  CrmExcelReadHelper(String filepath) {
        if (filepath == null) {
            log.info("文件路径为空");
            return null;
        }

        HttpURLConnection httpConn = null;

        try {
            URL urlObj = new URL(filepath);
            // 创建HttpURLConnection对象，通过这个对象打开跟远程服务器之间的连接
            httpConn = (HttpURLConnection) urlObj.openConnection();

            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(5000);
            httpConn.connect();

            InputStream is = httpConn.getInputStream();
            return is;

        } catch (FileNotFoundException e) {
            log.error("文件没有找到异常：", e);
        } catch (IOException e) {
            log.error("读取流异常：", e);
        }
        return null;
    }


    /**
     * 读取Excel表格表头的内容
     *
     * @param filepath
     * @return String 表头内容的数组
     * @author donnie/刘唐尼
     */

    public static Workbook readExcelTitle(InputStream isteam,String filepath) throws Exception {

        String ext = filepath.substring(filepath.lastIndexOf("."));
        Workbook wb=null;
        if (".xls".equals(ext)) {
            wb = new HSSFWorkbook(isteam);
        } else if (".xlsx".equals(ext)) {
            wb = new XSSFWorkbook(isteam);
        } else {
            wb = null;
            throw new Exception("Workbook对象为空！");
        }
        return wb;
    }



}