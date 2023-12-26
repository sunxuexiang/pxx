package com.wanmi.sbc.utils;

import com.wanmi.osd.ExceptionHandle.OSDException;
import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.osd.bean.OsdResource;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunGetResourceRequest;
import com.wanmi.sbc.setting.api.response.yunservice.YunAvailableConfigResponse;
import com.wanmi.sbc.wallet.exception.WalletRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 对象存储服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Slf4j
public class OsdWalletService {

    @Value("${yun.file.path.env.profile}")
    private String env;

    @Resource
    private YunServiceProvider yunServiceProvider;

    /**
     * 上传OSD
     * @param baos 缓存数据流
     * @param fileName 指定文件名
     * @return
     * @throws WalletRuntimeException
     */
    public void uploadExcel(ByteArrayOutputStream baos, String fileName) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(baos.toByteArray());
            YunAvailableConfigResponse configResponse = yunServiceProvider.getAvailableYun().getContext();
            OsdClientParam osdClientParam = KsBeanUtil.convert(configResponse,OsdClientParam.class);
            OsdClient.instance().putObject(osdClientParam, OsdResource.builder()
                    .osdInputStream(bais)
                    .osdResourceKey(fileName)
                    .build());
        } catch (OSDException osd) {
            log.error("上传文件-OSD服务器端错误->错误码:{},描述:{}", osd.getErrorCode(), osd.getMessage());
            throw new WalletRuntimeException("R-000102");
        } catch (IOException io) {
            throw new WalletRuntimeException("R-000102");
        } finally {
            if(bais != null){
                try {
                    bais.close();
                } catch (IOException e) {
                    log.error("缓存关闭异常->", e);
                }
            }
        }
    }

    /**
     * 删除OSD文件
     * @param files 文件
     * @return
     * @throws WalletRuntimeException
     */
    public void deleteFiles(List<String> files)  {
        try {
            YunAvailableConfigResponse configResponse = yunServiceProvider.getAvailableYun().getContext();
            OsdClientParam osdClientParam = KsBeanUtil.convert(configResponse,OsdClientParam.class);
            OsdClient.instance().deleteObject(osdClientParam, files);
        } catch (OSDException osd) {
            log.error("删除文件-OSD服务器端错误->错误码:{},描述:{}", osd.getErrorCode(), osd.getMessage());
            throw new WalletRuntimeException("R-000102");
        } catch (IOException io) {
            throw new WalletRuntimeException("R-000102");
        }
    }

    /**
     * 是否存在OSD文件
     * @param file 文件
     * @return
     * @throws WalletRuntimeException
     */
    public boolean existsFiles(String file) throws WalletRuntimeException {
        try {
            YunAvailableConfigResponse configResponse = yunServiceProvider.getAvailableYun().getContext();
            OsdClientParam osdClientParam = KsBeanUtil.convert(configResponse,OsdClientParam.class);
            return OsdClient.instance().doesObjectExist(osdClientParam, file);
        } catch (OSDException osd) {
            log.error("删除文件-OSD服务器端错误->错误码:{},描述:{}", osd.getErrorCode(), osd.getMessage());
            throw new WalletRuntimeException("R-000102");
        } catch (IOException io) {
            throw new WalletRuntimeException("R-000102");
        }
    }

    public byte[] getFile(YunGetResourceRequest yunGetResourceRequest) throws IOException {
        byte[] content = yunServiceProvider.getFile(yunGetResourceRequest).getContext().getContent();
        return content;
    }

    public static void convertToFile(byte[] content, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        YunAvailableConfigResponse configResponse = yunServiceProvider.getAvailableYun().getContext();
        OsdClientParam osdClientParam = KsBeanUtil.convert(configResponse,OsdClientParam.class);
        return OsdClient.instance().getPrefix(osdClientParam);
    }

    public String getFileRootPath(){
        return ConstantsWallet.rootPath.concat(env).concat("/");
    }
}
