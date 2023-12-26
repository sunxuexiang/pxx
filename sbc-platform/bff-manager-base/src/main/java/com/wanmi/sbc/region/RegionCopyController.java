package com.wanmi.sbc.region;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.region.RegionCopyProvider;
import com.wanmi.sbc.setting.bean.vo.RegionCopyFrontVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
/**
 * @desc  
 * @author shiy  2023/12/8 14:47
*/
@RestController
@RequestMapping("/regioncopy")
@Slf4j
@Validated
public class RegionCopyController {


    @Autowired
    private RegionCopyProvider regionCopyProvider;


    /**
     * @desc  
     * @author shiy  2023/12/8 14:49
    */
    @RequestMapping(value = "/getJsonBylevel/{level}", method = RequestMethod.GET)
    public void getJsonBylevel(@PathVariable Integer level, HttpServletResponse response) {
        String jsonFileName = jsonFileName(level);
        if(jsonFileName==null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"参数错误");
        }
        List<RegionCopyFrontVO> copyFrontVOList = regionCopyProvider.getFrontJsonByLevel(level).getContext();
        // 导出内容到浏览器
        try (OutputStream outputStream = response.getOutputStream();) {
            // 设置响应头
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\""+jsonFileName+"\"");
            // 将查询到的结果序列化为json格式
            String jsonString = JSON.toJSONString(copyFrontVOList, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
            // 将序列化的json数据集写入到输出流中
            outputStream.write(jsonString.getBytes(StandardCharsets.UTF_8));
            // 推送输出流结果到浏览器
            outputStream.flush();
        } catch (Exception e) {
            log.error("导出文件失败" + e);
        }
    }

    private String jsonFileName(Integer level){
        if(level==1){
            return "provinces.json";
        }
        if(level==2){
            return "cities.json";
        }
        if(level==3){
            return "areas.json";
        }
        if(level==4){
            return "streets.json";
        }
        return null;
    }
}
