//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 12/12/2020.
//

#include "Card.h"
#include "CardBuilder.h"

Card::Card(int value, Color color) : value(value), color(color) {}

CardBuilder Card::build(int value, Color color) {
    return CardBuilder(value, color);
}

bool Card::operator<(const Card &c) const{
    return value < c.value;
}

bool Card::operator>(const Card &c) const{
    return value > c.value;
}

bool Card::operator==(const Card &c) const{
    return value == c.value;
}

int Card::operator +(const Card &card) const {
    return points + card.points;
}

int Card::getValue() const {
    return value;
}

Card::Color Card::getColor() const {
    return color;
}

Card::Suit Card::getSuit() const {
    return suit;
}

int Card::getPoints() const {
    return points;
}

void Card::setColor(int c) {
    color = Card::Color(c);
}

void Card::setColor(Color c) {
    color = c;
}

ostream& operator <<(ostream& os, const Card& card) {
    string value, color, suit;

    switch (card.value) {
        case 11:
            value += "J";
            break;
        case 12:
            value += "Q";
            break;
        case 13:
            value += "K";
            break;
        case 14:
            value += "AS";
            break;
        case 15 :
            value += "JOKER";
            break;
        case 16 :
            value += "DRAW2";
            break;
        case 17 :
            value += "SKIP";
            break;
        case 18 :
            value += "REVERSE";
            break;
        case 19 :
            value += "WILD";
            break;
        default:
            value += to_string(card.value);
            break;
    }

    switch (card.color) {
        case Card::Color::Red:
            color += "RED";
            break;
        case Card::Color::Black:
            color += "BLACK";
            break;
        case Card::Color::Green:
            color += "GREEN";
            break;
        case Card::Color::Blue:
            color += "BLUE";
            break;
        case Card::Color::Yellow:
            color += "YELLOW";
            break;
        case Card::Color::None:
            color += "NONE";
            break;
        default:
            break;
    }

    switch (card.suit) {
        case Card::Suit::Club:
            suit += "CLUB";
            break;
        case Card::Suit::Diamond:
            suit += "DIAMOND";
            break;
        case Card::Suit::Heart:
            suit += "HEART";
            break;
        case Card::Suit::Spade:
            suit += "SPADE";
            break;
        case Card::Suit::None:
            suit += "";
            break;
        default:
            break;
    }

    string prefix;
    if (!suit.empty())
        prefix += " Of ";

    return os << "[ " << value << prefix << suit << " | " << color << " ]" << endl;
}