package com.wanmi.sbc.setting.systemfile.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.bean.enums.FileType;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台文件实体类
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@Data
@Entity
@Table(name = "system_file")
public class SystemFile implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 资源类型(0:图片,1:视频)
	 */
	@Column(name = "type")
	private FileType type;


	/**
	 * 文件KEY
	 */
	@Column(name = "file_key")
	private String fileKey;

	/**
	 * 文件名称
	 */
	@Column(name = "file_name")
	private String fileName;

	/**
	 * 文件地址
	 */
	@Column(name = "path")
	private String path;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * oss服务器类型，对应system_config的config_type
	 */
	@Column(name = "server_type")
	private String serverType;

}