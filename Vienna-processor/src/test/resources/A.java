import com.onemuggle.vienna.common.ReNamed;

import java.util.concurrent.Callable;

public class A {


    public void rStatic() {

        int m = 100;
        class R{}


        for (int i = 0; i < 2; i++) {

            class R_4 extends R implements Runnable {
                public void run() {
                    System.out.println("r4" + m);
                }
            }

            int x = 9;
            @ReNamed("R_3")
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("r3" + m + x);
                }
            };
            int y = 7;
            @ReNamed("R_11")
            Callable<Integer> r11 = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return 9999;
                }
            };
            R_4 r12 = new R_4() {
                public void run() {
                    System.out.println("R_12" + m);
                }
            };



            @ReNamed("R_22")
            Runnable r22 = () -> {
                System.out.println("r22" + m + x);
            };

            R_4 r_4 = new R_4();

        }




    }


}
