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



| 属　性　名                         | 默认值           | 描　　述                                                     |
| :--------------------------------- | :--------------- | :----------------------------------------------------------- |
| message.send.max.retries           | 3                | 设置当生产者向代理发信息时，若代理由于各种原因导致接受失败，生产者在丢弃该消息前进行重试的次数 |
| retry.backoff.ms                   | 100              | 在生产者每次重试之前，生产者会更新主题的 MetaData 信息，以此来检测新的 Leader 是否已选举出来。因为选举 Leader 需要一定时间，所以此选项指定更新主题的 MetaData 之前生产者需要等待的时间，单位为 ms |
| queue.buffering.max.ms             | 1000             | 在异步模式下，表示消息被缓存的最长时间，单位为 ms，当到达该时间后消息将开始批量发送；若在异步模式下同时配置了缓存数据的最大值 batch.num.messages，则达到这两个阈值之一都将开始批量发送消息 |
| queue.buffering.max.messages       | 10000            | 在异步模式下，在生产者必须被阻塞或者数据必须丢失之前，可以缓存到队列中的未发送的最大消息条数，即初始化消息队列的长度 |
| batch.num.messages                 | 200              | 在异步模式下每次批量发送消息的最大消息数                     |
| request.timeout.ms                 | 1500             | 当需要 acks 时，生产者等待代理应答的超时时间，单位为 ms。若在该时间范围内还没有收到应答，则会发送错误到客户端 |
| send.buffer.bytes                  | 100kb            | Socket 发送缓冲区大小                                        |
| topic.metadata.refresh.interval.ms | 5min             | 生产者定时请求更新主题元数据的时间间隔。若设置为0，则在每个消息发送后都去请求更新数据 |
| client.id                          | console-producer | 生产者指定的一个标识字段，在每次请求中包含该字段，用来追踪调用，根据该字段在逻辑上可以确认是哪个应用发出的请求 |
| queue.enqueue.timeout.ms           | 2147483647       | 该值为0表示当队列没满时直接入队，满了则立即丢弃，负数表示无条件阻塞且不丢弃，正数表示阻塞达到该值时长后抛出 Qu |

#### KafkaProducer 实现原理

​	KafkaProducer 是线程安全的，在一个 Kafka 集群中多线程之间共享同一个 KafkaProducer 实例通常比创建多个 KafkaProducer 实例性能要好。KafkaProducer 有一个缓存池，用于存储尚未向代理发送的消息，同时一个后台 I/O 线程负责从缓存池中读取消息构造请求，将消息发送至代理。	

`org.apache.kafka.clients.producer.KafkaProducer#send(org.apache.kafka.clients.producer.ProducerRecord<K,V>, org.apache.kafka.clients.producer.Callback)` 方法实现的是异步发送消息，但是callback会被顺序执行，即先后发送两此消息。第一条消息的callback总是先于第二条消息的callback。

* 如何保证被顺序执行？
  * 会将callback顺序保存在list中
  * 底层实现的时候将`org.apache.kafka.clients.producer.Callback` 对象与每次发送消息返回的`org.apache.kafka.clients.producer.internals.FutureRecordMetadata ` 对象封装成为一个`org.apache.kafka.clients.producer.internals.ProducerBatch.Thunk`对象。
  *  `ProducerBatch `会维护一个thunks列表: `private final List<Thunk> thunks = new ArrayList<>();`  
* 如果想要发送同步消息，该怎么处理？
  * 使用调用send方法后返回的`Future<RecordMetadata>`  对象，调用该对象的get()方法

##### 1、KafkaProducer 实例化过程

(1)、从config中解析出client.id，生产者指定该配置项的值以便追踪程序的运行情况，为String 类型，默认值为“”；如果没有进行设置，会通过以下代码进行设置:以前辍“producer-”后加一个从1递增的整数。

```java
if (clientId.length() <= 0)
                clientId = "producer-" + PRODUCER_CLIENT_ID_SEQUENCE.getAndIncrement();
            this.clientId = clientId;
```

（2）根据配置项创建和注册用于 Kafka metrics 指标收集的相关对象，用于对 Kafka 集群相关指标的追踪。

具体代码如下：

