package com.wanmi.sbc.goods.livegoods.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.ImageUtils;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.LiveErrCodeUtil;
import com.wanmi.sbc.common.util.MediaIdUtil;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.response.liveroom.LiveRoomDeleteResponse;
import com.wanmi.sbc.customer.api.response.liveroom.LiveRoomGoodsAddResponse;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsAuditRequest;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsQueryRequest;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsUpdateRequest;
import com.wanmi.sbc.goods.api.response.livegoods.LiveGoodsBySkuIdResponse;
import com.wanmi.sbc.goods.bean.vo.LiveGoodsVO;
import com.wanmi.sbc.goods.livegoods.model.root.LiveGoods;
import com.wanmi.sbc.goods.livegoods.repository.LiveGoodsRepository;
import com.wanmi.sbc.goods.liveroomlivegoodsrel.model.root.LiveRoomLiveGoodsRel;
import com.wanmi.sbc.goods.liveroomlivegoodsrel.repository.LiveRoomLiveGoodsRelRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>直播商品业务逻辑</p>
 *
 * @author zwb
 * @date 2020-06-06 18:49:08
 */
@Service("LiveGoodsService")
public class LiveGoodsService {
    @Autowired
    private LiveGoodsRepository liveGoodsRepository;

    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LiveRoomLiveGoodsRelRepository liveRoomLiveGoodsRelRepository;


    private static final Logger log = LoggerFactory.getLogger(LiveGoodsService.class);

    private String deleteGoodsUrl = "https://api.weixin.qq.com/wxaapi/broadcast/goods/delete?access_token=";

    private String auditGoodsUrl = "https://api.weixin.qq.com/wxaapi/broadcast/goods/add?access_token=";


    private String importCommodity = "https://api.weixin.qq.com/wxaapi/broadcast/room/addgoods?access_token=";

    /**
     * 直播间导入商品
     *
     * @author zwb
     */
    @Transactional
    public void add(Long roomId, List<Long> goodsIds, String accessToken) {
        //拼接Url
        String url = importCommodity + accessToken;
        Map<String, Object> map = new HashMap<>();
        map.put("ids", goodsIds);
        map.put("roomId", roomId);
        //调用微信导入商品接口
        String result = restTemplate.postForObject(url, map, String.class);
        LiveRoomGoodsAddResponse resp = JSONObject.parseObject(result, LiveRoomGoodsAddResponse.class);
        if (resp.getErrcode() != 0) {
            log.error("直播间导入直播商品异常，返回信息：" + resp.toString());
            throw new SbcRuntimeException(resp.getErrcode().toString(), LiveErrCodeUtil.getErrCodeMessage(resp.getErrcode()));
        }
        //存中间表
        ArrayList entityList = new ArrayList();
        goodsIds.stream().forEach(id -> {
            LiveRoomLiveGoodsRel entity = new LiveRoomLiveGoodsRel();
            entity.setRoomId(roomId);
            entity.setDelFlag(DeleteFlag.NO);
            entity.setCreateTime(LocalDateTime.now());
            entity.setGoodsId(id);
            entityList.add(entity);
        });
        liveRoomLiveGoodsRelRepository.saveAll(entityList);
    }

