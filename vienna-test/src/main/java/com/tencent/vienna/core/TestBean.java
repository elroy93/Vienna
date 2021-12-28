package com.tencent.vienna.core;


import com.tencent.vienna.processor.ReNamed;

public class TestBean {

    public static void main(String[] args) {
        TestBean.rStatic();
//        new TestBean().rStatic();
    }

    public static void rStatic() {

        int m = 100;
        class R_4 implements Runnable {
            public void run() {
                System.out.println("r4--" + m + this.getClass().getName());
            }
        }

        for (int i = 0; i < 1; i++) {
            int x = 9;

            @ReNamed(value = "R_3",isInterface = true)
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("r3--" + m + x + this.getClass().getName());
                }
            };

            int y = 7;
            @ReNamed(value = "R_11",isInterface = false)
            Runnable r11 = new R_4() {
                public void run() {
                    System.out.println("r11--" + m + y + this.getClass().getName());
                }
            };

            Runnable r22 = () -> {
                System.out.println("r22--" + m + x );
            };

            r1.run();
            r11.run();
            r22.run();
        }
    }
}
