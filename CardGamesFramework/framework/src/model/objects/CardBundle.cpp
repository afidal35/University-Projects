//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 15/12/2020.
//

#include "CardBundle.h"

Card &CardBundle::drawFromBack() {
    Card& card = bundle.back();
    bundle.pop_back();
    return card;
}

void CardBundle::addToBack(const Card &card) {
    bundle.push_back(card);
}

Card & CardBundle::getBackCard() {
    return bundle.back();
}

void CardBundle::addBegin(const Card &card) {
    bundle.insert(bundle.begin(), card);
}

Card &CardBundle::getCardFromPos(int pos) {
    if(pos < 0 || (long unsigned int) pos > (bundle.size()-1))
        //throw invalidIndex("");
        throw invalid_argument(" invalid index");
    return bundle[pos];
}

Card CardBundle::playCardPos(int pos) {
    Card c = bundle.at(pos);
    bundle.erase(bundle.begin() + pos);
    return c;
}

void CardBundle::shuffleDeck() {
    shuffle(bundle.begin(), bundle.end(), random_device());
}

void CardBundle::sort() {
    std::sort(bundle.begin(), bundle.end());
}

ostream& operator<<(ostream &os, const CardBundle &cd) {
    if (cd.bundle.empty())
        os << "The bundle is currently empty.";
    else{
        for(long unsigned int i=0;i<cd.bundle.size();i++)
            os << "Position " << i << "-------> " << cd.bundle[i] << endl;
    }
    return os;
}

bool CardBundle::isEmpty() const {
    return bundle.empty();
}

long unsigned int CardBundle::size() const {
    return bundle.size();
}
void CardBundle::clear() {
    bundle.clear();
}

