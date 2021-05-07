package com.demo.spring.newfeature.lambada;

import java.util.ArrayList;
import java.util.List;

public class LambadaExpression {
    public static void main(String[] args) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        // (parameters) -> expression
        list.forEach(item -> System.out.println(item));
        System.out.println("------------------------------");

        // (parameters) ->{ statements; }
        list.forEach(item -> {
            item = item * 2 - 1;
            System.out.println(item);
        });


        try {
            Thread.sleep(150000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
