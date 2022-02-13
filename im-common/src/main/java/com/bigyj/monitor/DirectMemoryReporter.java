package com.bigyj.monitor;

import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DirectMemoryReporter {
    private static final String BUSINESS_KEY = "netty_direct_memory";
    private static final int _1K = 1024 ;
    private static AtomicLong directMemory;

    public static  void init(){
        Field field = ReflectionUtils.findField(PlatformDependent.class,"DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);
        try{
            directMemory = ((AtomicLong)field.get(PlatformDependent.class));
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(DirectMemoryReporter::doReport, 1, 1, TimeUnit.SECONDS);
        }catch (Exception e){
        }
    }

    private static void doReport() {
        int memoryInb = (int) (directMemory.get()/_1K);
        logger.error("{}:{}K",BUSINESS_KEY,memoryInb);
    }
}
