package com.wanmi.sbc.returnorder.cache;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Invoice;
import com.wanmi.sbc.returnorder.trade.model.entity.value.Supplier;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EhCacheKeyGenerator implements KeyGenerator {


    @Override
    public Object generate(Object targetClass, Method method, Object... params) {
        // 这里可用HashMap
        Map<String,Object> container = new HashMap<>();
        Class<?> targetClassClass = targetClass.getClass();
        Package pack = targetClassClass.getPackage();
        String clazz = targetClassClass.toGenericString();
        String methodName = method.getName();
        // 包名称
        container.put("package",pack);
        // 类地址
        container.put("class",clazz);
        // 方法名称
        container.put("methodName",methodName);

        //会员校验
        if(methodName.equals("verifyCustomer")) {
            container.put(String.valueOf(0),params[0]);
        }

        //发票校验
        if(methodName.equals("verifyInvoice")) {
            if(params[0] instanceof Invoice) {
                Invoice invoice = (Invoice) params[0];
                container.put(String.valueOf(0),invoice.getType());
            }
            if(params[1] instanceof Supplier) {
                Supplier supplier = (Supplier) params[1];
                container.put(String.valueOf(1),supplier.getSupplierId());
            }
        }
        //店铺校验
        if(methodName.equals("verifyStore")) {
            if(params[0] instanceof List) {
                List storeIds = (List) params[0];
                for(int i=0;i<storeIds.size();i++) {
                    container.put(String.valueOf(i),storeIds.get(i));
                }
            }
        }


        //店铺查询
        if(methodName.equals("queryStoreList")) {
            if(params[0] instanceof List) {
                List storeIds = (List) params[0];
                for(int i=0;i<storeIds.size();i++) {
                    container.put(String.valueOf(i),storeIds.get(i));
                }
            }
        }

        //收货地址
        if(methodName.equals("getCustomerDeliveryAddressById")) {
            container.put(String.valueOf(0),params[0]);
        }

        //发票
        if(methodName.equals("getCustomerInvoiceByIdAndDelFlag")) {
            container.put(String.valueOf(0),params[0]);
        }

        //商品
        if(methodName.equals("getGoodsInfoViewByIds")) {
            if(params[0] instanceof List) {
                List skuIds = (List) params[0];
                for(int i=0;i<skuIds.size();i++) {
                    container.put(String.valueOf(i),skuIds.get(i));
                }
            }
        }
        //查询会员详情
        if(methodName.equals("getCustomerDetailByCustomerId")) {
            container.put(String.valueOf(0), params[0]);
        }


        //查询签约分类
        if(methodName.equals("queryContractCateList")) {
            container.put(String.valueOf(0), params[0]);
            container.put(String.valueOf(1), params[1]);
        }



        //查询运费模板
        if(methodName.equals("queryFreightTemplateGoodsListByIds")) {
            if(params[0] instanceof List) {
                List tempIdList = (List) params[0];
                for(int i=0;i<tempIdList.size();i++) {
                    container.put(String.valueOf(i),tempIdList.get(i));
                }
            }
        }


        //查询系统配置
        if(methodName.equals("getTradeConfigByType")) {
            ConfigType configType = (ConfigType) params[0];
            container.put(String.valueOf(0), configType.toValue());
        }
        // 转为JSON字符串
        String jsonString = JSON.toJSONString(container);
        // 做SHA256 Hash计算，得到一个SHA256摘要作为Key
        return DigestUtils.sha256Hex(jsonString);
    }
}
