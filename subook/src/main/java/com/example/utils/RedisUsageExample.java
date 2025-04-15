package com.example.utils;

/**
 * Redis工具类使用示例
 * <p>
 * 本文档提供了RedisUtils工具类的常见使用场景和示例代码，帮助开发人员快速上手。
 * 示例代码仅作参考，实际使用时请根据业务需求进行调整。
 * </p>
 * 
 * @author SuBook
 * @version 1.0
 * @since 2023
 */
public class RedisUsageExample {

    /**
     * 字符串类型示例
     * <pre>
     * // 注入RedisUtils
     * private RedisUtils redisUtils;
     * 
     * // 存储字符串
     * redisUtils.set("user:name", "张三");
     * 
     * // 存储字符串并设置过期时间（60秒）
     * redisUtils.set("user:token", "xxxxx-token-xxxxx", 60);
     * 
     * // 获取字符串
     * String name = (String) redisUtils.get("user:name");
     * 
     * // 递增操作（常用于计数器）
     * long viewCount = redisUtils.increment("article:1:views", 1);
     * 
     * // 判断key是否存在
     * boolean exists = redisUtils.hasKey("user:name");
     * 
     * // 删除key
     * redisUtils.delete("user:name");
     * </pre>
     */
    public void stringExample() {
        // 仅作为文档示例，不包含实际代码
    }

    /**
     * Hash类型示例（适合存储对象）
     * <pre>
     * // 存储Hash的单个字段
     * redisUtils.hashSet("user:1", "name", "张三");
     * redisUtils.hashSet("user:1", "age", 25);
     * 
     * // 批量存储Hash字段
     * Map<String, Object> userMap = new HashMap<>();
     * userMap.put("name", "张三");
     * userMap.put("age", 25);
     * userMap.put("email", "zhangsan@example.com");
     * redisUtils.hashSetAll("user:1", userMap);
     * 
     * // 获取Hash单个字段值
     * String name = (String) redisUtils.hashGet("user:1", "name");
     * 
     * // 获取Hash所有字段和值
     * Map<Object, Object> userInfo = redisUtils.hashGetAll("user:1");
     * 
     * // 判断Hash字段是否存在
     * boolean hasAge = redisUtils.hashHasKey("user:1", "age");
     * 
     * // 删除Hash字段
     * redisUtils.hashDelete("user:1", "email");
     * </pre>
     */
    public void hashExample() {
        // 仅作为文档示例，不包含实际代码
    }

    /**
     * List类型示例（适合存储列表数据）
     * <pre>
     * // 从右侧添加元素（后进）
     * redisUtils.listRightPush("messages", "消息1");
     * redisUtils.listRightPush("messages", "消息2");
     * redisUtils.listRightPush("messages", "消息3");
     * 
     * // 从左侧添加元素（先进）
     * redisUtils.listLeftPush("queue", "任务1");
     * redisUtils.listLeftPush("queue", "任务2");
     * 
     * // 获取列表指定范围的元素（0到-1表示获取全部）
     * List<Object> allMessages = redisUtils.listRange("messages", 0, -1);
     * 
     * // 获取列表长度
     * long size = redisUtils.listSize("messages");
     * 
     * // 从右侧弹出元素（先进先出 - FIFO）
     * String task = (String) redisUtils.listRightPop("queue");
     * 
     * // 从左侧弹出元素（后进先出 - LIFO）
     * String message = (String) redisUtils.listLeftPop("messages");
     * </pre>
     */
    public void listExample() {
        // 仅作为文档示例，不包含实际代码
    }

    /**
     * Set类型示例（适合存储不重复元素的集合）
     * <pre>
     * // 添加Set元素
     * redisUtils.setAdd("tags", "Java", "Spring", "Redis", "MySQL");
     * redisUtils.setAdd("tags", "Java");  // 重复添加无效
     * 
     * // 获取Set所有元素
     * Set<Object> allTags = redisUtils.setMembers("tags");
     * 
     * // 判断元素是否在Set中
     * boolean hasJava = redisUtils.setIsMember("tags", "Java");
     * 
     * // 获取Set大小
     * long size = redisUtils.setSize("tags");
     * 
     * // 移除Set元素
     * redisUtils.setRemove("tags", "MySQL");
     * </pre>
     */
    public void setExample() {
        // 仅作为文档示例，不包含实际代码
    }

    /**
     * ZSet（有序集合）类型示例（适合排行榜等场景）
     * <pre>
     * // 添加ZSet元素（带分数）
     * redisUtils.zSetAdd("ranking", "用户A", 100);
     * redisUtils.zSetAdd("ranking", "用户B", 80);
     * redisUtils.zSetAdd("ranking", "用户C", 120);
     * 
     * // 获取ZSet所有元素（按分数从小到大）
     * Set<Object> allUsers = redisUtils.zSetRange("ranking", 0, -1);
     * 
     * // 获取ZSet所有元素（按分数从大到小 - 常用于排行榜）
     * Set<Object> topUsers = redisUtils.zSetReverseRange("ranking", 0, -1);
     * 
     * // 获取ZSet大小
     * long size = redisUtils.zSetSize("ranking");
     * 
     * // 移除ZSet元素
     * redisUtils.zSetRemove("ranking", "用户B");
     * </pre>
     */
    public void zSetExample() {
        // 仅作为文档示例，不包含实际代码
    }

    /**
     * 综合应用场景示例
     * <pre>
     * // 1. 缓存用户信息（使用Hash存储对象）
     * Map<String, Object> userMap = new HashMap<>();
     * userMap.put("id", 1001);
     * userMap.put("name", "张三");
     * userMap.put("email", "zhangsan@example.com");
     * // 缓存2小时
     * redisUtils.hashSetAll("user:" + 1001, userMap, 7200);
     * 
     * // 2. 记录用户登录次数（使用increment）
     * long loginCount = redisUtils.increment("user:1001:loginCount", 1);
     * 
     * // 3. 存储用户最近访问的文章列表（使用List）
     * redisUtils.listRightPush("user:1001:history", "article:1001");
     * redisUtils.listRightPush("user:1001:history", "article:1002");
     * // 只保留最近10条记录
     * if (redisUtils.listSize("user:1001:history") > 10) {
     *     redisUtils.listLeftPop("user:1001:history");
     * }
     * 
     * // 4. 记录用户感兴趣的标签（使用Set）
     * redisUtils.setAdd("user:1001:tags", "Java", "Spring", "Redis");
     * 
     * // 5. 文章阅读量排行榜（使用ZSet）
     * redisUtils.zSetAdd("articles:ranking", "article:1001", 150);
     * redisUtils.zSetAdd("articles:ranking", "article:1002", 320);
     * // 获取阅读量前10的文章
     * Set<Object> top10Articles = redisUtils.zSetReverseRange("articles:ranking", 0, 9);
     * </pre>
     */
    public void applicationScenarios() {
        // 仅作为文档示例，不包含实际代码
    }
} 