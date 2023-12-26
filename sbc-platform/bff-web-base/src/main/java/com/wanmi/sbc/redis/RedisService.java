package com.wanmi.sbc.redis;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 *
 * @author djk
 */
@Service
public class RedisService {

    private static final String SIGNED = "SIGNED#";

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 查询当天是否有签到
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public boolean checkSign(String cacheKey, LocalDate localDate) {
        return redisTemplate.opsForValue().getBit(cacheKey, localDate.getDayOfMonth() - 1);
    }

    /**
     * 获取用户签到次数
     */
    public long getSignCount(String cacheKey) {
        Long bitCount = (Long) redisTemplate.execute((RedisCallback) cbk -> cbk.bitCount(cacheKey.getBytes()));
        return bitCount;
    }

    /**
     * 获取本月签到信息
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public List<String> getSignInfo(String cacheKey, LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        int lengthOfMonth = localDate.getDayOfMonth();
        List<Long> bitFieldList = (List<Long>) redisTemplate.execute((RedisCallback<List<Long>>) cbk
                -> cbk.bitField(cacheKey.getBytes(), BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(lengthOfMonth)).valueAt(0)));
        if (bitFieldList != null && bitFieldList.size() > 0) {
            long valueDec = bitFieldList.get(0) != null ? bitFieldList.get(0) : 0;
            for (int i = lengthOfMonth; i > 0; i--) {
                //获得这个月的第几天的时间
                LocalDate tempDayOfMonth = LocalDate.now().withDayOfMonth(i);
                if (valueDec >> 1 << 1 != valueDec) {
                    resultList.add(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                valueDec >>= 1;
            }
        }
        return resultList;
    }


    /**
     * 获取本月签到信息
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public List<String> getLastMonthSignInfo(String cacheKey, LocalDate localDate) {
        List<String> resultList = new ArrayList<>();
        int lengthOfMonth = localDate.getDayOfMonth();
        List<Long> bitFieldList = (List<Long>) redisTemplate.execute((RedisCallback<List<Long>>) cbk
                -> cbk.bitField(cacheKey.getBytes(), BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(lengthOfMonth)).valueAt(0)));
        if (bitFieldList != null && bitFieldList.size() > 0) {
            long valueDec = bitFieldList.get(0) != null ? bitFieldList.get(0) : 0;
            for (int i = lengthOfMonth; i > 0; i--) {
                //获得这个月的第几天的时间
                LocalDate tempDayOfMonth = localDate.withDayOfMonth(i);
                if (valueDec >> 1 << 1 != valueDec) {
                    resultList.add(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                valueDec >>= 1;
            }
        }
        return resultList;
    }

    /**
     * 获取本周签到信息
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public List<String> getThisWeekInSignfo(String cacheKey, LocalDate localDate) {
        //本周第一天
        LocalDate weekStart = LocalDate.parse(getWeekStart());
        //本周最后一天
        LocalDate weekEnd = LocalDate.parse(getWeekEnd());

        //今天是本月的第几天
        int todayOfMonth = LocalDate.now().getDayOfMonth();
        //今天是本周的第几天
        int weekByChooseDay = getWeekByChooseDay();

        //本月第一天
        LocalDate minDate = LocalDate.parse(getMinDate());
        //本月最后一天
        LocalDate manxDate = LocalDate.parse(getMaxDate());

        //获取传入时间的上个月的最后一周的开始时间
        String lastMonthLastWeek = lastMonthLastWeek(new Date());
        System.out.println("lastMonthLastWeek:" + lastMonthLastWeek);
        int dayOfMonth = weekStart.getDayOfMonth();
        //判断是否是月份交接日期，如果是的就需要查出上月最后一周的连续签到信息,月份交接日期判断条件：
        //本周最后一天的月份信息
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        String thisWeekEnd = new SimpleDateFormat("yyyy-MM").format(time);
        //当前时间的月份信息
        String currentMonth = new SimpleDateFormat("yyyy-MM").format(new Date());
        //当今天在本周的天数>今天在本月的天数,说明为月份交接期,需要特殊处理
        //先查出上月最后一周的签到信息,再查出这个月第一周的签到信息,为本周签到信息
        if (weekByChooseDay > todayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-1);
            //获取上个月
            String lastMonth = new SimpleDateFormat("yyyyMM").format(calendar.getTime());
            //获取上个月最后一周的签到信息
            String[] split = cacheKey.split("#");
            //上个月的key
            String lastMonthKey = split[0] + "#" +split[1] + "#" + lastMonth;
            //上一个月倒数第二周的最后一天
            LocalDate lastMonthLastWeekToLast = LocalDate.parse(lastMonthLastWeekToLast(new Date()));
            //上个月最后一天
            LocalDate lastMaxDate = LocalDate.parse(getLastMaxDate());
            //上个月所有的
            List<String> signInfo = getLastMonthSignInfo(lastMonthKey, lastMaxDate);
            //上个月倒数第二周以前的
            List<String> signInfo1 = getLastMonthSignInfo(lastMonthKey, lastMonthLastWeekToLast);
            signInfo.removeAll(signInfo1);
            //获取这个月的签到信息
            List<String> stringList = getSignInfo(cacheKey, LocalDate.now());
            stringList.addAll(signInfo);
            return stringList;
        } else {
            //说明本周今天和本周最后一天不是一个月,需要根据当前月份最后一天查询签到信息
            if (!thisWeekEnd.equals(currentMonth)) {
                List<String> weekEndInfo = getSignInfo(cacheKey, manxDate);
                int i = weekStart.getDayOfMonth() - 1;
                LocalDate localDate1 = LocalDate.now().withDayOfMonth(i);
                //再根据上周最后一天获取签到信息
                List<String> weekStartInfo = getSignInfo(cacheKey, localDate1);
                //再取差集
                weekEndInfo.removeAll(weekStartInfo);
                return weekEndInfo;
            } else {
                //当本周第一天不是等于本月第一天的时候说明有上一周
                if (dayOfMonth > 1) {
                    //需要判断本周的最后一天是不是不等于本月,如果不等于本月,则说明是下一个月,如果是下一个月,需要分别获取
                    //本周最后一天的月份
                    //先根据本周最后一天查询签到信息
                    List<String> weekEndInfo = getSignInfo(cacheKey, weekEnd);
                    int i = weekStart.getDayOfMonth() - 1;
                    LocalDate localDate1 = LocalDate.now().withDayOfMonth(i);
                    //再根据上周最后一天获取签到信息
                    List<String> weekStartInfo = getSignInfo(cacheKey, localDate1);
                    //再取差集
                    weekEndInfo.removeAll(weekStartInfo);
                    return weekEndInfo;
                } else {
                    return getSignInfo(cacheKey, localDate);
                }
            }
        }

    }

    /**
     * 根据时间段获取本月签到信息
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public List<String> getSignInfo(String cacheKey, LocalDate localDate, LocalDate localDate2) {
        List<String> resultList = new ArrayList<>();
        int lengthOfMonth = localDate.getDayOfMonth();
        int dayOfMonth = localDate2.getDayOfMonth();
        List<Long> bitFieldList = (List<Long>) redisTemplate.execute((RedisCallback<List<Long>>) cbk
                -> cbk.bitField(cacheKey.getBytes(), BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(lengthOfMonth)));
        if (bitFieldList != null && bitFieldList.size() > 0) {
            long valueDec = bitFieldList.get(0) != null ? bitFieldList.get(0) : 0;
            for (int i = lengthOfMonth; i > 0; i--) {
                //获得这个月的第几天的时间
                LocalDate tempDayOfMonth = LocalDate.now().withDayOfMonth(i);
                if (valueDec >> 1 << 1 != valueDec) {
                    resultList.add(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                valueDec >>= 1;
            }
        }
        return resultList;
    }


    /**
     * 签到操作
     */
    public boolean doSign(String cacheKey, LocalDate localDate) {
        if (localDate == null) {
            localDate = LocalDate.now();
        }
        //localDate 是 本月的第 dayOfMonth 天
        int currOffset = localDate.get(ChronoField.DAY_OF_MONTH) - 1;
        return redisTemplate.opsForValue().setBit(cacheKey, currOffset, true);
    }


