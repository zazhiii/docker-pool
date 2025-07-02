package com.zazhi.docker_pool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

//@SpringBootTest
class DockerPoolApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testExecCmd() throws  Exception {

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        String containerId = "bfc4434060fa936019b343ae69bac86bdb39a09b236bad6a9b40975b37b9c5c2"; // 替换为实际容器ID

        // 创建 exec 命令
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withCmd("java", "TestIO.java") // 替换为实际的命令和参数
                .exec();

        // 构建输出缓冲
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        InputStream stdin = new ByteArrayInputStream("as\n".getBytes(StandardCharsets.UTF_8));


        // 使用推荐方式执行命令
        dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .withStdIn(stdin)
//                .withTty(false) //  输出是原始的二进制数据流，stdout/stderr 是分开的，适合程序处理
//                .withDetach(false) // 直接阻塞等待命令执行完成
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            if (frame.getStreamType() == StreamType.STDOUT) {
                                stdout.write(frame.getPayload());
                            } else if (frame.getStreamType() == StreamType.STDERR) {
                                stderr.write(frame.getPayload());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).awaitCompletion();// 阻塞直到执行完毕

        stdin.close();
        // 打印输出结果
        System.out.println("Container Output:" + stdout.toString(StandardCharsets.UTF_8));
        System.out.println("Container Error Output:" + stderr);
        // 关闭资源
        stdout.close();
        stderr.close();

    }

}