```java
       //创建一个  度量指标的配置类
            Map<String, String> metricTags = Collections.singletonMap("client-id", clientId);
            //1、样本数量默认值为; 2、度量样本的计算时间窗口  默认值为：30s ;3、度量的最高记录级别 默认值为：INFO
            MetricConfig metricConfig = new MetricConfig().samples(config.getInt(ProducerConfig.METRICS_NUM_SAMPLES_CONFIG))
                    .timeWindow(config.getLong(ProducerConfig.METRICS_SAMPLE_WINDOW_MS_CONFIG), TimeUnit.MILLISECONDS)
                    .recordLevel(Sensor.RecordingLevel.forName(config.getString(ProducerConfig.METRICS_RECORDING_LEVEL_CONFIG)))
                    .tags(metricTags);
            List<MetricsReporter> reporters = config.getConfiguredInstances(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG,
                    MetricsReporter.class);
            reporters.add(new JmxReporter(JMX_PREFIX));
            this.metrics = new Metrics(metricConfig, reporters, time);
            ProducerMetrics metricsRegistry = new ProducerMetrics(this.metrics);
```

（3）实例化分区器。分区器用于为消息指定分区，客户端可以通过实现 Partitioner 接口自定义消息分配分区的规则。若用户没有自定义分区器，则在 KafkaProducer 实例化时会使用默认的 DefaultPartitioner，该分区器分配分区的规则是：若消息指定了 Key，则对 Key 取 hash 值，然后与可用的分区总数求模；若没有指定 Key，则 DefalutPartitioner 通过一个随机数与可用的总分区数取模。

`DefalutPartitioner ` 分区源码如下：

```java
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (keyBytes == null) {
            int nextValue = nextValue(topic);
            List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(topic);
            if (availablePartitions.size() > 0) {
                int part = Utils.toPositive(nextValue) % availablePartitions.size();
                return availablePartitions.get(part).partition();
            } else {
                // no partitions are available, give a non-available partition
                return Utils.toPositive(nextValue) % numPartitions;
            }
        } else {
            // hash the keyBytes to choose a partition
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }
```

实例化分区器 的源码如下：

```java
            //实例化分区器    键为:partitioner.class   默认的类为：org.apache.kafka.clients.producer.internals.DefaultPartitioner
            this.partitioner = config.getConfiguredInstance(ProducerConfig.PARTITIONER_CLASS_CONFIG, Partitioner.class);
            //尝试重试对给定主题分区的失败请求之前等待的时间。这避免了在某些故障场景下重复地在紧密循环中发送请求   默认值为100ms
            long retryBackoffMs = config.getLong(ProducerConfig.RETRY_BACKOFF_MS_CONFIG);
```

（4）实例化消息 Key 和 Value 进行序列化操作的 Serializer。Kafka 实现了七种基本类型的 Serializer，如 BytesSerializer、IntegerSerializer、LongSerializer 等。用户也可以实现 Serializer 接口分别为 Key 和 Value 自定义序列化方式，当然在消费者消费消息时要实现相应的反序列化操作。若用户不指定 Serializer，默认 Key 和 Value 使用相同的 ByteArraySerializer。

Key 和 Value 进行序列化操作的 Serializer源码如下：

```java
 if (keySerializer == null) {
                this.keySerializer = ensureExtended(config.getConfiguredInstance(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                        Serializer.class));
                this.keySerializer.configure(config.originals(), true);
            } else {
                config.ignore(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
                this.keySerializer = ensureExtended(keySerializer);
            }
            if (valueSerializer == null) {
                this.valueSerializer = ensureExtended(config.getConfiguredInstance(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        Serializer.class));
                this.valueSerializer.configure(config.originals(), false);
            } else {
                config.ignore(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
                this.valueSerializer = ensureExtended(valueSerializer);
            }
```

（5）根据配置实例化一组拦截器（ProducerInterceptor），用户可以指定多个拦截器。如果我们希望在消息发送前、消息发送到代理并 ack、消息还未到达代理而失败或调用 send() 方法失败这几种情景下进行相应处理操作，就可以通过自定义拦截器实现该接口中相应方法，多个拦截器会被顺序调用执行。

