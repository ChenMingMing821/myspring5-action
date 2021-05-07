package com.demo.spring.simulation.v5.servlet;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 视图解析器的作用是将逻辑视图转为物理视图，所有的视图解析器都必须实现ViewResolver接口。
 */
@Slf4j
public class MyViewResolver {
    // 视图默认后缀名
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    // 视图文件根路径
    private File templateRootDir;

    public MyViewResolver(String templateRoot) {
        // String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRoot);
    }

    /**
     * 根据视图名称获取视图定义信息
     *
     * @param viewName
     * @return
     */
    public MyView resolveViewName(String viewName) {
        log.info("MyViewResolver -> 视图解析");

        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new MyView(templateFile);
    }

}
