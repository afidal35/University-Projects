//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 26/12/2020.
//

#include "Briscola.h"

string Briscola::getGameHeader() const {
    string s =
            "====================================================================================================================================================\n"
            "\n\n\n\n"

            "|      BBBBBBBBBBBBBBBBBBBB            RRRRRRRRRRRRRRRRR           IIIIIIIIIIIIIIIIIII         SSSSSSSSSSSSSSSSSSS         CCCCCCCCCCCCCCCCCCC     |\n"
            "|      BBBBBBBBBBBBBBBBBBBBBBB        RRRRRRRRRRRRRRRRRRR          IIIIIIIIIIIIIIIIIII         SSSSSSSSSSSSSSSSSSS         CCCCCCCCCCCCCCCCCCC     |\n"
            "|      BBBBB             BBBBB        RRRRR          RRRR                  III                 SSSS                        CCCC                    |\n"
            "|      BBBBB             BBBBB        RRRRR          RRRR                  III                 SSSS                        CCCC                    |\n"
            "|      BBBBBBBBBBBBBBBBBBBBBBB        RRRRRRRRRRRRRRRRRRR                  III                 SSSSSSSSSSSSSSSSSSS         CCCC                    |\n"
            "|      BBBBB             BBBBB        RRRRR        RRRRRR                  III                                SSSS         CCCC                    |\n"
            "|      BBBBB             BBBBB        RRRRR         RRRRRR                 III                                SSSS         CCCC                    |\n"
            "|      BBBBBBBBBBBBBBBBBBBBBBB        RRRRR          RRRRR         IIIIIIIIIIIIIIIIIII         SSSSSSSSSSSSSSSSSSS         CCCCCCCCCCCCCCCCCCC     |\n"
            "|      BBBBBBBBBBBBBBBBBBBB           RRRRR          RRRRR         IIIIIIIIIIIIIIIIIII         SSSSSSSSSSSSSSSSSSS         CCCCCCCCCCCCCCCCCCC     |\n"

            "\n\n\n\n\n"
            "====================================================================================================================================================\n";

    return s;
}

string Briscola::getRules() const {
    string s =
    "==============================================================================================\n"
    "|                                      briscola rules                                        |\n"
    "|                                                                                            |\n"
    "|                          You can play as follow :                                          |\n"
    "|      - There is no condition for playing a card, play which one you want.                  |\n"
    "|      - The player who plays the card with the same [Suit] and the highest value wins       |\n"
    "         the round.                                                                          |\n"
    "|                                                                                            |\n"
    "|      When all players played once, they draw one card to always have 3 in their hand.      |\n"
    "==============================================================================================\n";

    return s;
}

int Briscola::getMaxPlayers() const {
    return 5;
}

bool Briscola::showTable() const {
    return true;
}

void Briscola::createDeck() {
    static int SUITS = 4;

    for(int i=0;i<SUITS;i++){
        int color = i % 2 == 0 ? 0 : 4;
        for(int j=1;j<8;j++){
            if(j==1)
                deck.addToBack(Card::build(j, Card::Color(color))
                                    .suit(Card::Suit(i))
                                    .points(11));
            else if(j==3)
                deck.addToBack(Card::build(j, Card::Color(color))
                                       .suit(Card::Suit(i))
                                       .points(10));
            else
                deck.addToBack(Card::build(j, Card::Color(color))
                                       .suit(Card::Suit(i))
                                       .points(0));
        }
        for (int k = 11; k < 14; k++)
            deck.addToBack(Card::build(k, Card::Color(color))
                                   .suit(Card::Suit(i))
                                   .points(k-9));
    }
}

bool Briscola::evaluationTurnCondition() const {
    return getPlayerIndex() != 0 && (getPlayerIndex() % (getNumberOfPlayers() - 1) == 0);
}

Player *Briscola::evaluateTurn() {

    // Make each players draw one card, so they have always 3 cards at the beginning of a turn
    for(auto const player : players){
        if(!deck.isEmpty())
            player->drawCard(deck.drawFromBack());
    }

    int max=0;
    int player = 0;
    for(long unsigned int i=1;i<table.size();i++){
        if(table.getCardFromPos(i).getPoints() > max){
            max = table.getCardFromPos(i).getPoints();
            player = i - 1;
        }
    }
    CardBundle playedCards;
    while(table.size() != 1){
        playedCards.addToBack(table.drawFromBack());
    }

    int n = Helpers::bestCardOfSuit(playedCards, table.getCardFromPos(0).getSuit());

    Player *winner;

    if (n==-1)
        winner = players[player];
    else
        winner = players[n];

    // Add the cards on the table to the winner's stack

    for(long unsigned int i=0;i<playedCards.size();i++)
        winner->addToStack(playedCards.drawFromBack());

    return winner;
}

Player *Briscola::evaluateWinnerOfRound() const {

    for(auto const player : players){
        if (!player->isHandEmpty())
            return nullptr;
    }

    vector<int> tmpPoints;

    for(long unsigned int i=0;i<players.size();i++)
        tmpPoints.push_back(0);

    for(long unsigned int i=0;i<players.size();i++){
        for(long unsigned int j=0;j<players[i]->getStack().size();j++)
            tmpPoints[i] += players[i]->getStack().getCardFromPos(j).getPoints();
    }

    int max = std::max_element(tmpPoints.begin(),tmpPoints.end()) - tmpPoints.begin();

    return players[max];
}

std::pair<int, int> Briscola::howToDeal() const {
    return std::make_pair(3, 1);
}

bool Briscola::isMoveValid(const Card &) {
    return true;
}

int Briscola::getCurrentPlayerValidMove() {
    return rand() % getCurrentPlayer()->getHandSize();
}
