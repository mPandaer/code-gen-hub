package com.pandaer.acm.template;


import java.util.Scanner;

/**
 * ACM 输入模板 (默认：循环获取值并求和)
 *
 * @author 090-90909090909090
 */
public class MainTemplate {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {

            // 读取输入元素个数
            int n = scanner.nextInt();

            // 读取数组
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = scanner.nextInt();
            }

            // 处理问题逻辑，根据需要进行输出
            // 示例：计算数组元素的和
            int sum = 0;
            for (int num : arr) {
                sum += num;
            }

            System.out.println("hahahahahahah" + sum);
        }

        scanner.close();
    }
}