//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 10/12/2020.
//

#ifndef CARD_GAMES_INVALIDINDEX_H
#define CARD_GAMES_INVALIDINDEX_H

#include <exception>
#include <string>

class invalidIndex : public std::exception {

    const std::string prefix = "Invalid index ";

public:
    const char * what () const noexcept override{
        return message.c_str();
    }

    explicit invalidIndex(const std::string& message) : message(prefix + message){}

private:
    std::string message;

};


#endif //CARD_GAMES_INVALIDINDEX_H
