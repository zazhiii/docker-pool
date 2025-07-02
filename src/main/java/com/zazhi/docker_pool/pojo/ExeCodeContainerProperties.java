package com.zazhi.docker_pool.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zazhi
 * @date 2025/7/2
 * @description: ExeCodeContainerProperties 类用于存储执行代码容器的属性
 */
@Component
@ConfigurationProperties(prefix = "exec-code-container")
@Data
public class ExeCodeContainerProperties {
    private String imageName; // 容器镜像名称
    private String containerName; // 容器名称
    private String containerWorkingDir; // 容器工作目录
    private String hostWorkingDir; // 主机工作目录
    private int memoryLimitMb; // 内存限制，单位为MB
    private int cpuLimitCores; // CPU 限制，单位为核心数
    private int maxExecutionTimeSeconds; // 最大执行时间，单位为秒
}
