package model;

import model.Card;
import model.Deck;
import junit.framework.TestCase;

public class DeckTest extends TestCase {

	private Deck testDeck;
	
	public void testMakeDeck() {
		testDeck = new Deck();
		assertEquals(52, testDeck.cardsLeft());
	}
	
	public void testDrawCard() {
		testDeck = new Deck();
        System.out.println("deck size: " + testDeck.cardsLeft());
        for (int i = 0; i < 52; i++) {
			Card randCard = testDeck.drawCard();
            System.out.println("card " + i + ": " + randCard.toString());
            assertNotNull(randCard);
			assertTrue(randCard.getValue() >= 1 && randCard.getValue() <= 13);
			Card.Suit suit = randCard.getSuit();
			assertTrue(suit == Card.Suit.CLUBS || suit == Card.Suit.DIAMONDS || 
						 suit == Card.Suit.HEARTS || suit == Card.Suit.SPADES);
		}
		Card nullCard = testDeck.drawCard();
		assertNull(nullCard);
	}
}
