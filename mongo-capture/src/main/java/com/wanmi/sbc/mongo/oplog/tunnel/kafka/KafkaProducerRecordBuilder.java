package com.wanmi.sbc.mongo.oplog.tunnel.kafka;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wanmi.sbc.mongo.oplog.data.OplogData;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-08-15 10:36
 */
public class KafkaProducerRecordBuilder {


    private static Cache<String, String> topicCache = CacheBuilder.newBuilder().build();

    /**
     * 通过缓存获取动态topic配置
     * @param dynamicTopic
     * @return
     */
    public static Cache<String,String> getTopicCache(String dynamicTopic){
        if(topicCache.size()<=0){
            if(StringUtils.isNotBlank(dynamicTopic)) {
                String[] router = StringUtils.split(StringUtils.replace(dynamicTopic, ",", ";"), ";");
                Set<String> topics = new HashSet<>();
                for (String item : router) {
                    int i = item.indexOf(":");
                    if (i > -1) {
                        String topic = item.substring(0, i).trim();
                        String topicConfigs = item.substring(i + 1).trim();
                        topicCache.put(topic, topicConfigs);
                    }
                }
            }
        }
        return topicCache;
    }

    /**
     * 根据oplog生成消息对象(topic为database.collection)
     * 使用默认分区器
     * @param oplogData
     * @return
     */
    public static ProducerRecord<String,String> generate(OplogData oplogData,String dynamicTopic){



        return new ProducerRecord<>(getTopic(oplogData,dynamicTopic),
                         JSONObject.toJSONString(oplogData));

    }


    public static ProducerRecord<String,String> generateByPartition(OplogData oplogData,String dynamicTopic,int partition){
        return new ProducerRecord<>(
                getTopic(oplogData,dynamicTopic),
                (oplogData.get_id().hashCode()& 0x7fffffff)%partition,  //注意hashCode有可能为负值所以需要转化
                null,
                JSONObject.toJSONString(oplogData));
    }

    /**
     * 根据配置动态获取topic
     * @param oplogData
     * @param dynamicTopic
     * @return
     */
    public static String getTopic(OplogData oplogData , String dynamicTopic){
        String name = new StringBuffer()
                .append(oplogData.getDatabase())
                .append(".")
                .append(oplogData.getCollection())
                .toString();
        String topic = getTopicCache(dynamicTopic).getIfPresent(name);
        return StringUtils.isBlank(topic)?name:topic;//配置不存在默认是用database+"."+collection
    }



}
