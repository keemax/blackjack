package model;

import java.util.List;

import model.Card;
import model.Hand;
import junit.framework.TestCase;

public class HandTest extends TestCase {
	private Hand testHand;
	
	public void testAddCard() {
		testHand = new Hand();
		assertEquals(0, testHand.getCards().size());
		Card testCard = new Card(Card.Suit.CLUBS, 12);
		testHand.addCard(testCard);
		
		List<Card> testCards = testHand.getCards();
		assertEquals(1, testCards.size());
		
		testCard = testCards.get(0);
		assertEquals(Card.Suit.CLUBS, testCard.getSuit());
		assertEquals(12, testCard.getRank());		
	}
	
	public void testHandSoftness() {
		testHand = new Hand();
		assertEquals(false, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(true, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 2));
		assertEquals(true, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 9));
		assertEquals(false, testHand.isSoft());
		
		testHand = new Hand();
		testHand.addCard(new Card(Card.Suit.CLUBS, 2));
		assertEquals(false, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 2));
		assertEquals(false, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 6));
		assertEquals(false, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(true, testHand.isSoft());
		
		testHand = new Hand();
		testHand.addCard(new Card(Card.Suit.CLUBS, 13));
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(true, testHand.isSoft());
		
		testHand = new Hand();
		testHand.addCard(new Card(Card.Suit.CLUBS, 3));
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(true, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 2));
		assertEquals(true, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(true, testHand.isSoft());	
		testHand.addCard(new Card(Card.Suit.CLUBS, 4));
		assertEquals(true, testHand.isSoft());
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(false, testHand.isSoft());	
	}
	
	public void testHandValue() {
		testHand = new Hand();
		assertEquals(0, testHand.getValue());
		testHand.addCard(new Card(Card.Suit.CLUBS, 3));
		assertEquals(3, testHand.getValue());
		testHand.addCard(new Card(Card.Suit.CLUBS, 4));
		assertEquals(7, testHand.getValue());
		testHand.addCard(new Card(Card.Suit.CLUBS, 1));
		assertEquals(8, testHand.getValue());
		testHand.addCard(new Card(Card.Suit.CLUBS, 13));
		assertEquals(18, testHand.getValue());
		testHand.addCard(new Card(Card.Suit.CLUBS, 11));
		assertEquals(28, testHand.getValue());
		
	}
	
}
