package main.model;

public class Player {
	
	private Hand myHand;
	private int stack;
	private int currentWager;
	
	public Player() {
		myHand = new Hand();
		stack = 0;
		currentWager = 0;
	}
	
	public void giveChips(int amount) {
		stack += amount;
	}
	
	public void takeChips(int amount) {
		stack -= amount;
	}
	
	public double getChipTotal() {
		return stack;
	}
	
	public void giveCard(Card c) {
		myHand.addCard(c);
	}
	
	public void bet(int amount) {
		currentWager += amount;
	}
	
	public void resetWager() {
		currentWager = 0;
	}
	

}
