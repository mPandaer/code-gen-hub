package com.pandaer.maker;


import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        MakerTemplate maker = new ZipGeneratorMaker();
        try {
            maker.make();
            System.out.println("代码生成器制作完毕！");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
