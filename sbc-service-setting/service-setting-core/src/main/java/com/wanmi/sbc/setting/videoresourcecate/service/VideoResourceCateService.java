package com.wanmi.sbc.setting.videoresourcecate.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceQueryRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateInitRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateQueryRequest;
import com.wanmi.sbc.setting.bean.enums.CateParentTop;
import com.wanmi.sbc.setting.bean.vo.MenuInfoVO;
import com.wanmi.sbc.setting.bean.vo.NewMenuInfoVO;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import com.wanmi.sbc.setting.videoresource.model.root.VideoResource;
import com.wanmi.sbc.setting.videoresource.repository.VideoResourceRepository;
import com.wanmi.sbc.setting.videoresource.service.VideoResourceWhereCriteriaBuilder;
import com.wanmi.sbc.setting.videoresourcecate.model.root.VideoResourceCate;
import com.wanmi.sbc.setting.videoresourcecate.repository.VideoResourceCateRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频教程资源资源分类表业务逻辑
 *
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@Service("VideoResourceCateService")
public class VideoResourceCateService {

    private final String SPLIT_CHAR = "|";

    private final int GRADE_MAX = 4;

    @Autowired
    private VideoResourceCateRepository videoResourceCateRepository;


    @Autowired
    private VideoResourceRepository videoResourceRepository;



