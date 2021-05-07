package com.demo.spring.simulation.v5.test.mvc;

import com.demo.spring.simulation.v5.annotation.MyAutowired;
import com.demo.spring.simulation.v5.annotation.MyController;
import com.demo.spring.simulation.v5.annotation.MyRequestMapping;
import com.demo.spring.simulation.v5.annotation.MyRequestParam;
import com.demo.spring.simulation.v5.servlet.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@MyController
@MyRequestMapping("/mvc/")
public class MvcController {

    //@MyAutowired
    //private IMvcService mvcService;

    @MyRequestMapping("/hello")
    public MyModelAndView hello(HttpServletRequest request, HttpServletResponse response, @MyRequestParam(value = "id") String id, @MyRequestParam(value = "name") String name) {
        //String result = mvcService.hello();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("message", "hello " + id + " " + name);
        return new MyModelAndView("hello", model);
    }
}
