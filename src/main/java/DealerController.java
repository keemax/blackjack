import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import util.IdGenerator;

//TODO: double down
//TODO: I think round info is wrong

@RestController
@EnableAutoConfiguration
public class DealerController {
	
	private final static int STARTING_CHIPS = 1000;
    private final static int MINIMUM_WAGER = 10;
	
	private Deck deck;
	private Hand dealerHand;
	private Map<String, Player> players;
	private int playerCount = 0;
    private StartInfo roundInfo;
    private final int NUM_ROUNDS;
    private int NUM_PLAYERS;
    private int round = 0;
    private boolean done = false;
    private int currentPosition = 0;
    private int dealRequests = 0;
    private IdGenerator idGen;

    private final Lock dealerLock;
    private final Object playerMonitor;

    public DealerController() {
        this(1000, 2);
    }
	public DealerController(int numRounds, int numPlayers) {
        deck = new Deck();
		players = new HashMap<String, Player>();
        roundInfo = new StartInfo();
		dealerLock = new ReentrantLock();
        playerMonitor = new Object();
        idGen = new IdGenerator();
        NUM_ROUNDS = numRounds;
        NUM_PLAYERS = numPlayers;
	}
	
	//creates new player and gives them 1000 chips
	//returns id of player for use in requests
    @RequestMapping("/addPlayer")
	public String addPlayer(@RequestParam(value = "name", required = true) String name) {

		dealerLock.lock();
        Player newPlayer = new Player();
        newPlayer.setPosition(playerCount);
        playerCount++;
        String playerId = idGen.nextId();
        newPlayer.setId(playerId);
        newPlayer.setName(name);
        newPlayer.giveChips(STARTING_CHIPS);
        players.put(playerId, newPlayer);
        System.out.println("added player with id: " + playerId);

        dealerLock.unlock();
		return playerId;
	}

    //deals cards in standard order
    //returns a list of hands and dealer up card
    @RequestMapping("/start")
	public StartInfo start(@RequestParam(value = "playerId", required = true) String playerId, @RequestParam(value = "wager", required = true) int wager) {
        while (playerCount < NUM_PLAYERS) {
            System.out.println("waiting for other players");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        //playerId error check
        dealerLock.lock();
        Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
            dealerLock.unlock();
            return null;
        }

        //make player wait their turn
        dealerLock.unlock();
        while(thisPlayer.getPosition() != currentPosition) {
            System.out.println(thisPlayer.getName() + " is waiting for his/her turn");
            playerWait();
        }
        dealerLock.lock();

        //wager error check
        if (wager < MINIMUM_WAGER || wager > thisPlayer.getStack()) {
            System.err.println("invalid wager amount for " + thisPlayer.getName() + ": " + wager);
            dealerLock.unlock();
            return null;
        }
        //check if player has called start already
        if (thisPlayer.getCurrentWager() != 0) {
            System.err.println(thisPlayer.getName() + " has already placed his/her bet");
            dealerLock.unlock();
            return null;
        }
        //see if game is over
        if (round == NUM_ROUNDS) {
            if (!done) {
                done = true;
            }
            dealerLock.unlock();
            return null;
        }
        System.out.println("deal requested: setting " + thisPlayer.getName() + "'s wager to " + wager);
        dealRequests++;
        thisPlayer.setCurrentWager(wager);

        //if this is the first request for a deal, deal cards to begin new round
        if (dealRequests == 1) {
            round++;
            dealCards();
        }

        roundInfo.setYourHand(thisPlayer.getHand());

        //if this is the last request, reset roundInfo for new round
        if (dealRequests == NUM_PLAYERS) {
            StartInfo roundInfoClone = roundInfo.clone();
            roundInfo.getRevealedCards().clear();
            roundInfo.setShuffled(false);
            dealRequests = 0;
            dealerLock.unlock();
            return roundInfoClone;
        }

        dealerLock.unlock();
        return roundInfo;
    }

    @RequestMapping("/hit")
	public Card hit(@RequestParam(value = "playerId", required = true) String playerId) {
		dealerLock.lock();
        checkDeck();
		Player thisPlayer = players.get(playerId);
        Card newCard = null;
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
        }
        else if (!thisPlayer.isActive()) {
            System.err.println(thisPlayer.getName() + " has already finished round");
        }
        else {
            newCard = deck.drawCard();
            roundInfo.addRevealedCard(newCard);
            thisPlayer.giveCard(newCard);
            System.out.println(thisPlayer.getName() + " hits: " + newCard.toString());
            checkBust(thisPlayer);
        }

