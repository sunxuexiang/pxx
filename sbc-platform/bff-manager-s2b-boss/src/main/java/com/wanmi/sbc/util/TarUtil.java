package com.wanmi.sbc.util;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-29 17:10
 * @Description: TODO
 * @Version 1.0
 */

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
@Slf4j
public class TarUtil {

    public static final String ENCODING="GBK";
    /**
     * 解压缩tar
     * @param file
     */
    public static String unTarFile(String file) {
        List<Map<String,Object>> lists= Lists.newArrayList();
        try (FileInputStream fis = new FileInputStream(new File(file));
             GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(
                     fis));
             ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
             InputStreamReader inr = new InputStreamReader(
                     is,ENCODING);//考虑到编码格式
             BufferedReader reader=new BufferedReader(inr)
        ){
            TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();
            String lineTxt=null;
            while (entry != null) {
                String name = entry.getName();
                String[] nameSplit=name.split("_");
                String sn=nameSplit[1];
                log.info("name:{},sn:{}",name,sn);
                while((lineTxt = reader.readLine()) != null){
                    String[] keys= lineTxt.split("\t");
                    Map<String,Object> map = Maps.newHashMap();
                    map.put("sn",sn);
                    for(String key:keys){
                        String[] values =key.split("=");
                        if(!StringUtils.isEmpty(values[0])){
                            map.put(values[0],values[1]);
                        }
                    }
                    lists.add(map);
                }
                entry = (TarArchiveEntry) in.getNextEntry();
            }
            return JSON.toJSONString(lists);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "";
        }
    }

    public static byte[] urlTobyte(String url) throws MalformedURLException {
        URL ur = new URL(url);
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(ur.openStream());
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] content = out.toByteArray();
        return content;
    }
    /**
     * 读取gz文件
     * @param url 地址
     */
    public static List<String> sendGetToGzip(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<String> lines = new ArrayList();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = new GZIPInputStream(entity.getContent());
                Scanner sc = new Scanner(in);
                while (sc.hasNextLine()) {
                    lines.add(sc.nextLine());
                }
                in.close();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }
}