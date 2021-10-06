//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 07/12/2020.
//

#include "Uno.h"

string Uno::getGameHeader() const {
    string s =
            "============================================================================================\n"
            "\n\n\n\n"

            "|      UUUUU             UUUUU        NNNNNNNNNNNNNNNNNNNN         OOOOOOOOOOOOOOOOO       |\n"
            "|      UUUUU             UUUUU        NNNNNNNNNNNNNNNNNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUU             UUUUU        NNNNN          NNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUU             UUUUU        NNNNN          NNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUU             UUUUU        NNNNN          NNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUU             UUUUU        NNNNN          NNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUUUUUUUUUUUUUUUUUUUU        NNNNN          NNNNN        OOOOO         OOOOO      |\n"
            "|      UUUUUUUUUUUUUUUUUUUUUUU        NNNNN          NNNNN         OOOOOOOOOOOOOOOOO       |\n"

            "\n\n\n\n\n"
            "============================================================================================\n";

    return s;
}

string Uno::getRules() const {
    string s =
            "==============================================================================================\n"
            "|                                   uno rules                                                |\n"
            "|                                                                                            |\n"
            "|                          You can play as follow :                                          |\n"
            "|      - Play a card which has the same [Color], [Value] or [Symbol] than the previous one.  |\n"
            "|      - Play a special card as [Joker] or [Draw 4 Wild].                                    |\n"
            "|                                                                                            |\n"
            "|      If these two options aren't available you will be forced to draw a card and re-apply  |\n"
            "|      the above rules.                                                                      |\n"
            "|      If you still can't play after drawing, your turn will be skipped.                     |\n"
            "==============================================================================================\n";

    return s;
}

bool Uno::equalityComparison(const Card &first, const Card &second) {
    return (first.getValue() == second.getValue() ||
            first.getColor() == second.getColor() ||
            first.getColor() == Card::Color::None ||
            second.getColor() == Card::Color::None);
}

int Uno::getMaxPlayers() const {
    return 10;
}

int Uno::chooseColor() {
    return 7;
}

bool Uno::needFirstCardEffect() const {
    return true;
}

int Uno::getCardEffect(const Card &card) {
    switch (card.getValue()) {
        case 16:
            return drawTwo();
        case 17:
            return skipTurn();
        case 18:
            return reverse();
        case 19:
            drawFour();
            nextTurn();
            return chooseColor();
        case 15:
            nextTurn();
            return chooseColor();
        default:
            return nextTurn();
    }
}

void Uno::createDeck() {
    static int COLORS = 4;
    for(int i=0;i<COLORS;i++){
        deck.addToBack(Card::build(0, Card::Color(i))
                               .points(0));
        for(int j=0;j<2;j++) {
            for (int k = 1; k < 10; k++)
                deck.addToBack(Card::build(k, Card::Color(i))
                                       .points(k));
        }
    }

    // Adding [2] +2 cards, [2] skip cards and [2] reverse cards for each colors
    for(int x = 16; x<19;x++) {
        for (int y = 0; y < COLORS; y++) {
            for (int z = 0; z < 2; z++)
                deck.addToBack(Card::build(x, Card::Color(y))
                                       .points(20));
        }
    }

    // Adding [4] jokers and [4] +4 wild cards
    for(int z=0;z<COLORS;z++) {
        deck.addToBack(Card::build(19, Card::Color::None)
                               .points(50));
        deck.addToBack(Card::build(15, Card::Color::None)
                               .points(50));
    }

}

void Uno::calculatePointsForWinner(Player *winner) {
    int points = 0;
    for (const auto &p : players) {
        for (long unsigned int i = 0; i < p->getHandSize(); i++)
            points += p->getCardFromPos(i).getPoints();
    }
    winner->setPoints(winner->getPoints() + points);
}

std::pair<int,int> Uno::howToDeal() const {
    return std::make_pair(7, 1);
}

bool Uno::isMoveValid(const Card &card) {
    if (equalityComparison(getLastCardPlayed(), card))
        return true;
    return false;
}

int Uno::getCurrentPlayerValidMove() {
    for(long unsigned int i=0;i<getCurrentPlayer()->getHandSize();i++){
        if (isMoveValid(getCurrentPlayer()->getCardFromPos(i)))
            return i;
    }
    return -1;
}