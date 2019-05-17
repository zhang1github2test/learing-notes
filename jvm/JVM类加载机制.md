# JVM类加载机制

一个java类的完整生命周期会经历加载、连接、初始化、使用、卸载五个大阶段。当程序需要某个类时，Java虚拟即首先要确保这个类已经被加载、连接和初始化，其中连接过程有包括验证、准备和解析这三个子步骤。这些步骤按照下面的顺序执行：

![](F:\learning\github\learing-notes\jvm\img\jvm类加载机制.png)

加载：查找并加载类的二进制数据，并利用字节码文件创建一个Class对象

连接：

```txt
- 验证：确保被加载的类的正确性，可被JVM执行
- 准备：为类静态变量分配内存，并将其初始化为系统默认值
		系统默认初始值如下：
			1、Java 八中基本数据类型默认的初始值是0；
			2、引用类型默认的初始值是null
			3、static final 修饰的常量为程序中设定的值
- 解析：把类中的符合引用转换为直接引用。
```

初始化：主要完成对静态变量的初始化（为类的静态变量赋予用户定义的初始值），静态块执行。类的初始化过程是按照顺序自上而下给静态变量复制并执行静态语句。如果有父类，则首先按照顺序父类中静态语句。

#### 注意：引用类的静态变量，不会导致子类初始化(这属下面将要讲到的被动使用的一种)

```java

class Person {
    public static String str = "hello world!";
    static {
        System.out.println("Person static block");
    }
}
class Teacher extends Person {
    static {
        System.out.println("Teacher static block");
    }
}

public class Test {
    public static void main(String[] args) {
        System.out.println(Teacher.str);
    }
}
```

运行 Test类的main方法，输出如下：

```txt
Person static block
hello world!
```

`ClassLoader类的loadClass()与Class.forName()方法区别`

这两个方法都可以用来加载目标类，但是调用ClassLoader类的loadClass()方法加载一个类，并不会导致类的初始化。如下面的示例：

```java
 class LoadTest {
    static {
        System.out.println("hello static");
    }
}

public class Test3 {

    public static void main(String[] args) throws ClassNotFoundException {
        // 初始化，
        Class.forName("com.kafkaDemo.LoadTest");
        // 不会初始化
        Class.forName("com.kafkaDemo.LoadTest", false, ClassLoader.getSystemClassLoader());
        ClassLoader.getSystemClassLoader().loadClass("com.kafkaDemo.LoadTest");
    }

}
```

* 主动使用(七种)：
  * 创建类的实例
  * 访问某个类或者接口的静态变量，或者对该静态变量赋值
  * 调用类的静态方法
  * 反射（如Class.forName("com.kafkaDemo.LoadTest"))
  * 初始化一个类的子类
  * java虚拟即启动时被 标明为启动类的类
  * JDK1.7开始提供的动态语言支持
* 被动使用，除主动使用外的其他情况
  * 引用父类的静态字段，只会引起父类的初始化，而不会引起子类的初始化；
  * 定义类数组,不会引起类的初始化
  * 引用类的常量，不会引起类的初始化

类的卸载

​	当程序中不在有该类的引用，该类也会被JVM执行垃圾回收，对象就不在存在，对象的生命也就走到了尽头，从此生命周期结束。下面用一张图来展示一个类的生命周期阶段

​	![](F:\learning\github\learing-notes\jvm\img\jvm生命周期图.png)

### 类加载器的双亲委派机制

###### 类加载器的任务什么？

根据一个类的全限定名来读取此类的二进制字节流到JVM中，然后转换为一个与目标类对象的Class对象。

###### JVM虚拟机中有哪些加载器？

* 启动（Bootstrap）类加载器(也称根类加载器)
  * c++语言编写，虚拟机的一部分
  * 用来装载核心类库；`jre/lib`、`jre/classes`  、或者被xbootclassPath参数指定的路劲的类
* 扩展（Extension）类加载器
  * java 编写，Launcher类中定义的静态内部类，具体就是`sun.misc.Launcher.ExtClassLoader`
  * 主要负责加载java的扩展类库：jre/lib/ext目录下所有jar包以及java.ext.dirs系统变量指定的路劲中类库。
  * 父加载器指定 为`BootstrapClassLoader` 
* 系统（System）类加载器
  * java编写，Launcher类中定义的静态内部类，具体就是`sun.misc.Launcher.AppClassLoader` 
  * 主要负责加载classpath所指定的位置的类或者jar包，我们编写的java代码也是有它加载。它也是我们实现自定义类加载器时默认的父类加载器
  * 父加载器为：`ExtClassLoader` 
  * `ClassLoader` 中提供了一个`getSytemClassLoader()` 方法返回的就是`AppClassLoader` 。

```java

public class Test5 {
    public static void main(String[] args) {
        ClassLoader loader = Test.class.getClassLoader();  //获取Test5类的类加载器
        System.out.println(loader);

        ClassLoader loader1 = loader.getParent();  //获取c这个类加载器的父类加载器
        System.out.println(loader1);

        ClassLoader loader2 = loader1.getParent(); //获取c1这个类加载器的父类加载器
        System.out.println(loader2);
    }
}

```

运行Test5的main方法，控制台输出如下：

```txt
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$ExtClassLoader@5b1d2887
null
```

