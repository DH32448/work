package com.example.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * <p>
 * 提供对Redis常用操作的封装，包括对String、Hash、List、Set等数据类型的操作。
 * 包含设置过期时间、判断key是否存在等实用方法。
 * </p>
 * 
 * @author SuBook
 * @version 1.0
 * @since 2023
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // ============================== 通用操作方法 ==============================

    /**
     * 设置过期时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置过期时间（默认秒）
     *
     * @param key  键
     * @param time 时间(秒)
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取key的过期时间
     *
     * @param key 键
     * @return 过期时间(秒)，返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true=存在；false=不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个或多个key
     *
     * @param keys 一个或多个键
     */
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(Arrays.asList(keys));
            }
        }
    }

    // ============================== String类型操作 ==============================

    /**
     * 获取字符串类型的值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 存储字符串类型的值
     *
     * @param key   键
     * @param value 值
     * @return true=设置成功；false=设置失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储字符串类型的值并设置过期时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储字符串类型的值并设置过期时间（默认秒）
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return true=设置成功；false=设置失败
     */
    public boolean set(String key, Object value, long time) {
        return set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 递增因子
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减因子
     * @return 递减后的值
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================== Hash类型操作 ==============================

    /**
     * 获取Hash中的指定项
     *
     * @param key     键
     * @param hashKey 项
     * @return 值
     */
    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash所有键值对
     *
     * @param key 键
     * @return 对应的多个键值对
     */
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置Hash值
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @return true=设置成功；false=设置失败
     */
    public boolean hashSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置Hash的多个键值对
     *
     * @param key  键
     * @param map  多个键值对
     * @return true=设置成功；false=设置失败
     */
    public boolean hashSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置Hash的多个键值对并设置过期时间
     *
     * @param key  键
     * @param map  多个键值对
     * @param time 过期时间(秒)
     * @return true=设置成功；false=设置失败
     */
    public boolean hashSetAll(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除Hash中的一个或多个项
     *
     * @param key      键
     * @param hashKeys 一个或多个项
     */
    public void hashDelete(String key, Object... hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断Hash中是否有该项
     *
     * @param key     键
     * @param hashKey 项
     * @return true=存在；false=不存在
     */
    public boolean hashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash递增
     *
     * @param key     键
     * @param hashKey 项
     * @param delta   递增因子
     * @return 递增后的值
     */
    public double hashIncrement(String key, String hashKey, double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    // ============================== List类型操作 ==============================

    /**
     * 获取List指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引，-1表示最后一个元素
     * @return 指定范围的元素列表
     */
    public List<Object> listRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取List长度
     *
     * @param key 键
     * @return 列表长度
     */
    public long listSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引获取List中的元素
     *
     * @param key   键
     * @param index 索引，index>=0时，0表示第一个元素；index<0时，-1表示最后一个元素
     * @return 元素
     */
    public Object listGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将元素放入List右侧
     *
     * @param key   键
     * @param value 值
     * @return true=设置成功；false=设置失败
     */
    public boolean listRightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从List右侧弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object listRightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将元素放入List左侧
     *
     * @param key   键
     * @param value
     * @return true=设置成功；false=设置失败
     */
    public boolean listLeftPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从List左侧弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object listLeftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ============================== Set类型操作 ==============================

    /**
     * 获取Set中的所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> setMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断Set是否包含特定元素
     *
     * @param key   键
     * @param value 值
     * @return true=包含；false=不包含
     */
    public boolean setIsMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向Set添加元素
     *
     * @param key    键
     * @param values 一个或多个值
     * @return 添加成功的数量
     */
    public long setAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 从Set移除元素
     *
     * @param key    键
     * @param values 一个或多个值
     * @return 移除成功的数量
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取Set的大小
     *
     * @param key 键
     * @return 集合大小
     */
    public long setSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ============================== ZSet类型操作 ==============================

    /**
     * 向ZSet添加元素
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return true=设置成功；false=设置失败
     */
    public boolean zSetAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取ZSet指定范围的元素（按分数从小到大）
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置，-1表示不限制结束
     * @return 元素集合
     */
    public Set<Object> zSetRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet指定范围的元素（按分数从大到小）
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置，-1表示不限制结束
     * @return 元素集合
     */
    public Set<Object> zSetReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet大小
     *
     * @param key 键
     * @return 集合大小
     */
    public long zSetSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除ZSet中的元素
     *
     * @param key    键
     * @param values 一个或多个值
     * @return 移除成功的数量
     */
    public long zSetRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
} 