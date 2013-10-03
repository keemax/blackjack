package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maxkeene
 * Date: 10/1/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartInfo {

    private Hand yourHand;
    private Card dealerUpCard;

    public StartInfo() {
        yourHand = new Hand();
        dealerUpCard = new Card();
    }

    public Hand getYourHand() {
        return yourHand;
    }

    public void setYourHand(Hand yourHand) {
        this.yourHand = yourHand;
    }

    public Card getDealerUpCard() {
        return dealerUpCard;
    }

    public void setDealerUpCard(Card dealerUpCard) {
        this.dealerUpCard = dealerUpCard;
    }
}
