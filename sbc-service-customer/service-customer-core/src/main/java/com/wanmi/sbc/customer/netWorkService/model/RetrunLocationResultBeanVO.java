package com.wanmi.sbc.customer.netWorkService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrunLocationResultBeanVO implements Serializable {
      private static final long serialVersionUID = -1755280444491292875L;
      private RetrunLocationJWBeanVO location;
      private int precise;//位置的附加信息，是否精确查找。1为精确查找，即准确打点；0为不精确，即模糊打点。
      private int confidence;//描述打点绝对精度（即坐标点的误差范围）。
      private int comprehension;//描述地址理解程度。分值范围0-100，分值越大，服务对地址理解程度越高（建议以该字段作为解析结果判断标准）；
      private String level;//可以打点到地址文本中的真实地址结构，

}
