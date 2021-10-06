//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 14/12/2020.
//

#ifndef CARD_GAMES_CARDBUNDLE_H
#define CARD_GAMES_CARDBUNDLE_H

#include <vector>
#include <algorithm>
#include <random>
#include <iostream>

#include "Card.h"

/*! \brief Represents a bundle of cards Card, "wrapper" over vector.
 */

class CardBundle {

public:

    friend ostream& operator<<(ostream &os, const CardBundle &cd);

    /*!
     * \brief shuffles the vector with algorithm function.
     */
    void shuffleDeck();

    /*!
     * Add a card to the beginning of the vector.
     * @param card
     */
    void addBegin(const Card &card);

    /*!
     * \brief Card at index pos.
     * @param pos the index of the card.
     * @return the card at the position pos.
     */
    Card& getCardFromPos(int pos);

    /*!
     * \brief Erase and return card at position pos.
     * @param pos
     * @return the erased card.
     */
    Card playCardPos(int pos);

    /*!
     * @return the last card of the vector after erasing it.
     */
    Card& drawFromBack();

    /*!
     *
     * @return the last card of the vector.
     */
    Card& getBackCard();

    /*!
     * \brief Add a card to the end of the vector.
     * @param card
     */
    void addToBack(const Card &card);

    /*!
     * Check if the vector is empty.
     * @return true if the vector is empty, false otherwise.
     */
    bool isEmpty() const;

    /*!
     * \brief get the vector size.
     * @return an integer representing the size of the vector.
     */
    long unsigned int size() const;

    /*!
     * \brief Sorts the vector based on Card values.
     */
    void sort();

    /*!
     * \brief Clears the vector.
     */
    void clear();

private:
    vector<Card> bundle; /**< \brief A vector that represents a bundle of Card. */

};

#endif //CARD_GAMES_CARDBUNDLE_H
