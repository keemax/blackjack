package util;

import dealer.DealerController;

/**
 * Created with IntelliJ IDEA.
 * User: maxkeene
 * Date: 10/4/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PitBoss extends Thread {
    private final static int BOOT_TIME = 10000;

    private long switchTime;
    private DealerController dc;

    public PitBoss(DealerController dc) {
        this.dc = dc;
    }

    public long getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(long switchTime) {
        this.switchTime = switchTime;
    }

    public void updateSwitchTime() {
        System.out.println("updated switch time to " + System.currentTimeMillis());
        this.switchTime = System.currentTimeMillis();
    }

    public void run() {
        while(true) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (System.currentTimeMillis() - switchTime > BOOT_TIME) {
                dc.removeCurrentPlayer();
            }
        }
    }
}
