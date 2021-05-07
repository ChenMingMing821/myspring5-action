package com.demo.spring.simulation.v5.servlet;

import com.demo.spring.simulation.v5.annotation.MyController;
import com.demo.spring.simulation.v5.annotation.MyRequestMapping;
import com.demo.spring.simulation.v5.context.MyApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DispatcherServlet负责请求调度和分发。
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet {

    // Spring配置文件路径
    private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    // Spring上下文，Spring IoC容器
    private MyApplicationContext applicationContext;

    // 保存请求URL和处理方法的映射关系
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();

    // 保存请求映射和处理Handler的关系
    private Map<MyHandlerMapping, MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    // 保存所有View解析器
    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("DispatcherServlet -> Create Web Server Starting.");

        // 1、初始化ApplicationContext。ApplicationContext包含了Spring核心IoC容器，完成Bean扫描、初始化和DI。
        log.info("DispatcherServlet -> Init Spring IoC/DI Starting.");
        applicationContext = new MyApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        log.info("DispatcherServlet -> Init Spring IoC/DI Finished.");

        // 2、初始化Spring MVC九大组件
        log.info("DispatcherServlet -> Init Spring MVC Starting.");
        initStrategies(applicationContext);
        log.info("DispatcherServlet -> Init Spring MVC Finished.");

        log.info("DispatcherServlet -> Create Web Server Finished.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("DispatcherServlet -> Receive client request.");

        try {
            // 3、委派,根据URL去找到一个对应的Method并通过response返回
            doDispatch(req, resp);
        } catch (Exception e) {
            try {
                processDispatchResult(req, resp, new MyModelAndView("500"));
            } catch (Exception e1) {
                e1.printStackTrace();
                resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
            }
        }

        log.info("DispatcherServlet -> Return client response.");
    }

    /**
     * 完成Spring MVC组件的初始化。
     *
     * @param context
     */
    private void initStrategies(MyApplicationContext context) {
        // 1、多文件上传的组件 TODO
        // initMultipartResolver(context);
        // log.info("DispatcherServlet -> 1、多文件上传的组件");

        // 2、初始化本地语言环境 TODO
        // initLocaleResolver(context);
        // log.info("DispatcherServlet -> 2、初始化本地语言环境");

        // 3、初始化模板处理器 TODO
        // initThemeResolver(context);
        // log.info("DispatcherServlet -> 3、初始化模板处理器");

        // 4、初始化HandlerMapping，必须实现。
        initHandlerMappings(context);
        log.info("DispatcherServlet -> 4、初始化HandlerMapping，必须实现。");

        // 5、初始化参数适配器，必须实现。
        initHandlerAdapters(context);
        log.info("DispatcherServlet -> 5、初始化参数适配器，必须实现。");

        // 6、初始化异常拦截器 TODO
        // initHandlerExceptionResolvers(context);
        // log.info("DispatcherServlet -> 6、初始化异常拦截器");

        // 7、初始化视图预处理器 TODO
        // initRequestToViewNameTranslator(context);
        // log.info("DispatcherServlet -> 7、初始化视图预处理器");

        // 8、初始化视图转换器，必须实现。
        initViewResolvers(context);
        log.info("DispatcherServlet -> 8、初始化视图转换器，必须实现。");

        // 9、初始化FlashMap管理器 TODO
        // initFlashMapManager(context);
        // log.info("DispatcherServlet -> 9、初始化FlashMap管理器");
    }

    /**
     * HandlerMapping：保存请求URL和处理方法的映射关系。
     *
     * @param context
     */
    private void initHandlerMappings(MyApplicationContext context) {
        log.info("DispatcherServlet -> 解析和缓存HandlerMapping");

        if (this.applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }

        for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();

            // 1、Controller注解的类才具备URL配置
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }

            // 2、提取 class上配置的base_url
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                baseUrl = clazz.getAnnotation(MyRequestMapping.class).value();
            }

            // 3、获取public的方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                // 4、提取每个方法上面配置的url
                MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);

                // 5、拼接URL
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);


                // 6、保存HandlerMapping映射关系
                handlerMappings.add(new MyHandlerMapping(pattern, instance, method));
            }
        }
    }

    /**
     * 初始化参数适配器。
     *
     * @param context
     */
    private void initHandlerAdapters(MyApplicationContext context) {
        log.info("DispatcherServlet -> 创建HandlerAdapter处理类。");

        // HandlerAdapter调用具体的方法对用户发来的请求来进行处理，所以每个HandlerMapping都对应一个HandlerAdapter。
        for (MyHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MyHandlerAdapter());
        }
    }

    /**
     * 根据请求URL找对对应处理Handler完成请求，并返回Response。
     *
     * @param req
     * @param resp
     * @throws Exception
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        log.info("DispatcherServlet -> 请求分发");

        // 1、通过从Request获得请求URL，去匹配一个HandlerMapping
        MyHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new MyModelAndView("404"));
            return;
        }

        // 2、根据一个HandlerMaping获得一个HandlerAdapter
        MyHandlerAdapter ha = getHandlerAdapter(handler);

        // 3、解析某一个方法的形参和返回值之后，统一封装为ModelAndView对象
        MyModelAndView mv = ha.handler(req, resp, handler);

        // 4、把ModelAndView变成一个ViewResolver
        processDispatchResult(req, resp, mv);
    }

    /**
     * 匹配到一个Handler处理器。
     *
     * @param handlerMapping
     * @return
     */
    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handlerMapping) {
        log.info("DispatcherServlet -> 获取请求对应的处理类。");

        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handlerMapping);
    }

    /**
     * 封装请求结果，输出到浏览器。
     *
     * @param req
     * @param resp
     * @param mv
     * @throws Exception
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView mv) throws Exception {
        log.info("DispatcherServlet -> 封装请求结果并输出");

        if (null == mv) {
            return;
        }

        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (MyViewResolver viewResolver : this.viewResolvers) {
            MyView view = viewResolver.resolveViewName(mv.getViewName());
            //直接往浏览器输出
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    /**
     * 从Request中获取URL，然后匹配对应的HandlerMapping。
     *
     * @param req
     * @return
     */
    private MyHandlerMapping getHandler(HttpServletRequest req) {
        log.info("DispatcherServlet -> 根据Request Url获取HandlerMapping");

        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        // 从Request中获取请求URL
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        // 匹配HandlerMapping
        for (MyHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return mapping;
        }

        return null;
    }

    /**
     * 初始化视图解析器，根据配置的根路径，遍历解析所有的View。
     *
     * @param context
     */
    private void initViewResolvers(MyApplicationContext context) {
        log.info("DispatcherServlet -> 初始化视图解析器");

        // 从配置中获取模板文件存放路径
        String templateRoot = context.getConfig().getProperty("templateRoot");
        // String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        String templateRootPath = this.getClass().getClassLoader().getResource("/").getPath().replaceAll("/target/classes", "");
        templateRootPath = templateRootPath + "/src/main" + templateRoot;
        templateRootPath = templateRootPath.replaceAll("/+","/");

        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new MyViewResolver(templateRootDir.getPath()));
        }
    }
}
