//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 27/12/2020.
//

#ifndef CARD_GAMES_INVALIDMOVE_H
#define CARD_GAMES_INVALIDMOVE_H

#include <exception>
#include <string>

class invalidMove : public std::exception {

    const std::string prefix = "Invalid move ";

public:
    const char * what () const noexcept override{
        return message.c_str();
    }

    explicit invalidMove(const std::string& message) : message(prefix + message){}

private:
    std::string message;

};

#endif //CARD_GAMES_INVALIDMOVE_H
