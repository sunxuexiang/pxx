package com.wanmi.sbc.goods.images.service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.util.ImageUtils;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 商品图片业务逻辑
 * </p>
 * 
 * @author liutao
 * @date 2019-02-26 10:35:57
 */
@Service("GoodsImageService")
@Slf4j
public class GoodsImageService {

	@Autowired
	private GoodsImageRepository goodsImageRepository;

	/**
	 * 根据商品ID查询
	 * 
	 * @author liutao
	 */
	public List<GoodsImageVO> findByGoodsId(String goodsId) {
		List<GoodsImage> goodsImageList = goodsImageRepository.findByGoodsId(goodsId);
		if (CollectionUtils.isEmpty(goodsImageList)) {
			return null;
		}
		List<GoodsImageVO> goodsImageVOList = goodsImageList.stream().map(goodsImage -> this.wrapperVo(goodsImage))
				.collect(Collectors.toList());
		return goodsImageVOList;
	}

	/**
	 * 根据商品ID查询
	 * 
	 * @author liutao
	 */
	public List<GoodsImageVO> findByGoodsIds(List<String> goodsIds) {
		List<GoodsImage> goodsImageList = goodsImageRepository.findByGoodsIds(goodsIds);
		if (CollectionUtils.isEmpty(goodsImageList)) {
			return null;
		}
		List<GoodsImageVO> goodsImageVOList = goodsImageList.stream().map(goodsImage -> this.wrapperVo(goodsImage))
				.collect(Collectors.toList());
		return goodsImageVOList;
	}

	/**
	 * 将实体包装成VO
	 * 
	 * @author liutao
	 */
	public GoodsImageVO wrapperVo(GoodsImage goodsImage) {
		if (goodsImage != null) {
			GoodsImageVO goodsImageVO = new GoodsImageVO();
			KsBeanUtil.copyPropertiesThird(goodsImage, goodsImageVO);
			return goodsImageVO;
		}
		return null;
	}

	@Autowired
	private YunServiceProvider yunServiceProvider;

	public String watermark(String artworkUrl) {
		int i_idx_1 = artworkUrl.lastIndexOf('/');
		int i_idx_2 = artworkUrl.lastIndexOf('.');
		String str_fileName = artworkUrl.substring(i_idx_1 + 1, i_idx_2);
		String str_extName = artworkUrl.substring(i_idx_2 + 1);
		String resourceKey = str_fileName + "_wm." + str_extName;
		byte[] content = ImageUtils.watermark(artworkUrl);
		return yunServiceProvider.justUploadFile(
				YunUploadResourceRequest.builder().resourceKey(resourceKey).content(content).build()
				).getContext();
	}
	
	/**
	 * 水印图片
	 * 
	 * @return
	 */
	@Transactional
	public int watermark() {
		int i_cnt = 0;
		List<Object> list = goodsImageRepository.findByWatermark();
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				try {
					Object obj = list.get(i);
					Object[] objArr = (Object[]) obj;
					long l_imageId = objArr[0] == null ? null : ((BigInteger) objArr[0]).longValue();
					String str_artworkUrl = objArr[1] == null ? null : objArr[1].toString();
					String resourceUrl = watermark(str_artworkUrl);
					i_cnt += goodsImageRepository.updateWatermarkBy(resourceUrl, l_imageId);
					log.info("处理第{}条", i + 1);
				} catch (Exception e) {
					log.error("第" + (i + 1) + "条出错", e);
				}
			}
		}
		return i_cnt;
	}

}
