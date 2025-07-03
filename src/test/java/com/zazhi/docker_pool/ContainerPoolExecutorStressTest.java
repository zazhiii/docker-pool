package com.zazhi.docker_pool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.zazhi.docker_pool.pojo.CodeExecContainer;
import com.zazhi.docker_pool.pool.CodeExecContainerFactory;
import com.zazhi.docker_pool.pool.ContainerPoolExecutor;
import com.zazhi.docker_pool.pool.DockerContainerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ContainerPoolExecutorStressTest {

    private DockerClient getDockerClient(){
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

    @Test
    public void stressTestPoolExecutor() throws InterruptedException {
        int threadCount = 20;
        int iterations = 100;
        int maxPoolSize = 10;
        long keepAlive = 1000L;
        DockerContainerFactory<CodeExecContainer> factory = new CodeExecContainerFactory(getDockerClient(), "/tmp", "/app", 128, "jg");
        ContainerPoolExecutor<CodeExecContainer> pool = new ContainerPoolExecutor(maxPoolSize, keepAlive, TimeUnit.MILLISECONDS, factory);

        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Throwable> errors = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        CodeExecContainer container = pool.acquireContainer();
                        Assertions.assertNotNull(container);
                        pool.releaseContainer(container);
                    }
                } catch (Throwable t) {
                    synchronized (errors) {
                        errors.add(t);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        boolean finished = latch.await(60, TimeUnit.SECONDS);
        Assertions.assertTrue(finished, "测试未在规定时间内完成");
        Assertions.assertTrue(errors.isEmpty(), "发现异常: " + errors);
    }
}

