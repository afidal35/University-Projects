//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 29/11/2020.
//

#ifndef CARD_GAMES_CARD_H
#define CARD_GAMES_CARD_H

#include <iostream>
#include <string>
#include <cstdlib>
#include <functional>

using namespace  std;
class CardBuilder;

/*!
 * \brief Represents a generic card usable by all card games that our framework aims at.
 */

class Card {

public:

    friend class CardBuilder;

    /*!
     * @enum Color represents colors of cards.
     */
    enum class Color {Red, Blue, Green, Yellow, Black, None};

    /*!
    * @enum Suit represents suits of cards.
    */
    enum class Suit {Club, Diamond, Heart, Spade, None};

    virtual ~Card() = default;

    /*! \brief Overloaded < operator based on the card value.
     * @return true if this.value is less that the card parameter value, false otherwise.
     */
    bool operator <(const Card &) const;

    /*! \brief Overloaded > operator based on the card value.
    * @return true if this.value is less that the card parameter value.
    */
    bool operator >(const Card&) const;

    /*! \brief Overloaded == operator based on the card value.
    * @return true if this.value is equal card parameter value.
    */
    bool operator ==(const Card&) const;
    int operator +(const Card&) const;

    /*! \brief Overloaded << operator to print a card.
    * @return an ostream with the representation of the card.
    */
    friend ostream& operator <<(ostream& os, const Card& card);


    static CardBuilder build(int value, Color color);

    Card(int value, Color color);


    int getValue() const;
    int getPoints() const;
    Color getColor() const;
    Suit getSuit() const;

    void setColor(int c);
    void setColor(Color c);

protected:

    int value; /**< \brief An integer that represents the value of the card. */
    Color color; /**< \brief A Color enum that represents the color of the card. */
    Suit suit; /**< \brief A Suit enum that represents the suit of the card. */
    int points; /**< \brief An integer that represents the points of the card. */
};


#endif //CARD_GAMES_CARD_H


/*
 *! \brief Constructor for Cards with only values and colors.
 * @param value is the card value.
 * @param color is the card color.
 * Suit is set to Card::Suit::None and points to 0.
 *
Card(int value, Color color);

*! \brief Constructor for Cards with values, colors and points.
 * @param value is the card value.
 * @param color is the card color.
 * @param points represents the points of the card.
 * Suit is set to Card::Suit::None.
 *
Card(int value, Color color, int points);

*! \brief Constructor for Cards with values, colors and suits.
* @param value is the card value.
* @param color is the card color.
* @param suit is the suit of the card.
* points is set to 0.
*
Card(int value, Color color, Suit suit);

*! \brief Constructor for Cards with values, colors, suits and points.
* @param value is the card value.
* @param color is the card color.
* @param suit is the suit of the card.
* @param points represents the points of the card.
*
Card(int value, Color color, Suit suit, int points);

Card::Card(int value, Color color, Suit suit) : value(value), color(color), suit(suit), points(0) {}
Card::Card(int value, Color color) : value(value), color(color), suit(Card::Suit::None), points(0) {}
Card::Card(int value, Color color, int points) : value(value), color(color), suit(Card::Suit::None), points(points) {}
Card::Card(int value, Color color, Suit suit, int points) : value(value),  color(color), suit(suit), points(points) {}
*/