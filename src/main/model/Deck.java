package main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
	
	private List<Card> cards;
	Random rand;
	
	//populates deck with standard 52 card set
	public Deck() {		
		cards = new ArrayList<Card>();
		for (int i = 1; i <=13; i++) {
			cards.add(new Card(Card.Suit.CLUBS, i));
			cards.add(new Card(Card.Suit.DIAMONDS, i));
			cards.add(new Card(Card.Suit.HEARTS, i));
			cards.add(new Card(Card.Suit.SPADES, i));
		}
		rand = new Random(System.currentTimeMillis());
	}
	
	public int cardsLeft() {
		return cards.size();
	}
	
	//draws random card from remaining
	//returns null if deck is empty
	public Card drawCard() {
		if (cards.isEmpty()) {
			return null;
		}
		int cardIndex = rand.nextInt(cards.size());
		return cards.remove(cardIndex);
	}

}
