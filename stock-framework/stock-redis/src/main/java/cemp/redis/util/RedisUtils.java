package cemp.redis.util;

import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wenyilu
 */
@Component
@Slf4j
public final class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 执行结果
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        if (hasKey(key)) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (Objects.isNull(expire)) {
                throw new RedisCommandExecutionException("获取过期时间操作异常");
            }
            return expire;
        } else {
            throw new RedisException(String.format("[%s] 键值不存在", key));
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
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

    public boolean pipleSet(RedisCallback callback) {
        try {
            redisTemplate.executePipelined(callback);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public boolean pipelineInsert(final Map<String, Object> value) {
        try {
            final RedisSerializer serializerKey = redisTemplate.getStringSerializer();
            final RedisSerializer serializerValue = redisTemplate.getValueSerializer();
            redisTemplate.executePipelined((RedisConnection connection) -> {
                value.forEach((k, v) -> {
                    connection.set(serializerKey.serialize(k), serializerValue.serialize(v));
                });
                return null;
            });
        } catch (Exception e) {
            log.info("使用管道操作出错:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param ttl   失效时间(单位：秒) ttl要大于0 如果ttl小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long ttl) {
        try {
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
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
     * 递增
     *
     * @param key   键
     * @param delta 递增值（要增加几）
     * @return 递增后的结果
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        if (Objects.isNull(increment)) {
            throw new RedisCommandExecutionException("递增执行异常");
        }
        return increment;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 递减值（要减少几）
     * @return 递减后的结果
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        Long decrement = redisTemplate.opsForValue().decrement(key, delta);
        if (Objects.isNull(decrement)) {
            throw new RedisCommandExecutionException("递减操作异常");
        }
        return decrement;
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key   键 不能为null
     * @param field 项 不能为null
     * @return 值
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 通过key获取数据集
     *
     * @param key 键
     * @return 域值对
     */
    public Map<Object, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Set<Object> hmkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 模糊获取keys
     *
     * @param key 键
     */
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

//    /**
//     * 扫描获取所有的键
//     * @param patternKey 正则匹配键
//     * @return 所有键集合
//     */
//    public Set<String> scan(String patternKey){
//       return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
//            Set<String> keysTmp = new HashSet<>();
//            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder()
//                    .match(patternKey)
//                    .count(100).build());
//            while (cursor.hasNext()) {
//                keysTmp.add(new String(cursor.next()));
//            }
//            return keysTmp;
//       });
//    }


    /**
     * 获取hashKey对应的所有键
     *
     * @param key 键
     * @return 对应的所有键
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
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
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addMapList(String key, Map<?, ?> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }

    public void addMap(String key, String item, Object value) {
        redisTemplate.boundHashOps(key).put(item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
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
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增小数 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 递增后的值
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递增正数 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 递增后的值
     */
    public long hincr(String key, String item, long by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return 递减后对的值
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> smembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public Object lLeftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
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
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
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
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //=================zset=============================

    /**
     * 添加数据
     * <p>
     * 添加方式：
     * 1.创建一个set集合
     * Set<ZSetOperations.TypedTuple<Object>> sets=new HashSet<>();
     * 2.创建一个有序集合
     * ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>(value,排序的数值，越小越在前);
     * 4.放入set集合
     * sets.add(objectTypedTuple1);
     * 5.放入缓存
     * reidsImpl.Zadd("zSet", list);
     *
     * @param key
     * @param tuples
     */
    public void zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        redisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * 放单个
     *
     * @param key
     * @param value
     * @param score
     */
    public void zAdd(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取有序集合的成员数
     *
     * @param key
     * @return
     */
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 计算在有序集合中指定区间分数的成员数
     *
     * @param key
     * @param min 最小排序分数
     * @param max 最大排序分数
     * @return
     */
    public Long zCount(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取有序集合下标区间 start 至 end 的成员  分数值从小到大排列
     *
     * @param key
     * @param start
     * @param end
     */
    public Set<Object> zRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取有序集合下标区间 start 至 end 的成员  分数值从大到小排列
     *
     * @param key
     * @param start
     * @param end
     */
    public Set<Object> reverseRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 返回 分数在min至max之间的数据 按分数值递减(从大到小)的次序排列。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> reverseRange(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 返回指定成员的下标
     *
     * @param key
     * @param value
     * @return
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 删除key的指定元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRemoveValue(String key, Object values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 移除下标从start至end的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }


    /**
     * 移除分数从min至max的元素
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }


    /**
     * 获取同步锁
     *
     * @param lockKey 锁的名称
     * @param expire  如果成功获取了锁，则给定锁的超时时间,单位：秒
     * @return 获取返回true，否则false
     */
    public boolean getLock(String lockKey, Long expire) {
        try {
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, expire, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    /*******************************以下为拓展方法*********************************/
//
//    /**
//     * 向一张hash表中放入数据,如果不存在将创建
//     *
//     * @param key   键
//     * @param hkey  项
//     * @param value 值
//     * @param time  时间(秒) 注意:该过期时间只有下次查询的时候做删除 或是定时任务清除
//     * @return true 成功 false失败
//     */
//    public boolean hsetEx(String key, String hkey, Object value, long time) {
//        try {
//            //记录存储过期对象
//            long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
//            RedisFieldExpireObject temp = new RedisFieldExpireObject(now + time, time, value);
//            redisTemplate.opsForHash().put(key, hkey, temp);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * HashGet
//     *
//     * @param key  键 不能为null
//     * @param hkey 项 不能为null
//     * @return 值
//     */
//    public Object hgetEx(String key, String hkey) {
//        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
//        Object temp = redisTemplate.opsForHash().get(key, hkey);
//        if (temp != null) {
//            RedisFieldExpireObject redisFieldExpireObject = JSON.parseObject(JSON.toJSONString(temp), RedisFieldExpireObject.class);
//            if (now <= redisFieldExpireObject.getExpireTime()) {
//                //当前时间小于等于预过期时间 则返回
//                return redisFieldExpireObject.getValue();
//            } else {
//                //当前时间大于预过期时间 则删除但钱key
//                hdel(key, hkey);
//            }
//        }
//        return null;
//    }

    /**
     * 从hash中模糊搜索某key值并将k-v返回
     *
     * @param key      hash最外层的key
     * @param matchStr 内层key的matcher
     * @return k-v
     */
    public List<Map.Entry<Object, Object>> hscan(String key, String matchStr) {
        Cursor<Map.Entry<Object, Object>> cursor = null;
        try {
            ScanOptions match = ScanOptions.scanOptions().match(matchStr).build();
            cursor = redisTemplate.opsForHash().scan(key, match);
            List<Map.Entry<Object, Object>> result = new ArrayList<>();
            Map.Entry<Object, Object> entry = null;
            while (cursor.hasNext()) {
                entry = cursor.next();
                result.add(entry);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    /**
     * 类似于keys
     *
     * @param matchStr      hash最外层的key
     * @return k-v
     */
    public List<String> scan(String matchStr) {
        Cursor<String> cursor = null;
        try {
            ScanOptions match = ScanOptions.scanOptions().match(matchStr).build();
            cursor = redisTemplate.scan(match);
            List<String> result = new ArrayList<>();
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }
}
