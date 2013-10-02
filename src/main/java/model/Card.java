package model;

public class Card {

	private int rank;
	private int value;

	public enum Suit { 
		CLUBS, DIAMONDS, HEARTS, SPADES 
	}

    private Suit suit;


	public Card() {
	}

	public Card(Suit suit, int rank) {
		this.suit = suit;
		setRank(rank);

	}

	public Suit getSuit() {
		return suit;
	}
	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public int getRank() {
		return rank;
	}
	
	//also sets card value based on rank
	public void setRank(int rank) {
		this.rank = rank;
		if (rank >= 10) {
			this.value = 10;
		}
		else {
			this.value = rank;
		}
	}
	
	public int getValue() {
		return value;
	}

    public Card clone() {
        Card cloneCard = new Card();
        cloneCard.setRank(this.rank);
        cloneCard.setSuit(this.suit);
        return cloneCard;
    }

    @Override
    public String toString() {
        return "Card{" +
                "rank=" + rank +
                ", value=" + value +
                ", suit=" + suit +
                '}';
    }

}
