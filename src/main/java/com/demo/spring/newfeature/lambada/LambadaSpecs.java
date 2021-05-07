package com.demo.spring.newfeature.lambada;

/**
 * Lambada表达式使用规范
 */
public class LambadaSpecs {
    public static void main(String[] args) {
        // 合理，n 和 d 的类型通过上下文推断
        IFunctionAdd add1 = (a, b) -> a + b;

        // 合理，指定 n 和 d 的类型
        IFunctionAdd add2 = (int a, int b) -> a + b;

        // 不合理，须显示声明所有参数类型(直接报错)
        // IFunctionAdd add3 = (int a, b) -> a + b;
    }

    // 定义函数式接口
    public interface IFunctionAdd {
        int add(int a, int b);
    }
}