```java
              userProvidedConfigs.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
            //实例化一组 ProducerInterceptor              默认的情况下没有拦截器
            List<ProducerInterceptor<K, V>> interceptorList = (List) (new ProducerConfig(userProvidedConfigs, false)).getConfiguredInstances(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                    ProducerInterceptor.class);
            this.interceptors = new ProducerInterceptors<>(interceptorList);
            ClusterResourceListeners clusterResourceListeners = configureClusterResourceListeners(keySerializer, valueSerializer, interceptorList, reporters);
            //请求的最大长度,默认值为：1M
            this.maxRequestSize = config.getInt(ProducerConfig.MAX_REQUEST_SIZE_CONFIG);
            //生产者可用于缓冲等待发送到服务器的记录的总内存字节数。如果记录发送的速度比发送到服务器的速度快，那么生产者将阻塞max.block。之后，它将抛出异常。
            //此设置应该大致对应于生成器将使用的总内存，但不是硬限制，因为生成器使用的并非所有内存都用于缓冲。一些额外的内存将用于压缩(如果启用了压缩)以及维护正在运行的请求。
            //默认大小：32M
            this.totalMemorySize = config.getLong(ProducerConfig.BUFFER_MEMORY_CONFIG);
            /**
             * 生成器生成的所有数据的压缩类型。默认值是none(即没有压缩)。
             * 有效值为none、gzip、snappy、lz4或zstd。
             * 压缩是整批数据的压缩，因此批处理的效果也会影响压缩比(批处理越多压缩越好)。
             */
            this.compressionType = CompressionType.forName(config.getString(ProducerConfig.COMPRESSION_TYPE_CONFIG));
            /**
             * 配置控制KafkaProducer.send()和KafkaProducer.partitionsFor()将阻塞多长时间。
             * 这些方法可以被阻塞，因为缓冲区已满或元数据不可用。
             * 在用户提供的序列化程序或分区程序中的阻塞将不计入此超时
             * 默认时长：60000ms
             */
            this.maxBlockTimeMs = config.getLong(ProducerConfig.MAX_BLOCK_MS_CONFIG);
            /**
             * 配置控制客户机等待请求响应的最大时间量。
             * 如果超时超时之前没有收到响应，客户端将在必要时重新发送请求，或者在重试耗尽时请求失败。
             * 这个应该大于replica.lag.time.max.ms(代理配置)以减少由于不必要的生成器重试而导致消息重复的可能性。
             * 默认值：30000ms
             */
            this.requestTimeoutMs = config.getInt(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG);
            //创建事务管理器
            this.transactionManager = configureTransactionState(config, logContext, log);
            /**
             * 设置重试次数
             * 如果开启幂等性,即enable.idempotence = true,此时
             *      1、如果retries参数没有设置，那么retries的值为Integer.MAX
             *      2、如果显示的设置retries为0,抛出ConfigException
             *      3、如果显示的设置为其他的大于0的整数，按照获取的值
             */
            int retries = configureRetries(config, transactionManager != null, log);
            /**
             * 客户机在阻塞之前在单个连接上发送的未确认请求的最大数量。
             * 注意，如果该设置被设置为大于1且发送失败，则进行重试(即，如果启用重试功能)。
             * 默认值为5,如果开启幂等性该值能大于5
             */
            int maxInflightRequests = configureInflightRequests(config, transactionManager != null);
            /**
             *确认字符设置
             * 默认值为：1 ;有效值为：[all, -1, 0, 1]  all 等价于-1
             *acks =0,那么生产者将不会等待任何来自服务器的确认。此场景是最容易丢失消息的
             * acks = 1,这将意味着领导者将记录写入其本地日志，但将在不等待所有追随者的完全确认的情况下作出响应。
             *        在这种情况下，如果领导者在确认记录后但是在追随者复制记录之前失败，那么记录将会丢失。
             * acks = -1  这意味着领导者将等待完整的同步副本来确认记录。
             *            这保证了只要至少有一个同步副本仍然存在，记录就不会丢失。
             *            这是最有力的保证。
             */
            short acks = configureAcks(config, transactionManager != null, log);

            this.apiVersions = new ApiVersions();

        
            /**
             * 获取引导服务器地址列表
             * 该列表值不需要包含集群的所有地址信息，但是最好配置超过一台，防止配置的服务器宕机。
             */
            List<InetSocketAddress> addresses = ClientUtils.parseAndValidateAddresses(config.getList(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
```

