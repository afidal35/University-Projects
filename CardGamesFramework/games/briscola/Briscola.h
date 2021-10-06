//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 26/12/2020.
//

#ifndef CARD_GAMES_BRISCOLA_H
#define CARD_GAMES_BRISCOLA_H

#include "Game.h"

class Briscola : public Game {

public:
    string getGameHeader() const override;
    string getRules() const override;
    int getMaxPlayers() const override;
    void createDeck() override;
    std::pair<int,int> howToDeal() const override;
    bool isMoveValid(const Card &) override;
    int getCurrentPlayerValidMove() override;

    bool showTable() const override;
    Player * evaluateTurn() override;
    Player* evaluateWinnerOfRound() const override;
    bool evaluationTurnCondition() const override;

};

#endif //CARD_GAMES_BRISCOLA_H
