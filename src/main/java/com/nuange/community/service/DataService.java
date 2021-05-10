package com.nuange.community.service;

import com.nuange.community.unity.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定的ip计入uv
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    //统计指定日期范围内的UV
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //整理改该日期范围内的key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //获取对应的key
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getUvKey(df.format(calendar.getTime()));
            keyList.add(key);
            calendar.add(Calendar.DATE, 1);
        }
        //合并这些key
        String redisKey = RedisKeyUtil.getUvKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //将指定用户计入DAU
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);

    }

    //统计指定日期范围内的DAU
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //整理改该日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //获取对应的key
        while (!calendar.getTime().after(end)) {
            String key = RedisKeyUtil.getDauKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            //日期加1
            calendar.add(Calendar.DATE, 1);
        }
        //进行or运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String key = RedisKeyUtil.getDauKey(df.format(start), df.format(end));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        key.getBytes(),keyList.toArray(new byte[0][0]));
                return redisConnection.bitCount(key.getBytes());
            }
        });
    }
}
