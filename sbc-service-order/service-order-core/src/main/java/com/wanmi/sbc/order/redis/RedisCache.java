package com.wanmi.sbc.order.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 新版Redis操作类
 */
@Component
public class RedisCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 缓存存储
     *
     * @param key
     * @param value
     * @param seconds
     * @return void
     */
    public void set(String key, String value, int seconds){
        ValueOperations<String,String> vo = redisTemplate.opsForValue();
        if(seconds > 0){
            vo.set(key, value, seconds, TimeUnit.SECONDS);
        }else{
            vo.set(key, value);
        }
    }

    /**
     * 缓存获取
     *
     * @param key
     * @return java.lang.String
     */
    public String get(String key){
        ValueOperations<String,String> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    /**
     * 缓存手动失效
     *
     * @param key
     * @return boolean
     */
    public boolean delete(String key){
        return redisTemplate.delete(key);
    }



    /**
     * 缓存存储并设置过期时间
     *
     * @param key
     * @param value
     * @param time
     * @return void
     */
    public void setex(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);

    }

    /**
     * 缓存批量获取
     *
     * @param keyList
     * @return java.util.List
     */
    public List mget(List<String> keyList) {
        return redisTemplate.opsForValue().multiGet(keyList);
    }

    /**
     * 删除key下的多个值
     *
     * @param key
     * @param values
     * @return void
     */
    public void srem(String key, String[] values) {
        redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 删除key下的多个值
     *
     * @param key
     * @param values
     * @return void
     */
    public void sadd(String key, String[] values) {
        redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 缓存成员获取
     *
     * @param key
     * @return java.util.Set
     */
    public Set smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存成员是否存在
     *
     * @param key
     * @param member
     * @return java.lang.Boolean
     */
    public Boolean sismember(String key, String member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 缓存有序区间值
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return java.util.Set
     */
    public Set zrangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 缓存有序区间值
     *
     * @param key
     * @param min
     * @param max
     * @return java.util.Set
     */
    public Set zrangeByScore2(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 倒序返回zset区间值
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set zrevrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 缓存倒序排列指定区间值
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return java.util.Set
     */
    public Set zrevrangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * 缓存有序存储
     *
     * @param key
     * @param member
     * @param score
     * @return java.lang.Boolean
     */
    public Boolean zadd(String key, String member, double score) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 缓存有序存储
     *
     * @param key
     * @param values
     * @return java.lang.Long
     */
    public Long zremove(String key, String... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 缓存有序数量
     *
     * @param key
     * @return java.lang.Long
     */
    public Long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }



    /**
     * 设置key过期时间
     * @param key
     * @param time
     * @param unit
     */
    public void expire(String key, long time, TimeUnit unit) {
        redisTemplate.expire(key, time, unit);
    }

    /**
     * 以list集合的形式添加数据
     *
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 以list集合的形式添加数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long lPushAll(String key, String... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 以list集合的形式添加数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long lPushAll(String key, List<String> values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 以list集合的形式添加数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long rPushAll(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 以list集合的形式添加数据
     *
     * @param key
     * @param values
     * @return
     */
    public Long rPushAll(String key, List<String> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 返回list集合下表区间的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 返回list集合的大小
     *
     * @param key
     * @return
     */
    public Long lsize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key
     * @return
     */
    public void expire(String key, long time) {
        this.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 执行lua脚本
     *
     * @param script
     * @param keys
     * @param args
     * @param <T>
     * @return
     */
    public <T> T execute(RedisScript<T> script, List<String> keys, String... args) {
        return (T) redisTemplate.execute(script, keys, args);
    }

    /**
     * 缓存存储并设置过期时间
     *
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     * @return void
     */
    public void setex(String key, String value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * 缓存增量
     *
     * @param key
     * @param increment
     * @return java.lang.Long
     */
    public Long increment(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }
    /**
     * 缓存减量
     *
     * @param key
     * @param decrement
     * @return java.lang.Long
     */
    public Long decrement(String key, long decrement) {
        return redisTemplate.opsForValue().decrement(key, decrement);
    }
    /**
     * 缓存失效时间
     *
     * @param key
     * @param timeUnit
     * @return java.lang.Long
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key
     * @param date
     * @return java.lang.Boolean
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 获取hash多field值
     * @param key
     * @param list
     * @return
     */
    public List<String> multiGet(String key, List<String> list) {
        List list1 = redisTemplate.opsForHash().multiGet(key, list);
        Iterator it = list1.iterator();
        while(it.hasNext()){
            String string = (String) it.next();

            if (Objects.isNull(string) || "null".equalsIgnoreCase(string)){
                it.remove();
            }
        }
        return list1;
    }

    //获取hash里面所有的key
    public Set HashKeys(String key){
        return  redisTemplate.opsForHash().keys(key);
    }


    public Boolean HashHasKey(String key,String nkey){
      return   redisTemplate.opsForHash().hasKey(key,nkey);
    }

    /**
     * 获取hash 内key值
     * @param key
     * @param nkey
     * @return
     */
    public Object HashGet (String key, String nkey){
        return  redisTemplate.opsForHash().get(key,nkey);
    }




    public List<String> hMultiGet(String key, List<String> list) {
        return redisTemplate.opsForHash().multiGet(key, list);
    }

    /**
     * 获取key对应的所有map键值对
     * @param key
     * @return
     */
    public Map HashGetAll (String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取hash变量中的键值对
     * 对应redis hgetall 命令
     *
     * @param key
     * @return
     */
    public Map<String, String> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 新增hash值
     *
     * @param key
     * @param field
     * @param value
     * @return java.lang.Boolean
     */
    public Boolean putIfAbsent(String key, String field, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }
    public void put(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * hash 增加
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double incrementMap(String key, String field,double delta){
        return redisTemplate.opsForHash().increment(key,field,delta);
    }


    /**
     * 批量新增hash值
     * @param key

     * @param map
     */
    public void setHashAll(String key,  Map map){
          redisTemplate.opsForHash().putAll(key,map);
    }

    /**
     * 以map集合的形式添加hash键值对
     *
     * @param key
     * @param map
     */
    public void hPutAll(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    
    public Long hashDel(String key,String nkey){
        return redisTemplate.opsForHash().delete(key, nkey);
    }

    public Long hLen(String key){
        return redisTemplate.opsForHash().size(key);
    }

    public String hGet (String key,String nkey){
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return  opsForHash.get(key,nkey);
    }

    /**
     * 判断hash field是否存在
     *
     * @return
     */
    public boolean hFieldExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * scan实现
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    private void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Set<String> scan(String pattern) {
        Set<String> keys = new HashSet<>();
        this.scan(pattern, item -> {
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

}
