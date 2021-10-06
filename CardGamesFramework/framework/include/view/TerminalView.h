//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 10/12/2020.
//

#ifndef CARD_GAMES_TERMINALVIEW_H
#define CARD_GAMES_TERMINALVIEW_H

#include "View.h"
#include "invalidIndex.h"

/*!
 * \brief A Terminal view provided by our framework.
 */
class TerminalView : public View {

public:

    int askForIntegerAnswer(const string &question, int min, int max) const;

    /*!
     * \brief Show the game name.
     * @param s the string to be displayed.
     * Used in the controller, passing the game header defined in the model to this method.
     */
    void displayFromModel(const string& s) const override;

    /*!
     * \brief Display a brief text to announce the game.
     */
    void displayPreamble()const override;

    /*!
     * \brief Asks the number of players for the game to the user.
     * @param min the minimum of players.
     * @param max the maximums of players.
     * @param b a boolean, true for Humans and false for AIs players.
     * @return an integer that represents a number.
     */
    int askNumberOfPlayers(int min, int max, bool b) const override;

    /*!
    * \brief Asks the players names to the user.
    * @param min min the minimum of players.
    * @param max the maximums of players.
    * @param b a boolean, true for Humans and false for AIs players.
    * @return a vector of string containing the names.
    */
    vector<string> askPlayersNames(int size, bool b) const override;

    /*!
     * \brief Asks the number of points to win to the user.
     * @return an integer that represents the points.
     */
    int askNumberOfPointsToWin() const override;

    void displayFirstPlayer(Player *first) const override;

    /*!
     * \brief Asks a card to play.
     * @param b a boolean, true is the card is choosable for the game , false otherwise.
     * @param c a boolean, true if we show the table to the player, false if we only show him the last card played.
     * @param p a Player.
     * @param table a CardBundle that represents the table of the game.
     * @param deck a CardBundle that represents the deck of the game.
     * @return an integer that represents a card index in the player's hand.
     */
    int askCardIndex(bool b, bool c, Player *p, CardBundle &table, CardBundle &deck) override;

    /*!
     * \brief Displays a card.
     * @param card the card to display.
     */
    void showCard(const Card &card, const string &message) const;

    void showCardPlayed(const Card &card) const override;
    void showFirstCard(const Card &card) const override;

    /*!
     * \brief displays a message with information relative to a Player.
     * @param p a Player.
     * @param message_1 a string representing a message.
     * @param message_2 a string representing a message.
     */
    void announceWithPlayer(Player *p, const string &message_1, const string &message_2) const;

    void announcePlayerTurn(Player *p) const override;
    void announceWinnerOfTurn(Player *p) const override;
    void announceForcedToDraw(Player *p) const override;
    void announceForcedToSkipTurn(Player *p) const override;

    /*!
     * \brief displays a message.
     * @param message
     */
    void announce(const string &message) const;

    void announceInvalidMove() const override;
    void announceNoWinner() const override;

    /*!
    * \brief Asks the users if he wants to play more.
    * @return a boolean, true for yes, false otherwise.
    */
    bool playAgain() const override;

    /*!
     * \brief waits for the player to press enter to play the next turn.
     */
    void playNextTurn() const override;

    /*!
    * \brief waits for the player to press enter to play the next round.
    */
    void playNextRound() const override;


    /*!
     * \brief Announce the winner of a round.
     * @param winner a Player representing the winner of the round.
     * @param players a vector of Player representing the players of the round.
     * Shows the point of the winner as well as the points of the other players.
     */
    void announceWinnerOfRound(Player *winner, const vector<Player*> &players) const override;

    /*!
    * \brief Announce the winner of a game.
    * @param winner a Player representing the winner of the game.
    * @param players a vector of Player representing the players of the game.
    * Shows the point of the winner as well as the points of the other players.
    */
    void announceWinnerOfGame(Player *winner, const vector<Player*> &players) const override;

    /*!
     * \brief Displays a card effect and interact with the player if needed.
     * @param n an integer that represents the card effect.
     * @param isAI a boolean, true if the player is a Bot , false otherwise.
     * @return -1 if the effect does not imply a choice, a card index of the player's hand otherwise.
     */
    int announceCardEffect(int n, bool isAI) const override;

    /*!
    * \brief Displays a cut.
    * @param p a Player.
    * @param table a CardBundle that represents the table of the game.
    * @param cut a tuple of 3 integers that represents the indexes of the cut.
    */
    void showCutPlayed(Player *p, CardBundle &table, std::tuple<int,int,int> cut) const override;

    /*!
     *  \brief Asks the player for a cut.
     * @param p a Player.
     * @param table a CardBundle that represents the table of the game.
     * @return a tuple of 3 values representing a cut (scopa).
     */
    tuple<int,int,int> askForCut(Player *p, CardBundle &table) const override;

    /*!
     * \brief Asks the user if he wants to cut or not.
     * @param p a Player.
     * @param table
     * @return a boolean, true if the Player wants to cut, false otherwise.
     */
    bool wantToCut(Player *p, CardBundle &table) const override;
};

#endif //CARD_GAMES_TERMINALVIEW_H
