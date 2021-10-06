//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 09/12/2020.
//

#include "American8.h"

string American8::getGameHeader() const {
    string s =
            "===============================================================================================\n"
            "\n\n\n\n"

            "|        8888888888888888888          AAAAAAAAAAAAAAAAAAAA        MMMMM           MMMMMM      |\n"
            "|      8888               8888        AAAAAAAAAAAAAAAAAAAA        MMMMMMM        MMMMMMM      |\n"
            "|      8888               8888        AAAAA          AAAAA        MMMMM MM      MM MMMMM      |\n"
            "|      8888               8888        AAAAA          AAAAA        MMMMM  MM    MM  MMMMM      |\n"
            "|      88888888888888888888888        AAAAAAAAAAAAAAAAAAAA        MMMMM   MMMMM    MMMMM      |\n"
            "|      8888               8888        AAAAA          AAAAA        MMMMM            MMMMM      |\n"
            "|      8888               8888        AAAAA          AAAAA        MMMMM            MMMMM      |\n"
            "|        8888888888888888888          AAAAA          AAAAA        MMMMM            MMMMM      |\n"

            "\n\n\n\n\n"
            "===============================================================================================\n";

    return s;
}

string American8::getRules() const {
    string s =
            "==============================================================================================\n"
            "|                                     American 8 rules                                       |\n"
            "|                                                                                            |\n"
            "|                          You can play as follow :                                          |\n"
            "|      - Play a card which has the same [Suit] or [Value] than the previous one.             |\n"
            "|      - Play a special card as [Joker], [8], [Jack], [2].                                   |\n"
            "|                                                                                            |\n"
            "|      If these two options aren't available you will be forced to draw a card and re-apply  |\n"
            "|      the above rules.                                                                      |\n"
            "|      If you still can't play after drawing, your turn will be skipped.                     |\n"
            "==============================================================================================\n";

    return s;
}

bool American8::equalityComparison(const Card &first, const Card &second) {

    return (first.getValue() == second.getValue() ||
            first.getSuit() == second.getSuit() ||
            first.getValue() == 2 ||
            second.getValue() == 2 ||
            first.getValue() == 8 ||
            second.getValue() == 8 ||
            first.getValue() == 11 ||
            second.getValue() == 11 ||
            first.getValue() == 14 ||
            second.getValue() == 14 ||
            first.getSuit() == Card::Suit::None ||
            second.getSuit() == Card::Suit::None);
}

int American8::getMaxPlayers() const {
    return 5;
}

int American8::chooseColor() {
    return 6;
}

bool American8::needFirstCardEffect() const {
    return true;
}

int American8::getCardEffect(const Card &card) {
    switch (card.getValue()) {
        case 2:
            return drawTwo();
        case 8:
            nextTurn();
            return chooseColor();
        case 11:
            return skipTurn();
        case 14:
            return reverse();
        case 15:
            return drawFour();
        default:
            return nextTurn();
    }
}

void American8::createDeck() {
    static int VALUES = 15;
    static int SUITS = 4;

    for(int i=0;i<SUITS;i++) {
        int color = i % 2 == 0 ? 0 : 4;
        for (int j = 2; j <= VALUES; j++) {
            if (j == 2 || j == 11 || j == 14) {
                deck.addToBack(Card::build(j, Card::Color(color))
                                       .suit(Card::Suit(i))
                                       .points(20));
            }else if (j == 8)
                deck.addToBack(Card::build(j, Card::Color(color))
                                .suit(Card::Suit(i))
                                .points(50));
            else if (j == 12 || j == 13)
                deck.addToBack(Card::build(j, Card::Color(color))
                                .suit(Card::Suit(i))
                                .points(10));
            else
                deck.addToBack(Card::build(j, Card::Color(color))
                                .suit(Card::Suit(i))
                                .points(j));
        }
    }
    deck.addToBack(Card::build(15, Card::Color(0))
                    .suit(Card::Suit::None)
                    .points(50));
    deck.addToBack(Card::build(15, Card::Color(4))
                    .suit(Card::Suit::None)
                    .points(50));

}

std::pair<int,int> American8::howToDeal() const {
    return std::make_pair(7, 1);
}

void American8::calculatePointsForWinner(Player *winner) {
    int points = 0;
    for (const auto &p : players) {
        for (long unsigned int i = 0; i < p->getHandSize(); i++) {
            points += p->getCardFromPos(i).getPoints();
        }
    }
    winner->setPoints(winner->getPoints() + points);
}


bool American8::isMoveValid(const Card &card) {
    if (equalityComparison(getLastCardPlayed(), card))
        return true;
    return false;
}

int American8::getCurrentPlayerValidMove() {
    for(long unsigned int i=0;i<getCurrentPlayer()->getHandSize();i++){
        if (isMoveValid(getCurrentPlayer()->getCardFromPos(i)))
            return i;
    }
    return -1;
}
