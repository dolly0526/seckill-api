package com.github.dolly0526.seckillapi;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.util.concurrent.RateLimiter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yusenyang
 * @create 2021/4/14 12:39
 */
public class TestGuava {

    @SneakyThrows
    @Test
    public void testRateLimiter() {

        // 新建一个每秒允许2个请求的限流器
        RateLimiter rateLimiter = RateLimiter.create(2);

        // 新建一个固定一个线程的线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 向队列提交20个请求，预估需要10s处理完
        for (int i = 0; i < 20; i++) {

            executor.execute(() -> {

                // 阻塞等待限流器放行
                double acquire = rateLimiter.acquire();

                // 每0.5s左右，放行一个请求
                System.out.println(acquire);
            });
        }

        // 保留主线程睡11s，确保请求能处理完
        Thread.sleep(11000);
    }

    @Test
    public void testEventBus() {

        // 新建一个发布订阅的事件总线
        EventBus eventBus = new EventBus();

        // 注册监听器，所有监听器都会监听到，发布时按照注册顺序
        eventBus.register(new Listener1());
        eventBus.register(new Listener2());

        // 发布事件
        eventBus.post("hello");
        eventBus.post(new double[]{1.1, 1.2});
        eventBus.post(100);
    }

    public static class Listener1 {

        @Subscribe
        public void listen(String event) {
            System.out.println("rcv1 str: " + event);
        }

        @Subscribe
        public void listen(Integer event) {
            System.out.println("rcv1 int: " + event);
        }
    }

    public static class Listener2 {

        @Subscribe
        public void listen(String event) {
            System.out.println("rcv2 str: " + event);
        }

        @Subscribe
        public void listen(double[] event) {
            System.out.println("rcv2 arr: " + Arrays.toString(event));
        }
    }

    @Test
    public void testBloomFilter() {

        // 新建一个预期放入1000个元素的布隆过滤器
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000);

        // 添加1000以内的数
        for (int i = 0; i < 1000; i++) {
            bloomFilter.put(i);
        }

        // 检验2000以内的数
        int cnt = 0;

        for (int i = 1000; i < 2000; i++) {
            if (bloomFilter.mightContain(i)) cnt++;
        }

        System.out.println("误判个数：" + cnt);
    }

    public class LRUCache extends LinkedHashMap<Integer, Integer> {
        private int capacity;

        public LRUCache(int capacity) {

            // 调用父类构造器必须在子类构造器的第一行！
            super(capacity, 0.75F, true);
            this.capacity = capacity;
        }

        public int get(int key) {
            return getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            put(key, value);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > capacity;
        }
    }
}
