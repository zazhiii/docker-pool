package com.zazhi.docker_pool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.zazhi.docker_pool.pojo.CodeExecContainer;
import com.zazhi.docker_pool.pojo.CodeRunResult;
import com.zazhi.docker_pool.pojo.DockerContainer;
import com.zazhi.docker_pool.pojo.ExecCmdResult;
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

        CodeExecContainer codeExecContainer = new CodeExecContainer(dockerClient, containerId, "test-container",  "G:\\code_exec_docker_v");

        String s = codeExecContainer.compileJavaCode(FILE_NAME);

        if(s != null && !s.isEmpty()) {
            System.out.println("编译错误信息: " + s);
        } else {
            System.out.println("Java代码编译成功");
        }
    }

    @Test
    public void testCompileAndRunJavaCode() throws InterruptedException {

        DockerClient dockerClient = getDockerClient();

        String containerId = "8469bd7c0280e5cad8a688e32ed94a0fadeaec502875afa8322fcbbb9dbef14f"; // 替换为实际容器ID

        CodeExecContainer container = new CodeExecContainer(dockerClient, containerId, "",  "G:\\code_exec_docker_v");

        String[] cmd = new String[]{"javac", "Main.java"};
        ExecCmdResult compileRes = container.execCmd(cmd, "", 10, TimeUnit.SECONDS);
        System.out.println("编译输出信息：" + compileRes.getStdout());
        System.out.println("编译错误信息：" + compileRes.getStderr());


        cmd = new String[]{"java", "Main"};
        ExecCmdResult res = container.execCmd(cmd, "1 2\n", 10, TimeUnit.SECONDS);
        System.out.println("执行结果: " + res.getStdout());
        System.out.println("执行错误信息" + res.getStderr());

    }


}
