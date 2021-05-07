package com.demo.spring.simulation.v5.servlet;

import com.demo.spring.simulation.v5.annotation.MyRequestParam;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * HandlerAdapter调用具体的方法对用户发来的请求来进行处理。
 */
@Slf4j
public class MyHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof MyHandlerMapping);
    }

    /**
     * @param req
     * @param resp
     * @param handlerMapping
     * @return
     * @throws Exception
     */
    public MyModelAndView handler(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handlerMapping) throws Exception {
        log.info("MyViewResolver -> Request请求处理核心逻辑");

        // 1、将方法的形参列表和Request参数列表进行匹配和对应。
        Map<String, Integer> paramIndexMapping = new HashMap<String, Integer>();

        // 2、获取请求处理方法的参数注解。
        // 提取方法中加入了注解的参数。一个参数可以有多个注解，而一个方法又有多个参数，所以是一个二维数组。
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof MyRequestParam) {
                    String paramName = ((MyRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        // 3、提取Request和Response参数。
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramterType = paramTypes[i];
            if (paramterType == HttpServletRequest.class || paramterType == HttpServletResponse.class) {
                paramIndexMapping.put(paramterType.getName(), i);
            }
        }


        // 4、获取请求方法的形参列表。
        // Eg.http://localhost/web/query?name=Tom&Cat=1
        Map<String, String[]> params = req.getParameterMap();

        // 实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]", "")
                    .replaceAll("\\s+", ",");

            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }

            int index = paramIndexMapping.get(param.getKey());

            //允许自定义的类型转换器Converter
            paramValues[index] = castStringValue(value, paramTypes[index]);
        }

        // 处理Request
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        // 处理Response
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        // 5、通过反射执行方法体
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MyModelAndView.class;
        if (isModelAndView) {
            return (MyModelAndView) result;
        }

        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if (String.class == paramType) {
            return value;
        } else if (Integer.class == paramType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }

    }
}
