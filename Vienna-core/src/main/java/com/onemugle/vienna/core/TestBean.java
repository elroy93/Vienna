package com.onemugle.vienna.core;

import com.onemuggle.vienna.common.ReNamed;

@SuppressWarnings("serial")
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
            @ReNamed("R_3")
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("r3--" + m + x + this.getClass().getName());
                }
            };
            int y = 7;
            @ReNamed("R_11")
            Runnable r11 = new Runnable() {
                public void run() {
                    System.out.println("r11--" + m + x + this.getClass().getName());
                }
            };

            Runnable r22 = () -> {
                System.out.println("r22--" + m + x );
            };

            R_4 r_4 = new R_4();
            r1.run();
            r11.run();
            r22.run();
        }
    }
}
