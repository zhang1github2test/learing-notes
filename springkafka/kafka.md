# kafka 入门

## 一、安装kafka

### 0、准备工作(安装zookeeper)

* 下载zookeeper

  进入[zookeeper下载官网](https://www.apache.org/dyn/closer.cgi/zookeeper/),点击下图进行下载：

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper下载地址截图.png)

  最终下载的地址为：[http://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.4.14/](http://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.4.14/)，选择3.4.14进行下载。

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper下载地址截图2.png)

* 解压zookeeper,目录结构如下：

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper文件结构.png)

* 创建配置文件，将conf/zoo_sample.cfg文件复制为 conf/zoo.cfg

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper配置文件.png)

* 启动zookeeper。zookeeper_home\zookeeper-3.4.14\bin,双击zkServer.cmd文件。或者在该目录下使用cmd窗口命令zkServer.cmd

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper启动.png)

* 完成上述步骤，如果启动成功，会出现如下的界面.

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper启动成功截图.png)

* 测试是否真正启动成功,进入bin目录，双击zkCli.cmd文件或者在执行命令：.\zkCli.cmd -server 127.0.0.1:2181。连接成功后的截图如下：

  ![](F:\learning\github\learing-notes\springkafka\img\zookeeper客户端连接成功截图.png)

* 使用help命令查看当前能够执行的命令，示例如下：

  ```txt
  [zk: 127.0.0.1:2181(CONNECTED) 0] help
  ZooKeeper -server host:port cmd args
          stat path [watch]
          set path data [version]
          ls path [watch]
          delquota [-n|-b] path
          ls2 path [watch]
          setAcl path acl
          setquota -n|-b val path
          history
          redo cmdno
          printwatches on|off
          delete path [version]
          sync path
          listquota path
          rmr path
          get path [watch]
          create [-s] [-e] path data acl
          addauth scheme auth
          quit
          getAcl path
          close
          connect host:port
  [zk: 127.0.0.1:2181(CONNECTED) 1]
  ```

* 查看zookeeper的节点

  ```txt
  ZooKeeper -server host:port cmd args
          stat path [watch]
          set path data [version]
          ls path [watch]
          delquota [-n|-b] path
          ls2 path [watch]
          setAcl path acl
          setquota -n|-b val path
          history
          redo cmdno
          printwatches on|off
          delete path [version]
          sync path
          listquota path
          rmr path
          get path [watch]
          create [-s] [-e] path data acl
          addauth scheme auth
          quit
          getAcl path
          close
          connect host:port
  [zk: 127.0.0.1:2181(CONNECTED) 1] ls /
  [ zookeeper]
  ```

  

* 通过运行命令 create /zk_test my_data 来创建一个新的节点

  ```txt
  [zk: 127.0.0.1:2181(CONNECTED) 2] create /zk_test my_data
  Created /zk_test
  [zk: 127.0.0.1:2181(CONNECTED) 3] ls /
  [zookeeper, zk_test]
  ```

### 1、安装kafka

* 下载[官网地址](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.2.0/kafka_2.12-2.2.0.tgz)

  ![](F:\learning\github\learing-notes\springkafka\img\kafka下载地址.png)



* 下载完成后解压文件，得到的解压文件如下：

  ![](F:\learning\github\learing-notes\springkafka\img\kafka目录结构.png)

* 启动kafka服务端,进入在上述的文件目录下执行命令：.\bin\windows\kafka-server-start.bat .\config\server.properties

  ![](F:\learning\github\learing-notes\springkafka\img\启动kafka服务端.png)

### 2、直接使用kafka的java依赖使用

* 生产者代码：

  ```java
  package com.kafkaDemo;
  
  import org.apache.kafka.clients.producer.KafkaProducer;
  import org.apache.kafka.clients.producer.Producer;
  import org.apache.kafka.clients.producer.ProducerRecord;
  
  import java.util.Properties;
  
  public class ProducerDemo {
      public static void main(String[] args) {
          Properties props = new Properties();
          props.put("bootstrap.servers", "localhost:9092");
          props.put("acks", "all");
          props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
          props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
  
          Producer<String, String> producer = new KafkaProducer<>(props);
          for (int i = 0; i < 100; i++)
              producer.send(new ProducerRecord<String, String>("my-topic", "key"+Integer.toString(i),"value" + Integer.toString(i)));
  
          producer.close();
      }
  }
  ```

  