（6）实例化用于存储消息的 RecordAccumulator。RecordAccumulator 的作用类似一个队列，这里称为消息累加器。KafkaProducer 发送的消息都先被追加到消息累加器的一个双端对列 Deque中，在消息累加器内部每一个主题的每一个分区 TopicPartition 对应一个双端队列，队列中的元素是 RecordBatch，而 RecordBatch 是由同一个主题发往同一个分区的多条消息 Record 组成（在老版本中称消息称为 message，在 0.9 之后版本中称为 Record），并将结果以每个 TopicPartiton 作为 Key，该 TopicPartition 所对应的双端队列作为 Value 保存到一个 ConcurrentMap 类型的 batches 中。采用双端队列是为了当消息发送失败需要重试时，将消息优先插入到队列的头部，而最新的消息总是插入到队列尾部，只有需要重试发送时才在队列头部插入，发送消息是从队列头部获取 RecordBatch，这样就实现了对发送失败的消息进行重试发送。但是双端队列只是指定了 RecordBatch 的顺序存储方式，而并没有定义存储空间大小，在消息累加器中有一个 BufferPool 缓存数据结构，用于存储消息 Record。在 KafkaProducer 初始化时根据指定的 BufferPool 的大小初始化一个 BufferPool，引用名为 free。

```java
    /**
             * 实例化记录聚集器队列，
             * 将记录累积到MemoryRecords实例中,并发送给服务器。
             */
            this.accumulator = new RecordAccumulator(logContext,
                    //发送到同一分区的批处理的最大值  默认为：16KB
                    config.getInt(ProducerConfig.BATCH_SIZE_CONFIG),
                    this.totalMemorySize,
                    this.compressionType,
                    //消息延迟发送的毫秒数
                    config.getLong(ProducerConfig.LINGER_MS_CONFIG),
                    retryBackoffMs,
                    metrics,
                    time,
                    apiVersions,
                    transactionManager);
```

kafkaProducer重要配置属性说明

| 属性名                                | 默认值 | 属性描述                                                     |
| :------------------------------------ | :----- | :----------------------------------------------------------- |
| metadata.max.age.ms                   | 5 min  | 用于配置强制更新 metadata 的时间间隔，单位是ms               |
| max.request.size                      | 1 MB   | 用于配置生产者每次请求的最大字节数                           |
| buffer.memory                         | 32 MB  | 用于配置 RecordAccumulator 中 BufferPool 的大小              |
| batch.size                            | 16 KB  | 用于配置 RecordBatch 的大小                                  |
| linger.ms                             | 0 ms   | 生产者默认会把两次发送时间间隔内收集到的所有发送消息的请求进行一次聚合然后再发送，以此提高吞吐量，如消息聚合的数量小于 batch.size，则再在这个时间间隔内再增加一些延时。通过该配置项可以在消息产生速度大于发送速度时，一定程度上降低负载。 |
| max.block.ms                          | 60 s   | 消息发送或获取分区元数据信息时最大等待时间                   |
| max.in.flight.requests.per.connection | 5      | 用于设置每个连接的最大请求个数                               |
| retries                               | 0      | 用于配置发送失败的重试次数，默认是0，即不重试。Kafka 自带的客户端设置发送失败时重试3次 |

（7）实例化用于消息发送相关元数据信息的 MetaData 对象。MetaData 是被客户线程共享的，因此 MetaData 必须是线程安全的。MetaData 的主要数据结构由两部分组成，一类是用于控制 MetaData 进行更新操作的相关配置信息，另一类就是集群信息 Cluster。Cluster 保存了集群中所有的主题以及所有主题对应的分区信息列表、可用的分区列表、集群的代理列表等信息，在 KafkaProducer 实例化过程中会根据指定的代理列表初始化 Cluster，并第一次更新 MetaData。

```java
  if (metadata != null) {
                this.metadata = metadata;
            } else {
                this.metadata = new Metadata(retryBackoffMs, config.getLong(ProducerConfig.METADATA_MAX_AGE_CONFIG),
                        true, true, clusterResourceListeners);
                this.metadata.update(Cluster.bootstrap(addresses), Collections.<String>emptySet(), time.milliseconds());
            }
```

（8）根据指定的安全协议`${ security.protocol}`创建一个 ChannelBuilder，Kafka 目前支持 PLAINTEXT、SSL、`SASL_PLAINTEXT`、`SASL_SSL`和 TRACE 这5种协议。然后创建 NetworkClient 实例，这个对象的底层是通过维持一个 Socket 连接来进行 TCP 通信的，用于生产者与各个代理进行 Socket 通信。由 NetworkClient 对象构造一个用于数据发送的 Sender 实例 sender 线程，最后通过 sender 创建一个 KafkaThread 线程，启动该线程，该线程是一个守护线程，在后台不断轮询，将消息发送给代理。

