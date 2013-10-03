**************************
*                        *
*       BLACKJACK        *
*                        *
**************************

****OBJECT REFERENCE****

Card
--------
{"rank": int,
 "value": int,
 "suit": string}

rank: 1 (ace) through 13 (king)
value: Value of card according to blackjack rules. Aces have value 1, facecards 10, everything else same as rank.
suit: One of ("CLUBS", "HEARTS", "DIAMONDS", "SPADES") 




****ENDPOINTS****
(all requests should be of the GET variety, with parameters provided in the URI)

Add a player
-------------------
mapping: /addPlayer
parameters: name (String)
returns: String

Adds a player to the game, gives them 1000 chips and a seat, and returns the id of that player. Provided name is for display purposes, returned id will be used in all subsequent requests.


Player Start
-------------------
mapping: /start
parameters: playerId (String), wager (int)
returns: {"yourHand": {"cards": Card[],
					   "value": int,
					   "soft": boolean},
		  "dealerUpCard": Card}

Starts round and gives player required information. When start is called out of turn, repeatedly, or with bad parameters, it will return a 400 status.


Hit
-------------------
mapping: /hit
parameters: playerId (String)
returns: {Card}

Gives the player a new card. Must be called after "/start". Returns 400 status if called out of turn, after player busts/stands/doubles down, before calling start for the current round, etc.


Double Down
-------------------
mapping: /doubleDown
parameters: playerId (String)
returns {Card}

Doubles player's current wager and deals one card. That player is now done for the round. You cannot double down after hitting. Returns 400 status if called inappropiately.


Stand
-------------------
mapping: /stand
parameters: playerId (String)
returns: nothing

Indicated the player wishes to end his/her turn.


Get Revealed Cards
-------------------
mapping: /getRevealedCards
parameters: none
returns: {"deckNumber": int,
		  "revealedCards": Card[]}

Returns ALL the cards that have been flipped face up from the current deck. When the deck is shuffled, deckNumber is incremented and revealedCards is cleared.


Game Over
-------------------
mapping: /done
parameters: none
returns: boolean

Returns true if the maximum number of rounds has been reached.