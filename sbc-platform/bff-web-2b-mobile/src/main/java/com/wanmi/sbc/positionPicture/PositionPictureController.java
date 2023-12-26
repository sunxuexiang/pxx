package com.wanmi.sbc.positionPicture;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.goods.api.provider.postionPicture.PostionPictureProvider;
import com.wanmi.sbc.goods.bean.vo.PositionPictureVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户须知图片类
 */
@RestController
@RequestMapping("/positionPicture")
@Api(tags = "PositionPictureController", description = "S2B 平台端-商家管理API")
@Slf4j
public class PositionPictureController {

   @Autowired
   private PostionPictureProvider postionPictureProvider;
   @Autowired
   private CommonUtil commonUtil;

    /**
     * 查询网点列表
     *
     * @param request
     *
     * type:0是批发1是散批2是零售
     * allTyep: 0是全查 1是加仓库id查 默认是全查
     * wareId :仓库id
     *
     * @return
     */
    @ApiOperation(value = "查询图片列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<List<PositionPictureVO>> list(@RequestBody PositionPictureVO param) {
        Map<String, Object> request = new HashMap<>();
        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        request.put("wareId",wareId);
        log.info("APP请求用户须知图片参数 {}", JSON.toJSON(param));
        if (param.getStoreId() == null || param.getStoreId() < 1) {
            return BaseResponse.success(new ArrayList<>());
        }
        request.put("storeId", param.getStoreId());
        request.put("type", param.getType());
        List<PositionPictureVO> context = postionPictureProvider.getImages(request).getContext();
        return BaseResponse.success(context);
    }


    /**
     * 新增图片
     * @param request
     * @return
     */
    @ApiOperation(value = "新增商品图片")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody PositionPictureVO request) {
         postionPictureProvider.addAndFlush(request);
         return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改图片
     * @param request
     * @return
     */
    @ApiOperation(value = "修改网点")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public BaseResponse update(@RequestBody PositionPictureVO request) {
        postionPictureProvider.addAndFlush(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除网点
     * @param request
     * @return
     */
    @ApiOperation(value = "新增商品图片")
    @RequestMapping(value = "/delet", method = RequestMethod.DELETE)
    public BaseResponse delet(@RequestBody PositionPictureVO request) {
        postionPictureProvider.addAndFlush(request);
        return BaseResponse.SUCCESSFUL();
    }


}
