package me.jesonlee.rpc.server;

import me.jesonlee.rpc.common.Calculator;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class CalculatorImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }
}
