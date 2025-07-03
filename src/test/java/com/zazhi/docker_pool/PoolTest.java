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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author zazhi
 * @date 2025/7/2
 * @description: PoolTest 类用于测试容器池的功能
 */
public class PoolTest {

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
    void testContainerPool() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        ContainerPoolExecutor<CodeExecContainer> pool = new ContainerPoolExecutor<>(
                5, // maximumPoolSize
                10, // keepAliveTime
                TimeUnit.SECONDS,
                new CodeExecContainerFactory(
                        dockerClient,
                        "G:\\code_exec_docker_v", // 主机工作目录
                        "/app", // 容器工作目录
                        512, // 内存限制 (MB)
                        "jg" // 镜像名称
                )
        );

        try {
            CodeExecContainer container = pool.acquireContainer();
            pool.releaseContainer(container);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
