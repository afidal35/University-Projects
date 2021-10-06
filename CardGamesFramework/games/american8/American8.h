//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 24/12/2020.
//

#ifndef AMERICAN8_H
#define AMERICAN8_H

#include "Game.h"

class American8 : public Game {

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
    void calculatePointsForWinner(Player *winner) override;
    bool needFirstCardEffect() const override;

};


#endif //AMERICAN8_H
