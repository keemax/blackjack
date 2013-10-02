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

    private List<Card> revealedCards;
    private Hand yourHand;
    private Card dealerUpCard;
    private boolean shuffled;

    public StartInfo() {
        revealedCards = new ArrayList<Card>();
        yourHand = new Hand();
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public List<Card> getRevealedCards() {
        return revealedCards;
    }

    public void setRevealedCards(List<Card> revealedCards) {
        this.revealedCards = revealedCards;
    }

    public void addRevealedCard(Card card) {
        revealedCards.add(card);
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

    public StartInfo clone() {
        StartInfo clone = new StartInfo();
        List<Card> cloneRevealedCards = new ArrayList<Card>();
        for(Card c : this.revealedCards) {
            cloneRevealedCards.add(c.clone());
        }
        clone.setRevealedCards(cloneRevealedCards);
        Hand cloneHand = new Hand();
        for (Card c : yourHand.getCards()) {
            cloneHand.addCard(c.clone());
        }
        clone.setYourHand(cloneHand);
        clone.setDealerUpCard(this.dealerUpCard.clone());
        clone.setShuffled(this.shuffled);
        return clone;
    }
}
