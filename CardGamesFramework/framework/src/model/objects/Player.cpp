//
// Created by Fidalgo Alex & Feaux De Lacroix Martin 29/11/2020.
//


#include "Player.h"
#include <utility>

Player::Player(string n, bool b) : name(std::move(n)) , isAI(b), points(0){}

ostream& operator <<(ostream &os, const Player &player){
    os << player.getName() << " YOU CURRENTLY HAVE " << player.hand.size() << " CARDS IN YOUR HAND.\n" << endl;
    cout << player.hand;
    return os;
}

int Player::getPoints() const {
    return points;
}

void Player::setPoints(int x) {
    points = x;
}

string Player::getName() const {
    return name;
}

CardBundle& Player::getHand() {
    return hand;
}

CardBundle& Player::getStack() {
    return stack;
}

void Player::drawCard(const Card &card){
    hand.addToBack(card);
}

Card Player::playCardFromPos(int pos){
    return hand.playCardPos(pos);
}

Card& Player::getCardFromPos(int pos){
    return hand.getCardFromPos(pos);
}

void Player::drawCardBegin(const Card &card) {
    hand.addBegin(card);
}

Card& Player::playBackCard(){
    return hand.drawFromBack();
}

void Player::addToStack(const Card& card){
    stack.addToBack(card);
}

bool Player::isHandEmpty() {
    return hand.isEmpty();
}

long unsigned int Player::getHandSize(){
    return hand.size();
}

long unsigned int Player::getStackSize() {
    return stack.size();
}

bool Player::isAIPlayer() const {
    return isAI;
}