    /**
     * 修改直播商品
     *
     * @author zwb
     */
    @Transactional
    public LiveGoods modify(LiveGoods entity) {
        liveGoodsRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除直播商品（微信端）
     *
     * @author zwb
     */
    @Transactional
    public void deleteById(Long id, Long goodsId, String accessToken) {

        if (goodsId != null) {
            //拼接Url
            String url = deleteGoodsUrl + accessToken;
            Map<String, Long> map = new HashMap<>();
            map.put("goodsId", goodsId);
            //调用删除接口，删除直播商品
            String result = restTemplate.postForObject(url, map, String.class);
            LiveRoomDeleteResponse resp = JSONObject.parseObject(result, LiveRoomDeleteResponse.class);
            if (resp.getErrcode() != 0) {
                log.error("微信删除直播商品异常，返回信息：" + resp.toString());
                throw new SbcRuntimeException(resp.getErrcode().toString(), LiveErrCodeUtil.getErrCodeMessage(resp.getErrcode()));
            }
            List<LiveRoomLiveGoodsRel> liveGoodsRelList= liveRoomLiveGoodsRelRepository.findByGoodsIdAndDelFlag(goodsId, DeleteFlag.NO);
            if (CollectionUtils.isNotEmpty(liveGoodsRelList)){
                //删除中间表数据
                liveRoomLiveGoodsRelRepository.deleteByGoodsIdAndDelFlag(goodsId,DeleteFlag.NO);
            }
        }
        liveGoodsRepository.deleteById(id);
    }


    /**
     * 批量删除直播商品
     *
     * @author zwb
     */
    @Transactional
    public void deleteByIdList(List<Long> ids) {
        liveGoodsRepository.deleteByIdList(ids);
    }

    /**
     * 单个查询直播商品
     *
     * @author zwb
     */
    public LiveGoods getOne(Long id) {

        return liveGoodsRepository.findByGoodsIdAndDelFlag(id, DeleteFlag.NO)
                .orElse(new LiveGoods());
    }

    /**
     * 分页查询直播商品
     *
     * @author zwb
     */
    public Page<LiveGoods> page(LiveGoodsQueryRequest queryReq) {
         /*if (queryReq.getAuditStatus()==0){
			  List<LiveGoods> liveGoodsList = liveGoodsRepository.findByAuditStatusAndDelFlag(queryReq.getAuditStatus(), DeleteFlag.NO);

			return new PageImpl<LiveGoods>(liveGoodsList, queryReq.getPageable(),liveGoodsList.size());
		  }*/
        return liveGoodsRepository.findAll(
                LiveGoodsWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询直播商品
     *
     * @author zwb
     */
    public List<LiveGoods> list(LiveGoodsQueryRequest queryReq) {
        return liveGoodsRepository.findAll(LiveGoodsWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * 将实体包装成VO
     *
     * @author zwb
     */
    public LiveGoodsVO wrapperVo(LiveGoods liveGoods) {
        if (liveGoods != null) {
            LiveGoodsVO liveGoodsVO = KsBeanUtil.convert(liveGoods, LiveGoodsVO.class);
            return liveGoodsVO;
        }
        return null;
    }

    /**
     * 直播商品提交审核
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(LiveGoodsAuditRequest liveGoodsAuditRequest) {
        String accessToken = liveGoodsAuditRequest.getAccessToken();
        //拼接Url
        String url = auditGoodsUrl + accessToken;
        Map<String, LiveGoods> map = new HashMap<>();
        List<LiveGoodsVO> goodsInfoVOList = liveGoodsAuditRequest.getGoodsInfoVOList();
        goodsInfoVOList.stream().forEach(c -> {
            //商品名称是否过长截取和补充
            String name = c.getName();
            try {
                if (name.getBytes("gbk").length < 5) {
                    name = name + "    ";
                }
                try {
                    c.setName(LiveGoodsService.substring(name, 28));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String coverImgUrl = c.getCoverImgUrl();
            //获取mediaID 调用微信接口
            String mediaId = null;
            if (StringUtils.isNotEmpty(c.getCoverImgUrl())) {
                try {
                    //下载图片到本地，根据本地路径去微信接口查询 media_id
                    //将图片缩放至300*300
                    File uploadURL = new File(Objects.requireNonNull(MediaIdUtil.uploadURL(coverImgUrl)));
                    if (ImageIO.read(uploadURL).getHeight() > 300 || ImageIO.read(uploadURL).getWidth() > 300) {
                        ImageUtils.reSize(uploadURL, uploadURL, 300, 300, true);
                    }
                    mediaId = MediaIdUtil.uploadFile(uploadURL.toString(), accessToken, "image");
                    //删除本地图片
                    if(uploadURL.exists()){
                        uploadURL.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.setCoverImgUrl(mediaId);

			/*try {
				c.setUrl(URLEncoder.encode(c.getUrl(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/

            LiveGoods liveGoods = KsBeanUtil.convert(c, LiveGoods.class);
            map.put("goodsInfo", liveGoods);

            String result = restTemplate.postForObject(url, map, String.class);

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (0 != (Integer) jsonObject.get("errcode")) {
                log.error("微信提审直播商品异常，返回信息：" + jsonObject.toString());
                throw new SbcRuntimeException(String.valueOf(jsonObject.getInteger("errcode")), LiveErrCodeUtil.getErrCodeMessage(jsonObject.getInteger("errcode")));
            }
            //保存接口返回的结果
            Long goodsIdNew = Long.valueOf(jsonObject.getString("goodsId"));
            //Long auditId = Long.valueOf(jsonObject.getString("auditId"));
            //修改商品审核状态为待审核，修改商品goodsId
            liveGoodsRepository.updateGoodsIdAndAuditStatusById(goodsIdNew, 1, c.getId());
        });

    }

    /**
     * 定时任务更新商品状态
     *
     * @param req
     */
    @Transactional
    public void update(LiveGoodsUpdateRequest req) {

        /*List<Long> goodsIdList = liveGoodsRepository.findByGoodsIdList(req.getGoodsIdList(), DeleteFlag.NO).stream()
                .filter(Objects::nonNull)
                .map(LiveGoods::getGoodsId)
                .collect(Collectors.toList());*/
           //批量修改商品状态
        if (CollectionUtils.isNotEmpty(req.getGoodsIdList())) {
            liveGoodsRepository.updateByGoodsIdList(req.getAuditStatus(), req.getGoodsIdList());
        }
    }

    /**
     * supplier端添加商品
     *
     * @param goodsInfoVOList
     * @return
     */
    @Transactional
    public List<LiveGoodsVO> supplier(List<LiveGoodsVO> goodsInfoVOList) {
        List<LiveGoods> liveGoodsList = goodsInfoVOList.stream().map(entity -> {
            LiveGoods convert = KsBeanUtil.convert(entity, LiveGoods.class);
            convert.setAuditStatus(0);
            entity.setAuditStatus(0);
            convert.setDelFlag(DeleteFlag.NO);
            return convert;
        }).collect(Collectors.toList());
        liveGoodsRepository.saveAll(liveGoodsList);
        return goodsInfoVOList;
    }

    /**
     * 审核状态修改
     *
     * @param entity
     * @return
     */
    @Transactional
    public LiveGoods status(LiveGoods entity) {

        String auditReason = entity.getAuditReason();
        if (auditReason == null) {
            auditReason = "";
        }
        liveGoodsRepository.updateAuditStatusById(entity.getAuditStatus(), auditReason, entity.getId());

        return entity;
    }


    //判断是否是汉字
    public static boolean isChineseChar(char c) throws UnsupportedEncodingException {
        return String.valueOf(c).getBytes("UTF-8").length > 1;
    }

    //截取字符串方法
    public static String substring(String orignal, int count) throws UnsupportedEncodingException { // 原始字符不为null，也不是空字符串   
        if (orignal != null && !"".equals(orignal)) {

            // 要截取的字节数大于0，且小于原始字符串的字节数   
            if (count > 0 && count < orignal.getBytes("gbk").length) {
                StringBuffer buff = new StringBuffer();
                char c;
                for (int i = 0; i < count; i++) {
                    c = orignal.charAt(i);
                    if (i==count-1&&LiveGoodsService.isChineseChar(c)){
                        break;
                    }
                    buff.append(c);
                    if (LiveGoodsService.isChineseChar(c)) {
                        // 遇到中文汉字，截取字节总数减1   
                        --count;
                    }
                }
                return buff.toString();
            }
        }
        return orignal;
    }

    public LiveGoodsBySkuIdResponse getRoomInfoBySkuId(String goodsInfoId) {
        Long roomInfoBySkuId = liveGoodsRepository.getRoomInfoBySkuId(goodsInfoId);
        return LiveGoodsBySkuIdResponse.builder().roomId(roomInfoBySkuId).build();
    }

    public List<LiveGoodsVO> getRoomInfoByGoodsInfoIds(List<String> goodsInfoId) {
        List<LiveGoods> roomInfoBySkuId = liveGoodsRepository.getRoomInfoByGoodsInfoIds(goodsInfoId);
        List<LiveGoodsVO> list = KsBeanUtil.copyListProperties(roomInfoBySkuId, LiveGoodsVO.class);
        return list;
    }
}