    /**
     * 获取当月连续签到次数
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public long getContinuousSignCount(String cacheKey, LocalDate localDate) {
        long signCount = 0;
        List<Long> list = redisTemplate.opsForValue().bitField(cacheKey, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType
                .unsigned(localDate.getDayOfMonth())).valueAt(0));
        if (list != null && list.size() > 0) {
            long valueDec = list.get(0) != null ? list.get(0) : 0;
            for (int i = 0; i < localDate.getDayOfMonth(); i++) {
                if (valueDec >> 1 << 1 == valueDec) {
                    if (i > 0) {
                        break;
                    }
                } else {
                    signCount += 1;
                }
                valueDec >>= 1;
            }
        }
        return signCount;
    }

    /**
     * 获取本周的连续签到数量
     *
     * @param cacheKey
     * @param localDate
     * @return
     */
    public long getWeekContinuousSignCount(String cacheKey, LocalDate localDate) {
        //今天是本月的第几天
        int monthByChooseDay = getMonthByChooseDay();

        List<String> resultList = new ArrayList<>();
        //本周第一天
        LocalDate weekStart = LocalDate.parse(getWeekStart());
        //本周最后一天
        LocalDate weekEnd = LocalDate.parse(getWeekEnd());
        //本周第一天
        int lengthOfWeekStart = weekStart.getDayOfMonth();
        System.out.println("本周第一天是本月的第:" + lengthOfWeekStart + "天");
        List<Long> list = redisTemplate.opsForValue().bitField(cacheKey, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType
                .unsigned(monthByChooseDay)).valueAt(0));
        long signCount = 0;
        if (list != null && list.size() > 0) {
            long valueDec = list.get(0) != null ? list.get(0) : 0;
//            System.out.println("valueDec..." + valueDec);
            for (int i = lengthOfWeekStart; i < 22; i++) {
                if (valueDec >> 1 << 1 == valueDec) {
                    if (i > 0) {
                        break;
                    }
                } else {
                    signCount += 1;
                }
                valueDec >>= 1;
            }
        }
        return signCount;
    }

    /**
     * 获取当月首次签到日期
     */
    public LocalDate getFirstSignDate(String cacheKey, LocalDate localDate) {
        long bitPosition = (Long) redisTemplate.execute((RedisCallback) cbk -> cbk.bitPos(cacheKey.getBytes(), true));
        return bitPosition < 0 ? null : localDate.withDayOfMonth((int) bitPosition + 1);
    }

    public void signed(String customerId, String date) {
        String key = SIGNED + customerId;
        //存进去。实现BitMap签到，用这个方法。
        Boolean isSign = (Boolean) redisTemplate.execute((RedisCallback<Boolean>)
                con -> con.setBit(key.getBytes(), Long.parseLong(date), true));//参数true代表1,false代表0
        System.out.println("是否签到：" + isSign); //返回false签到成功
        //统计
        Long number = (Long) redisTemplate.execute((RedisCallback<Long>)
                con -> con.bitCount(key.getBytes()));
        System.out.println("本月已经签到" + number + "次");
    }


    public String getCount(String id, String start, String end) {
        String key = SIGNED + id;
        Long count = (Long) redisTemplate.execute((RedisCallback<Long>)
                con -> con.bitCount(key.getBytes(), Long.parseLong(start), Long.parseLong(end)));
        return "用户" + id + "签到" + count + "次";
    }

    public Boolean isSign(String id, String date) {
        String key = SIGNED + id;
        Boolean aBoolean = (Boolean) redisTemplate.execute((RedisCallback<Boolean>)
                con -> con.getBit(key.getBytes(), Long.parseLong(date)));
        return aBoolean;

    }

    /**
     * 根据key删除缓存
     *
     * @param key
     * @return
     */
    public void delete(final String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            LOGGER.error("Delete cache fail and key : " + key);
        }
    }

    /**
     * 保存数据到redis中
     */
    public boolean put(final String key, final Serializable value) {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                connection.set(redisTemplate.getStringSerializer().serialize(key),
                        new JdkSerializationRedisSerializer().serialize(value));
                return true;
            });
        } catch (Exception e) {
            LOGGER.error("Put value to redis fail...", e);
        }

        return false;
    }

    /**
     * 保存字符串到redis中
     */
    public boolean setString(final String key, final String value) {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
                redisConnection.set(redisTemplate.getStringSerializer().serialize(key),
                        redisTemplate.getStringSerializer().serialize(value));
                return true;
            });
        } catch (Exception e) {
            LOGGER.error("putString value to redis fail...", e);
        }

        return false;
    }

    public boolean hset(final String key, final String field, final String value) {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>) redisConnection ->
                    redisConnection.hSet(redisTemplate.getStringSerializer().serialize(key),
                            redisTemplate.getStringSerializer().serialize(field),
                            redisTemplate.getStringSerializer().serialize(value)));
        } catch (Exception e) {
            LOGGER.error("hset value to redis fail...", e);
        }

        return false;
    }

    public boolean hsetPipeline(final String key, final List<RedisHsetBean> fieldValues) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    redisConnection.openPipeline();
                    for (RedisHsetBean bean : fieldValues) {
                        redisConnection.hSet(redisTemplate.getStringSerializer().serialize(key),
                                redisTemplate.getStringSerializer().serialize(bean.getField()),
                                redisTemplate.getStringSerializer().serialize(bean.getValue()));
                    }
                    List<Object> objects = redisConnection.closePipeline();
                    return !CollectionUtils.isEmpty(objects);
                }
            });
        } catch (Exception e) {
            LOGGER.error("hsetPipeline value to redis fail...", e);
        }

        return false;
    }


    public String hget(final String key, final String field) {
        try {
            return redisTemplate.execute((RedisCallback<String>) redisConnection -> {
                byte[] bytes = redisConnection.hGet(redisTemplate.getStringSerializer().serialize(key),
                        redisTemplate.getStringSerializer().serialize(field));
                return null != bytes ? redisTemplate.getStringSerializer().deserialize(bytes) : null;
            });
        } catch (Exception e) {
            LOGGER.error("hget value from redis fail...", e);
        }

        return null;
    }

    /**
     * 根据key删除缓存
     *
     * @param key
     * @return
     */
    public boolean hdelete(final String key, final String field) {
        try {
            redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
                Long res = redisConnection.hDel(redisTemplate.getStringSerializer().serialize(key), redisTemplate
                        .getStringSerializer().serialize(field));
                return res != null ? Boolean.TRUE : Boolean.FALSE;
            });
        } catch (Exception e) {
            LOGGER.error("hget value from redis fail...", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 从redis 中查询数据
     */
    public Object get(final String key) {
        try {
            return redisTemplate.execute((RedisCallback<Object>) connection ->
                    new JdkSerializationRedisSerializer()
                            .deserialize(connection.get(redisTemplate.getStringSerializer().serialize(key))));
        } catch (Exception e) {
            LOGGER.error("Get value from  redis fail...", e);
        }
        return null;
    }

    /**
     * 从redis 中查询字符串对象
     */
    public String getString(final String key) throws SbcRuntimeException {
        try {
            return redisTemplate.execute((RedisCallback<String>) connection -> {
                byte[] bytes = connection.get(redisTemplate.getStringSerializer().serialize(key));
                return null != bytes ? redisTemplate.getStringSerializer().deserialize(bytes) : null;
            });
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
    }


    public boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 利用redis 分布式锁
     *
     * @param key
     * @return
     */
    public boolean setNx(final String key) {
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection ->
                redisConnection.setNX(redisTemplate.getStringSerializer().serialize(key),
                        redisTemplate.getStringSerializer().serialize(key)));
    }

    /**
     * 利用redis 分布式锁，加超时
     *
     * @param key
     * @return
     */
    public Boolean setNx(String key, String value, Long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 以毫秒为单位设置key的超时时间
     *
     * @param key        key
     * @param expireTime 超时时间
     * @return boolean
     */
    public boolean expireByMilliseconds(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 以秒为单位设置key的超时时间
     *
     * @param key        key
     * @param expireTime 超时时间
     * @return boolean
     */
    public boolean expireBySeconds(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 以分钟为单位设置key的超时时间
     *
     * @param key        key
     * @param expireTime 超时时间
     * @return boolean
     */
    public boolean expireByMinutes(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 对存储在指定key的数值执行原子的加1操作
     *
     * @param key key
     * @return
     */
    public Long incrKey(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                connection.incr(redisTemplate.getStringSerializer().serialize(key))
        );
    }

    /**
     * 从redis 中查询数据
     */
    public <T> T getObj(final String key, Class<T> clazz) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSONObject.parseObject(value, clazz);
    }

    /**
     * 从redis 中查询数据
     */
    public <T> List<T> getList(final String key, Class<T> clazz) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSONObject.parseArray(value, clazz);
    }

    /**
     * 保存对象字符串到redis中
     *
     * @param key
     * @param obj
     * @param seconds 失效时间，-1的时候表示持久化
     * @return
     */
    public boolean setObj(final String key, final Object obj, final long seconds) {
        String value = JSONObject.toJSONString(obj);
        return this.setString(key, value, seconds);
    }

    /**
     * 保存字符串到redis中,失效时间单位秒
     */
    public boolean setString(final String key, final String value, final long seconds) {
        try {
            return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
                redisConnection.setEx(redisTemplate.getStringSerializer().serialize(key),
                        seconds, redisTemplate.getStringSerializer().serialize(value));
                return true;
            });
        } catch (Exception e) {
            LOGGER.error("putString value to redis fail...", e);
        }
        return false;
    }

    public Map<String, String> hgetall(final String key) {
        try {
            return redisTemplate.execute((RedisCallback<Map<String, String>>) con -> {
                Map<byte[], byte[]> result = con.hGetAll(key.getBytes());
                if (CollectionUtils.isEmpty(result)) {
                    return new HashMap<>(0);
                }
                Map<String, String> ans = new HashMap<>(result.size());
                for (Map.Entry<byte[], byte[]> entry : result.entrySet()) {
                    ans.put(new String(entry.getKey()), new String(entry.getValue()));
                }
                return ans;
            });
        } catch (Exception e) {
            LOGGER.error("hget value from redis fail...", e);
        }
        return null;
    }

    /**
     * 获取set中的全部元素
     *
     * @param key
     * @return
     */
    public List<String> gSets(String key) {
        Set<String> strings = redisTemplate.opsForSet().members(key);
        return new ArrayList<>(strings);
    }

    /**
     * 缓存至redis的set结构中
     *
     * @param key
     * @param values
     */
    public void iSet(String key, List<String> values) {
        values.stream().forEach(v -> {
            redisTemplate.opsForSet().add(key, v);
        });
    }


    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static String getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }


    /**
     * 获取本月第一天
     *
     * @return
     */
    public String getMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public String getMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }


    /**
     * 获取本月最后一天
     *
     * @return
     */
    public String getLastMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,-1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取十七获取本周的时间
     *
     * @return String
     **/
    public String getWeekByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.add(Calendar.DAY_OF_WEEK, day);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 判断选择的日期本月的第几天
     */
    public int getMonthByChooseDay() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        calendar.setTime(today);// 此处可换为具体某一时间
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断今天是本周的第几天
     *
     * @return
     */
    public int getWeekByChooseDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * 获取上个月的时间
     *
     * @param date
     * @return
     */
    private String lastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);    //得到前一个月
        return new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
    }

    /**
     * 获取上个月最后一周的开始时间
     */
    private String lastMonthLastWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);    //得到前一个月
        //获取上个月最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.WEEK_OF_MONTH, 0);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        //获得上个月最后一周的开始时间
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取上个月最后一个周日,即倒数第二周结算时间
     * @param date
     * @return
     */
    private String lastMonthLastWeekToLast(Date date){
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.MONTH,-1);
        return getDate(instance.getTime(),1);
    }

    /**
     * 本月最后一个星期五
     * @param month
     * @param year
     * @return
     */
    public static Date getLastFriday( int month, int year ) {
        Calendar cal = Calendar.getInstance();
        cal.set( year, month + 1, 1 );
        cal.add( Calendar.DAY_OF_MONTH, -( cal.get( Calendar.DAY_OF_WEEK ) % 7 + 1 ) );
        return cal.getTime();
    }

    public static void main(String[] args) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        int i1 = cal.get(Calendar.DAY_OF_WEEK);
