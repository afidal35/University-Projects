//
// Created by root on 10/6/21.
//

#ifndef CARD_GAMES_CARDBUILDER_H
#define CARD_GAMES_CARDBUILDER_H

#include "Card.h"

class CardBuilder {

    Card card;

public:
    CardBuilder (int value, Card::Color color);

    operator Card() const { return move(card); }

    CardBuilder& suit(Card::Suit suit);
    CardBuilder& points(int points);

};


#endif //CARD_GAMES_CARDBUILDER_H
