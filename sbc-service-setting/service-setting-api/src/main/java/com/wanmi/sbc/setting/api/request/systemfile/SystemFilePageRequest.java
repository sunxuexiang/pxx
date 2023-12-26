package com.wanmi.sbc.setting.api.request.systemfile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.FileType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台文件分页查询请求参数
 *
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFilePageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 批量查询-IDList
     */
    @ApiModelProperty(value = "批量查询-IDList")
    private List<Long> idList;

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 资源类型(0:zip文件, 1:图片,2:视频)
     */
    @ApiModelProperty(value = "资源类型(0:zip文件, 1:图片,2:视频)")
    private FileType type;


    /**
     * 文件KEY
     */
    @ApiModelProperty(value = "文件KEY")
    private String fileKey;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String path;

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


    public List<Long> getIdList() {
        return this.idList;
    }

    public Long getId() {
        return this.id;
    }

    public FileType getType() {
        return this.type;
    }

    public String getFileKey() {
        return this.fileKey;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getPath() {
        return this.path;
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

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
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


    public String toString() {
        return "SystemFilePageRequest(idList=" + this.getIdList() + ", id=" + this.getId() + ", type=" + this.getType() +  ", fileKey=" + this.getFileKey() + ", fileName=" + this.getFileName() + ", path=" + this.getPath() + ", createTimeBegin=" + this.getCreateTimeBegin() + ", createTimeEnd=" + this.getCreateTimeEnd() + ", updateTimeBegin=" + this.getUpdateTimeBegin() + ", updateTimeEnd=" + this.getUpdateTimeEnd() + ", delFlag=" + this.getDelFlag() + ", serverType=" + this.getServerType() + ")";
    }
}