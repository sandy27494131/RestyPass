package com.github.df.restypass.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 事件总线
 * Created by darrenfu on 17-7-23.
 */
@SuppressWarnings("WeakerAccess")
public class EventBus {
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    /**
     * 事件-消费者缓存(eventKey->Consumer)
     */
    //TODO 注意应用场景，在常驻对象上使用，否则可能存在内存泄漏
    private static ConcurrentHashMap<String, List<Consumer>> eventMap = new ConcurrentHashMap<>();

    /**
     * 注册事件和消费者
     *
     * @param event    the event
     * @param consumer the consumer
     */
    static void registerEventAndConsumer(String event, Consumer consumer) {
        // 不存在 event
        if (!eventMap.containsKey(event)) {
            //
            List<Consumer> consumerList = new CopyOnWriteArrayList<>();
            consumerList.add(consumer);
            // 新增
            List<Consumer> existConsumers = eventMap.putIfAbsent(event, consumerList);
            if (existConsumers != null) {
                // 并发，新增
                existConsumers.add(consumer);
            }
        } else {
            // event已存在
            List<Consumer> consumerList = eventMap.get(event);
            if (consumerList != null) {
                consumerList.add(consumer);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("注册事件：{}成功", event);
        }
    }

    /**
     * 消费事件
     *
     * @param <T>   the type parameter
     * @param event the event
     * @param obj   the obj
     */
    @SuppressWarnings("unchecked")
    static <T> void emitEvent(String event, T obj) {

        List<Consumer> consumers = eventMap.get(event);
        if (consumers != null && consumers.size() > 0) {
            for (Consumer consumer : consumers) {
                consumer.accept(obj);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("消费事件:{},参数:{}", event, obj);
        }
    }

}
