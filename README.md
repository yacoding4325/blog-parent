项目讲解说明：

提供前端工程，只需要实现后端接口即可
项目以单体架构入手，先快速开发，不考虑项目优化，降低开发负担
开发完成后，开始优化项目，提升编程思维能力
比如页面静态化，缓存，云存储，日志等

项目使用技术 ： springboot + mybatisplus+redis+mysql

#1. 工程搭建

#2. 首页

#3.登录

#3. 注册

#4. 文章详情--线程池的使用

#5. 评论

#6. 写文章

#7. 图片上传

#8.管理后台--权限




博客项目

1.项目搭建 pom文件导入依赖出现，子工程无法继承父工程--将父工程给封装，创建子工程来继承父工程的pom文件

2.数据库连接idea，了解   IOC 控制反转，依赖注入，

AOP ：面向切面编程

3.jwt登录功能。。

4.首页文章列表功能无法展现。。无法找到TagMapper所对应的findTagsById（）方法 ？？？

解决：在创建TagMapper.xml时应使用 / 创建包名， 否则无法映射出数据库的内容...

5.数据库连接问题-----jdbc无法连接到本地Navicat导致前端页面无法出现文章内容

![image-20220316160620923](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220316160620923.png)

![image-20220316160704563](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220316160704563.png)

![image-20220316160716788](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220316160716788.png)

解决：数据库密码 账户写错，改了老半天。。。



6.实现前端最热标签功能

定义一个service最热标签的接口，并在impl中实现它，判断为空并且在数据库查询实现数量个数

SELECT tag_id from `ms_article_tag` GROUP BY tag_id ORDER BY count(*) DESC `limit` 2;

7.最热文章无法加载

![image-20220316170605165](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220316170605165.png)

解决：在ArticleVo中定义的属性未使用，无法将属性“commentCounts”从源复制到目标。直接注释不使用，同时日期可以删除（删不删都可以）

8文章归档实现，同理上面的最热文章 添加ArticleMapper.xml映射数据库的内容，mapper接口下的ArticleMapper对应ArticleMapper.xml文件。在impl中实现对应的文章归档功能

![image-20220316175854327](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220316175854327.png)



9.登录功能

- 登录使用JWT技术。

- jwt 可以生成 一个加密的token，做为用户登录的令牌，当用户登录成功之后，发放给客户端。

- 请求需要登录的资源或者接口的时候，将token携带，后端验证token是否合法。

- jwt 有三部分组成：A.B.C

- A：Header，{"type":"JWT","alg":"HS256"} 固定

- B：playload，存放信息，比如，用户id，过期时间等等，可以被解密，不能存放敏感信息

- C: 签证，A和B加上秘钥 加密而成，只要秘钥不丢失，可以认为是安全的。

- jwt 验证，主要就是验证C部分 是否合法。

  无法实现用户的登录-----前端页面显示用户名或密码不存在，，，，

  使用postman来测试前端连接数据---这里连接成功，但出现数据库用户名密码不存在的情况

![image-20220318150746285](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220318150746285.png)

#### 使用postman测试，因为登录后，需要跳转页面，进行token认证，有接口未写，前端会出现问题。token前端获取到之后，会存储在 storage中 h5 ，本地存储

（2）出现了 Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@1aaede5]（关闭非事务性SqlSession）

（3）未注册同步，因为同步未处于活动状态



10.注册功能

当时用户注册时，理论上数据库不含有该账户，但在数据库中却有，不符合规范的需要回滚![image-20220318174443875](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220318174443875.png)

![image-20220318174521897](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220318174521897.png)

添加事务

```java
@Transactional
```

完成对数据的回滚实现当注册用户数据异常时，可以数据回滚



