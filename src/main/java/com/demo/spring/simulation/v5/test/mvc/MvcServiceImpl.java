package com.demo.spring.simulation.v5.test.mvc;

import com.demo.spring.simulation.v5.annotation.MyService;

@MyService
public class MvcServiceImpl implements IMvcService {
    @Override
    public String hello() {
        return "hello boy, welcome to spring.";
    }
}
