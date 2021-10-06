//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 15/12/2020.
//

#include "Scopa.h"

string Scopa::getGameHeader() const {
    string s =
            "====================================================================================================================================================\n"
            "\n\n\n\n"

            "|      SSSSSSSSSSSSSSSSSSSSSSS        CCCCCCCCCCCCCCCCCCCC         OOOOOOOOOOOOOOOOO         PPPPPPPPPPPPPPPPPPPP        AAAAAAAAAAAAAAAAAAAA      |\n"
            "|      SSSSSSSSSSSSSSSSSSSSSSS        CCCCCCCCCCCCCCCCCCCC        OOOOOOOOOOOOOOOOOOO        PPPPPPPPPPPPPPPPPPPP        AAAAAAAAAAAAAAAAAAAA      |\n"
            "|      SSSSS                          CCCCC                       OOOOO         OOOOO        PPPPP          PPPPP        AAAAA          AAAAA      |\n"
            "|      SSSSS                          CCCCC                       OOOOO         OOOOO        PPPPP          PPPPP        AAAAA          AAAAA      |\n"
            "|      SSSSSSSSSSSSSSSSSSSSSSS        CCCCC                       OOOOO         OOOOO        PPPPPPPPPPPPPPPPPPPP        AAAAAAAAAAAAAAAAAAAA      |\n"
            "|                        SSSSS        CCCCC                       OOOOO         OOOOO        PPPPPPPPPPPPPPPPPPPP        AAAAAAAAAAAAAAAAAAAA      |\n"
            "|                        SSSSS        CCCCC                       OOOOO         OOOOO        PPPPP                       AAAAA          AAAAA      |\n"
            "|      SSSSSSSSSSSSSSSSSSSSSSS        CCCCCCCCCCCCCCCCCCCC        OOOOOOOOOOOOOOOOOOO        PPPPP                       AAAAA          AAAAA      |\n"
            "|      SSSSSSSSSSSSSSSSSSSSSSS        CCCCCCCCCCCCCCCCCCCC         OOOOOOOOOOOOOOOOO         PPPPP                       AAAAA          AAAAA      |\n"

            "\n\n\n\n\n"
            "====================================================================================================================================================\n";

    return s;
}

string Scopa::getRules() const {
    string s =
            "==============================================================================================\n"
            "|                                      scopa rules                                           |\n"
            "|                                                                                            |\n"
            "|                          You can play as follow :                                          |\n"
            "|      - Play a card which is the [sum] of [two cards] of the [table] (example :             |\n"
            "|          - You play a [5 of Heart], you can take a [3 of Spade] and a [2 of Diamond].      |\n"
            "|      - Play a card which isn't the sum of two other cards from the table.                  |\n"
            "|                                                                                            |\n"
            "==============================================================================================\n";

    return s;
}

int Scopa::getMaxPlayers() const {
    return 6;
}

void Scopa::createDeck() {

    static int SUITS = 4;

    for(int i=0;i<SUITS;i++){
        int color = i%2 == 0 ? 0 : 4;
        for(int j=2;j<6;j++)
            deck.addToBack(Card::build(j, Card::Color(color))
                                   .suit(Card::Suit(i))
                                   .points(i+10));
        deck.addToBack(Card::build(1, Card::Color(color))
                               .suit(Card::Suit(i))
                               .points(16));
        for(int k=6; k<8; k++)
            deck.addToBack(Card::build(k, Card::Color(color))
                                   .suit(Card::Suit(i))
                                   .points(k*3));
        for (int k = 11; k < 14; k++)
            deck.addToBack(Card::build(k, Card::Color(color))
                                   .suit(Card::Suit(i))
                                   .points(10));
    }
}

bool Scopa::evaluationTurnCondition() const {
    for(auto &player : players) {
        if (!player->isHandEmpty())
            return false;
    }
    return true;
}

Player* Scopa::evaluateTurn() {
    for (auto &player : players) {
        for(int i=0;i<3;i++) {
            if (!deck.isEmpty())
                player->drawCard(deck.drawFromBack());
        }
    }
    return nullptr;
}

Player* Scopa::evaluateWinnerOfRound() const {
    if(deck.isEmpty()) {

        std::map<Player *, int> tmpPoints;

        for(auto const player : players){
            tmpPoints.insert({player, 0});
        }

        Player *one = Helpers::hasMoreCards(players);
        Player *two = Helpers::hasMoreOfSuit(players, Card::Suit::Diamond);
        Player *tree = Helpers::hasCardWithSuit(players, 7, Card::Suit::Diamond);
        Player *four = Helpers::morePointsWithFourBestCards(players);

        for(auto it = tmpPoints.cbegin(); it != tmpPoints.cend(); ++it){
            if(one == it->first)
                tmpPoints[it->first]++;
            if(two == it->first)
                tmpPoints[it->first]++;
            if(tree == it->first)
                tmpPoints[it->first]++;
            if(four == it->first)
                tmpPoints[it->first]++;
        }

        int max = 0;
        Player *winner;
        for(auto & it : tmpPoints){
            if(it.second > max){
                winner = it.first;
                max = it.second;
            }
        }
        return winner;
    }

    return nullptr;

}

bool Scopa::showTable() const {
    return true;
}

std::pair<int,int> Scopa::howToDeal() const {
    return std::make_pair(3, 4);
}

bool Scopa::isAbleToCut() {
    for(long unsigned int i=0;i<getCurrentPlayer()->getHandSize();i++){
        if(Helpers::hasVectorTwoCandidates(table, getCurrentPlayer()->getCardFromPos(i).getValue())
           || Helpers::contains(table, getCurrentPlayer()->getCardFromPos(i).getValue()))
            return true;
    }
    return false;
}

bool Scopa::isCutValid(std::tuple<int,int,int> move) {
    Card played = getCurrentPlayer()->getCardFromPos(std::get<0>(move));
    Card take1 = getTable().getCardFromPos(std::get<1>(move));

    int take2Value = 0;
    if(std::get<2>(move) != -1)
        take2Value = getTable().getCardFromPos(std::get<2>(move)).getValue();

    if(std::get<2>(move) == -1)
        return played == take1;
    else
        return played.getValue() == take1.getValue() + take2Value;

}

std::tuple<int,int,int> Scopa::getValidCut() {
    for(long unsigned int i=0;i<getCurrentPlayer()->getHandSize();i++) {
        if(Helpers::hasVectorTwoCandidates(table, getCurrentPlayer()->getCardFromPos(i).getValue())){
            return Helpers::getVectorTwoCandidates(table, getCurrentPlayer(), i);
        }else if (Helpers::contains(table, getCurrentPlayer()->getCardFromPos(i).getValue())){
            int tableIndex = 0;
            for(long unsigned int j=0;j<table.size();j++){
                if(table.getCardFromPos(j).getValue() == getCurrentPlayer()->getCardFromPos(i).getValue())
                    tableIndex = j;
            }
            return std::make_tuple(i, tableIndex, -1);
        }
    }
    return std::make_tuple(-1, -1, -1);
}

bool Scopa::isMoveValid(const Card &card) {
    /*if(Helpers::hasVectorTwoCandidates(table, card.getValue())
       || Helpers::contains(table, card.getValue()))
        return false;*/
    return true;
}

int Scopa::getCurrentPlayerValidMove() {
    for(long unsigned int i=0;i<getCurrentPlayer()->getHandSize();i++){
        if(isMoveValid(getCurrentPlayer()->getCardFromPos(i)))
            return i;
    }
    return -1;
}
