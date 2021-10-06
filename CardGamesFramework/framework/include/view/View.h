//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 29/11/2020.
//

#ifndef CARD_GAMES_VIEW_H
#define CARD_GAMES_VIEW_H

#include <string>
#include <iostream>
#include "Player.h"

using namespace std;

/*!
 * \brief The abstract view class representing the view of our model (game) view controller pattern.
 */
class View {
public:

    virtual ~View() = default;

    virtual void displayFromModel(const string& s) const = 0;
    virtual void displayPreamble() const = 0;
    virtual int askNumberOfPlayers(int min, int max, bool b) const = 0;
    virtual vector<string> askPlayersNames(int size, bool b) const = 0;
    virtual int askNumberOfPointsToWin() const = 0;
    virtual void displayFirstPlayer(Player *first) const = 0;

    virtual int askCardIndex(bool b, bool c, Player *p, CardBundle &table, CardBundle &deck) = 0;
    virtual void showCardPlayed(const Card &) const = 0;
    virtual void showFirstCard(const Card &) const = 0;

    virtual void announcePlayerTurn(Player *p) const = 0;
    virtual void announceWinnerOfTurn(Player *p) const = 0;
    virtual void announceForcedToDraw(Player *p) const = 0;
    virtual void announceForcedToSkipTurn(Player *p) const = 0;

    virtual void announceInvalidMove() const = 0;
    virtual void announceNoWinner() const = 0;

    virtual void announceWinnerOfRound(Player *winner, const vector<Player*> &players) const = 0;
    virtual void announceWinnerOfGame(Player *winner, const vector<Player*> &players) const = 0;

    virtual int announceCardEffect(int n, bool isAI) const = 0;

    virtual bool playAgain() const = 0;
    virtual void playNextTurn() const = 0;
    virtual void playNextRound() const = 0;

    virtual bool wantToCut(Player *p, CardBundle &) const = 0;
    virtual tuple<int,int,int> askForCut(Player *p, CardBundle &) const = 0;
    virtual void showCutPlayed(Player *p, CardBundle &table, std::tuple<int,int,int> cut) const = 0;

};

#endif //CARD_GAMES_VIEW_H