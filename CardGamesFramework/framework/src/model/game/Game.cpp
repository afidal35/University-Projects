//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 07/12/2020.
//

#include "Game.h"

Game::~Game(){
    for(auto player : players)
        delete player;
    players.clear();
}

void Game::clearGame() {

    if(!players.empty()) {
        for (auto player : players)
            delete player;
        players.clear();
    }

    deck.clear();
    table.clear();
    playerIndex = 0;
    numberOfPlayers = 0;
    numberOfPointsToWin = 0;
    numberOfRoundsPlayed = 0;
    start = true;

}

void Game::resetGame() {

    for(const auto &player : players){
        player->getHand().clear();
        player->getStack().clear();
    }

    deck.clear();
    table.clear();
    playerIndex = 0;
    numberOfRoundsPlayed = 0;
    start = true;

}

void Game::fillDeckWithTable() {
    for(long unsigned int i=0;i<table.size() - 1;i++)
        deck.addToBack(table.playCardPos(i));
}

int Game::getPlayerIndex() const {
    return playerIndex;
}

void Game::setPlayerIndex(int x){
    playerIndex = x;
}

void Game::setNumberOfPlayers(int x) {
    numberOfPlayers = x;
}

int Game::getNumberOfPlayers() const {
    return numberOfPlayers;
}

void Game::setNumberOfPointsToWin(int points) {
    numberOfPointsToWin = points;
}

int Game::getNumberOfPointsToWin() const {
    return numberOfPointsToWin;
}

void Game::setNumberOfRoundsPlayed(int round){
    numberOfRoundsPlayed = round;
}

int Game::getNumberOfRoundsPlayed() const {
    return numberOfRoundsPlayed;
}

Card Game::getLastCardPlayed(){
    return table.getBackCard();
}

Player* Game::getCurrentPlayer(){
    return players[playerIndex];
}

Card Game::playerPlayCardFromPos(int n){
    return getCurrentPlayer()->playCardFromPos(n);
}

void Game::addToCurrentPlayerStack(const Card& card){
    getCurrentPlayer()->addToStack(card);
}

void Game::addToTable(const Card &card){
    table.addToBack(card);
}

Card& Game::getCardFromTable(int pos) {
    return table.getCardFromPos(pos);
}

Card Game::removeCardFromTable(int pos) {
    return table.playCardPos(pos);
}

bool Game::showTable() const {
    return false;
}

CardBundle& Game::getTable() {
    return table;
}

CardBundle& Game::getDeck() {
    return deck;
}

void Game::makeCurrentPlayerDraw(int n){
    for(int i=0;i<n;i++) {
        if(deck.isEmpty())
            fillDeckWithTable();
        getCurrentPlayer()->drawCard(deck.drawFromBack());
    }
}

int Game::callLastCardEffect() {
    return getCardEffect(table.getBackCard());
}

bool Game::needFirstCardEffect() const {
    return false;
}

void Game::dealCardsAndSetTable() {
    auto t = howToDeal();

    for(int i=0;i<t.first;i++){
        for(auto & player : players)
            player->drawCard(deck.drawFromBack());
    }

    for(int j=0; j<t.second;j++)
        table.addToBack(deck.drawFromBack());

}

int Game::nextTurn(){
    if(!start) {
        if (playerIndex == (numberOfPlayers - 1))
            playerIndex = 0;
        else
            playerIndex++;
    }
    return 1;
}

int Game::skipTurn() {
    if(playerIndex == (numberOfPlayers - 1))
        playerIndex = 1;
    else if (playerIndex == (numberOfPlayers - 2))
        playerIndex = 0;
    else
        playerIndex += 2;

    return 2;
}

int Game::drawTwo() {
    if(!start)
        nextTurn();
    makeCurrentPlayerDraw(2);
    return 3;
}

int Game::drawFour() {
    if(!start)
        nextTurn();
    makeCurrentPlayerDraw(4);
    return 4;
}

int Game::reverse() {
    std::reverse(players.begin(),players.end());

    if (playerIndex == 0)
        playerIndex = 0;
    else
        nextTurn();

    return 5;
}

int Game::chooseColor() {
    return -1;
}

string Game::getGameHeader() const {
    return "";
}

string Game::getRules() const {
    return "";
}

// For uno and 8 American there is also the condition when
// The deck is empty and both players can't play
// In this case we have to count the points and return the winner based on that.
Player* Game::evaluateWinnerOfRound() const{
    for (const auto &player : players) {
        if(player->isHandEmpty())
            return player;
    }
    return nullptr;
}

void Game::calculatePointsForWinner(Player *winner) {
    winner->setPoints(winner->getPoints() + 1);
}

Player* Game::evaluateWinnerOfGame() const {
    for(const auto &player : players){
        if(player->getPoints() >= getNumberOfPointsToWin())
            return player;
    }
    return nullptr;
}

void Game::setPlayers(vector<string> &names, bool b){
    for(const auto& name : names)
        players.push_back(new Player(name, b));
}

vector<Player*> Game::getPlayers() {
    return players;
}

int Game::getMinPlayers() const {
    return 2;
}

bool Game::isCardChoosable() const {
    return true;
}

int Game::getCardEffect(const Card& card) {
    return nextTurn();
}

bool Game::isMoveValid(const Card &card) {
    return true;
}

bool Game::isCutValid(std::tuple<int,int,int>) {
    return true;
}

bool Game::isAbleToCut() {
    return false;
}

std::tuple<int,int,int> Game::getValidCut() {
    return std::make_tuple(0, 0, 0);
}

void Game::shuffleDeck() {
    deck.shuffleDeck();
}

void Game::shufflePlayers() {
    shuffle(players.begin(), players.end(), random_device());
}

Player* Game::evaluateTurn() {
    return nullptr;
}

bool Game::evaluationTurnCondition() const {
    return false;
}

bool Game::getStart() const{
    return start;
}

void Game::setStart(bool b) {
    start = b;
}