```java
    public static ChannelBuilder createChannelBuilder(AbstractConfig config) {
        /**
         * 获取security.protocol的配置值，默认为：PLAINTEXT  (明文)
         */
        SecurityProtocol securityProtocol = SecurityProtocol.forName(config.getString(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        String clientSaslMechanism = config.getString(SaslConfigs.SASL_MECHANISM);
        return ChannelBuilders.clientChannelBuilder(securityProtocol, JaasContext.Type.CLIENT, config, null,
                clientSaslMechanism, true);
    }
```

(9)其他一些设置：实例化发送线程`Sender`，将该 sender设置到kafka的ioThread线程中。

```java
			 /**
             * 实例化发送线程
             */
            this.sender = new Sender(logContext,
                    client,
                    this.metadata,
                    this.accumulator,
                    maxInflightRequests == 1,
                    config.getInt(ProducerConfig.MAX_REQUEST_SIZE_CONFIG),
                    acks,
                    retries,
                    metricsRegistry.senderMetrics,
                    Time.SYSTEM,
                    this.requestTimeoutMs,
                    config.getLong(ProducerConfig.RETRY_BACKOFF_MS_CONFIG),
                    this.transactionManager,
                    apiVersions);
            String ioThreadName = NETWORK_THREAD_PREFIX + " | " + clientId;
            this.ioThread = new KafkaThread(ioThreadName, this.sender, true);
            this.ioThread.start();
            this.errors = this.metrics.sensor("errors");
            config.logUnused();
            AppInfoParser.registerAppInfo(JMX_PREFIX, clientId, metrics);
```

##### 2、send 过程分析

​	在 KafkaProducer 实例化后，调用 KafkaProducer.send() 方法进行消息发送。下面通过对 Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) 方法进行分析，详细讲解消息 send 的过程。

​	首先，若客户端指定了拦截器链 ProducerInterceptors（由一个或多个 ProducerInterceptor 构成的 List，List<ProducerInterceptor<K, V>> interceptors），则 ProducerRecord 会被拦截器链中每个 ProducerInterceptor 调用其 onSend(ProducerRecord<K, V> record) 方法进行处理。

接着，调用 KafkaProducer.doSend() 方法进行处理。为了讲解方便，将 doSend() 方法分以下几步进行讲解。

（1）获取 MetaData。通过调用 waitOnMetadata() 方法对 MetaData 进行相应处理获取元数据信息 MetaData，因为只有获取到 Metadata 元数据信息才能真正进行消息的投递，因此该方法会一直被阻塞尝试去获取 MetaData，若超过`${max.block.ms}`时间后，依然没有获取到 MetaData 信息，则会抛出 TimeoutException 宣告消息发送失败，若客户端定义了拦截器，同时实现了 onAcknowledgement() 方法则该异常会被拦截器进行处理。KafkaProducer 会调用 ProducerInterceptors.on SendError() 方法进行处理，在该方法中会按序逐个调用 ProducerInterceptor.onAcknowledgement() 进行处理。

```java
 // Issue metadata requests until we have metadata for the topic or maxWaitTimeMs is exceeded.
        // In case we already have cached metadata for the topic, but the requested partition is greater
        // than expected, issue an update request only once. This is necessary in case the metadata
        // is stale and the number of partitions for this topic has increased in the meantime.
        do {
            log.trace("Requesting metadata update for topic {}.", topic);
            metadata.add(topic);
            int version = metadata.requestUpdate();
            sender.wakeup();
            try {
              //  等待元数据更新，直到当前版本大于我们所知的最后一个版本
                metadata.awaitUpdate(version, remainingWaitMs);
            } catch (TimeoutException ex) {
                // Rethrow with original maxWaitMs to prevent logging exception with remainingWaitMs
                throw new TimeoutException("Failed to update metadata after " + maxWaitMs + " ms.");
            }
            cluster = metadata.fetch();
            elapsed = time.milliseconds() - begin;
            if (elapsed >= maxWaitMs)
                throw new TimeoutException("Failed to update metadata after " + maxWaitMs + " ms.");
            if (cluster.unauthorizedTopics().contains(topic))
                throw new TopicAuthorizationException(topic);
            remainingWaitMs = maxWaitMs - elapsed;
            partitionsCount = cluster.partitionCountForTopic(topic);
        } while (partitionsCount == null);  do {
            log.trace("Requesting metadata update for topic {}.", topic);
            metadata.add(topic);
            int version = metadata.requestUpdate();
            sender.wakeup();
            try {
                metadata.awaitUpdate(version, remainingWaitMs);
            } catch (TimeoutException ex) {
                // Rethrow with original maxWaitMs to prevent logging exception with remainingWaitMs
                throw new TimeoutException("Failed to update metadata after " + maxWaitMs + " ms.");
            }
            cluster = metadata.fetch();
            elapsed = time.milliseconds() - begin;
            if (elapsed >= maxWaitMs)
                throw new TimeoutException("Failed to update metadata after " + maxWaitMs + " ms.");
            if (cluster.unauthorizedTopics().contains(topic))
                throw new TopicAuthorizationException(topic);
            remainingWaitMs = maxWaitMs - elapsed;
            partitionsCount = cluster.partitionCountForTopic(topic);
        } while (partitionsCount == null);
```

