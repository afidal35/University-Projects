//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 07/12/2020.
//

#ifndef UNO_H
#define UNO_H

#include "Game.h"

class Uno : public Game {

public:

    string getGameHeader() const override;
    string getRules() const override;
    int getMaxPlayers() const override;
    void createDeck() override;
    std::pair<int,int> howToDeal() const override;
    bool isMoveValid(const Card &) override;
    int getCurrentPlayerValidMove() override;
    static bool equalityComparison(const Card &, const Card &) ;

    int chooseColor() override;
    int getCardEffect(const Card&) override;
    bool needFirstCardEffect() const override;
    void calculatePointsForWinner(Player *winner) override;
};

#endif //UNO_H
