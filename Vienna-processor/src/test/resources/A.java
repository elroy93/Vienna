import com.onemuggle.vienna.common.ReNamed;

import java.util.concurrent.Callable;

public class A {


    public void rStatic() {

        int m = 100;
        class R {
        }


        for (int i = 0; i < 2; i++) {

            class RR extends R implements Runnable {
                public void run() {
                    System.out.println("r4" + m);
                }
            }

            int x = 9;
            @ReNamed(value = "R_3", isInterface = true)
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("r3" + m + x);
                }
            };
            int y = 7;
            @ReNamed(value = "R_11", isInterface = true)
            Callable<Integer> r11 = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return 9999;
                }
            };
            @ReNamed(value = "R_444", isInterface = false)
            RR r12 = new RR() {
                public void run() {
                    System.out.println("R_444" + m);
                }
            };


            @ReNamed(value = "R_22", isInterface = true)
            Runnable r22 = () -> {
                System.out.println("r22" + m + x);
            };

            RR r_4 = new RR();

        }


    }


}
