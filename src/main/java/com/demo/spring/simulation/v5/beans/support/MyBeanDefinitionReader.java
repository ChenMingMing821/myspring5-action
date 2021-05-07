package com.demo.spring.simulation.v5.beans.support;

import com.demo.spring.simulation.v5.beans.config.MyBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 扫描配置文件，读取Bean定义
 */
@Slf4j
public class MyBeanDefinitionReader {

    // 保存扫描的结果
    private List<String> regitryBeanClasses = new ArrayList<String>();

    // 保存配置信息
    private Properties contextConfig = new Properties();

    public MyBeanDefinitionReader(String... configLocations) {
        log.info("BeanDefinitionReader -> 构造器执行开始。");

        // 1、读取配置信息。
        doLoadConfig(configLocations[0]);
        log.info("BeanDefinitionReader -> 1、读取配置信息。");

        // 2、扫描配置文件中的配置的相关的类。
        doScanner(contextConfig.getProperty("scanPackage"));
        log.info("BeanDefinitionReader -> 2、扫描配置文件中的配置的相关的类。");

        log.info("BeanDefinitionReader -> 构造器执行完成。");
    }

    /**
     * 将Bean封装为BeanDefinition
     *
     * @return
     */
    public List<MyBeanDefinition> loadBeanDefinitions() {
        log.info("BeanDefinitionReader -> 将扫描的Bean信息封装成BeanDefinition。");

        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        try {
            for (String className : regitryBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                // 接口不能实例化
                if (beanClass.isInterface()) {
                    continue;
                }

                // BeanName有三种情况：
                // 1、默认是类名首字母小写
                // 2、自定义名称
                // 3、接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                // 如果是多个实现类，只能覆盖
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private MyBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setFactoryBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    /**
     * 从配置文件中加载Spring配置信息
     *
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        log.info("BeanDefinitionReader -> 加载Spring配置文件。");

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据配置的basePackage扫描获取Bean定义
     *
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        log.info("BeanDefinitionReader -> 根据scanPackage路径逐层扫描，获取Bean定义。");

        //jar 、 war 、zip 、rar
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());

        //当成是一个ClassPath文件夹
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                //全类名 = 包名.类名
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                //Class.forName(className);
                regitryBeanClasses.add(className);
            }
        }
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

    /**
     * 获取配置信息
     *
     * @return
     */
    public Properties getConfig() {
        return this.contextConfig;
    }
}
