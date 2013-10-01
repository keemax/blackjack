package main.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import main.model.*;

public class DealerController {
	
	private final static int STARTING_CHIPS = 1000;
	
	private Deck deck;
	private Hand dealerHand;
	private Map<Integer, Player> players;
	private int playerCount;
	
	private Object dealerLock;
	
	public DealerController() {
		players = new HashMap<Integer, Player>();
		playerCount = 0;
		dealerLock = new Object();
	}
	
	//creates new player and gives them 1000 chips
	//returns id of player for use in requests
	public int addPlayer() {
		int playerId = playerCount++;
		Player newPlayer = new Player();
		newPlayer.giveChips(STARTING_CHIPS);
		players.put(playerId, newPlayer);
		return playerId;
	}
	
	//notifies first player to begin
	//players also hit this endpoint to stand
	//deals new hand
	public void notifyPlayerStart() {
		if (deck.cardsLeft() < 15) {
			deck = new Deck();
		}
		
		Hand playerHand = new Hand();
		playerHand.addCard(deck.drawCard());
		playerHand.addCard(deck.drawCard());
		
		//call PlayerController.startRound(playerHand)
	}
	
	//updates player's current wager
	//should probably return a status
	public void playerBet(int playerId, int amount) {
		Player thisPlayer = players.get(playerId);
		if (thisPlayer == null) {
			return;
		}
		thisPlayer.bet(amount);
	}
	
	public void dealCard() {
		if (deck.cardsLeft() < 15) {
			deck = new Deck();
		}
		
	}
	
	private void getLock() {
		
	}
	
	
}
