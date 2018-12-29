package cn.itcast.redis;

import jdk.nashorn.internal.objects.annotations.Constructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //测试objing
    @Test
    public void testString(){
        redisTemplate.boundValueOps("string_key").set("传智播客");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println("testString = "+obj);
    }

    //测试散列hash 123
    @Test
    public void testHash(){
        redisTemplate.boundHashOps("hash_key").put("f2","v2");
        redisTemplate.boundHashOps("hash_key").put("f1","v1");
        redisTemplate.boundHashOps("hash_key").put("f3","v3");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println("testHash = "+list);
    }

    //测试列表list  有序
    @Test
    public void testList(){
        redisTemplate.boundListOps("list_key").leftPush("b");
        redisTemplate.boundListOps("list_key").leftPush("a");
        redisTemplate.boundListOps("list_key").rightPush("c");
        List list = redisTemplate.boundListOps("list_key").range(0, -1);
        System.out.println("testList = "+list);
    }

    //测试集合set  无序
    @Test
    public void testSet(){
        redisTemplate.boundSetOps("set_key").add(1,3,5,"itcast",7);
        Set set = redisTemplate.boundSetOps("set_key").members();
        System.out.println("testSet = "+set);
    }

    //测试有序集合 sorted set
    @Test
    public void testSortedSet(){
        redisTemplate.boundZSetOps("zset_key").add("aa",2);
        redisTemplate.boundZSetOps("zset_key").add("bb",200);
        redisTemplate.boundZSetOps("zset_key").add("cc",20);
        Set set = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println("testSortedSet = "+ set);

    }

}
