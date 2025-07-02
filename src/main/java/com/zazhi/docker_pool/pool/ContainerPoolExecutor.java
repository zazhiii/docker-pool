package com.zazhi.docker_pool.pool;

import com.zazhi.docker_pool.pojo.CodeExecContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zazhi
 * @date 2025/7/2
 * @description: ContainerPoolExecutor 类用于管理容器池的执行器
 */
public class ContainerPoolExecutor {

    private final int maximumPoolSize;

    private final long keepStartTime;

    private final CodeExecContainerFactory dockerContainerFactory;

    private final LinkedBlockingQueue<CodeExecContainer> containerQueue = new LinkedBlockingQueue<>();

    private final AtomicInteger containerCount;

    public ContainerPoolExecutor(int maximumPoolSize,
                                 long keepStartTime,
                                 TimeUnit unit,
                                 CodeExecContainerFactory dockerContainerFactory) {
        this.maximumPoolSize = maximumPoolSize;
        this.keepStartTime = unit.toMillis(keepStartTime);
        this.dockerContainerFactory = dockerContainerFactory;
        this.containerCount = new AtomicInteger(0);

        // 启动定时任务停止空闲容器
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanIdleContainers,
                1, 1, TimeUnit.SECONDS);
    }

    public CodeExecContainer acquireContainer() throws InterruptedException {
        CodeExecContainer container = containerQueue.poll();
        if (container == null) {
            if (containerCount.get() < maximumPoolSize) {
                // 创建新的容器
                container = dockerContainerFactory.createDockerContainer();
                containerCount.incrementAndGet();
            } else {
                // 等待直到有可用的容器
                container = containerQueue.poll();
            }
        }
        if(container == null){
            throw new InterruptedException("No available container in the pool");
        }
        if(!container.isRunning()){
            container.start();
        }
        container.setLastUsedTime(System.currentTimeMillis());
        return container;
    }

    public void releaseContainer(CodeExecContainer container) {
        if (container != null) {
            containerQueue.offer(container);
        }
    }

    private void cleanIdleContainers(){
        long currentTime = System.currentTimeMillis();
        containerQueue.forEach(container -> {
            if (currentTime - container.getLastUsedTime() > keepStartTime) {
                container.stop();
            }
        });
    }

}
