package com.wanmi.sbc.mongo.oplog.tunnel.kafka;

import com.wanmi.sbc.mongo.oplog.config.ClientConfig;
import com.wanmi.sbc.mongo.oplog.config.TunnelConfig;
import com.wanmi.sbc.mongo.oplog.data.OplogData;
import com.wanmi.sbc.mongo.oplog.tunnel.TunnelLauncher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description: kafka隧道实现
 * @Date: 2019-08-14 17:55
 */
@Component
public class KafkaTunnelLauncher implements TunnelLauncher {

    @Deprecated
    private Integer partition;

    private String dynamicTopic;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public KafkaTunnelLauncher(TunnelConfig config, ClientConfig clientConfig){
        this.partition = config.getPartition();
        this.dynamicTopic = clientConfig.getDynamicTopic();
    }

    @Override
    public void dispatch(OplogData oplogData) {
        //获取kafka的partition的数量
        List<PartitionInfo> partitionInfoList = kafkaTemplate.partitionsFor(KafkaProducerRecordBuilder.getTopic(oplogData,dynamicTopic));
        if(CollectionUtils.isNotEmpty(partitionInfoList)){
            sendPartition(oplogData,partitionInfoList.size());
        }else {
            send(oplogData);
        }
    }

    private void send(OplogData oplogData){
        ProducerRecord<String, String> producerRecord = KafkaProducerRecordBuilder.generate(oplogData,dynamicTopic);
        kafkaTemplate.send(producerRecord);
    }

    /**
     * 多partition发送，根据_id的hashCode对partition进行取模
     * @param oplogData
     * @param partition
     */
    private void sendPartition(OplogData oplogData,int partition){
        ProducerRecord<String, String> producerRecord = KafkaProducerRecordBuilder.generateByPartition(oplogData,dynamicTopic,partition);
        kafkaTemplate.send(producerRecord);
    }

}
