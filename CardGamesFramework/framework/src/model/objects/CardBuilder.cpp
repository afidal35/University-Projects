//
// Created by root on 10/6/21.
//

#include "CardBuilder.h"

CardBuilder::CardBuilder(int value, Card::Color color) : card(value, color) {}

CardBuilder& CardBuilder::points(int points) {
    card.points = points;
    return *this;
}

CardBuilder& CardBuilder::suit(Card::Suit suit) {
    card.suit = suit;
    return *this;
}
