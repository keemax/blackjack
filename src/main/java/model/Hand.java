package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	
	List<Card> cards;
	int value;
	//if soft is true, hand contains ace
	//value is either current value or current value + 10
	boolean soft;
	
	public Hand() {
		this.cards = new ArrayList<Card>();
		value = 0;
		soft = false;
	}
	
	public List<Card> getCards() {
		return cards;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isSoft() {
		return soft;
	}
	
	//updates value of hand and determines softness
	public void addCard(Card newCard) {
		cards.add(newCard);
		value += newCard.getValue();
		if (!soft) {
			if (newCard.getRank() == 1) {
				if (value <= 11) {
					soft = true;
				}
			}
		}
		else {
			if (value > 11) {
				soft = false;
			}
		}
	}

}
