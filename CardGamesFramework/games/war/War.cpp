//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 06/12/2020.
//

#include "War.h"

string War::getGameHeader() const {
    string s =
            "============================================================================================\n"
            "\n\n\n\n"

            "|      WWWWW             WWWWW        AAAAAAAAAAAAAAAAAAAA         RRRRRRRRRRRRRRRRR       |\n"
            "|      WWWWW             WWWWW        AAAAAAAAAAAAAAAAAAAA        RRRRR         RRRRR      |\n"
            "|      WWWWW    WWWWW    WWWWW        AAAAA          AAAAA        RRRRR         RRRRR      |\n"
            "|      WWWWW    WWWWW    WWWWW        AAAAA          AAAAA        RRRRR         RRRRR      |\n"
            "|      WWWWW    WWWWW    WWWWW        AAAAAAAAAAAAAAAAAAAA        RRRRRRRRRRRRRRRRRRR      |\n"
            "|      WWWWW    WWWWW    WWWWW        AAAAA          AAAAA        RRRRR     RRRRRRRRR      |\n"
            "|      WWWWWWWWWWWWWWWWWWWWWWW        AAAAA          AAAAA        RRRRR         RRRRR      |\n"
            "|      WWWWWWWWWWWWWWWWWWWWWWW        AAAAA          AAAAA        RRRRR         RRRRR      |\n"

            "\n\n\n\n\n"
            "============================================================================================\n";

    return s;
}

string War::getRules() const {
    string s =
            "==============================================================================================\n"
            "|                                      war rules                                             |\n"
            "|                                                                                            |\n"
            "|                          This game is played as follow :                                   |\n"
            "|                  - Play a card.                                                            |\n"
            "|                  - Your opponent plays a card.                                             |\n"
            "|                  - The player who has the best card wins both and it continues.            |\n"
            "|                                                                                            |\n"
            "|                                                                                            |\n"
            "|     If those two cards are equals, this is WAR ! Both players put down 2 cards and re-apply|\n"
            "|     the above rules.                                                                       |\n"
            "|                          The first out of cards loses.                                     |\n"
            "==============================================================================================\n";

    return s;
}

int War::getMaxPlayers() const  {
    return 2;
}

bool War::isCardChoosable() const {
    return false;
}

void War::createDeck()  {
    static int VALUES = 14;
    static int SUITS = 4;

    for(int i=0;i<SUITS;i++) {
        for (int j = 2; j <= VALUES; j++)
            deck.addToBack(Card::build(j, Card::Color(i % 2 == 0 ? 0 : 4))
                                                .suit(Card::Suit(i))
                                                .points(50));
    }
    deck.addToBack(Card::build(15, Card::Color(0))
                           .suit(Card::Suit::None)
                           .points(50));
    deck.addToBack(Card::build(15, Card::Color(4))
                           .suit(Card::Suit::None)
                           .points(50));

}

std::pair<int,int> War::howToDeal() const {
    return std::make_pair(27, 0);
}

Player * War::evaluateTurn() {

    // Re-shuffle both hands when we reach a big amount of rounds played to end the game..
    if(getNumberOfRoundsPlayed() > 5000) {
        players[0]->getHand().shuffleDeck();
        players[1]->getHand().shuffleDeck();
    }

    Card &first = table.getCardFromPos(table.size()-2);
    Card &second = table.getBackCard();
    if (first > second) {
        for(long unsigned int i=0;i<table.size();i++)
            players[0]->drawCardBegin(table.getCardFromPos(i));
        table.clear();
        return players[0];
    } else if (first < second) {
        for(long unsigned int i=0;i<table.size();i++)
            players[1]->drawCardBegin(table.getCardFromPos(i));
        table.clear();
        return players[1];
    } else {

        // Make each players put two card down before playing the next card (faster...)
        for(int i=0;i<2;i++) {
            if(!players[0]->isHandEmpty())
                table.addToBack(players[0]->playBackCard());
            else
                return players[1];
        }

        for(int i=0;i<2;i++) {
            if(!players[1]->isHandEmpty())
                table.addToBack(players[1]->playBackCard());
            else
                return players[0];
        }

        return nullptr;
    }
}

bool War::evaluationTurnCondition() const {
    return getPlayerIndex() != 0 && (getPlayerIndex() % (getNumberOfPlayers() - 1) == 0);
}

int War::getCurrentPlayerValidMove() {
    return getCurrentPlayer()->getHandSize() - 1;
}

Player * War::evaluateWinnerOfRound() const {
    if(players[0]->isHandEmpty())
        return players[1];
    else if(players[1]->isHandEmpty())
        return players[0];
    else
        return nullptr;
}