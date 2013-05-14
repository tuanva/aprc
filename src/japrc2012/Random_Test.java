package japrc2012;

import java.util.Random;

public class Random_Test {

    private int iProbability = 0;
    int result = 0;

    public Random_Test() {
    }

    private void run() {
        Random r = new Random();

        System.out.println(iProbability);

        for(int i =0; i < 100; i++) {

            result = r.nextInt(iProbability);


            System.out.println(result);

            if(result < iProbability) {
                System.out.println("OK");

                continue;
            }
        }

    }

    private void setProbability(double prob) {
        iProbability = (int) prob * 100;
    }

    public static void main(String[] args) {
        Random_Test ran = new Random_Test();

        ran.setProbability(0.8);
        ran.run();
    }
}