（2）序列化。根据 KafkaProducer 实例化时创建的 Key 和 Value 的 Serializer，分别对 ProducerRecord 的 Key 和 Value 进行序列化操作，将 Key 和 Value 转为 byte 数组类型。

```java
  try {
                serializedKey = keySerializer.serialize(record.topic(), record.headers(), record.key());
            } catch (ClassCastException cce) {
                throw new SerializationException("Can't convert key of class " + record.key().getClass().getName() +
                        " to class " + producerConfig.getClass(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG).getName() +
                        " specified in key.serializer", cce);
            }
            byte[] serializedValue;
            try {
                serializedValue = valueSerializer.serialize(record.topic(), record.headers(), record.value());
            } catch (ClassCastException cce) {
                throw new SerializationException("Can't convert value of class " + record.value().getClass().getName() +
                        " to class " + producerConfig.getClass(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG).getName() +
                        " specified in value.serializer", cce);
            }
```

（3）获取分区。计算 ProducerRecord 将被发往的分区对应的 partitionId，如果客户端在创建 ProducerRecord 时指定了 partitionId 则直接返回所指定的 partitionId，否则根据分区器定义的分区分配策略计算出 partitionId。

```java
   private int partition(ProducerRecord<K, V> record, byte[] serializedKey, byte[] serializedValue, Cluster cluster) {
        Integer partition = record.partition();
        return partition != null ?
                partition :
                partitioner.partition(
                        record.topic(), record.key(), serializedKey, record.value(), serializedValue, cluster);
    }
```

（4）创建 TopicPartition 对象。根据 ProducerRecord 对应的 topic 及 partitionId，创建一个 TopicPartition 对象，在 RecordAccumulator 中会为每个 TopicPartiton 创建一个双端队列。

（5）ProducerRecord 长度有效性检查。检查 ProducerRecord 总长度是否超过了`${max.request.size}`及`${buffer.memory}`所设阈值，超过任何一项阈值配置都会抛出 RecordTooLargeException。

```java
    private void ensureValidRecordSize(int size) {
        if (size > this.maxRequestSize)
            throw new RecordTooLargeException("The message is " + size +
                    " bytes when serialized which is larger than the maximum request size you have configured with the " +
                    ProducerConfig.MAX_REQUEST_SIZE_CONFIG +
                    " configuration.");
        if (size > this.totalMemorySize)
            throw new RecordTooLargeException("The message is " + size +
                    " bytes when serialized which is larger than the total memory buffer you have configured with the " +
                    ProducerConfig.BUFFER_MEMORY_CONFIG +
                    " configuration.");
    }
```

（6）构造 Callback 对象。由 KafkaProducer 实例化时定义的 ProducerInterceptors 和 Callback 重新构造一个 Callback 对象，该对象最终会交由 RecordBatch 处理。

```java
 Callback interceptCallback = new org.apache.kafka.clients.producer.KafkaProducer.InterceptorCallback<>(callback, this.interceptors, tp);
```

（7）写 BufferPool 操作。这一步是调用 RecordAccumulator.append() 方法将 ProducerRecord 写入 RecordAccumulator 的 BufferPool 中。

```java
   RecordAccumulator.RecordAppendResult result = accumulator.append(tp, timestamp, serializedKey,
                    serializedValue, headers, interceptCallback, remainingWaitMs);
```

（8）返回第7步的处理结果。至此，send 过程分析完毕

##### 3、RecordAccumulator.append() 方法实现逻辑进行分析

