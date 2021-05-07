package com.demo.spring.simulation.v5.aop.support;

import com.demo.spring.simulation.v5.aop.aspect.MyAdvice;
import com.demo.spring.simulation.v5.aop.config.MyAopConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AOP配置解析。
 * 1、解析PointCut正则匹配；
 * 2、解析切面Aspect方法逻辑；
 * 3、解析被代理类切入方法；
 * 4、建立被代理类切入方法和切面方法的关系；
 */
public class MyAdvisedSupport {

    // 代理的目标类
    private Class<?> targetClass;
    // 代理的目标类的实例对象
    private Object target;

    // AOP配置信息
    private MyAopConfig config;
    // AOP切点匹配规则
    private Pattern pointCutClassPattern;

    // 享元的共享池，用于保存被代理类方法和通知方法对应关系
    private transient Map<Method, Map<String, MyAdvice>> methodCache;

    public MyAdvisedSupport(MyAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return this.target;
    }

    /**
     * 获取方法对应的AOP信息。
     *
     * @param method
     * @param targetClass
     * @return
     * @throws NoSuchMethodException
     */
    public Map<String, MyAdvice> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass)
        throws NoSuchMethodException {

        // 获取AOP方法
        Map<String, MyAdvice> cache = methodCache.get(method);
        if (null == cache) {
            // 目标对象方法
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);

            // 对代理方法进行兼容处理  TODO 这里没太看懂
            this.methodCache.put(m, cache);
        }
        return cache;
    }

    /**
     * 设置被代理类的Class类型。
     *
     * @param targetClass
     */
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;

        // 解析AOP配置，设置AOP匹配逻辑。
        this.parse();
    }

    //解析配置文件的方法
    private void parse() {

        // 1、把PointCut的Spring Excpress转换成Java正则表达式
        String pointCut =
            config.getPointCut().replaceAll("\\.", "\\\\.").replaceAll("\\\\.\\*", ".*").replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        // 保存专门匹配Class的正则
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\("));
        pointCutClassPattern =
            Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1, pointCutForClassRegex.lastIndexOf("\\.")));
        // 保存专门匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);

        // 2、享元的共享池，用于保存被代理类方法和通知方法对应关系。
        methodCache = new HashMap<Method, Map<String, MyAdvice>>();

        try {
            // 3、获取切面中定义的所有Method。
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            // 4、获取目标代理类的所有方法
            for (Method method : this.targetClass.getMethods()) {
                // 获取方法名称
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                // 5、如果匹配切点规则，进行切面逻辑设定。
                Matcher matcher = pointCutPattern.matcher(methodString);
                if (matcher.matches()) {
                    Map<String, MyAdvice> advices = new HashMap<String, MyAdvice>();

                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.put("before",
                            new MyAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        advices.put("after",
                            new MyAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfter())));
                    }
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        MyAdvice advice =
                            new MyAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.put("afterThrow", advice);
                    }

                    // 6、保存目标代理类业务方法和环绕通知类的关系。
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //根据一个目标代理类的方法，获得其对应的通知
    public Map<String, MyAdvice> getAdvices(Method method, Object o) throws Exception {
        //享元设计模式的应用
        Map<String, MyAdvice> cache = methodCache.get(method);
        if (null == cache) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m, cache);
        }
        return cache;
    }

    /**
     * 设置被代理类的实例化对象。
     *
     * @param target
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 代理的目标类是否匹配切点。
     * 在ApplicationContext IoC中的对象初始化时调用，决定要不要生成代理类的逻辑。
     *
     * @return
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
