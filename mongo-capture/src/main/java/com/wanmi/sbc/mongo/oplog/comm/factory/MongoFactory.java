package com.wanmi.sbc.mongo.oplog.comm.factory;

import com.mongodb.*;
import com.wanmi.sbc.mongo.oplog.config.MongoCollectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
   * @Description: 手动连接创建Mongo连接对象工厂
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:34
   */

@Component

public class MongoFactory {
    private static Logger log = LoggerFactory.getLogger(MongoFactory.class);
    private static final int DEFAULTPORT = 27017;


    private DBCollection dbCollection;

    /**
     * 通过配置文件获取连接信息，创建 DBCollection
     */

    public MongoFactory(MongoCollectionProperties properties){
        MongoClient mongoClient = build(properties);
        dbCollection =  mongoClient.getDB(properties.getDb()).getCollection(properties.getCollection());
    }

    public MongoClient build(MongoCollectionProperties properties) {

        try {
            List<ServerAddress> servers = getSeeds(properties);
            MongoCredential credentials = createCredentials(properties);

            MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
            MongoClientOptions options = optionsBuilder.build();

            log.info("Mongo database is {}", properties.getDb());
            if(credentials!=null) {
                return new MongoClient(servers, credentials, options);
            }else{
                return new MongoClient(servers,options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not configure MongoDB client.", e);
        }
    }


    MongoCredential createCredentials(MongoCollectionProperties properties) {
        MongoCredential credential = StringUtils.isEmpty(properties.getUserName())
                ? null: MongoCredential.createCredential(
                        properties.getUserName(),
                        StringUtils.isEmpty(properties.getAuthenticationDatabase()) ? properties.getDb() : properties.getAuthenticationDatabase(),
                        properties.getPassword().toCharArray()
                );

        if (credential == null) {
            log.info("Not found mongo credentials.");
        } else {

            log.info("Found mongo credential for {} on database {}.", credential.getUserName(), credential.getSource());
        }

        return credential;
    }


    private List<ServerAddress> getSeeds(MongoCollectionProperties properties) throws NumberFormatException, UnknownHostException {
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        String[] hostPorts = properties.getUri().split(",");
        String[] hp = null;
        String host = null;
        int port = 0;
        for (String hostPort : hostPorts) {
            hp = hostPort.split(":");
            if (hp == null || hp.length <= 0) {
                host = "127.0.0.1";
                port = DEFAULTPORT;
            } else {
                if (hp.length == 1) {
                    host = hp[0];
                    port = DEFAULTPORT;
                } else {
                    host = hp[0];
                    port = Integer.parseInt(hp[1]);
                }
            }
            seeds.add(new ServerAddress(host, port));
        }
        return seeds;
    }

    public DBCollection getDBCollection() {
        return this.dbCollection;
    }
}
