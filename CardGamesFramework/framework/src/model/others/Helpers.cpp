//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 22/12/2020.
//

#include "Helpers.h"

bool Helpers::hasVectorTwoCandidates(CardBundle &v, int sum) {
    int l, r;
    v.sort();
    l = 0;
    r = v.size() - 1;
    while (l < r) {
        if (v.getCardFromPos(l).getValue() + v.getCardFromPos(r).getValue() == sum)
            return true;
        else if (v.getCardFromPos(l).getValue() + v.getCardFromPos(r).getValue() < sum)
            l++;
        else
            r--;
    }
    return false;
}

std::tuple<int,int,int> Helpers::getVectorTwoCandidates(CardBundle &v, Player *p, int index) {
    int l, r;
    v.sort();
    l = 0;
    r = v.size() - 1;
    int sum = p->getCardFromPos(index).getValue();
    while (l < r) {
        if (v.getCardFromPos(l).getValue() + v.getCardFromPos(r).getValue() == sum)
            return std::make_tuple(index, r, l);
        else if (v.getCardFromPos(l).getValue() + v.getCardFromPos(r).getValue() < sum)
            l++;
        else
            r--;
    }
    return std::make_tuple(-1, -1, -1);
}

bool Helpers::contains(CardBundle &cb, int n) {
    for(long unsigned int i=0;i<cb.size();i++){
        if (cb.getCardFromPos(i).getValue() == n)
            return true;
    }
    return false;
}


Player* Helpers::hasMoreCards(vector<Player*> players) {
    int n = 0;
    int player = 0;
    for(long unsigned int i=0;i<players.size();i++){
        if(players[i]->getStackSize() > (long unsigned int) n) {
            n = players[i]->getStackSize();
            player = i;
        }
    }
    return players[player];
}

Player* Helpers::hasMoreOfSuit(vector<Player*> players, Card::Suit suit) {
    std::map<Player*, int> map;

    for(auto & player : players)
        map.insert({player, 0});

    for(auto & player : players){
        for(long unsigned int j=0;j<player->getStackSize();j++){
            if(player->getStack().getCardFromPos(j).getSuit() == suit)
                map[player]++;
        }
    }

    int max = 0;
    Player *winner;
    for(auto & it : map){
        if(it.second > max){
            winner = it.first;
            max = it.second;
        }
    }

    return winner;

}

Player* Helpers::hasCardWithSuit(vector<Player*> players, int value, Card::Suit suit) {
    for(auto & player : players){
        for(long unsigned int j=0;j<player->getStackSize();j++){
            if(player->getStack().getCardFromPos(j).getValue() == value &&
               player->getStack().getCardFromPos(j).getSuit() == suit)
                return player;
        }
    }
    return nullptr;
}


Player* Helpers::morePointsWithFourBestCards(vector<Player*> players) {
    std::map<Player*, int> map;

    for(auto &player : players){
        int points = 0;
        // We first sort the stack of cards according to their values
        player->getStack().sort();
        // If the stack size is less than or equal to 4 we count all cards points
        if(player->getStack().size() <=4){
            for(long unsigned int i=0;i<player->getStackSize();i++)
                points += player->getStack().getCardFromPos(i).getPoints();
            map.insert({player, points});
        }else {
            // Else we take the first four cards and count the points
            for (int i = 0; i < 4; i++)
                points += player->getStack().getCardFromPos(i).getPoints();
            map.insert({player, points});
        }
    }

    // We search for the maximum of points on the map and return the player associated
    int max = 0;
    Player *winner;
    for(auto & it : map){
        if(it.second > max){
            winner = it.first;
            max = it.second;
        }
    }

    return winner;
}

int Helpers::bestCardOfSuit(CardBundle &cb, Card::Suit suit) {

    std::map<Card, int> map;

    for(long unsigned int i=0;i<cb.size();i++) {
        if (cb.getCardFromPos(i).getSuit() == suit)
            map.insert({cb.getCardFromPos(i), i});
    }

    int max = -1;
    for(auto & it : map){
        if(it.second > max)
            max = it.second;
    }

    return max;

}

Player* Helpers::hasMorePoints(vector<Player*> players) {
    int max = 0;
    int player = 0;
    for(long unsigned int i=0;i<players.size(); i++){
        if(players[i]->getPoints() > max){
            max = players[i]->getPoints();
            player = i;
        }
    }
    return players[player];
}