//        int i = i1 % 7 + 1;
//        cal.add( Calendar.DAY_OF_MONTH, -i );
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
    }

    public static String getDate(Date date,int day){
        int b = day ;//星期几  的下标 ，假如 星期三 ，下标就是4，星期日的下标是1，
        //java实现一个月的最后一个星期天
        Calendar instance = Calendar.getInstance();
        //System.out.println(instance.getTime());
        //System.out.println(instance.get(Calendar.THURSDAY));
        instance.setTime(date);
        instance.add(Calendar.MONTH, 1);//月份+1
        instance.set(Calendar.DAY_OF_MONTH, 1);//天设为一个月的第一天
        //System.out.println(instance.getTime());
        instance.add(Calendar.DAY_OF_MONTH, -1);//本月最后一天
        //System.out.println(instance.getTime());
        int a  = instance.get(Calendar.DAY_OF_WEEK);
        System.out.println("a="+a);
        instance.add(Calendar.DAY_OF_MONTH,
                b - a > 0?-a-(7-b):b-a);//根据月末最后一天是星期几，向前偏移至最近的周几
        //System.out.println(instance.get(Calendar.THURSDAY));
        String date_str = new SimpleDateFormat("yyyy-MM-dd").format(instance.getTime());
        System.out.println(date_str);
        return date_str;
    }

    public static Date parseDate(String strDate, String pattern)
    {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try
        {
            return df.parse(strDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDate(Date date, SimpleDateFormat sdf)
    {
        return sdf.format(date);
    }

    /**
     * 获取Zset单个value排名第几
     * @param key
     * @param value
     * @return
     */
    public Long getSingleZset(String key,String value){
        try {
            return redisTemplate.opsForZSet().reverseRank(key,value);
        }catch (Exception e){
            return -1L;
        }
    }

    /**
     * 获取排行榜
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> getRangeWithScores(String key,int start,int end){
        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(key,start,end);
        return rangeWithScores;
    }
}