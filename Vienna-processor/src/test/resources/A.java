import com.onemuggle.vienna.common.ReNamed;

public class A {


    public void rStatic() {

        int m = 100;
        class R_4 implements Runnable {
            public void run() {
                System.out.println("r4" + m);
            }
        }

        for (int i = 0; i < 2; i++) {
            int x = 9;
            @ReNamed("R_3")
            Runnable r1 = new Runnable() {
                public void run() {
                    System.out.println("r3" + m + x);
                }
            };
            int y = 7;
            @ReNamed("R_11")
            Runnable r11 = new Runnable() {
                public void run() {
                    System.out.println("r11" + m + x);
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
