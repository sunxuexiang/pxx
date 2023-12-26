package com.wanmi.sbc.wms.record.model.root;

import lombok.experimental.Accessors;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>请求记录实体类</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "record")
public class Record implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 记录主键
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Long recordId;

	/**
	 * 请求类型
	 */
	@Column(name = "method")
	private Integer method;//1:post 2:put

	/**
	 * 请求的地址
	 */
	@Column(name = "request_url")
	private String requestUrl;

	/**
	 * 请求的实体
	 */
	@Column(name = "request_body")
	private String requestBody;

	/**
	 * 返回的信息
	 */
	@Column(name = "respose_info")
	private String resposeInfo;

	/**
	 * 返回的状态
	 */
	@Column(name = "status")
	private String status;

	/**
	 * 请求时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

}