* 消费者代码：

  ```java
  package com.kafkaDemo;
  
  import org.apache.kafka.clients.consumer.ConsumerRecord;
  import org.apache.kafka.clients.consumer.ConsumerRecords;
  import org.apache.kafka.clients.consumer.KafkaConsumer;
  
  import java.time.Duration;
  import java.util.Arrays;
  import java.util.Properties;
  
  public class ConsumerDemo {
      public static void main(String[] args) {
          Properties props = new Properties();
          props.setProperty("bootstrap.servers", "localhost:9092");
          props.setProperty("group.id", "test");
          props.setProperty("enable.auto.commit", "true");
          props.setProperty("auto.commit.interval.ms", "1000");
          props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
          props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
          KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
          consumer.subscribe(Arrays.asList("my-topic", "bar"));
          while (true) {
              ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
              for (ConsumerRecord<String, String> record : records)
                  System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
          }
      }
  }
  ```

  

### 3、下载spring kafka的示例代码

* [下载地址](<https://github.com/spring-projects/spring-kafka>)

![](F:\learning\github\learing-notes\springkafka\img\spring kafka下载示例图.png)

* 导入代码:samples\sample-01

这个示例中的代码的POM文件缺少如下两个依赖，

```xml
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.9.5</version>
		</dependency>

		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-validator</artifactId>
			<version>5.3.0.Final</version>
		</dependency>
```

* 启动application，在下面的两个地方加上断点。

* 使用postMan访问地址：`http://localhost:8082/send/foo/hello-world` 

* 观察断点出：

  ![](F:\learning\github\learing-notes\springkafka\img\kafka发送消息.png)

  日志输出如下：

  ```txt
  2019-04-30 17:23:14.245  INFO 14188 --- [ fooGroup-0-C-1] com.example.Application                  : Received: Foo2 [foo=hello-world]
  ```

  

### 4、整个demo的源码下载地址[https://github.com/zhang1github2test/learing-notes/tree/master/springkafka](<https://github.com/zhang1github2test/learing-notes/tree/master/springkafka>)

## 二、kafka核心流程

### 1、kafkaServer启动流程分析

kafka自带一个启动脚本：kafka-server-start.sh。该脚本调用kafka.kafka类，脚本的核心代码如下：

```txt
exec $base_dir/kafka-run-class.sh $EXTRA_ARGS kafka.Kafka "$@"
```

KafkaServer 启动的入口是 kafka.Kafka.scala。KafkaServer 启动时主要组件的调用关系如图所示。

![](F:\learning\github\learing-notes\springkafka\img\kafka启动流程.png)

KafkaServer 启动的工作是由 KafkaServer.startup() 来完成的，在 Kafka.startup() 方法中会完成相应组件的初始化并启动这些组件。这些组件主要包括任务调度器（KafkaScheduler）、日志管理器（LogManager）、网络通信服务器（SockeServer）、副本管理器（ReplicaManager）、控制器（KafkaController）、组协调器（GroupCoordinator）、动态配置管理器（DynamicConfigManager）以及 Kafka 健康状态检测（KafkaHealthcheck）等。KafkaServer. startup() 所依赖的组件如图所示

![](F:\learning\github\learing-notes\springkafka\img\kafka的startup方法.png)

KafkaServer 在实例化时会在`$log.dir`指定的每个目录下创建一个 meta.properties 文件，该文件记录了与当前 Kafka 版本对应的一个版本 version 字段，当前版本的 Kafka 设置 version 为固定值0，还有一个记录当前代理的 broker.id 的字段。因此，当我们在不改变代理对应的 log.dir 配置而想修改代理的 brokerId 时，需要修改两处的配置。

（1）修改代理对应的 server.properties 文件的 broker.id 的值。

（2）修改 log.dir 目录下 meta.properties 文件的 broker.id 值。若 log.dir 配置了多个目录，则要分别修改各目录下的 meta.properties 文件的 broker.id 值。

KafkaServer 实例化成功后，调用 startup() 方法来完成 KafkaServer 启动操作，具体过程如下。

（1）首先实例化用于限流的 QuotaManagers，这些 Quota 会在后续其他组件实例化时作为入参注入当中，接着设置代理状态为 Starting，即开始启动代理。代理状态机提供了6种状态，如表4-1所示。

| 状　态　名                    | 状态值（单位字节） | 描　　述                                                     |
| :---------------------------- | :----------------- | :----------------------------------------------------------- |
| NotRunning                    | 0                  | 代理未启动                                                   |
| Starting                      | 1                  | 代理正在启动中                                               |
| RecoveringFromUncleanShutdown | 2                  | 代理非正常关闭，在`${log.dir}`配置的每个路径下存在`.kafka_cleanshutdown`文件 |
| RunningAsBroker               | 3                  | 代理已正常启动                                               |
| PendingControlledShutdown     | 6                  | KafkaController 被关闭                                       |
| BrokerShuttingDown            | 7                  | 代理正在准备关闭                                             |

BrokerStates  提供了newState()方法来设置代理的状态变迁。

![](F:\learning\github\learing-notes\springkafka\img\代理的状态变迁.png)

（2）启动任务调度器（KafkaScheduler），KafkaScheduler 是基于 Java.util.concurrent. ScheduledThreadPoolExecutor 来实现的，在 KafkaServer 启动时会构造一个线程总数为`${background.threads}`的线程池，该配置项默认值为10，每个线程的线程名以“kafka-scheduler-”为前缀，后面连接递增的序列号，这些线程作为守护线程在 KafkaServer 启动时开始运行，负责副本管理及日志管理调度等。

（3）创建与 ZooKeeper 的连接，检查并在 ZooKeeper 中创建存储元数据的目录节点，若目录不存在则创建相应目录。KafkaServer 启动时在 ZooKeeper 中要保证如图4-4所示文件目录树被成功创建。

Kafka 在 ZooKeeper 中创建的各节点说明如表所示。

![](F:\learning\github\learing-notes\springkafka\img\kafka的在zookeeper创建的节点列表.png)

表4-2　Kafka 在 ZooKeeper 中注册节点说明

| 节　　点                   | 说　　明                                                     |
| :------------------------- | :----------------------------------------------------------- |
| /consumers                 | 旧版消费者启动后会在 ZooKeeper 的该节点路径下创建一个消费组的节点。将在消费者启动流程中进行介绍 |
| /brokers/seqid             | 辅助生成代理的 id，当用户没有配置 broker.id 时，ZooKeeper 会自动生成一个全局唯一的 id，每次自动生成时会从该路由读取当前代理的 id 最大值，然后加1 |
| /brokers/topics            | 每创建一个主题时就会在该目录下创建一个与主题同名的节点       |
| /brokers/ids               | 当 Kafka 每启动一个 KafkaServer 时会在该目录下创建一个名为`${broker.id}`的子节点 |
| /config/topics             | 存储动态修改主题级别的配置信息                               |
| /config/clients            | 存储动态修改客户端级别的配置信息                             |
| /config/changes            | 动态修改配置时存储相应的信息，在5.5节会做介绍                |
| /admin/delete_topics       | 在对主题进行删除操作时保存待删除主题的信息                   |
| /cluster/id                | 保存集群 id 信息                                             |
| /controller                | 保存控制器对应的 brokerId 信息等                             |
| `/isr_change_notification` | 保存 Kafka 副本 ISR 列表发生变化时通知的相应路径             |

（4）通过 UUID.randomUUID() 生成一个 uuid 值，然后经过 base64 处理得到的值作为 Cluster 的 id，调用 Kafka 实现的 org.apache.kafka.common.ClusterResourceListener 通知集群元数据信息发生变更操作。此时生成的 Cluster 的 id 信息会写入 ZooKeeper 的 /cluster/id 节点中，在 ZooKeeper 客户端通过 get 命令可以查看该 Cluster 的 id 信息。

（5）实例化并启动日志管理器（LogManager）。LogManager 负责日志的创建、读写、检索、清理等操作。

（6）实例化并启动 SocketServer 服务。SocketServer 启动过程在3.4节已有详细介绍，这里不再赘述。

（7）实例化并启动副本管理器（ReplicaManager）。副本管理器负责管理分区副本，它依赖于任务调度器与日志管理器，处理副本消息的添加与读取的操作以及副本数据同步等操作。

（8）实例化并启动控制器。每个代理对应一个 KafkaController 实例，KafkaController 在实例化时会同时实例化分区状态机、副本状态机和控制器选举器 ZooKeeperLeaderElector，实例化4种用于分区选举 Leader 的 PartitionLeaderSelector 对象。在 KafkaController 启动后，会从 KafkaController 中选出一个节点作为 Leader 控制器。Leader 控制器主要负责分区和副本状态的管理、分区重分配、当新创建主题时调用相关方法创建分区等。

（9）实例化并启动组协调器 GroupCoordinator。Kafka 会从代理中选出一个组协调器，对消费者进行管理，当消费者或者订阅的分区主题发生变化时进行平衡操作。

（10）实例权限认证组件以及 Handler 线程池（KafkaRequestHandlerPool）。在 KafkaRequest HandlerPool 中主要是创建`${ num.io.threads }`个 KafkaRequestHandler，Handler 循环从 Request Channel 中取出 Request 并交给 kafka.server.KafkaApis 来处理具体的业务逻辑。在实例化 KafkaRequestHandlerPool 之前先要实例化 KafkaApis，Kafka 将所有请求的 requestId 封装成一个枚举类 ApiKeys。当前版本的 Kafka支持21种类型的请求。

（11）实例化动态配置管理器。注册监听 ZooKeeper 的 /config 路径下各子节点信息变化。

（12）实例化并启动 Kafka 健康状态检查（KafkaHealthcheck）。Kafka 健康检查机制主要是在 ZooKeeper 的 /brokers/ids 路径下创建一个与当前代理的 id 同名的节点，该节点也是一个临时节点。当代理离线时，该节点会被删除，其他代理或者消费者通过判断 /brokers/ids 路径下是否有某个代理的 brokerId 来确定该代理的健康状态。

（13）向 meta.properties 文件中写入当前代理的 id 以及固定版本号为 0 的 version 信息。

（14）注册 Kafka 的 metrics 信息，在 KafkaServer 启动时将一些动态的 JMX Beans 进行注册，以便于对 Kafka 进行跟踪监控。

最后将当前代理的状态设置为 RunningAsBroker，表示当前 KafkaServer 已正常启动完成，

### 创建主题流程分析

创建主题分为两个阶段：第一个阶段是客户端将主题元数据写入Zookeeper，称为客户端创建主题，第二阶段是控制器负责管理主题的创建，我们称为服务端创建主题。

###### 在客户端我们可以通过调用相应 API 或者通过 kafka-topics.sh 脚本来创建一个主题，kafka-topics.sh 脚本只有一行代码：

```txt
exec $(dirname $0)/kafka-run-class.sh kafka.admin.TopicCommand "$@"
```

无论是调用 API 还是通过命令行来创建主题，底层都是客户端通过调用 TopicCommand.create Topic(zkUtils: ZkUtils, opts: TopicCommandOptions) 方法创建主题。该方法逻辑较简单，首先是对主题及相关的配置信息进行相应的校验，然后执行分区副本分配，当然客户端可以直接指定副本分配方案，若客户端没有指定分区副本分配方案，Kafka 会根据分区副本分配策略自动进行分配，最后是在 ZooKeeper 的 /brokers/topics/ 路径下创建节点，将分区副本分配方案写入每个分区节点之中。

![](F:\learning\github\learing-notes\springkafka\img\kafka客户端创建主题.png)

kafka主题命名规则：主题名不能超过249个字母、数字、着重号下划线、连接号。正则表达式为[a-zA-Z0-9\.\_\-]+。

不允许主题名字只有着重号（.）组成。Kafka 建议为了避免主题名字与这些指标字段名称冲突，主题最好不要包括着重号及下划线字符。