​	`ExtClassLoader` 的partent为null,原因是因为`BootstrapClassLoader` 语言编写的，这对java是不可见的。如果某个类的Class对象的classLoader属性为null，那么就表示这个类也是由根加载器加载的。

​	类加载器的父子关系并非通常所说的类的继承关系，而是采用组合关系来符合父类加载器的相关代码。除顶层的启动类加载器外，其余的类加载器都应当由自己的父类加载器。

#### 双亲委派机制

###### 	什么双亲委派？

	>双亲委派机制就是如果一个类加载器收到了类加载请求，它并不会自己先去加载，而是把这个请求委托给父类加载器去执行，如果父类加载器还存在其他的父类加载器，则进一步向上委托，一次递归。这样请求最终将到达顶层的启动类加载器，如果父类加载器可以完成类加载任务，就成功返回。否则子类才会尝试自己去加载，这就是双亲委派机制。

![](F:\learning\github\learing-notes\jvm\img\JVM双亲加载机制.png)

###### 	双亲委派机制是如何体现的？ClassLoader类的loadClass方法：

```java
protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException
  {
      synchronized (getClassLoadingLock(name)) {
          // 先查找该class对象否已经被加载，找到就不用重新加载
          Class<?> c = findLoadedClass(name);
          if (c == null) {
              long t0 = System.nanoTime();
              try {
                  if (parent != null) {
                      //如果存在父类加载器，则委托给父类加载器去加载
                      c = parent.loadClass(name, false);
                  } else {
                  //如果没有父类，则委托给启动加载器去加载
                      c = findBootstrapClassOrNull(name);
                  }
              } catch (ClassNotFoundException e) {
                  // ClassNotFoundException thrown if class not found
                  // from the non-null parent class loader
              }

              if (c == null) {
                  // If still not found, then invoke findClass in order
                  // 如果父类加载器和启动类加载器都不能完成加载任务，则通过自定义实现的findClass去查找并加载
                  c = findClass(name);

                  // this is the defining class loader; record the stats
                  sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                  sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                  sun.misc.PerfCounter.getFindClasses().increment();
              }
          }
          if (resolve) {//是否需要在加载时进行解析
              resolveClass(c);
          }
          return c;
      }
```

>类请求到来的是，加载的步骤：
>
>​	1、先从JVM缓存中查找该类对象，如果存在就直接返回；如果不存在则交给该类加载器的父类加载器去加载，倘若没有父类加载器则交给顶层启动类加载器进行加载。

###### 为什么要使用双亲委派？

* 避免类的重复加载
  * 当父类已经加载了某个类时，子类加载器就没有必要再加载一次
* 考虑到安全因素，java核心基础api中定义的类不能被随意替换
  * 如果通过网络传递一个名为`java.lang.Integer` 的类，通过双亲委派模式传递后，父加载器发现已经加载了该类，就不会重新加载传递过来的`java.lang.Integer` ,而直接返回已经加载的Integer.class,这样可以防止核心API库被篡改 。

###### 自定义classLoader

​	java 提供的几种默认的ClassLoader，用来加载指定目录下的class文件，如果我们想加载其他位置的类时，比如加载网络上的一个class文件时，动态加载到内存后。自定义类加载器一般分为两步：

> 1、继承java.lang.classLoader类
>
> 2、重写父类的findClass()方法。

​	findClass()方法是loadClass()方法中被调用，当loadClass()方法中父加载器加载失败后，则会调用自己的`findClass()` 方法来完成类加载，这样就可以保证自己定义的类加载器也符合双亲委托模式。如果没有特殊的要求，不建议重写loadClass()方法中搜索类的算法。

```java
public class Test6 extends ClassLoader {

    private final String fileExtenstion = ".class";

    private String classLoaderName;

    private String path = "C:\\Users\\ason\\Desktop\\";

    public Test6(String classLoaderName) {
        this.classLoaderName = classLoaderName;
    }

    //由loadClass()方法调用
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 会不会执行？
        System.out.println("findClass invoke: "+name);

        byte[] data = loadClassData(name);
        return defineClass(name, data, 0, data.length);
    }

    private byte[] loadClassData(String name) {
        InputStream is = null;
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        name = name.replace(".", "\\");
        try {
            is = new FileInputStream(new File(this.path+name+this.fileExtenstion));
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while (-1 !=(ch = is.read()))
            {
                baos.write(ch);
            }
            data = baos.toByteArray();

        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void main(String[] args) throws Exception {
        Test6 myloader = new Test6("myloader");
        Class<?> clazz = myloader.loadClass("com.chm.jvm.Test");
        Object object = clazz.newInstance();
        System.out.println(object);

    }
}
```

程序执行后会打印如下结果：

```txt
findClass invoke:com.chm.jvm.Test
com.chm.jvm.Test@14ae5a5
```

如果程序值打印了一行，说明com.chm.jvm.Test 已经被 appClassLoader进行加载了。

#### 命名空间

​	每个类加载器都有自己的命令空间。命令空间由该类加载即所有父类加载器所加载的类组成。同一个命名空间中，不会出现类的完成名字相同的两个类；反之，在不同的命令空间中，可能会出现类的完成名字相同的类。

相同名称的类可以存在Java虚拟机中，只需要用不同的类加载器来加载他们即可。 如果两个加载器之间没有直接或者间接的父子关系，那么他们各自加载的类相互不可见，也就是说处于不同命令空间的两个类是不可见的。



