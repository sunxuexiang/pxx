package com.wanmi.sbc.positionPicture;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.postionPicture.PostionPictureProvider;
import com.wanmi.sbc.goods.bean.vo.PositionPictureVO;
import com.wanmi.sbc.job.ImHistoryMessageDownloadJobHandler;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private OperateLogMQUtil operateLogMQUtil;

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
    public BaseResponse<List<PositionPictureVO>> list(@RequestBody Map<String,Object> request) {
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
         postionPictureProvider.add(request);
         //操作日志记录
         operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "新增商品图片", "新增商品图片:仓库名称"+request.getWareName());
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
        postionPictureProvider.add(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "修改网点", "修改网点:仓库名称"+request.getWareName());
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
        //操作日志记录
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理", "删除网点", "删除网点:仓库名称"+request.getWareName());
        return BaseResponse.SUCCESSFUL();
    }


    @Autowired
    private ImHistoryMessageDownloadJobHandler imHistoryMessageDownloadJobHandler;

    @RequestMapping(value = "/testPullImMessage", method = RequestMethod.GET)
    public BaseResponse testPullImMessage () {
        try {
            imHistoryMessageDownloadJobHandler.execute("");
        } catch (Exception e) {
            log.error("异常", e);
        }
        return BaseResponse.success("");
    }
}
