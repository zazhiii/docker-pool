
## 📦 ContainerPoolExecutor

### 🧠 简介 | Introduction

`ContainerPoolExecutor` 是一个轻量的 Docker 容器池管理器，灵感来源于线程池的设计模式。该组件旨在解决高并发环境下对容器频繁创建/销毁导致的性能浪费问题，通过复用、超时管理和资源限制来实现高效、可控的容器生命周期管理。

This project is a lightweight container pool inspired by the ThreadPoolExecutor pattern, designed to manage Docker containers for tasks like online code execution or sandbox environments.

---

### 🚀 特性 | Features

* ✅ 最大容器数限制 (`maximumPoolSize`)
* ✅ 自动复用空闲容器
* ✅ 空闲容器超时后自动停止（非销毁）
* ✅ 可插拔容器工厂接口（`CodeExecContainerFactory`）
* ✅ 后台线程定期检查容器状态
* ✅ 可与 Docker Java SDK 完美集成

---

### 🔧 使用方法 | Usage

#### 1️⃣ 创建容器工厂（例如 `CodeExecContainerFactory` ）

#### 2️⃣ 创建容器池执行器

```java
ContainerPoolExecutor executor = new ContainerPoolExecutor(
    10,                         // 最大容器数
    5, TimeUnit.MINUTES,        // 空闲容器保持时间
    new MyContainerFactory()    // 容器工厂
);
```

#### 3️⃣ 获取/释放容器

```java
CodeExecContainer container = executor.aquireContainer();
try {
    // 执行任务
} finally {
    executor.releaseContainer(container);
}
```
---

### 🛠️ 核心类 | Core Classes

| 类名                         | 功能描述                   |
| -------------------------- | ---------------------- |
| `ContainerPoolExecutor`    | 管理容器池，提供获取/释放逻辑和定时清理机制 |
| `CodeExecContainer`        | 抽象的容器对象，封装启动、停止等逻辑     |
| `CodeExecContainerFactory` | 容器工厂接口，提供容器创建逻辑        |

---

### 💡 应用场景 | Use Cases

* 🧑‍💻 在线编程判题系统（Online Judge）
* 🌐 Web 后端在线代码运行服务

---

### 📦 技术栈 | Tech Stack

* Java 17+
* [docker-java](https://github.com/docker-java/docker-java) Docker 客户端 SDK

### TODO
- 目前可能有线程安全问题，需要修复
