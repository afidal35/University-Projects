//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 29/11/2020.
//

#ifndef CARD_GAMES_GAME_H
#define CARD_GAMES_GAME_H

#include <cstdlib>
#include <ctime>

#include "Player.h"
#include "Helpers.h"
#include "CardBuilder.h"

/*!
 * \brief The abstract model class representing the model (game) of our model (game) view controller pattern.
 */

class Game {

public:

    virtual ~Game();

    /*! \brief Clear the game.
     * Clears the game in such a way that everything is reset.
     */
    void clearGame();

    /*! \brief Resets the game.
     * Clears the game in such a way that same players can continue playing.
     */
    void resetGame();

    /*!
     * @return the maximum amount of players that the game can handle.
     */
    virtual int getMaxPlayers() const = 0;

    /*!
     * Creates a deck for a game.
     */
    virtual void createDeck() = 0;

    /*! \brief Get a valid move.
     * Get a valid move for the current player, useful for bots.
     * @return the index of the valid move in the current player's hand.
     */
    virtual int getCurrentPlayerValidMove() = 0;

    /*! \brief Allow model to know how to deal.
     * Give information to the abstract model class to know how to deal the cards at the beginning of the game.
     * @return a pair, the first integer reference the number of cards to deal to each players while
     * the second one reference how many cards to put on the table at the beginning of the game.
     */
    virtual std::pair<int,int> howToDeal() const = 0;

    /*!
     * \brief Get a string representation of the rules.
     * @return return a string containing the rules of the game, so the view can display it.
     */
    virtual string getRules() const;

    // General methods for all models, can be re-implemented as well for sub-models...

    /*!
     * @return the minimum amount of players that the game can handle.
     */
    virtual int getMinPlayers() const;

    /*! \brief game name.
     * Allow the view to get a representation of the game name.
     * @return a string that represents the game name.
     */
    virtual string getGameHeader() const;

    /*! \brief Winner of the round.
     * Evaluate if there is a winner of a round.
     * @return a Player* representing the winner of the round if there is one, nullptr otherwise.
     */
    virtual Player* evaluateWinnerOfRound() const;

    /*! \brief Winner of the game.
    * Evaluate if there is a winner of the whole game.
    * @return a Player* representing the winner of the game if there is one, nullptr otherwise.
    */
    virtual Player* evaluateWinnerOfGame() const;

    /*!
     * Useful for the view, to know if it asks an index to the user or not.
     * @return a boolean, true if the game logic allows user to choose cards they will play, false otherwise.
     */
    virtual bool isCardChoosable() const;

    /*! \brief Check if a move is valid.
     * @param card.
     * @return true if the Card represents a valid move for the game, false otherwise.
     */
    virtual bool isMoveValid(const Card &card);

    /*! \brief Calculates points.
     * @param winner is the winner of the round.
     * Calculates the points of the winner.
     */
    virtual void calculatePointsForWinner(Player *winner);

    /*! \brief Get effect of the card.
     * @param card
     * @return an integer value representing a returned value from a method which represents the effect of the card.
     */
    virtual int getCardEffect(const Card &card);

    /*! \brief Need to apply first card ?
     * Tells whether or not the game need to apply the effect of the first card placed on the table at the beginning
     * of the game.
     * @return true if the effect is needed, false otherwises.
     */
    virtual bool needFirstCardEffect() const;

    // Need to find an alternative
    virtual bool showTable() const;

    /*! \brief Check if a cut is valid.
     * @param cut a tuple of 3 values
     * -> the first one represents the position of the card played by the current player.
     * -> the second one represents the position of the first take of the player on the table.
     * -> the third one represents the position of the second take of the player on the table.
     * @return true if the cut is valid, false otherwise.
     */
    virtual bool isCutValid(std::tuple<int,int,int> cut);

    /*! \brief Get a valid cut.
     * Get a valid cut for the current player, for the scopa game, useful for bots.
     * @return a tuple of 3 values representing a valid cut.
     */
    virtual std::tuple<int,int,int> getValidCut();

