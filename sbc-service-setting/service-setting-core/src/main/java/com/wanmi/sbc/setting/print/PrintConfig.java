package com.wanmi.sbc.setting.print;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 系统配置实体类
 * Created by dyt on 2017/4/23.
 */
@Entity
@Table(name = "system_print_modal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintConfig implements Serializable {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "print_id")
    private Long printId;


    /**
     * 打印头部
     */
    @Column(name = "print_head")
    private String printHead;


    /**
     * 打印尾部
     */
    @Column(name = "print_bottom")
    private String printBottom;

}
