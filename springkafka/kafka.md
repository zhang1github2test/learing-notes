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

* 启动kafka服务端,进入在上述的文件目录下执行命令：bin/windows/zookeeper-server-start.bat config/zookeeper.properties

  ![](F:\learning\github\learing-notes\springkafka\img\启动kafka服务端.png)

### 2、下载spring kafka的示例代码

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

  

3、整个demo的源码下载地址[https://github.com/zhang1github2test/learing-notes/tree/master/springkafka](<https://github.com/zhang1github2test/learing-notes/tree/master/springkafka>)

