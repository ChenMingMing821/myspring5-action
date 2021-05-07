package com.demo.spring.simulation.v5.context;


import com.demo.spring.simulation.v5.annotation.MyAutowired;
import com.demo.spring.simulation.v5.annotation.MyController;
import com.demo.spring.simulation.v5.annotation.MyService;
import com.demo.spring.simulation.v5.aop.MyJdkDynamicAopProxy;
import com.demo.spring.simulation.v5.aop.config.MyAopConfig;
import com.demo.spring.simulation.v5.aop.support.MyAdvisedSupport;
import com.demo.spring.simulation.v5.beans.MyBeanWrapper;
import com.demo.spring.simulation.v5.beans.config.MyBeanDefinition;
import com.demo.spring.simulation.v5.beans.support.MyBeanDefinitionReader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 完成Bean的扫描、创建和DI。
 */
@Slf4j
public class MyApplicationContext {

    // 负责读取Bean配置
    private MyBeanDefinitionReader reader;

    // 存储注册Bean定义的IoC容器
    private Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();

    // 存放单例的IoC容器
    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    // 通用的IoC容器
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new HashMap<String, MyBeanWrapper>();

    /**
     * Spring上下文环境初始化
     *
     * @param configLocations 配置文件路径
     */
    public MyApplicationContext(String... configLocations) {

        // 1、加载、解析配置文件，扫描相关的类。
        reader = new MyBeanDefinitionReader(configLocations);
        log.info("ApplicationContext -> 1、加载、解析配置文件，扫描相关的类。");

        try {
            // 2、将扫描的Bean封装成BeanDefinition。
            List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            log.info("ApplicationContext -> 2、将扫描的Bean封装成BeanDefinition。");

            // 3、注册，把BeanDefintion缓存到容器。
            doRegistBeanDefinition(beanDefinitions);
            log.info("ApplicationContext -> 3、注册，把BeanDefintion缓存到容器。");

            // 4、完成自动依赖注入。
            doAutowrited();
            log.info("ApplicationContext -> 4、完成自动依赖注入。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成Bean的实例化和自动依赖注入(非延迟加载的场景)。
     */
    private void doAutowrited() {
        // 到这步，所有的Bean并没有真正的实例化，还只是配置阶段。
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            // getBean才真正完成依赖注入
            getBean(beanName);
        }
    }

    /**
     * 把BeanDefintion缓存起来
     *
     * @param beanDefinitions 通过扫描配置文件获取的Bean定义
     * @throws Exception
     */
    private void doRegistBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        log.info("ApplicationContext -> 缓存BeanDefinition信息。");

        for (MyBeanDefinition beanDefinition : beanDefinitions) {

            // Bean在IoC容器中名称必须唯一
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + "is exists");
            }

            // 分别用两种名称存储，便于查找
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    /**
     * Bean的实例化和DI是从这个方法开始的。
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        log.info("ApplicationContext -> 执行getBean()，将BeanDefinition保存到IoC容器并完成自动依赖注入。");

        // 1、先拿到BeanDefinition配置信息
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        // 2、反射实例化newInstance();
        Object instance = instantiateBean(beanName, beanDefinition);

        // 3、封装成一个叫做BeanWrapper
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        // 4、保存到IoC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);

        // 5、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);

        // 6、返回对象
        return beanWrapper.getWrapperInstance();
    }

    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    /**
     * DI核心逻辑。
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {
        log.info("ApplicationContext -> 完成依赖注入核心逻辑。");

        // TODO 可能涉及到循环依赖待解决，如果依赖对象还未实例化，注入的示例为Null引发后续问题，这里需要考虑如何解决。

        // 1、拿到当前Bean实例化对象
        Object instance = beanWrapper.getWrapperInstance();

        // 2、拿到当前Bean的类信息
        Class<?> clazz = beanWrapper.getWrapperClass();

        // 3、只有注解的类，才执行依赖注入
        if (!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))) {
            return;
        }

        // 把所有的包括private/protected/default/public 修饰字段都取出来
        // TODO 这里只考虑接口注入的方式，实际还要考虑构造器注入和Setter注入。
        for (Field field : clazz.getDeclaredFields()) {

            // 是否被Autowired标记为自动注入
            if (!field.isAnnotationPresent(MyAutowired.class)) {
                continue;
            }

            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
            // 如果用户没有自定义的beanName，就默认根据类型注入
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {

                // field.getType().getName() 获取字段的类型的全限定名
                autowiredBeanName = toLowerFirstCase(field.getType().getSimpleName());
            }

            // 暴力访问
            field.setAccessible(true);

            try {
                // 获取对应名称的bean实例对象 TODO
                // 此处没有考虑Bean的实例化顺序，可能需要注入的对象此时还没有完成实例化，在IoC容器中无法正确获取。不过除了在初始化时触发DI，
                // 在实际调用的时候，通过getBean()获取对象时，仍然会触发DI操作。
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }

                // ioc.get(beanName) 相当于通过接口的全名拿到接口的实现的实例
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 创建真正的实例对象
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {
        log.info("ApplicationContext -> 通过反射创建Bean实例。");

        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            // AOP部分 -> Start
            // 在初始化是确定是返回原生Bean实例还是Bean Proxy实例。
            MyAdvisedSupport advisedSupport = instantionAopConfig();
            advisedSupport.setTargetClass(clazz);
            advisedSupport.setTarget(instance);

            // 符合PointCut规则，进行代理
            if (advisedSupport.pointCutMatch()) {
                instance = new MyJdkDynamicAopProxy(advisedSupport).getProxy();
            }
            // AOP部分 -> End

            // 默认的类名首字母小写
            this.factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 获取AOP定义配置信息。
     *
     * @return
     */
    private MyAdvisedSupport instantionAopConfig() {
        log.info("ApplicationContext -> 加载AOP配置。");

        // 从配置文件中获取AOP配置信息。
        MyAopConfig config = new MyAopConfig();

        // AOP应该是配置在XML或者Annotation上的，这里为了模拟简单，直接定义在Properties文件中。
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));

        return new MyAdvisedSupport(config);
    }

    /**
     * 已注册所有Bean的名称
     *
     * @return
     */
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    /**
     * 已注册Bean的数量
     *
     * @return
     */
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

    /**
     * 将大写字母转换为小写
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
