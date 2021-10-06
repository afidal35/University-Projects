//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 29/11/2020.
//

#ifndef CARD_GAMES_PLAYER_H
#define CARD_GAMES_PLAYER_H

#include <vector>

#include "CardBundle.h"

/*!
 * \brief A class that represents a human or an AI player.
 */
class Player {

public:

    Player(string n, bool b);
    virtual ~Player() = default;

    friend ostream& operator <<(ostream&, const Player &);

    string getName() const;
    int getPoints() const;
    void setPoints(int x);

    void drawCard(const Card &card);
    Card playCardFromPos(int pos);
    Card& getCardFromPos(int pos);
    void drawCardBegin(const Card &card);
    Card& playBackCard();
    CardBundle& getHand();
    CardBundle& getStack();

    void addToStack(const Card& card);

    bool isHandEmpty();
    long unsigned int getHandSize();
    long unsigned int getStackSize();
    bool isAIPlayer() const;

private:
    CardBundle stack; /**< \brief A CardBundle that represents the stack of the player. */
    CardBundle hand; /**< \brief A CardBundle that represents the hand of the player. */
    string name; /**< \brief A string that represents the name of the player. */
    bool isAI; /**< \brief A boolean that represents either the player is a bot or not. */
    int points; /**< \brief An integer that represents the points of the player. */
};

#endif //CARD_GAMES_PLAYER_H
