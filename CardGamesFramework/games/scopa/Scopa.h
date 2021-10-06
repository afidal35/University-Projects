//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 15/12/2020.
//

#ifndef SCOPA_H
#define SCOPA_H

#include "Game.h"

class Scopa : public Game {

public:

    string getGameHeader() const override;
    string getRules() const override;
    int getMaxPlayers() const override;
    void createDeck() override;
    std::pair<int,int> howToDeal() const override;
    bool isMoveValid(const Card &) override;
    int getCurrentPlayerValidMove() override;
    Player * evaluateTurn() override;
    Player* evaluateWinnerOfRound() const override;

    bool showTable() const override;
    bool isCutValid(std::tuple<int,int,int>) override;
    std::tuple<int,int,int> getValidCut() override;
    bool isAbleToCut() override;

    bool evaluationTurnCondition() const override;

};

#endif //SCOPA_H
