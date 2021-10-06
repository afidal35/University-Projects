//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 22/12/2020.
//

#ifndef CARD_GAMES_HELPERS_H
#define CARD_GAMES_HELPERS_H

#include "CardBundle.h"
#include "Player.h"

#include <map>

/*!
 * \brief Some helpers functions to facilitate some little algorithms for certain games.
 * Mostly used for scopa where evaluations of points are a little more complex than in the other games.
 */
class Helpers {

public:

    /*! \brief Retrieve two cards that are the sum of the given value.
     * @param cb a bundle of cards.
     * @param sum the sum that needs to be found.
     * @return true when a stack of cards contains two elements which values are the sum of the given value.
     */
    static bool hasVectorTwoCandidates(CardBundle &cb, int sum);

    static std::tuple<int,int,int> getVectorTwoCandidates(CardBundle &cb, Player *p, int sum);

    /*!
     * \brief Check if a vector contain a card.
     * @param cb a bundle of cards
     * @param n an integer that represents the value to find.
     * @return true if the bundle contains the card of value n, false otherwise.
     */
    static bool contains(CardBundle &cb, int n);

    /*!
     * \brief Player who have the most cards.
     * @param players a vector of players.
     * @return the player who has the higher number of cards in his stack.
     */
    static Player* hasMoreCards(vector<Player*> players);

    /*!
     *
     * @param players a vector of players.
     * @param suit the suit to find.
     * @return the Player in the vector of players who has the higher number of cards with the suit given according to their stack.
     */
    static Player* hasMoreOfSuit(vector<Player*> players, Card::Suit suit);

    /*!
     *
     * @param players a vector of players.
     * @param value the card value.
     * @param suit the card suit.
     * @return the Player in the vector of players who has the higher number of cards with the suit given according to their stack, nullptr if no one is found.
     */
    static Player* hasCardWithSuit(vector<Player*> players, int value, Card::Suit suit);

    // Return the [Player] in the vector of [players] who has the more points based on the four bests cards according to their stack.
    static Player* morePointsWithFourBestCards(vector<Player*> players);

    /*!
     * \brief Who has more points.
     * @param players a vector of players.
     * @return  the Player in the vector of players who has the more points.
     */
    static Player* hasMorePoints(vector<Player*> players);

    /*!
     *
     * @param cb a bundle of cards.
     * @param suit the suit that we look for.
     * @return an integer representing the index of the best card with the suit suit according to the card point.
     */
    static int bestCardOfSuit(CardBundle &cb, Card::Suit suit);

};

#endif //CARD_GAMES_HELPERS_H
