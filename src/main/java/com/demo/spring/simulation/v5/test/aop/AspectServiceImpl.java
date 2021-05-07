package com.demo.spring.simulation.v5.test.aop;

import com.demo.spring.simulation.v5.annotation.MyService;
import com.demo.spring.simulation.v5.aop.MyAopProxy;
import com.demo.spring.simulation.v5.servlet.MyModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务类，执行AOP操作
 */
@MyService("aspectService")
public class AspectServiceImpl implements MyAopProxy {

    @Override
    public void print() {
        System.out.println("Invoke Business Method.");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("message", "hello ");
        new MyModelAndView("hello", model);
    }
}