    /**
     * 新增视频教程资源资源分类表
     *
     * @author hudong
     */
    @Transactional(rollbackFor = Exception.class)
    public VideoResourceCate add(VideoResourceCate videoResourceCate) {
        //校验新增子类层级不能为null
        if (StringUtils.isNotEmpty(videoResourceCate.getCateParentId()) && Objects.isNull(videoResourceCate.getCateGrade())) {
            throw new SbcRuntimeException(SettingErrorCode.PARENT_RESOURCE_CATE_SUB_GRADE_ERROR,SettingErrorCode.PARENT_RESOURCE_CATE_SUB_GRADE_ERROR_MSG);
        }
        if (videoResourceCate.getCateParentId() == null || videoResourceCate.getCateGrade() == 1) {
            videoResourceCate.setCateParentId(String.valueOf(CateParentTop.ZERO.toValue()));
        }

        //验证重复名称
        VideoResourceCateQueryRequest resourceCateQueryRequest = VideoResourceCateQueryRequest.builder()
                .cateName(videoResourceCate.getCateName())
                .cateId(videoResourceCate.getCateId())
                .cateGrade(videoResourceCate.getCateGrade())
                .storeId(videoResourceCate.getStoreId())
                .companyInfoId(videoResourceCate.getCompanyInfoId())
                .cateType(videoResourceCate.getCateType())
                .delFlag(DeleteFlag.NO).build();

        if (videoResourceCateRepository.count(VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest))
                > 0) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_CATE_NAME_EXIST_ERROR);
        }

        //验证在同一父类下是否超过20个分类
        resourceCateQueryRequest.setCateName(null);
        resourceCateQueryRequest.setCateParentId(videoResourceCate.getCateParentId());
        if (videoResourceCateRepository.count(VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest))
                >= 200) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_CHILD_CATE_MAX_COUNT_ERROR);
        }

        videoResourceCate.setDelFlag(DeleteFlag.NO);
        videoResourceCate.setIsDefault(DefaultFlag.NO);
        videoResourceCate.setCreateTime(LocalDateTime.now());
        videoResourceCate.setUpdateTime(LocalDateTime.now());
        videoResourceCate.setCateGrade(1);
        videoResourceCate.setSort(0);
        videoResourceCate.setStoreId(0L);
        videoResourceCate.setCompanyInfoId(0L);
        videoResourceCate.setCateId(String.valueOf(System.currentTimeMillis()));

        //填充分类路径，获取父类的分类路径进行拼凑,例01|001|0001
        String catePath = String.valueOf(CateParentTop.ZERO.toValue()).concat(SPLIT_CHAR);
        if (!videoResourceCate.getCateParentId().equals(String.valueOf(CateParentTop.ZERO.toValue()))) {

            VideoResourceCate parentResourceCate = videoResourceCateRepository.findByCateId(videoResourceCate
                    .getCateParentId());
            if (parentResourceCate == null || parentResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
                throw new SbcRuntimeException(SettingErrorCode.PARENT_RESOURCE_CATE_NOT_EXIST_ERROR);
            }
            catePath = parentResourceCate.getCatePath().concat(String.valueOf(parentResourceCate.getCateId())).concat
                    (SPLIT_CHAR);
            videoResourceCate.setCateGrade(parentResourceCate.getCateGrade() + 1);
        }
        videoResourceCate.setCatePath(catePath);
        //校验最高层级不能大于4
        if (videoResourceCate.getCateGrade() > GRADE_MAX) {
            throw new SbcRuntimeException(SettingErrorCode.PARENT_RESOURCE_CATE_GRADE_ERROR,SettingErrorCode.PARENT_RESOURCE_CATE_GRADE_ERROR_MSG);
        }
        return videoResourceCateRepository.save(videoResourceCate);
    }

    /**
     * 修改视频教程资源资源分类表
     *
     * @author hudong
     */
    @Transactional(rollbackFor = Exception.class)
    public VideoResourceCate modify(VideoResourceCate newResourceCate) {
        VideoResourceCate oldResourceCate = videoResourceCateRepository.findByCateIdAndStoreId(newResourceCate
                .getCateId(), newResourceCate.getStoreId());
        if (oldResourceCate == null || oldResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.INVALID_PARAMETER);
        }
        if (newResourceCate.getCateParentId() == null) {
            newResourceCate.setCateParentId(CateParentTop.ZERO.toString());
            newResourceCate.setCateGrade(1);
        }

        //验证重复名称
        VideoResourceCateQueryRequest resourceCateQueryRequest = VideoResourceCateQueryRequest.builder()
                .cateName(newResourceCate.getCateName())
                .storeId(newResourceCate.getStoreId())
                .companyInfoId(newResourceCate.getCompanyInfoId())
                .notCateId(newResourceCate.getCateId())
                .cateType(newResourceCate.getCateType())
                .delFlag(DeleteFlag.NO).build();

        if (videoResourceCateRepository.count(VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest))
                > 0) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_CATE_NAME_EXIST_ERROR);
        }

        //验证在同一父类下是否超过20个分类
        resourceCateQueryRequest.setCateName(null);
        resourceCateQueryRequest.setCateParentId(newResourceCate.getCateParentId());
        if (videoResourceCateRepository.count(VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest))
                >= 20) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_CHILD_CATE_MAX_COUNT_ERROR);
        }


        //填充分类路径，获取父类的分类路径进行拼凑,例01|001|0001
        String catePath = String.valueOf(CateParentTop.ZERO.toValue()).concat(SPLIT_CHAR);
        if (!newResourceCate.getCateParentId().equals(CateParentTop.ZERO.toString())) {
            VideoResourceCate parentResourceCate = videoResourceCateRepository.findByCateIdAndStoreId(newResourceCate
                    .getCateParentId(), newResourceCate.getStoreId());
            if (parentResourceCate == null || parentResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
                throw new SbcRuntimeException(SettingErrorCode.PARENT_RESOURCE_CATE_NOT_EXIST_ERROR);
            }
            catePath = parentResourceCate.getCatePath().concat(String.valueOf(parentResourceCate.getCateId())).concat
                    (SPLIT_CHAR);
            newResourceCate.setCateGrade(parentResourceCate.getCateGrade() + 1);
        } else {
            newResourceCate.setCateGrade(1);
        }
        newResourceCate.setCatePath(catePath);


        //历史原因：一级路径存在俩种格式（1： 0 ，2： 0|--现在统一为0|）
        //如果分类路径有变化，将所有子类进行更新路径
        if (!catePath.equals(oldResourceCate.getCatePath()) && !catePath.equals(oldResourceCate.getCatePath().concat(SPLIT_CHAR))) {
            final String newCatePath = catePath.concat(String.valueOf(oldResourceCate.getCateId())).concat(SPLIT_CHAR);

            String likeCatePath = oldResourceCate.getCatePath().concat(String.valueOf(oldResourceCate.getCateId()))
                    .concat(SPLIT_CHAR);
            VideoResourceCateQueryRequest resourceCate1 = VideoResourceCateQueryRequest.builder()
                    .likeCatePath(likeCatePath)
                    .build();
            List<VideoResourceCate> ResourceCateList = videoResourceCateRepository.findAll
                    (VideoResourceCateWhereCriteriaBuilder.build(resourceCate1));
            if (CollectionUtils.isNotEmpty(ResourceCateList)) {
                ResourceCateList.stream().forEach(resourceCate2 -> {
                    resourceCate2.setCatePath(resourceCate2.getCatePath().replace(likeCatePath, newCatePath));
                    resourceCate2.setCateGrade(resourceCate2.getCatePath().split("\\" + SPLIT_CHAR).length - 1);
                    resourceCate2.setUpdateTime(LocalDateTime.now());
                });
            }
            this.videoResourceCateRepository.saveAll(ResourceCateList);
        }

        //更新分类
        newResourceCate.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(newResourceCate, oldResourceCate);
        return videoResourceCateRepository.save(oldResourceCate);
    }

    /**
     * 单个删除店铺资源资源分类表
     *
     * @author lq
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String cateId) {
        VideoResourceCate videoResourceCate = videoResourceCateRepository.findByCateId(cateId);
        if (videoResourceCate == null || videoResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.INVALID_PARAMETER);
        }
        //查询分类下的子分类并进行逻辑删除
        deleteByCateParentId(cateId,videoResourceCate);
    }

    /**
     * 根据父类删除父类下面的子类数据
     * @param cateId
     * @param videoResourceCate
     */
    public void deleteByCateParentId(String cateId, VideoResourceCate videoResourceCate) {
        //查询分类下的子分类
        VideoResourceCateQueryRequest resourceCate1 = VideoResourceCateQueryRequest.builder()
                .cateParentId(cateId)
                .isDefault(DefaultFlag.NO).build();

        List<VideoResourceCate> childCateList = videoResourceCateRepository.findAll
                (VideoResourceCateWhereCriteriaBuilder.build(resourceCate1));
        //子分类不为空 则进行逻辑删除
        if (CollectionUtils.isNotEmpty(childCateList)) {
            childCateList.stream().forEach(cate -> {
                cate.setDelFlag(DeleteFlag.YES);
                videoResourceCateRepository.saveAll(childCateList);
                //递归删除子类分类ID关联的视频资源
                this.deleteResourceByCateId(cate.getCateId());
                //递归删除子集
                if (cate.getCateGrade() < GRADE_MAX) {
                    this.deleteByCateParentId(cate.getCateId(),cate);
                }
            });
        }
        //逻辑删除父分类
        videoResourceCate.setDelFlag(DeleteFlag.YES);
        videoResourceCateRepository.saveAndFlush(videoResourceCate);
        //递归删除父类分类ID关联的视频资源
        this.deleteResourceByCateId(cateId);
    }

    public void deleteResourceByCateId(String cateId) {
        //查询资源分类下的视频数据 进行逻辑删除
        List<VideoResource> byIdList = videoResourceRepository.findByIdList(Arrays.asList(cateId));
        if (CollectionUtils.isNotEmpty(byIdList)) {
            List<Long> resourceIds = byIdList.stream().map(VideoResource::getResourceId).collect(Collectors.toList());
            videoResourceRepository.deleteByIdList(resourceIds,0L);
        }
    }


    /**
     * 单个查询视频教程资源资源分类表
     *
     * @author hudong
     */
    public VideoResourceCate getById(String id) {
        return videoResourceCateRepository.findByCateId(id);
    }

    /**
     * 批量查询视频教程资源资源分类表
     * @param newMenuInfoVOS
     * @return
     */
    public List<VideoResourceCate> findByIdList(List<NewMenuInfoVO> newMenuInfoVOS) {
        List<String> cateIdList = newMenuInfoVOS.stream().map(NewMenuInfoVO::getId).collect(Collectors.toList());
        List<VideoResourceCate> videoResourceCateList = this.list(VideoResourceCateQueryRequest
                .builder().cateIdList(cateIdList)
                .delFlag(DeleteFlag.NO).build());
        return videoResourceCateList;
    }


    /**
     * 初始化视频教程资源资源分类表
     * @param menuInfoVOS
     * @return
     */
    public void initMenuInfo(List<MenuInfoVO> menuInfoVOS) {
        //过滤出父级节点集合
        List<MenuInfoVO> parentMenuInfoVOS = menuInfoVOS.stream().filter(m-> m.getGrade() == 2).collect(Collectors.toList());
        //查询父级节点的cate集合
        List<VideoResourceCate> parentVideoResourceCateList = this.list(VideoResourceCateQueryRequest
                .builder()
                .cateIdList(getIdList(parentMenuInfoVOS))
                .delFlag(DeleteFlag.NO).build());
        //目录id父集合数据与cateId集合二级节点数据一致 无需同步
        if(!validNum(parentVideoResourceCateList,parentMenuInfoVOS)) {
            //同步父级节点
            syncMenuInfoToCate(parentMenuInfoVOS,parentVideoResourceCateList);
        }
        //过滤出子级节点集合
        List<MenuInfoVO> childrenMenuInfoVOS = menuInfoVOS.stream().filter(m-> m.getGrade() == 3).collect(Collectors.toList());
        //查询子级节点的cate集合
        List<VideoResourceCate> childrenVideoResourceCateList = this.list(VideoResourceCateQueryRequest
                .builder()
                .cateIdList(getIdList(childrenMenuInfoVOS))
                .delFlag(DeleteFlag.NO).build());
        //目录id子集合数据与cateId集合三级节点数据一致 无需同步
        if(!validNum(childrenVideoResourceCateList,childrenMenuInfoVOS)) {
            //同步子集节点
            syncMenuInfoToCate(childrenMenuInfoVOS,childrenVideoResourceCateList);
        }
    }

    /**
     * 同步目录菜单到资源表
     * @param menuInfoVOS
     * @param records
     */
    public void syncMenuInfoToCate(List<MenuInfoVO> menuInfoVOS,List<VideoResourceCate> records) {
        //如果同步的记录不为空 则需要过滤出新增的需要同步的目录信息进行同步 否则 全量同步
        if(CollectionUtils.isNotEmpty(records)){
            //保存记录到Map集合中
            Map<String,MenuInfoVO> menuIds = menuInfoVOS.stream().collect(Collectors.toMap(menuInfoVO->menuInfoVO.getId(),menuInfoVO ->menuInfoVO));
            //找出menuInfoVOS集合中存在 records中不存在的menuId进行增加同步
            List<String> diffMenuIds = menuInfoVOS.stream().map(MenuInfoVO::getId)
                    .filter(e -> !records.stream().map(VideoResourceCate::getCateId).anyMatch(e2 -> e.equals(e2)))
                    .collect(Collectors.toList());
            diffMenuIds.forEach( menuId-> {
                this.initVideoResourceCate(menuIds.get(menuId));
            });

            //找出records集合中存在 menuInfoVOS中不存在的menuId进行逻辑删除同步
            List<String> diffRecordMenuIds = records.stream().map(VideoResourceCate::getCateId)
                    .filter(e -> !menuInfoVOS.stream().map(MenuInfoVO::getId).anyMatch(e2 -> e.equals(e2)))
                    .collect(Collectors.toList());
            diffRecordMenuIds.forEach( menuId-> {
                this.delete(menuId);
            });
        } else {
            menuInfoVOS.forEach( menuInfoVO -> {
                this.initVideoResourceCate(menuInfoVO);
            });
        }
    }

    /**
     * 分页查询视频教程资源资源分类表
     *
     * @author hudong
     */
    public Page<VideoResourceCate> page(VideoResourceCateQueryRequest queryReq) {
        return videoResourceCateRepository.findAll(
                VideoResourceCateWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询视频教程资源资源分类表
     *
     * @author hudong
     */
    public List<VideoResourceCate> list(VideoResourceCateQueryRequest queryReq) {
        return videoResourceCateRepository.findAll(
                VideoResourceCateWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 将实体包装成VO
     *
     * @author hudong
     */
    public VideoResourceCateVO wrapperVo(VideoResourceCate videoResourceCate) {
        if (videoResourceCate != null) {
            VideoResourceCateVO videoResourceCateVO = new VideoResourceCateVO();
            KsBeanUtil.copyPropertiesThird(videoResourceCate, videoResourceCateVO);
            return videoResourceCateVO;
        }
        return null;
    }

    /**
     * 验证是否有子类
     *
     * @param cateId  素材分类id
     * @param storeId 店铺id
     */
    public Integer checkChild(String cateId, Long storeId) {
        VideoResourceCate videoResourceCate = videoResourceCateRepository.findByCateIdAndStoreId(cateId, storeId);
        if (videoResourceCate == null || videoResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.INVALID_PARAMETER);
        }

        String oldCatePath = videoResourceCate.getCatePath().concat(String.valueOf(videoResourceCate.getCateId())
                .concat(SPLIT_CHAR));
        VideoResourceCateQueryRequest resourceCateQueryRequest = VideoResourceCateQueryRequest.builder()
                .delFlag(DeleteFlag.NO).likeCatePath(oldCatePath).storeId(storeId).build();
        if (videoResourceCateRepository.count(VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest))
                > 0) {
            return DefaultFlag.YES.toValue();
        }
        return DefaultFlag.NO.toValue();
    }

    /**
     * 验证是否有素材
     *
     * @param cateId  素材分类id
     * @param storeId 店铺id
     */
    public Integer checkResource(String cateId, Long storeId) {
        VideoResourceCate videoResourceCate = videoResourceCateRepository.findByCateIdAndStoreId(cateId, storeId);
        if (videoResourceCate == null || videoResourceCate.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.INVALID_PARAMETER);
        }
        List<String> allCate = new ArrayList<>();
        allCate.add(videoResourceCate.getCateId());
        String oldCatePath = videoResourceCate.getCatePath().concat(String.valueOf(videoResourceCate.getCateId()))
                .concat(SPLIT_CHAR);

        VideoResourceCateQueryRequest resourceCateQueryRequest = VideoResourceCateQueryRequest.builder()
                .delFlag(DeleteFlag.NO).likeCatePath(oldCatePath).storeId(storeId).build();
        List<VideoResourceCate> childCateList = videoResourceCateRepository.findAll
                (VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest));
        if (CollectionUtils.isNotEmpty(childCateList)) {
            childCateList.stream().forEach(cate -> {
                allCate.add(cate.getCateId());
            });
        }
        //素材
        VideoResourceQueryRequest resource = VideoResourceQueryRequest.builder()
                .delFlag(DeleteFlag.NO).cateIds(allCate).storeId(storeId).build();
        if (videoResourceRepository.count(VideoResourceWhereCriteriaBuilder.build(resource)) > 0) {
            return DefaultFlag.YES.toValue();
        }
        return DefaultFlag.NO.toValue();
    }


    /**
     * 初始化分类，生成默认分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void init(VideoResourceCateInitRequest videoResourceCate) {
        VideoResourceCateQueryRequest resourceCateQueryRequest = VideoResourceCateQueryRequest.builder()
                .storeId(videoResourceCate.getStoreId())
                .companyInfoId(videoResourceCate.getCompanyInfoId())
                .cateParentId(videoResourceCate.getCateParentId()).build();
        List<VideoResourceCate> resourceCateList = videoResourceCateRepository.findAll
                (VideoResourceCateWhereCriteriaBuilder.build(resourceCateQueryRequest));

        if (CollectionUtils.isEmpty(resourceCateList)) {
            VideoResourceCate videoResourceCate1 = new VideoResourceCate();
            videoResourceCate1.setCateName("默认分类");
            videoResourceCate1.setCateParentId(CateParentTop.ZERO.toString());
            videoResourceCate1.setIsDefault(DefaultFlag.YES);
            videoResourceCate1.setDelFlag(DeleteFlag.NO);
            videoResourceCate1.setCateGrade(1);
            videoResourceCate1.setCatePath(String.valueOf(videoResourceCate.getCateParentId()).concat(SPLIT_CHAR));
            videoResourceCate1.setCompanyInfoId(videoResourceCate.getCompanyInfoId());
            videoResourceCate1.setStoreId(videoResourceCate.getStoreId());
            videoResourceCate1.setSort(0);
            videoResourceCate1.setCreateTime(LocalDateTime.now());
            videoResourceCate1.setUpdateTime(LocalDateTime.now());
            videoResourceCateRepository.save(videoResourceCate1);
        }
    }

    /**
     * 初始化目录分类
     * @param menuInfoVO
     * @return
     */
    public VideoResourceCate initVideoResourceCate(MenuInfoVO menuInfoVO){
        VideoResourceCate videoResourceCate = new VideoResourceCate();
        //设置主键的值
        videoResourceCate.setCateId(menuInfoVO.getId());
        videoResourceCate.setCateName(menuInfoVO.getTitle());
        videoResourceCate.setCateParentId(menuInfoVO.getPid());
        videoResourceCate.setIsDefault(DefaultFlag.YES);
        videoResourceCate.setDelFlag(DeleteFlag.NO);
        //设置父子级 目录id为2 则是父级 3则为子集
        videoResourceCate.setCateGrade(menuInfoVO.getGrade() == 2 ? 1 : 2);
        videoResourceCate.setCatePath(String.valueOf(videoResourceCate.getCateParentId()).concat(SPLIT_CHAR));
        videoResourceCate.setCompanyInfoId(0L);
        videoResourceCate.setStoreId(0L);
        videoResourceCate.setSort(0);
        videoResourceCate.setCreateTime(LocalDateTime.now());
        videoResourceCate.setUpdateTime(LocalDateTime.now());
        videoResourceCate = add(videoResourceCate);
        return videoResourceCate;
    }

    /**
     * 获取list集合中的id集合
     * @param menuInfoVOS
     * @return
     */
    private List<String> getIdList(List<MenuInfoVO> menuInfoVOS){
        return menuInfoVOS.stream().map(MenuInfoVO::getId).collect(Collectors.toList());
    }

    /**
     * 校验目录菜单与资源表菜单是否一致
     * @param videoResourceCateList
     * @param menuInfoVOS
     * @return
     */
    private Boolean validNum(List<VideoResourceCate> videoResourceCateList,List<MenuInfoVO> menuInfoVOS){
        if(CollectionUtils.isNotEmpty(videoResourceCateList) && menuInfoVOS.size() == videoResourceCateList.size()){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
