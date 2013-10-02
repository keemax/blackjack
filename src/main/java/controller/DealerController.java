package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DealerController {
	
	private final static int STARTING_CHIPS = 1000;
    private final static int MINIMUM_WAGER = 10;
	
	private Deck deck;
	private Hand dealerHand;
	private Map<Integer, Player> players;
	private Player currentPlayer;
	private int currentPlayerId;
	private int playerCount = 0;
    private int playersLeft = 0;
    private int dealRequests = 0;
    private StartInfo roundInfo;

	private Object dealerLock;
	
	public DealerController() {
		players = new HashMap<Integer, Player>();
        roundInfo = new StartInfo();
		dealerLock = new Object();
	}
	
	//creates new player and gives them 1000 chips
	//returns id of player for use in requests
    @RequestMapping("/addPlayer")
	public @ResponseBody int addPlayer() {
		getLock();
		int playerId = playerCount++;
        playersLeft++;
        Player newPlayer = new Player();
        newPlayer.setId(playerId);
        newPlayer.giveChips(STARTING_CHIPS);
        //set current player to first player
        if (currentPlayer == null) {
            currentPlayerId = playerId;
            currentPlayer = newPlayer;
        }
        players.put(playerId, newPlayer);
        System.out.println("added player with id: " + playerId);
        releaseLock();
		return playerId;
	}

    //deals cards in standard order
    //returns a list of hands and dealer up card
    @RequestMapping("/start")
	public @ResponseBody StartInfo dealCards(@RequestParam int playerId, @RequestParam int wager) {
        getLock();
        Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
            return null;
        }
        if (wager < MINIMUM_WAGER || wager > thisPlayer.getStack()) {
            System.err.println("invalid wager amount: " + wager);
            return null;
        }
        System.out.println("setting player " + playerId + "'s wager to " + wager);
        thisPlayer.setCurrentWager(wager);

        if (dealRequests == 0) {
            dealCards();
        }
        dealRequests++;
        releaseLock();
        return roundInfo;
    }

    private void dealCards() {
        System.out.println("discarding all hands");
        for (Player player : players.values()) {
            player.discardHand();
        }
        dealerHand = new Hand();
        for (int i = 0; i < 2; i++) {
            for (Player player : players.values()) {
                checkDeck();
                Card newCard = deck.drawCard();
                roundInfo.addRevealedCard(newCard);
                player.giveCard(newCard);
                System.out.println("dealing " + newCard.toString() + " to player " + player.getId());
            }
            Card dealerCard = deck.drawCard();
            dealerHand.addCard(dealerCard);
            System.out.println("dealing " + dealerCard.toString() + " to dealer");

            if (i == 1) {
                roundInfo.addRevealedCard(dealerCard);
                roundInfo.setDealerUpCard(dealerCard);
            }
        }
    }

    @RequestMapping("/hit")
	public @ResponseBody Card dealCard(@RequestParam int playerId) {
		getLock();
		checkDeck();
        if (playerId == 0) {
            roundInfo.getRevealedCards().clear();
        }
		Player thisPlayer = players.get(playerId);
        Card newCard = deck.drawCard();
        roundInfo.addRevealedCard(newCard);
		thisPlayer.giveCard(newCard);
		if (thisPlayer.getHand().getValue() > 21) {
			currentPlayer.setCurrentWager(0);
			currentPlayerId++;
			currentPlayer = players.get(currentPlayerId);
            playersLeft--;
            if (playersLeft == 0) {
                resolveBets();
            }
		}
		releaseLock();
        return newCard;
	}
    private void resolveBets() {


    }
    private void checkDeck() {
        if (deck.cardsLeft() < 5) {
            deck = new Deck();
            roundInfo.getRevealedCards().clear();
            roundInfo.setShuffled(true);
        }
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
