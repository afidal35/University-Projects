//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 06/12/2020.
//

#ifndef WAR_H
#define WAR_H

#include "Game.h"

class War : public Game {

public:
    string getGameHeader() const override;
    string getRules() const override;
    int getMaxPlayers() const override;
    void createDeck() override;
    std::pair<int,int> howToDeal() const override;
    bool isCardChoosable() const override;
    Player * evaluateTurn() override;
    Player * evaluateWinnerOfRound() const override;
    int getCurrentPlayerValidMove() override;

    bool evaluationTurnCondition() const override;
};


#endif //WAR_H