    /*!
     * Check if the current player is able to make a cut.
     * @return true if so, false otherwise.
     */
    virtual bool isAbleToCut();


    /*! \brief Shuffles the deck of the game.
     */
    void shuffleDeck();

    /*! \brief Winner of the round.
    * Evaluate if there is a winner of a turn, for turn by turn games.
    * @return a Player* representing the winner of the turn if there is one, nullptr otherwise.
    */
    virtual Player* evaluateTurn();

    /*! \brief Winner of the turn.
     * To know when a turn must be evaluated by evaluateTurn().
     * @return true if the condition passed, false otherwise.
     */
    virtual bool evaluationTurnCondition() const;

    // Document it ...
    virtual int chooseColor();


    // Getters / Setters / Helpers

    int getPlayerIndex() const;
    void setPlayerIndex(int x);

    void setPlayers(vector<string> &names, bool b);
    vector<Player*> getPlayers();

    bool getStart() const;
    void setStart(bool b);

    int getNumberOfPlayers() const;
    void setNumberOfPlayers(int x);

    int getNumberOfPointsToWin() const;
    void setNumberOfPointsToWin(int points);

    int getNumberOfRoundsPlayed() const;
    void setNumberOfRoundsPlayed(int round);

    CardBundle& getDeck();
    CardBundle& getTable();

    /*!
     * @return a Card which is the last one placed on the table.
     */
    Card getLastCardPlayed();

    /*!
     * @return the current Player.
     */
    Player* getCurrentPlayer();

    /*!
     * @param pos the position of the card in the current player's hand to be played.
     * @return a the Card played.
     */
    Card playerPlayCardFromPos(int pos);

    /*!
     * Add a card to the current player stack.
     * @param card which will be added to the current Player stack.
     */
    void addToCurrentPlayerStack(const Card &card);

    /*!
    * Add a card on the table.
    * @param card which will be added to the table.
    */
    void addToTable(const Card &card);

    // for scopa
    Card& getCardFromTable(int pos);
    Card removeCardFromTable(int pos);

    /*!
     * Shuffle the players to get a random first player.
     */
    void shufflePlayers();

    /*!
     * Make the current player draw n Card(s).
     * @param n number of cards to be drawn.
     */
    void makeCurrentPlayerDraw(int n);

    /*!
     * Call the effect of the last card placed on the table.
     * @return an integer representing the effect, allowing to pass it to the view to announce the effect.
     */
    int callLastCardEffect();

    /*!
     * Deals the cards and set the table according to the howToDeal() method defined in sub-models.
     */
    void dealCardsAndSetTable();

    void fillDeckWithTable();

    /*!
     * Pass a turn based on the current player index.
     * @return 1.
     */
    int nextTurn();

    /*!
     * Skip a turn based on the current player index.
     * @return 2.
     */
    int skipTurn();

    /*!
    * Make the current player draw two cards from the deck.
    * @return 3.
    */
    int drawTwo();

    /*!
    * Make the current player draw four cards from the deck.
    * @return 4.
    */
    int drawFour();

    /*!
    * Reverse the sens of the game.
    * @return 5.
    */
    int reverse();


protected:

    int playerIndex = 0; /**< \brief An integer that represents the current player index. */
    int numberOfPlayers;  /**< \brief An integer that represents the number of players in the current game. */
    int numberOfPointsToWin;  /**< \brief An integer that represents the number of points that a player needs to win the game. . */
    int numberOfRoundsPlayed;  /**< \brief An integer that represents the number of rounds played
    * Useful for war where endless loops can happen, so we can check for a certain amount before re-shuffling for more
    * randomness. */
    bool start = true;  /**< \brief True if the game just started, false otherwise.
    * Allows to apply the first
    * card effect placed on the table only at the beginning. */
    vector<Player*> players;  /**< \brief A vector that represents all the players in the current game. */
    CardBundle deck;  /**< \brief A CardBundle that represents the deck of the game. */
    CardBundle table; /**< \brief A CardBundle that represents the table of the game. */

private:

};

#endif //CARD_GAMES_GAME_H
