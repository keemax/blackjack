import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//TODO: blackjack

@RestController
@EnableAutoConfiguration
public class DealerController {
	
	private final static int STARTING_CHIPS = 1000;
    private final static int MINIMUM_WAGER = 10;
	
	private Deck deck;
	private Hand dealerHand;
	private Map<Integer, Player> players;
	private int playerCount = 0;
    private int playersLeft = 0;
    private StartInfo roundInfo;
    private final int NUM_ROUNDS;
    private int round = 0;
    private boolean done = false;

    private final Lock dealerLock;

    public DealerController() {
        this(100);
    }
	public DealerController(int numRounds) {
        deck = new Deck();
		players = new HashMap<Integer, Player>();
        roundInfo = new StartInfo();
		dealerLock = new ReentrantLock();
        NUM_ROUNDS = numRounds;
	}
	
	//creates new player and gives them 1000 chips
	//returns id of player for use in requests
    @RequestMapping("/addPlayer")
	public int addPlayer() {

		dealerLock.lock();
        int playerId = playerCount++;
        Player newPlayer = new Player();
        newPlayer.setId(playerId);
        newPlayer.giveChips(STARTING_CHIPS);
        players.put(playerId, newPlayer);
        System.out.println("added player with id: " + playerId);

        dealerLock.unlock();

		return playerId;
	}

    //deals cards in standard order
    //returns a list of hands and dealer up card
    @RequestMapping("/start")
	public StartInfo start(@RequestParam(value = "playerId", required = true) int playerId, @RequestParam(value = "wager", required = true) int wager) {
        dealerLock.lock();
        Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
            dealerLock.unlock();
            return null;
        }
        if (wager < MINIMUM_WAGER || wager > thisPlayer.getStack()) {
            System.err.println("invalid wager amount: " + wager);
            dealerLock.unlock();
            return null;
        }
        if (thisPlayer.getCurrentWager() != 0) {
            System.err.println("player " + playerId + " has already placed his/her bet");
            dealerLock.unlock();
            return null;
        }
        if (round == NUM_ROUNDS) {
            if (!done) {
                printResults();
                done = true;
            }
            return null;
        }

        System.out.println("deal requested: setting player " + playerId + "'s wager to " + wager);
        thisPlayer.setCurrentWager(wager);

        if (playersLeft == 0) {
            round++;
            dealCards();
        }
        playersLeft++;
        roundInfo.setYourHand(thisPlayer.getHand());
        if (playersLeft == playerCount) {
            StartInfo roundInfoClone = roundInfo.clone();
            roundInfo.getRevealedCards().clear();
            roundInfo.setShuffled(false);
            dealerLock.unlock();
            return roundInfoClone;
        }
        dealerLock.unlock();
        return roundInfo;
    }

    @RequestMapping("/hit")
	public Card hit(@RequestParam(value = "playerId", required = true) int playerId) {
		dealerLock.lock();
        checkDeck();
		Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
            dealerLock.unlock();
            return null;
        }
        else if (!thisPlayer.isActive()) {
            System.err.println("player " + playerId + " has already finished round");
            dealerLock.unlock();
            return null;
        }
        Card newCard = deck.drawCard();
        roundInfo.addRevealedCard(newCard);
		thisPlayer.giveCard(newCard);
        checkBust(thisPlayer);

		dealerLock.unlock();
        return newCard;
	}

    @RequestMapping("/stand")
    public void stand(@RequestParam(value = "playerId", required = true) int playerId) {
        dealerLock.lock();
        Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
            dealerLock.unlock();
        }
        else {
            thisPlayer.setActive(false);
            dealerLock.unlock();
        }
    }

    private void dealCards() {
        System.out.println("discarding all hands");
        System.out.println("round " + round);
        for (Player player : players.values()) {
            player.discardHand();
            player.setActive(true);
        }
        dealerHand = new Hand();
        for (int i = 0; i < 2; i++) {
            for (Player player : players.values()) {
                checkDeck();
                Card newCard = deck.drawCard();
                roundInfo.addRevealedCard(newCard);
                player.giveCard(newCard);
                players.put(player.getId(), player);
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

    private void checkBust(Player player) {
        //if players hand is over 21, take their wager and set them to inactive for this round
        if (player.getHand().getValue() > 21) {
            System.out.println("player " + player.getId() + " busted");
            player.setCurrentWager(0);
            player.setActive(false);
            playersLeft--;
            if (playersLeft == 0) {
                System.out.println("all players done, dealer's turn");
                determineDealerHand();
            }
        }
    }
    private void determineDealerHand() {
        //reveal the dealer down card
        Card dealerDownCard = dealerHand.getCards().get(0);
        System.out.println("revealing dealer down card: " + dealerDownCard.toString());
        roundInfo.addRevealedCard(dealerDownCard);
        while (dealerHand.getValue() < 17 && (dealerHand.isSoft() && dealerHand.getValue() < 8)) {
            Card newCard = deck.drawCard();
            System.out.println("dealer hits: " + newCard.toString());
            roundInfo.addRevealedCard(newCard);
            dealerHand.addCard(newCard);
        }

        int dealer = dealerHand.getValue();
        //if dealer stays with a soft hand, make sure higher value is used
        if (dealerHand.isSoft()) {
            dealer += 10;
        }
        System.out.println("dealer hand value: " + dealer);
        //dealer busts, everyone currently in the round wins
        if (dealer > 21) {
            System.out.println("dealer busted, everybody wins!");
            for (Player p : players.values()) {
                p.giveChips(p.getCurrentWager() * 2);
                p.setCurrentWager(0);
            }
        }
        else {
            for (Player p : players.values()) {
                //player hand beats dealer
                if (p.getHand().getValue() > dealer && p.getHand().getValue() <= 21) {
                    System.out.println("player " + p.getId() + " beat dealer with a " + p.getHand().getValue());
                    p.giveChips(p.getCurrentWager() * 2);
                }
                //push
                else if (p.getHand().getValue() == dealer) {
                    System.out.println("player " + p.getId() + " pushes");
                    p.giveChips(p.getCurrentWager());
                }
                p.setCurrentWager(0);
            }
        }

    }
    private void checkDeck() {
        if (deck.cardsLeft() < 5) {
            System.out.println("shuffling deck");
            deck = new Deck();
            roundInfo.getRevealedCards().clear();
            roundInfo.setShuffled(true);
        }
    }


    private void printResults() {
        System.out.println("****************** game over ******************\n\n");
        for (Player p : players.values()) {
            System.out.println("player " + p.getId() + ":");
            System.out.println("\tchip total: " + p.getStack());
            System.out.println(" ");
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DealerController.class, args);
    }
	
	
}
