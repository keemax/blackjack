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

Starts round and gives player required information. If soft is true, your hand contains an ace and can either have value "value" or "value + 10". When start is called out of turn or repeatedly, the server will return 403 Forbidden. If it is called with bad parameters, the server will return 400 Bad Request.


Hit
-------------------
mapping: /hit
parameters: playerId (String)
returns: {Card}

Gives the player a new card. Must be called after "/start". Returns 403 Forbidden if called out of turn, and 400 Bad Request if called with bad parameters.


Double Down
-------------------
mapping: /doubleDown
parameters: playerId (String)
returns {Card}

Doubles player's current wager and deals one card. That player is now done for the round. You cannot double down after hitting. Returns 403 Forbidden if called out of turn or after hitting, and 400 Bad Request if called with bad parameters.


Stand
-------------------
mapping: /stand
parameters: playerId (String)
returns: nothing

Indicated the player wishes to end his/her turn. Returns 403 Forbidden if called out of turn, and 400 Bad Request if called with bad parameters. 


Get Revealed Cards
-------------------
mapping: /getRevealedCards
parameters: none
returns: {"deckNumber": int,
		  "revealedCards": Card[]}

Returns ALL the cards that have been flipped face up from the current deck. When the deck is shuffled, deckNumber is incremented and revealedCards is cleared.


Get Chip Count
-------------------
mapping: /getStack
parameters: playerId (String)
returns: int

Returns current chip count for player with id playerId. Returns 400 Bad Request if playerId is invalid.


Game Over
-------------------
mapping: /done
parameters: none
returns: boolean

Returns true if the maximum number of rounds has been reached.






******EXAMPLE******
One round with one player could unfold as follows:

---------------
REQUEST:
---------------
http://localhost:8080/addPlayer?name=max
---------------
RESPONSE:
---------------
376ab3qcgu3vom9tbhoefr8r2u


Now I've been added to the game. I'll use this ID for my future requests.

Let's make sure I received my chips.


---------------
REQUEST:
---------------
http://localhost:8080/getStack?playerId=376ab3qcgu3vom9tbhoefr8r2u
---------------
RESPONSE:
---------------
1000


Everything looks good, time to start.


---------------
REQUEST
---------------
http://localhost:8080/start?playerId=376ab3qcgu3vom9tbhoefr8r2u&wager=10
---------------
RESPONSE:
---------------
{"yourHand":{"cards":[{"rank":5,"value":5,"suit":"HEARTS"},{"rank":6,"value":6,"suit":"SPADES"}],"value":11,"soft":false},"dealerUpCard":{"rank":4,"value":4,"suit":"CLUBS"}}


Looks like the dealer is showing a 4 and I've got an 11. I think the book says double down in this situation.


---------------
REQUEST:
---------------
http://localhost:8080/doubleDown?playerId=376ab3qcgu3vom9tbhoefr8r2u
---------------
RESPONSE:
---------------
{"rank":3,"value":3,"suit":"CLUBS"}


Not the best card..... Let's see who won


---------------
REQUEST:
---------------
http://localhost:8080/getStack?playerId=376ab3qcgu3vom9tbhoefr8r2u
---------------
RESPONSE:
---------------
1020


Woohoo!






