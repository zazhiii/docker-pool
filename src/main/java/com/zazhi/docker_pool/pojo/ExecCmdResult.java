package com.zazhi.docker_pool.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author lixh
 * @since 2025/7/19 22:28
 */
@Data
@Builder
public class ExecCmdResult {
    private String stdout; // 标准输出
    private String stderr; // 错误输出
    private boolean timeout; // 是否超时
}
