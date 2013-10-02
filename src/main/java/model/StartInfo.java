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
    private Card dealerUpCard;
    private boolean shuffled;

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public StartInfo() {
        revealedCards = new ArrayList<Card>();
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

    public Card getDealerUpCard() {
        return dealerUpCard;
    }

    public void setDealerUpCard(Card dealerUpCard) {
        this.dealerUpCard = dealerUpCard;
    }
}
