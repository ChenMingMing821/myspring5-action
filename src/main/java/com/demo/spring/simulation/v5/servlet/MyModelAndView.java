package com.demo.spring.simulation.v5.servlet;

import java.util.Map;

/**
 * ModelAndView类用来存储处理完后的结果数据，以及显示该数据的视图。
 */
public class MyModelAndView {

    // 该属性用来存储返回的视图信息
    private String viewName;

    // Model代表模型数据
    private Map<String,?> model;

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}


