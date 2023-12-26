package com.wanmi.sbc.setting.api.request.videoresource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频教程资源库分页查询请求参数
 *
 * @author hudong
 * @date 2023-06-26 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourcePageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 批量查询-素材IDList
     */
    @ApiModelProperty(value = "批量查询-素材IDList")
    private List<Long> resourceIdList;

    /**
     * 素材ID
     */
    @ApiModelProperty(value = "素材ID")
    private Long resourceId;

    /**
     * 资源类型(0:图片,1:视频)
     */
    @ApiModelProperty(value = "资源类型(0:图片,1:视频)")
    private ResourceType resourceType;

    /**
     * 素材分类ID
     */
    @ApiModelProperty(value = "素材分类ID")
    private String cateId;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 商家标识
     */
    @ApiModelProperty(value = "商家标识")
    private Long companyInfoId;

    /**
     * 素材KEY
     */
    @ApiModelProperty(value = "素材KEY")
    private String resourceKey;

    /**
     * 素材名称
     */
    @ApiModelProperty(value = "素材名称")
    private String resourceName;

    /**
     * 素材地址
     */
    @ApiModelProperty(value = "素材地址")
    private String artworkUrl;

    /**
     * 搜索条件:创建时间开始
     */
    @ApiModelProperty(value = "搜索条件:创建时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeBegin;
    /**
     * 搜索条件:创建时间截止
     */
    @ApiModelProperty(value = "搜索条件:创建时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTimeEnd;

    /**
     * 搜索条件:更新时间开始
     */
    @ApiModelProperty(value = "搜索条件:更新时间开始")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeBegin;
    /**
     * 搜索条件:更新时间截止
     */
    @ApiModelProperty(value = "搜索条件:更新时间截止")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTimeEnd;

    /**
     * 删除标识,0:未删除1:已删除
     */
    @ApiModelProperty(value = "删除标识,0:未删除1:已删除")
    private DeleteFlag delFlag;

    /**
     * oss服务器类型，对应system_config的config_type
     */
    @ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
    private String serverType;


    @ApiModelProperty(value = "批量查询-素材分类id")
    private List<String> cateIds;

    public List<Long> getResourceIdList() {
        return this.resourceIdList;
    }

    public Long getResourceId() {
        return this.resourceId;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public String getCateId() {
        return this.cateId;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public Long getCompanyInfoId() {
        return this.companyInfoId;
    }

    public String getResourceKey() {
        return this.resourceKey;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public String getArtworkUrl() {
        return this.artworkUrl;
    }

    public LocalDateTime getCreateTimeBegin() {
        return this.createTimeBegin;
    }

    public LocalDateTime getCreateTimeEnd() {
        return this.createTimeEnd;
    }

    public LocalDateTime getUpdateTimeBegin() {
        return this.updateTimeBegin;
    }

    public LocalDateTime getUpdateTimeEnd() {
        return this.updateTimeEnd;
    }

    public DeleteFlag getDelFlag() {
        return this.delFlag;
    }

    public String getServerType() {
        return this.serverType;
    }

    public List<String> getCateIds() {
        return this.cateIds;
    }

    public void setResourceIdList(List<Long> resourceIdList) {
        this.resourceIdList = resourceIdList;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public void setCompanyInfoId(Long companyInfoId) {
        this.companyInfoId = companyInfoId;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    public void setCreateTimeBegin(LocalDateTime createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    public void setUpdateTimeBegin(LocalDateTime updateTimeBegin) {
        this.updateTimeBegin = updateTimeBegin;
    }

    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    public void setUpdateTimeEnd(LocalDateTime updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
    }

    public void setDelFlag(DeleteFlag delFlag) {
        this.delFlag = delFlag;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public void setCateIds(List<String> cateIds) {
        this.cateIds = cateIds;
    }

    public String toString() {
        return "VideoResourcePageRequest(resourceIdList=" + this.getResourceIdList() + ", resourceId=" + this.getResourceId() + ", resourceType=" + this.getResourceType() + ", cateId=" + this.getCateId() + ", storeId=" + this.getStoreId() + ", companyInfoId=" + this.getCompanyInfoId() + ", resourceKey=" + this.getResourceKey() + ", resourceName=" + this.getResourceName() + ", artworkUrl=" + this.getArtworkUrl() + ", createTimeBegin=" + this.getCreateTimeBegin() + ", createTimeEnd=" + this.getCreateTimeEnd() + ", updateTimeBegin=" + this.getUpdateTimeBegin() + ", updateTimeEnd=" + this.getUpdateTimeEnd() + ", delFlag=" + this.getDelFlag() + ", serverType=" + this.getServerType() + ", cateIds=" + this.getCateIds() + ")";
    }
}