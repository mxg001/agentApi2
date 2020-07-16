# 包结构
- com.eeepay        基础包
    - frame         基础代码
        - annotation    自定义注解
        - aop           aop切面
        - bean          公用的基础的bean
        - config        spring boot相应的配置类
        - db            数据源相关代码
        - exception     异常类
        - interceptor   请求拦截器
        - utils         工具类
    - modules       模块化代码
        - controller    控制层 
        - service       服务层
            - impl      服务层实现
        - dao           mysql dao层

# 编码规范
- 建议所有的接口统一返回值`com.eeepay.frame.bean.ResponseBean`
- 在需要在服务层实现类,判断业务逻辑的话不通过的话,
    可以使用`throw new AppException("错误信息")` 或`throw new AppException(错误码, "错误信息")`,
    直接抛出错误消息,程序自动获取错误,封装一个`ResponseBean`返回给前端
- 建议控制层的类以`Controller`结尾
- 控制层开发完成的接口在方法标注`@SwaggerDeveloped`,表示接口可联调
- 建议服务层接口以`Service`结尾
- 建议服务层实现类以`ServiceImpl`结尾
- 建议dao层接口以`Dao`结尾
- 建议所有的模型类以`Bean`结尾
- 简单的sql可以直接用mybatis注解
- 复杂的sql使用mybatis xml mapper
- 建议在写接口的使用,使用`@ApiOperation`简单描述接口的用途
- 不建议将`HttpServletRequest`和`HttpServletResponse`这两个当作参数传到服务层,服务层只做业务逻辑

# 数据库读写分离
- 可以使用`com.eeepay.frame.annotation.DataSourceSwitch`注解来切换数据源,
- `DataSourceSwitch`注解一般用于服务层实现类的方法上
- 默认使用`@DataSourceSwitch`走的是写库,
- 如果不使用注解`DataSourceSwitch`或者使用`@DataSourceSwitch(DataSourceType.READ)`则走的是读库
- **注意** 以下是服务层出现相互调用的情况
```java
@Service
public class TestServiceImpl implements TestService {
    @Resource
    private TestDao testDao;
    @Resource
    private TestService testService;
    /**
    * test0没有配置DataSourceSwitch,默认都读库
    * 如果被嵌套调用,跟随调用方DataSourceSwitch的配置
    */
    public void test0(){
        testDao.foo();  // 没有配置,走读库
    }
    /**
    * test1强制配置走写库
    * 如果被嵌套调用,还是走写库
    */
    @DataSourceSwitch(DataSourceType.WRITE)
    public void test1(){
      testDao.foo();  // 走写库
    }
  
    /**
    * test2强制配置走读库
    * 如果被嵌套调用,还是走读库
    */
    @DataSourceSwitch(DataSourceType.READ)
    public void test2(){
      testDao.foo();  // 走读库
    }
  
    /** 
     * 因为test3没有配置DataSourceSwitch,所以test3不会走aop切面,默认走读库
     * 而且test3是直接调用内部方法test0,test1和test2,因此不会走aop切面
     * 所以test3全部方法都会走读库
     */
    public void test3(){
      test0();  
      test1();            
      test2();
      testDao.foo();  
    }
    /** 
     * 因为test4没有配置DataSourceSwitch, 所以test4不会走aop切面,默认走读库
     * test0没有配置DataSourceSwitch,因此不会走aop切面,所以test0根据test4的配置会走读库
     * test1是通过注入testService后进行调用的,且配置DataSourceSwitch,因此会走aop切面,所以test1会走写库
     * test2是通过注入testService后进行调用的,且配置DataSourceSwitch,因此会走aop切面,所以test2会走读库 
     * 因为test4默认走写库,所以testDao.foo();  会走读库  
     */
    public void test4(){
        testService.test0();      // 根据test4配置,走读库    
        testService.test1();      // 根据test1配置,走写库
        testService.test2();      // 根据test2配置,走读库
        testDao.foo();            // 根据test4配置,走读库  
    }
    /** 
     * 因为test5配置DataSourceSwitch(DataSourceType.READ),所以test5会走aop切面,因此test5会走读库
     * test0没有配置DataSourceSwitch,,因此不会走aop切面,根据test5配置,会走读库
     * test1是通过注入testService后进行调用的,且配置DataSourceSwitch,因此会走aop切面,所以test1会走写库
     * test2是通过注入testService后进行调用的,且配置DataSourceSwitch,因此会走aop切面,所以test2会走读库 
     * 因为test5配置走读库,所以testDao.foo();  会走读库
     */
    @DataSourceSwitch(DataSourceType.READ)
    public void test5(){
        testService.test0();      // 根据test5配置,走读库     
        testService.test1();      // 根据test1配置,走写库       
        testService.test2();      // 根据test2配置,走读库
        testDao.foo();            // 根据test5配置,走读库
    }
}
```

# 登陆相关
- 基本上所有的接口都必须做登陆校验(拦截器实现)
- 如果需要获取当前登陆代理商编号,可以使用`com.eeepay.frame.utils.WebUtils.getLoginAgentNo`获取
    - 不建议直接把`HttpServletRequest`传到服务层
    - 如果服务层需要获取登陆代理商信息,
        建议通过`WebUtils.getLoginAgentNo(HttpServletRequest)`把登陆代理商编号传给服务层
- 获取当前登陆用户信息,可以在控制层的方法参数使用`@CurrentUser UserInfoBean userInfoBean`获取
    - 例子
    ```java
        @ApiOperation("商户汇总")
        @GetMapping("/merchantSummary")
        public ResponseBean merchantSummary(@CurrentUser UserInfoBean userInfoBean) {
            return ResponseBean.success(userInfoBean);
        }
    ```
- app公共参数放在请求头`user-agent`中,参数内容用json格式,对应的实体类为`com.eeepay.frame.bean.AppDeviceInfo`
- 如果需要获取登陆用户的真实ip,请使用`com.eeepay.frame.utils.WebUtils.getRealIp`
- 如果某些接口不需要做登陆拦截校验,请使用注解`LoginValid`
- 登陆成功之后会返回一个登陆`loginToken`, 然后其他接口需要回传该`loginToken`, 
    后台会以下面的顺序通过`LOGIN_TOKEN`依次去获取该`loginToken`
    1. 通过请求参数获取(是request param不是request body)
    2. 通过请求头获取
    3. 通过cookie获取
        


# mysql和elasticsearch混用说明
- 如果在数据量比较大,以至于`mysql`性能比较低的情况,可以考虑一下能不能使用`elasticsearch`实现
- 一般情况下,查详情,列表数据等都可以使用`mysql`
- 一般情况下,对数据做汇总可以考虑使用`elasticsearch`
- `elasticsearch`只做查询用,不做插入更新删除等操作
    > 额外会通过logstash, 写一个同步脚本将mysql的数据同步到elasticsearch中
- `elasticsearch`目前项目引入以下两种调用方式,看哪种方便就用哪种
    - 引入了`spring-data-elasticsearch`通过默认端口`9300`进行java api调用
    - 引入了`bboss-elasticsearch-spring-boot-starter`直接通过默认端口`9200`进行http调用
        