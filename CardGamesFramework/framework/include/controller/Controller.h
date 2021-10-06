//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 29/11/2020.
//

#ifndef CARD_GAMES_CONTROLLER_H
#define CARD_GAMES_CONTROLLER_H

#include "View.h"
#include "Game.h"

/*!
 * \brief Template class representing the controller of our model (game) view controller pattern.
 * @tparam M
 * \brief Represents a game model.
 * @tparam V
 * \brief Represents a view.
 */

template <class M, class V> class Controller {

public:

    /*!
     * Initialize the model (game) and the view by itself.
     */
    Controller(){
        model = new M();
        view = new V();
    }

    /*!
     * Destroys the model (game) and the view.
     */
    ~Controller<M,V>(){
        delete [] model;
        delete [] view;
    }

    /*!
     * \brief Main method of the controller to run a game.
     * It proceeds a generic game loop consisting on :
     * -> Asking informations to setup the game.
     * -> Waiting for game ending condition.
     * -> Waiting for game round ending condition.
     * -> Playing the game.
     * -> Asking the user if he wants to play another game.
     */

    void run() {

    do {

        // Re-initialise the game, useful if we want to play multiple games in a row ...
        model->clearGame();

        // The view display the header of the game depending on the model
        view->displayFromModel(model->getGameHeader());

        // The view ask the names of the players as well as the number
        vector<string> human_players_names =
                view->askPlayersNames(
                        view->askNumberOfPlayers(0, model->getMaxPlayers(), false), false);

        // The model set his human players with the data obtained by the view
        model->setPlayers(human_players_names, false);

        // Set minimum players to -1 since we can after choose AI players as well.

        vector<string> ai_players_names;
        if (human_players_names.size() != (long unsigned int) model->getMaxPlayers()) {
            ai_players_names = view->askPlayersNames(
                    view->askNumberOfPlayers(
                            human_players_names.size() >= (long unsigned int) model->getMinPlayers() ? 0 : model->getMinPlayers() -
                                                                                       human_players_names.size(),
                            (long unsigned int) model->getMaxPlayers() - human_players_names.size(), true), true);
        }

        // The model set his ai players with the data obtained by the view
        model->setPlayers(ai_players_names, true);

        model->setNumberOfPlayers(human_players_names.size() + ai_players_names.size());
        model->setNumberOfPointsToWin(view->askNumberOfPointsToWin());
        system("clear");


        // While the ending condition of the game isn't null we loop
        while (model->evaluateWinnerOfGame() == nullptr) {

            // The model create, shuffle and deal the cards to the players
            model->createDeck();
            model->shuffleDeck();
            model->dealCardsAndSetTable();
            view->displayPreamble();
            model->shufflePlayers();
            view->displayFromModel(model->getRules());
            view->displayFirstPlayer(model->getCurrentPlayer());

            while (model->evaluateWinnerOfRound() == nullptr) {

                // Call the effect of the first card if the game need it
                if (model->getStart() && model->needFirstCardEffect()) {
                    view->showFirstCard(model->getLastCardPlayed());
                    int color = view->announceCardEffect(model->callLastCardEffect(), model->getCurrentPlayer()->isAIPlayer());
                    if (color != -1)
                        model->addToTable(Card(-1, Card::Color(color)));
                }

                model->setStart(false);

                view->announcePlayerTurn(model->getCurrentPlayer());

                // Check whether the player is forced to draw or not
                if (model->getCurrentPlayerValidMove() == -1) {
                    view->announceForcedToDraw(model->getCurrentPlayer());
                    model->makeCurrentPlayerDraw(1);
                }

                // If he cannot play a second time, we skip his turn
                if (model->getCurrentPlayerValidMove() == -1) {
                    view->announceForcedToSkipTurn(model->getCurrentPlayer());
                    model->nextTurn();
                    continue;
                }


                // FOR GAMES WHERE YOU CAN CUT LIKE SCOPA, TAROT ..
                bool wantCut;
                if (model->isAbleToCut())
                    wantCut = view->wantToCut(model->getCurrentPlayer(), model->getTable());

                if (!model->isAbleToCut() || !wantCut) {

                    int cardIndex;
                    Card *cardPlayed;

                    if (!model->getCurrentPlayer()->isAIPlayer()) {
                        // Ask the card to the player

                        cardIndex = view->askCardIndex(model->isCardChoosable(), model->showTable(),
                                                       model->getCurrentPlayer(),
                                                       model->getTable(), model->getDeck());

                        cardPlayed = &model->getCurrentPlayer()->getCardFromPos(cardIndex);

                        while (!model->isMoveValid(*cardPlayed)) {
                            view->announceInvalidMove();
                            view->displayFromModel(model->getRules());
                            cardIndex = view->askCardIndex(model->isCardChoosable(), model->showTable(),
                                                           model->getCurrentPlayer(),
                                                           model->getTable(), model->getDeck());
                            cardPlayed = &model->getCurrentPlayer()->getCardFromPos(cardIndex);
                        }

                    } else {
                        cardIndex = model->getCurrentPlayerValidMove();
                        cardPlayed = &model->getCurrentPlayer()->getCardFromPos(cardIndex);
                    }

                    // The move has been validated by the model so the player play this card
                    Card current = model->playerPlayCardFromPos(cardIndex);

                    view->showCardPlayed(current);
                    model->addToTable(current);

                    // The view show the card played by the player
                    //view->showCardPlayed(*cardPlayed);
                    //model->addToTable(*cardPlayed);

                    // FOR GAMES WHERE YOU CAN CUT LIKE SCOPA, TAROT ..
                } else {

                    model->getTable().sort();
                    std::tuple<int, int, int> cut;

                    if (!model->getCurrentPlayer()->isAIPlayer()) {
                        cut = view->askForCut(model->getCurrentPlayer(), model->getTable());

                        while (!model->isCutValid(cut)) {
                            view->announceInvalidMove();
                            view->displayFromModel(model->getRules());
                            cut = view->askForCut(model->getCurrentPlayer(), model->getTable());
                        }

                        view->showCutPlayed(model->getCurrentPlayer(), model->getTable(), cut);

                    }else {
                        cut = model->getValidCut();
                    }

                    Card played = model->playerPlayCardFromPos(std::get<0>(cut));
                    Card take1 = model->removeCardFromTable(std::get<1>(cut));

                    model->addToCurrentPlayerStack(played);
                    model->addToCurrentPlayerStack(take1);

                    if (std::get<2>(cut) != -1) {
                        Card take2 = model->removeCardFromTable(std::get<2>(cut));
                        model->addToCurrentPlayerStack(take2);
                    }
                }

                // If we reach the end of one turn we need to evaluate who won that turn
                if (model->evaluationTurnCondition()) {
                    // We reset the turn so we can play again
                    model->setPlayerIndex(0);

                    // The model try to evaluate the turn, if there is a winner the view announce it
                    Player *p = model->evaluateTurn();
                    if (p == nullptr) {
                        view->announceNoWinner();
                        continue;
                    } else
                        view->announceWinnerOfTurn(p);

                    if (!model->getCurrentPlayer()->isAIPlayer())
                        view->playNextTurn();

                    model->setNumberOfRoundsPlayed(model->getNumberOfRoundsPlayed() + 1);
                    continue;
                }

                // Call the effect of the previous card played and get the color if it was choosing a color
                int color = view->announceCardEffect(model->callLastCardEffect(), model->getCurrentPlayer()->isAIPlayer());
                if (color != -1)
                    model->addToTable(Card(-1, Card::Color(color)));
                // model->getLastCardPlayed().setColor(Card::Color(color));
                // Setter not working for color, maybe because const ?
            }

            // We are out of the round loop, this round is done, the view announce it and reset the game for another round with the same players !
            Player *winner = model->evaluateWinnerOfRound();
            model->calculatePointsForWinner(winner);
            view->announceWinnerOfRound(winner, model->getPlayers());
            model->resetGame();
            view->playNextRound();

        }

        // We are out of the game loop, the game is done, the view announce it !
        view->announceWinnerOfGame(model->evaluateWinnerOfGame(), model->getPlayers());

        // The view asks the user if he wants to play another game
    }while(view->playAgain());
}


private:
    M *model; /*!< Represents the model of the template. */
    V *view;  /*!< Represents the view of the template. */
};

#endif //CARD_GAMES_CONTROLLER_H
