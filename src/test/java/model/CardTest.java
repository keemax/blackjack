package model;

import junit.framework.TestCase;
import model.Card;

public class CardTest extends TestCase {
	private Card testCard;
	
	public void testSetSuit() {
		testCard = new Card();
		assertEquals(null, testCard.getSuit());
		
		testCard.setSuit(Card.Suit.CLUBS);
		assertEquals(Card.Suit.CLUBS, testCard.getSuit());		
	}
	
	public void testSetRankAndValue() {
		testCard = new Card();
		assertEquals(0, testCard.getRank());
		
		testCard.setRank(1);
		assertEquals(1, testCard.getRank());
		assertEquals(1, testCard.getValue());
		
		testCard.setRank(2);
		assertEquals(2, testCard.getRank());
		assertEquals(2, testCard.getValue());
		
		testCard.setRank(3);
		assertEquals(3, testCard.getRank());
		assertEquals(3, testCard.getValue());
		
		testCard.setRank(4);
		assertEquals(4, testCard.getRank());
		assertEquals(4, testCard.getValue());
		
		testCard.setRank(5);
		assertEquals(5, testCard.getRank());
		assertEquals(5, testCard.getValue());
		
		testCard.setRank(6);
		assertEquals(6, testCard.getRank());
		assertEquals(6, testCard.getValue());
		
		testCard.setRank(7);
		assertEquals(7, testCard.getRank());
		assertEquals(7, testCard.getValue());
		
		testCard.setRank(8);
		assertEquals(8, testCard.getRank());
		assertEquals(8, testCard.getValue());
		
		testCard.setRank(9);
		assertEquals(9, testCard.getRank());
		assertEquals(9, testCard.getValue());
		
		testCard.setRank(10);
		assertEquals(10, testCard.getRank());
		assertEquals(10, testCard.getValue());
		
		testCard.setRank(11);
		assertEquals(11, testCard.getRank());
		assertEquals(10, testCard.getValue());
		
		testCard.setRank(12);
		assertEquals(12, testCard.getRank());
		assertEquals(10, testCard.getValue());
		
		testCard.setRank(13);
		assertEquals(13, testCard.getRank());
		assertEquals(10, testCard.getValue());		
	}
	
	
}
