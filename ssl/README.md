### spring boot   配置ssl简单示例

##### 一、下载spring boot 的demo包

​	官网下载地址：[下载地址](<https://start.spring.io/>) 

##### 二、新建一个Controller类

```java
package cn.szyrm.ssl.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ssl")
public class SSLController {

    @RequestMapping("/hello")
    public String pingpang(String hello){
        return  hello;
    }
}

```

##### 三、配置http访问的时候跳转到https

```java
@Bean
	public Connector connector(){
		Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(80);
		connector.setSecure(false);
		connector.setRedirectPort(443);
		return connector;
	}

	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory(Connector connector){
		TomcatServletWebServerFactory tomcat=new TomcatServletWebServerFactory(){
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint=new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection=new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(connector);
		return tomcat;
	}
```

##### 四、使用java 自带工具生成密钥文件

```txt
 keytool -genkey -alias tomcat  -storetype PKCS12 -keyalg RSA -keysize 2048  -keystore keystore.p12 -validity 3650
```

![](F:\learning\github\learing-notes\ssl\img\生成密钥截图.png)

##### 五、application.properties  增加如下配置

```properties
server.port=443
server.ssl.key-store=keystore.p12
server.ssl.key-store-password=123456
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
server.tomcat.uri-encoding=utf-8
```

##### 六、测试使用

启功SslApplication应用，在浏览器上输入<https://localhost/ssl/hello?hello=adfdh>

看到如下图所示的返回

![](F:\learning\github\learing-notes\ssl\img\测试https.png)测试使用http://localhost/ssl/hello?hello=adfdh打开，发现浏览器会重新跳转到 https://localhost/ssl/hello?hello=adfdh 地址 



至此，spring boot 配置 https已经完成。