* （1）、首先调用`appendsInProgress.incrementAndGet();` ,对appendsInProgress 进行加1操作。`RecordAccumulator.append()` 方法完成后，不论成功或失败。都需要将appendsInProgress进行decrementAndGet操作恢复原始值，计数减 1。以确保关闭生产者(即调用`KafkaProducer.close() `),能够放弃未处理完的请求，释放资源。

* （2）、通过本ProducerRecord构造的TopicPartition获取其对应的双端队列Deque<RecordBatch>。如获取不到创建一个新的空队列，并将其保存再batches中。

  ```java
  private Deque<ProducerBatch> getOrCreateDeque(TopicPartition tp) {
          Deque<ProducerBatch> d = this.batches.get(tp);
          if (d != null)
              return d;
          d = new ArrayDeque<>();
          Deque<ProducerBatch> previous = this.batches.putIfAbsent(tp, d);
          if (previous == null)
              return d;
          else
              return previous;
      }
  ```

  

* （3）、获取到Deque后，调用`org.apache.kafka.clients.producer.internals.RecordAccumulator#tryAppend`方法尝试对消息进入写入，该过程是一个同步操作。锁对象为当前的deque实例，这样保证了相同的`TopicPartition` 的append操作只能顺序执行，同时保证了同一个`TopicPartition` 写入同一个分区的BufferPool是有序的。

  ```java
      Deque<ProducerBatch> dq = getOrCreateDeque(tp);
              synchronized (dq) {
                  if (closed)
                      throw new KafkaException("Producer closed while send in progress");
                  RecordAppendResult appendResult = tryAppend(timestamp, key, value, headers, callback, dq);
                  if (appendResult != null)
                      return appendResult;
              }
  ```

##### 4、RecordAccumulator.tryAppend() 方法的具体实现

