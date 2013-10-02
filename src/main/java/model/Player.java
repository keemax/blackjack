package model;

import model.Card;
import model.Hand;

public class Player {

    String id;
    String name;
	private Hand myHand;
	private int stack;
	private int currentWager;
    boolean active;

    private final int position;
	
	public Player(int position) {
		myHand = new Hand();
		stack = 0;
		currentWager = 0;
        active = false;
        this.position = position;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
	
	public void giveChips(int amount) {
		stack += amount;
	}
	
	public void takeChips(int amount) {
		stack -= amount;
	}
	
	public int getStack() {
		return stack;
	}
	
	public void giveCard(Card c) {
		myHand.addCard(c);
	}
	
	public Hand getHand() {
		return myHand;
	}

    public void discardHand() {
        myHand = new Hand();
    }
	
	public void setCurrentWager(int amount) {
		stack -= amount;
		currentWager = amount;
	}
    public int getCurrentWager() {
        return currentWager;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPosition() {
        return position;
    }
	

}
