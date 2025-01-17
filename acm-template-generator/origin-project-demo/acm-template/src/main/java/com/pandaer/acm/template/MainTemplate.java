package com.pandaer.acm.template;


import java.util.Scanner;

/**
 * ACM 输入模板 (默认：循环获取值并求和)
 * @author pandaer
 */
public class MainTemplate {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入读取值的个数：");
        int count = sc.nextInt();

        // 求和
        int sum = 0;
        for (int i = 0; i < count; i++) {
            System.out.printf("请输入第%d个值：",i+1);
            int num = sc.nextInt();
            sum += num;
        }
        System.out.println("总和为：" + sum);
    }
}
