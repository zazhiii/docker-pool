package com.zazhi.docker_pool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.zazhi.docker_pool.pojo.CodeExecContainer;
import com.zazhi.docker_pool.pojo.CodeRunResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author zazhi
 * @date 2025/7/2
 * @description: CodeExecContainer 测试类
 */
public class CodeExecContainerTest {

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
    public void testCompileJavaCode() {

        String FILE_NAME = "ForLoop1e8Test.java";

        DockerClient dockerClient = getDockerClient();

        String containerId = "bfc4434060fa936019b343ae69bac86bdb39a09b236bad6a9b40975b37b9c5c2"; // 替换为实际容器ID

        CodeExecContainer codeExecContainer = new CodeExecContainer(dockerClient, containerId, "test-container", "/app", "G:\\code_exec_docker_v");

        String s = codeExecContainer.compileJavaCode(codeExecContainer.getContainerWorkingDir() + "/" + FILE_NAME);

        if(s != null && !s.isEmpty()) {
            System.out.println("编译错误信息: " + s);
        } else {
            System.out.println("Java代码编译成功");
        }
    }

    @Test
    public void testRunJavaCode() {
        DockerClient dockerClient = getDockerClient();

        String containerId = "bfc4434060fa936019b343ae69bac86bdb39a09b236bad6a9b40975b37b9c5c2"; // 替换为实际容器ID

        CodeExecContainer codeExecContainer = new CodeExecContainer(dockerClient, containerId, "test-container", "/app", "G:\\code_exec_docker_v");

        InputStream stdin = new ByteArrayInputStream("1\n".getBytes(StandardCharsets.UTF_8));

        CodeRunResult result = codeExecContainer.runJavaCode(
                codeExecContainer.getContainerWorkingDir(),
                "ExceptionTest.java",
                stdin,
                10,
                TimeUnit.SECONDS
        );

        System.out.println(result);
    }


}