* (1）、在分析tryAppend()方法之前，首先要明确:RecordAccumulator、BufferPool、RecordBatch、MemoryRecords、ByteBuffer 之间的关系。

* (2)调用`this.recordsBuilder.append` 方法将record写入到消息缓存区。消息写入成功后将会进行如下操作：

  * 取当前的ProducerBatch 的maxRecordSize与写入消息的总长度两者之中较大者更新当前 Record Batch 的 maxRecordSize。maxRecordSize 用于 Kafka 相关指标监控

  * 更新lastAppendTime,每次 append() 操作完成后更新该字段，记录最后一次追加操作的时间。

    ```java
     this.lastAppendTime = now;
    ```

  * 构造一个FutureRecordMetadata类型future对象。

  * 通过future和callBack创建一个Thunk对象添加到thunks列表中
  * 用于统计producerBatch中的Record总数的recordCount加1
  * 返回future

##### 5、Sender发送消息

​	上面对kafkaProducer的send过程进行了分析，但send操作没有发起网络请求。只是将消息发送到消息缓冲区。而网络请求是由 KafkaProducer 实例化时创建的 Sender 线程来完成的。后台线程 Sender 不断循环，把消息发送给 Kafka 集群。一个完整的 KafkaProducer 发送消息过程。

###### 							kafkaProducer生产消息主体流程

![](F:\learning\github\learing-notes\springkafka\img\kafka 消息发送流程图.png)

kafkaProducer实例化的时候，创建了一个kafkaThread线程对象，该对象包含了一个Sender线程。下图介绍其在发送消息在网络层执行过程设计的类。

![](F:\learning\github\learing-notes\springkafka\img\kafka  消息发送过程网络层类层次关系.png)

从Sender所依赖的3个类，我们可以大致梳理出Sender基本操作流程如下。

​	





### 三、kafkaConsumer初始化

kafkaConsumer是非线程安全的。如何保证在多线程环境下的数据消费的正确性：

> 1、通过调用acquire()方法检测是否由多线程并发操作
>
> 2、已经发现多线程并发操作就抛出异常

基于上述，我们可以认为`kafkaConsumer` 相关操作是在“轻量锁”的控制下完成。

acquire()方法和release()成对出现与锁的lock和unlock用法类似。



`kafkaConsumer` 实现了`Consumer` 接口，`Consumer` 定义了对外提供API,主要有：

>1、订阅消息的方法：sunsrcibe() 方法 、assign()方法,分别用来指定订阅主题和订阅主题的某些分区
>
>2、poll()方法，拉去消息
>
>3、seek()方法、seekToBeginning()方法 和seekToEnd()方法，用来指定消费起始位置
>
>4、commitSync()方法和commitAsync()方法，分别用来以同步和异步方式提交消费偏移量
>
>5、assignment()方法， 获取分区分配关系的assignment()方法
>
>6、position()方法,获取下一次消费消息位置
>
>7、pause()，resume()方法等。

###### 1、kafka初始化过程

与producer实例化过程类似，只是实例化的组件不同 而已。定义的变量如下：

- `CONSUMER_CLIENT_ID_SEQUENCE`：当客户端没有指定消费者的 clientId 时，Kafka 自动为该消费者线程生成一个 clientId，该 clientId 以“consumer-”为前缀，之后为以`CONSUMER_CLIENT_ID_SEQUENCE`生成的自增整数组合构成的字符串。
- currentThread：记录当前操作 KafkaConsumer 的线程 Id，该字段起始值为−1（`NO_CURR ENT_THREAD`）。在 acquire() 方法中通过检测该字段是否等于−1。若不等于−1则表示已有线程在操作 KafkaConsumer，此时抛出 ConcurrentModificationException；若该字段值等于−1则表示目前还没有线程在操作，此时调用 acquire() 方法检测的线程将获得 KafkaConsumer 的使用权。
- refcount：用于记录当前操作 KafkaConsumer 的线程数，初始值为0。在 acquire() 方法中若检测到当前线程具有对 KafkaConsumer 的使用权后，refcount 值加1操作（incrementAndGet），即记录当前已有一个线程在使用 KafkaConsumer。在 release() 方法中，若 refcount 减1操作（decrementAndGet）之后的值等于0，则将 currentThread 的值重置为−1，这样新的线程就可以请求使用 KafkaConsumer 了。

表4-8　KafkaConsumer 重要配置说明

| 属性名                  | 默认值    | 描述                                                         |
| :---------------------- | :-------- | :----------------------------------------------------------- |
| group.id                | /         | 消费组 id，新版本消费者必须由客户端指定                      |
| client.id               | /         | KafkaConsumer 对应的客户端 id，客户端可以不指定，Kafka 会自动生成一个 clientId 字符串 |
| key.deserializer        | /         | 消息的 Key 反序列化类，需要实现 org.apache.ka fka.common.seria lization.Deserializer 接口 |
| value.deserializer      | /         | 消息的 Value 反序列化类，需要实现 org.apache.ka f ka.common. serialization.Deserializer 接口 |
| enable.auto.commit      | true      | 是否开启自动提交消费偏移量                                   |
| max.poll.records        | 500       | 一次拉取消息的最大数量                                       |
| max.poll.interval.ms    | 300000 ms | 当通过消费组管理消费者时，该配置指定拉取消息线程最长空闲时间，若超过这个时间间隔还没有发起 poll 操作，则消费组认为该消费者已离开了消费组，将进行平衡操作 |
| send.buffer.bytes       | 128 KB    | Socket 发送消息缓冲区大小                                    |
| receive.buffer.bytes    | 64 KB     | Socket 接收消息缓冲区大小                                    |
| fetch.min.bytes         | 1         | 一次拉取操作等待消息的最小字节数                             |
| fetch.max.bytes         | 50 MB     | 一次拉取操作获取消息的最大字节数                             |
| session.timeout.ms      | 10000 ms  | 与 ZooKeeper 会话超时时间，当通过消费组管理消费者时，如果在该配置的时间内组协调器没有收到消费者发来的心跳请求，则协调器会将该消费者从消费组中移除 |
| request.timeout.ms      | 305000 ms | 客户端发送请求后等待回应的超时时间                           |
| heartbeat.interval.ms   | 3000 ms   | 发送心跳请求的时间间隔                                       |
| auto.commit.interval.ms | 5000 ms   | 自动提交消费偏移量的时间间隔                                 |
| fetch.max.wait.ms       | 500 ms    | 若是不满足 fetch.min.bytes 时，客户端等待请求的最长等待时间  |



###### kafka主要依赖组件如下图所示：

![](F:\learning\github\learing-notes\springkafka\img\kafkaConsumer 依赖的组件图.png)

ConsumerConfig:消费者级别的 配置，将相应的配置传递给其他组件。

SubscriptionState:维护消费者消费和订阅消息的情况。

ConsumerCoodinator：负责消费者与服务端 GroupCoordinator 通信

ConsumerNetworkClient:对网络层通信NetworkClient的封装，用于消费者与服务端的通信

Fetcher:对ConsumerNetworkClient进行了包装，负责从服务端获取消息。