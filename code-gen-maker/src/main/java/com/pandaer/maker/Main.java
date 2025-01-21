package com.pandaer.maker;


import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        DefaultGeneratorMaker defaultGeneratorMaker = new DefaultGeneratorMaker();
        try {
            defaultGeneratorMaker.make();
            System.out.println("代码生成器制作完毕！");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