11.[登录拦截器](https://zhuanlan.zhihu.com/p/376616838)

（1）原理：SpringBoot通过实现HandlerInterceptor接口实现拦截器，通过实现WebMvcConfigurer接口实现一个配置类，在配置类中注入拦截器，最后再通过@Configuration注解注入配置.

（2）问题

- 每次访问需要登录的资源的时候，都需要在代码中进行判断，一旦登录的逻辑有所改变，代码都得进行变动，非常不合适。
- 一次登录判断---使用拦截器拦截，如果遇到登录才能访问的接口，如果未登录，拦截器直接返回，并跳转登录页面

（3）解决：[登录拦截器](https://zhuanlan.zhihu.com/p/376616838)

```java
//实现HandlerInterceptor接口需要实现3个方法 preHandle、postHandle、afterCompletion
package blog.interceptor;

import blog.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserLoginInterceptor implements HandlerInterceptor {

    /***
     * 在请求处理之前进行调用(Controller方法调用之前)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("执行了拦截器的preHandle方法");
        try {
            HttpSession session = request.getSession();
            //统一拦截（查询当前session是否存在user）(这里user会在每次登录成功后，写入session)
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return true;
            }
            response.sendRedirect(request.getContextPath() + "login");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        //如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        //如果设置为true时，请求将会继续执行后面的操作
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("执行了拦截器的postHandle方法");
    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("执行了拦截器的afterCompletion方法");
    }
}
```

### 

当登录成功的，会得到一个token     token会将之前登录的信息放入到redis缓存中，因此在访问HandlerInterceptor接口时时，后台会认为登录验证成功的，就会放行。test资源就会访问，返回的就是success



拓展：我希望在controller中 直接获取用户的信息 怎么获取 ---使用ThreadLocal（本地线程）[ThreadLocal](https://zhuanlan.zhihu.com/p/102744180)

（4）`ThreadLocal` 中填充的的是当前线程的变量，该变量对其他线程而言是封闭且隔离的，`ThreadLocal` 为变量在每个线程中创建了一个副本，这样每个线程都可以访问自己内部的副本变量。

 （5）ThreadLocal内存泄露

```java
/**
         * The entries in this hash map extend WeakReference, using
         * its main ref field as the key (which is always a
         * ThreadLocal object).  Note that null keys (i.e. entry.get()
         * == null) mean that the key is no longer referenced, so the
         * entry can be expunged from table.  Such entries are referred to
         * as "stale entries" in the code that follows.
         */
        static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;
            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
```

![image-20220216163505747](https://www.mszlu.com/assets/image-20220216163505747.dd146ca6.png)

- **ThreadLocal是null了，也就是要被垃圾回收器回收了，但是此时我们的ThreadLocalMap（thread 的内部属性）生命周期和Thread的一样，它不会回收，这时候就出现了一个现象。那就是ThreadLocalMap的key没了，但是value还在，这就造成了内存泄漏。**
- 解决办法：使用完`ThreadLocal`后，执行`remove`操作，避免出现内存溢出情况。
- 使用了线程池，可以达到“线程复用”的效果。但是归还线程之前记得清除`ThreadLocalMap`，要不然再取出该线程的时候，`ThreadLocal`变量还会存在。这就不仅仅是内存泄露的问题了，整个业务逻辑都可能会出错。



**解释：实线代表强引用,虚线代表弱引用**

每一个Thread维护一个ThreadLocalMap, key为使用**弱引用**的ThreadLocal实例，value为线程变量的副本。

**强引用**，使用最普遍的引用，一个对象具有强引用，不会被垃圾回收器回收。当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不回收这种对象。

**如果想取消强引用和某个对象之间的关联，可以显式地将引用赋值为null，这样可以使JVM在合适的时间就会回收该对象。**

**弱引用**，JVM进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。在java中，用java.lang.ref.WeakReference类来表示。

（6）再思考 ： 为什么key使用弱引用？

如果使用强引用，当`ThreadLocal` 对象的引用（强引用）被回收了，`ThreadLocalMap`本身依然还持有`ThreadLocal`的强引用，如果没有手动删除这个key ,则`ThreadLocal`不会被回收，所以只要当前线程不消亡，`ThreadLocalMap`引用的那些对象就不会被回收， 可以认为这导致`Entry`内存泄漏。



12.文章详情

制作文章详情的时候出现无法在前端查看到数据

![image-20220319152857510](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220319152857510.png)

（1）尝试更改ArticleVo内的属性数据类型 ---- 将int 改为 Integer 封装文章的属性

依旧出现文章无法加载的情况。。。。。查看后台报错为 

![image-20220319155411360](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220319155411360.png)

```java
 public ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //并不是所有的接口都需要标签，作者信息
     return articleVo;
 }
```

(2)尝试使用相关的 BeanUtils.copyProperties()



13.[线程池的使用](https://www.jianshu.com/p/7ab4ae9443b9)

（1）原理：线程池，本质上是一种对象池，用于管理线程资源。 在任务执行前，需要从线程池中拿出线程来执行。 在任务执行完成之后，需要把线程放回线程池。 通过线程的这种反复利用机制，可以有效地避免直接创建线程所带来的坏处。

> ```
> //看完文章后，新增阅读数，有没有问题
> //查看完文章之后，本应该直接返回了，这时候做了一个更新操作，更新时加入写锁，阻塞其他的读操作，性能就会比较低
> //更新 增加了 此次接口的 耗时 如果一旦更新出问题，不能影响 查看文章的 操作
> //线程池 可以 把更新操作扔到线程池中，和主线程就不相关了
> //
> ```

当更新阅读数时，不能延迟文章的内容。使用多线程来增加业务的异步操作，使用锁的知识来增加多线程使用

```java
ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
// 设置核心线程数
executor.setCorePoolSize(5);
// 设置最大线程数
executor.setMaxPoolSize(20);
//配置队列大小
executor.setQueueCapacity(Integer.MAX_VALUE);
// 设置线程活跃时间（秒）
executor.setKeepAliveSeconds(60);
// 等待所有任务结束后再关闭线程池
executor.setWaitForTasksToCompleteOnShutdown(true);
//执行初始化
executor.initialize();
 return executor;

```

（2）问题：之前Article中的commentCounts，viewCounts，weight 字段为int，会造成更新阅读次数的时候，将其余两个字段设为初始值0

解决：当文章刷新时，评论次数会出现0的bug，默认值为0当mybatis Plus更新时，在article只给了其中一个值数值，int基本类型本身也是有值的，当对象不为null，就会生成到sql语句中更新。因此，与数据库映射的类不能出现int类型

（3）如果最热文章也出现了失效，就将ArticleVo中的属性也改为Integer类型



14.缓存优化

- 文章可以放入es当中，便于后续中文分词搜索。springboot教程有和es的整合
- 评论数据，可以考虑放入mongodb当中 电商系统当中 评论数据放入mongo中
- 阅读数和评论数 ，考虑把阅读数和评论数 增加的时候 放入redis incr自增，使用定时任务 定时把数据固话到数据库当中
- 为了加快访问速度，部署的时候，可以把图片，js，css等放入七牛云存储中，加快网站访问速度
- 做一个后台 用springsecurity 做一个权限系统，对工作帮助比较大将域名注册，备案，部署相关



15.管理后台

（1）导入依赖

（2）相关配置文件

（3）启动类

（4）确定扫包路劲

（5）导入前端工程---放入resources下的static目录中，前端工程在资料中有

数据库的配置文件登录密码错误，导致无法登录--修改后--写入自己的数据库密码--成功

16.分页查询 ---

（1）登录后台管理插入页面数，实现两页的交替，查看后台的数据库数据

（2）实现页码的切换

![image-20220321081231923](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220321081231923.png)

```html
//切换页码
handleCurrentChange(currentPage) {
    this.pagination.currentPage = currentPage;
    this.findPage();
```

（3）后台的管理权限--实现用户数据的增删改查

```java
 public Result add(Permission permission) {
        this.permissionMapper.insert(permission);
        return Result.success(null);
    }

    public Result update(Permission permission) {
        this.permissionMapper.updateById(permission);
        return Result.success(null);
    }

    public Result delete(Long id) {
        this.permissionMapper.deleteById(id);
        return Result.success(null);
    }
```

```java
 @PostMapping("permission/add")
    public Result add(@RequestBody Permission permission){
        return permissionService.add(permission);
    }

    @PostMapping("permission/update")
    public Result update(@RequestBody Permission permission){
        return permissionService.update(permission);
    }

    @GetMapping("permission/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        return permissionService.delete(id);
    }
```

![image-20220321082340934](C:\Users\杨翔压民\AppData\Roaming\Typora\typora-user-images\image-20220321082340934.png)



Spring Security 是 Spring 家族为我们提供的一款安全管理的框架，它是一个功能强大并且可以灵活定制的身份验证和访问控制框架。Spring Security 侧重于为 Java 应用程序提供身份验证和授权。与所有 Spring 项目一样，Spring Security 的真正强大之处在于它非常容易扩展来满足我们的不同需求。


总结 ：

1. jwt + redis

   token令牌的登录方式，访问认证速度快，session共享，安全性

   redis做了 令牌和 用户信息的对应管理，1. 进一步增加了安全性 2. 登录用户做了缓存 3. 灵活控制用户的过期（续期，踢掉线等）

2. threadLocal 使用了保存用户信息，请求的线程之内，可以随时获取登录的用户，做了线程隔离

3. 在使用完ThreadLocal之后，做了value的删除，防止了内存泄漏

4. 线程安全- update table set value = newValue where id=1 and value=oldValue

5. 线程池 应用非常广，面试 7个核心参数 （对当前的主业务流程 无影响的操作，放入线程池执行）

   1. 登录 ，记录日志

6. 权限系统 重点内容

7. 统一日志记录，统一缓存处理

破笔记 老是出bug sb sb 。。。。。，差评。。。。



16.部署

（1）打包：安装插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

(2)安装docker

- Docker 并非是一个通用的容器工具，它依赖于已存在并运行的 Linux 内核环境。Docker 实质上是在已经运行的 Linux 下制造了一个隔离的文件环境，因此它执行的效率几乎等同于所部署的 Linux 主机。因此，Docker 必须部署在 Linux 内核的系统上。如果其他系统想部署 Docker 就必须安装一个虚拟 Linux 环境。

- #### Docker 是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的镜像中，然后发布到任何流行的 Linux或Windows 机器上，也可以实现虚拟化。容器是完全使用沙箱机制，相互之间不会有任何接口

```bash
## 1、yum 包更新到最新 
yum update
## 2、安装需要的软件包， yum-util 提供yum-config-manager功能，另外两个是devicemapper驱动依赖的 
yum install -y yum-utils device-mapper-persistent-data lvm2
## 3、 设置yum源
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
## 4、 安装docker，出现输入的界面都按 y 
yum install -y docker-ce
## 5、 查看docker版本，验证是否验证成功
docker -v
##启动docker
/bin/systemctl start docker.service
```

(3)拉取镜像

```bash
docker pull nginx
docker pull redis:5.0.3
docker pull java:8
docker pull mysql:5.7
```



17.缓存一致性

之前在文章列表读取，最新文章等接口的时候我们加了缓存，但是加了缓存会有一些问题，当我们修改或者用户浏览了文章，那么最新的修改和文章的浏览数量无法及时的更新-----RocketMQ（队列）解决












