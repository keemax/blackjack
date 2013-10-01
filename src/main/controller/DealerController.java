package main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.model.*;

public class DealerController {
	
	private final static int STARTING_CHIPS = 1000;
	
	private Deck deck;
	private Hand dealerHand;
	private List<Player> players;
	private Player currentPlayer;
	private int currentPlayerId;
	private int playerCount;
	
	private Object dealerLock;
	
	public DealerController() {
		players = new ArrayList<Player>();
		playerCount = 0;
		dealerLock = new Object();
	}
	
	//creates new player and gives them 1000 chips
	//returns id of player for use in requests
	public int addPlayer() {
//		getLock();
		int playerId = playerCount++;
		Player newPlayer = new Player();
		newPlayer.giveChips(STARTING_CHIPS);
		//set current player to first player
		if (currentPlayer == null) {
			currentPlayerId = playerId;
			currentPlayer = newPlayer;		
		}
		players.add(newPlayer);
//		releaseLock();
		return playerId;
	}
	
	public void dealCards() {
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				player.giveCard(deck.drawCard());
			}
			dealerHand.addCard(deck.drawCard());
		}
	}
	
	//notifies first player to begin
	//players also hit this endpoint to stand
	//deals new hand and tells player dealer up card
	public void notifyPlayerStart() {
//		getLock();
		if (deck.cardsLeft() < 15) {
			deck = new Deck();
		}		
		Hand playerHand = currentPlayer.getHand();
//		releaseLock();
		//call PlayerController.startRound(playerHand, Card upCard)
	}
	
	//updates player's current wager
	//should probably return a status
	public void playerBet(int playerId, int amount) {
//		getLock();
		Player thisPlayer = players.get(playerId);
		if (thisPlayer == null) {
			releaseLock();
			return;
		}
		thisPlayer.bet(amount);
//		releaseLock();
	}
	
	public void dealCard(int playerId) {
//		getLock();
		if (deck.cardsLeft() < 15) {
			deck = new Deck();
		}
		Player thisPlayer = players.get(playerId);
		thisPlayer.giveCard(deck.drawCard());
		if (thisPlayer.getHand().getValue() > 21) {
			currentPlayer.resetWager();
			currentPlayerId++;
			currentPlayer = players.get(currentPlayerId);
			notifyPlayerStart();
		}
//		releaseLock();
		//call player.receiveCard
		
	}
	
	private void getLock() {
		synchronized(dealerLock) {
			try {
				dealerLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void releaseLock() {
		synchronized(dealerLock) {
			dealerLock.notify();
		}
	}
	
	
}