		dealerLock.unlock();
        return newCard;
	}

    @RequestMapping("/stand")
    public void stand(@RequestParam(value = "playerId", required = true) String playerId) {
        dealerLock.lock();
        Player thisPlayer = players.get(playerId);
        if (thisPlayer == null) {
            System.err.println("invalid player id: " + playerId);
        }
        else if (!thisPlayer.isActive()) {
            System.err.println(thisPlayer.getName() + " has already finished round");
        }
        else {
            thisPlayer.setActive(false);
            System.out.println(thisPlayer.getName() + " stands");
            currentPosition++;
            if (currentPosition == NUM_PLAYERS) {
                System.out.println("all players done, dealer's turn");
                determineDealerHand();
            }
            else {
                playerDone();
            }
        }
        dealerLock.unlock();
    }

    private void dealCards() {
        System.out.println("discarding all hands");
        System.out.println("round " + round);
        for (Player player : players.values()) {
            player.discardHand();
            player.setActive(true);
        }
        dealerHand = new Hand();
        //deal cards in standard order and update players map
        for (int i = 0; i < 2; i++) {
            for (Player player : players.values()) {
                checkDeck();
                Card newCard = deck.drawCard();
                roundInfo.addRevealedCard(newCard);
                player.giveCard(newCard);
                players.put(player.getId(), player);
                System.out.println("dealing " + newCard.toString() + " to " + player.getName());
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
            System.out.println(player.getName() + " busted with a " + player.getHand().getValue());
            player.setCurrentWager(0);
            player.setActive(false);
            currentPosition++;
            System.out.println("moving on to position " + currentPosition);
            if (currentPosition == NUM_PLAYERS) {
                System.out.println("all players done, dealer's turn");
                determineDealerHand();
            }
            else {
                playerDone();
            }
        }
    }
    private void determineDealerHand() {
        //reveal the dealer down card
        Card dealerDownCard = dealerHand.getCards().get(0);
        System.out.println("revealing dealer down card: " + dealerDownCard.toString());
        roundInfo.addRevealedCard(dealerDownCard);
        if (dealerHand.isSoft()) {
            while(dealerHand.getValue() < 8) {
                checkDeck();
                Card newCard = deck.drawCard();
                System.out.println("dealer hits: " + newCard.toString());
                roundInfo.addRevealedCard(newCard);
                dealerHand.addCard(newCard);
            }
        }
        else {
            while (dealerHand.getValue() < 17) {
                checkDeck();
                Card newCard = deck.drawCard();
                System.out.println("dealer hits: " + newCard.toString());
                roundInfo.addRevealedCard(newCard);
                dealerHand.addCard(newCard);
            }

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
                Hand playerHand = p.getHand();
                //blackjack
                if (playerHand.getValue() == 21 && playerHand.getCards().size() == 2) {
                    System.out.println(p.getName() + " got a blackjack!");
                    p.giveChips((int)Math.ceil((double)p.getCurrentWager() * 2.5));
                }
                //player hand beats dealer
                else if (playerHand.getValue() > dealer && playerHand.getValue() <= 21) {
                    System.out.println(p.getName() + " beat dealer with a " + playerHand.getValue());
                    p.giveChips(p.getCurrentWager() * 2);
                }
                //push
                else if (playerHand.getValue() == dealer) {
                    System.out.println(p.getName() + " pushes");
                    p.giveChips(p.getCurrentWager());
                }
                p.setCurrentWager(0);

                //check for broke players and remove them
                if (p.getStack() < MINIMUM_WAGER) {
                    System.out.println(p.getName() + " ran out of chips");
                    int playerPosition = p.getPosition();
                    players.remove(p.getId());
                    for (Player pl : players.values()) {
                        int position = pl.getPosition();
                        if (position > playerPosition) {
                            pl.setPosition(position - 1);
                            players.put(pl.getId(), pl);
                        }
                    }
                    NUM_PLAYERS--;
                }
            }
        }

        printTotals();
        currentPosition = 0;
        playerDone();

    }
    private void checkDeck() {
        if (deck.cardsLeft() < 5) {
            System.out.println("shuffling deck");
            deck = new Deck();
            roundInfo.getRevealedCards().clear();
            roundInfo.setShuffled(true);
        }
    }


    private void printTotals() {
        System.out.println("****************** chip totals ******************\n");
        for (Player p : players.values()) {
            System.out.println("\t" + p.getName() + ": " + p.getStack());
        }
        System.out.println("");
    }

    private void playerWait() {
        synchronized (playerMonitor) {
            try {
                playerMonitor.wait();
            }
            catch(Exception e) {
                System.err.println("error waiting for player lock");
            }
        }
    }
    private void playerDone() {
        synchronized (playerMonitor) {
            playerMonitor.notifyAll();
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DealerController.class, args);
    }
	
	
}
