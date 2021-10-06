//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 10/12/2020.
//

#include "TerminalView.h"

void TerminalView::displayFromModel(const string &s) const {
    cout << s;
}

void checkIndex(int min, int max, int index){
    if ((min != -1 && index < min) || (max != -1 && index > max))
        throw invalidIndex(": you must enter a number between [" + to_string(min) + "] and [" + to_string(max) + "] .");
}

void TerminalView::displayPreamble() const {
    cout << "================================================" << endl;
    cout << "   SHUFFLING THE DECK AND DEALING THE CARDS... |" << endl;
    cout << "             THE GAME CAN START.               |" << endl;
    cout << "================================================" << endl;
}

int TerminalView::askForIntegerAnswer(const string &question, int min, int max) const {
    while(true) {
        cout << question;
        string input;
        getline(cin, input);
        int answer;

        try {
            answer = stoi(input);
            checkIndex(min, max, answer);
        } catch (const invalid_argument &e) {
            cerr << e.what() << " you must enter a number." << endl;
            continue;
        }
        catch (const invalidIndex &e) {
            cout << e.what() << endl;
            continue;
        }
        catch (const exception &e) {
            cerr << e.what() << " please enter a number." << endl;
            continue;
        }
        return answer;
    }
}

int TerminalView::askNumberOfPlayers(int min, int max, bool b) const {
    string type = b ? "AI" : "HUMAN";
    string question =
            "=========================================================================\n"
            "|  TYPE IN THE NUMBER OF " + type + " PLAYERS, BETWEEN " + to_string(min) + " AND " + to_string(max) +"\n"
            "=========================================================================\n";
    return askForIntegerAnswer(question, min, max);
}

int TerminalView::askNumberOfPointsToWin() const {

    string question =
            "=========================================================================\n"
            "|          TYPE IN THE NUMBER OF POINTS NEEDED TO WIN THE GAME          |\n"
            "=========================================================================\n";

    return askForIntegerAnswer(question, 1, -1);
}

vector<string> TerminalView::askPlayersNames(int size, bool b) const {
    vector<string> players_names;
    string type = b ? "AI" : "HUMAN";
    for (int i=0;i<size;i++) {
        string input;
        cout << "===============================================================" << endl;
        cout << "|  TYPE IN THE NAME OF THE " << type << " PLAYER " << i+1 << " :" << endl;
        cout << "===============================================================" << endl;
        getline(cin, input);
        players_names.push_back(input);
    }
    return players_names;
}


int TerminalView::askCardIndex(bool b, bool c, Player *p, CardBundle &table, CardBundle &deck) {
    if (!b) {
        cout << "==================================================================\n";
        cout << "|                                                                |" << endl;
        cout << "|              YOU HAVE " << p->getHandSize() << " CARDS IN YOUR HAND ! \n\n";
        cout << "|              PRESS [ENTER] TO PLAY YOUR NEXT CARD              |\n" << endl;
        cout << "|                                                                |" << endl;
        cout << "==================================================================\n";
        while (true) {
            char choice;
            choice = cin.get();

            if (choice == '\n')
                return p->getHandSize() - 1;
            else
                continue;
        }
    } else {

        cout << "===================================================\n";
        cout << "|          THERE IS " << deck.size() << " CARDS IN THE DECK " << endl;
        cout << "===================================================\n\n";

        if(!c) {
            cout << "==========================================================\n\n";
            cout << "       THE LAST CARD PLAYED IS " << table.getBackCard() << endl;
            cout << "==========================================================\n\n";
        }else {
            cout << "===========================================\n";
            cout << "       THE TABLE LOOKS LIKE  \n" << endl;
            cout << table << "\n\n";

            cout << "====================================================\n";
            cout << "|   YOU CHOOSE TO NOT CUT OR YOU ARE NOT ABLE TO ! |\n" << endl;
            cout << "====================================================\n";
        }

        cout << *p << endl;
        string question =
                "==================================================================\n"
                "|                                                                |\n"
                "|  CHOOSE THE CARD YOU WANNA PLAY BY TYPING [INDEX] AND [ENTER]  |\n"
                "|                                                                |\n"
                "==================================================================\n\n";
        return askForIntegerAnswer(question, 0, p->getHandSize() - 1);
    }
}

bool TerminalView::playAgain() const {
    cin.sync();
    char ans;
    cout << "====================================\n";
    cout << "|Do you want to play again [y/n] ? |\n";
    cout << "====================================\n";
    cin >> ans;
    cin.sync();
    return (ans == 'Y' || ans == 'y');
}

void TerminalView::announceWithPlayer(Player *p, const string &message_1, const string &message_2) const {
    cout << "===================================================================\n";
    cout << "|          " << message_1 <<                                      endl;
    cout << "|                                                                 |" << endl;
    cout << "               " << p->getName() << message_2                     << endl;
    cout << "|                                                                 |" << endl;
    cout << "===================================================================\n\n";
}

void TerminalView::displayFirstPlayer(Player *first) const {
    announceWithPlayer(first, "", " YOU ARE THE FIRST PICKED TO START ! ");
}

void TerminalView::announcePlayerTurn(Player *p) const {
    announceWithPlayer(p, "", " IT'S YOUR TURN TO PLAY !       ");
}

void TerminalView::announceWinnerOfTurn(Player *p) const {
    announceWithPlayer(p, "THIS TURN IS OVER", " WON THIS TURN ! ");
}

void TerminalView::announceForcedToDraw(Player *p) const {
    announceWithPlayer(p, "", " YOU ARE FORCED TO DRAW YOU CANNOT PLAY !");
}

void TerminalView::announceForcedToSkipTurn(Player *p) const {
    announceWithPlayer(p, "", " AFTER YOU DREW A CARD YOU STILL CANNOT PLAY ! TURN SKIPPED ! ");
}

void TerminalView::announceWinnerOfRound(Player *winner, const vector<Player*> &players) const{
    announceWithPlayer(winner,"THIS ROUND IS OVER", " WON THE ROUND !");

    cout << "          POINTS OF THE PLAYERS         \n" << endl;

    cout << "========================================\n" << endl;

    for(long unsigned int i=0;i<players.size();i++){

        cout << "Player " << i << " " << players[i]->getName() << " : ";
        cout << players[i]->getPoints() << " Points \n" << endl;
    }

    cout << "========================================\n" << endl;

}

void TerminalView::announceWinnerOfGame(Player *winner, const vector<Player*> &players) const {
    announceWithPlayer(winner, "THIS GAME IS OVER", " WON THE GAME ! ");

    cout << "         POINTS OF THE PLAYERS          \n" << endl;

    cout << "========================================\n" << endl;

    for(long unsigned int i=0;i<players.size();i++){

        cout << "Player " << i << " " << players[i]->getName() << " : ";
        cout << players[i]->getPoints() << " Points \n" << endl;
    }

    cout << "========================================\n" << endl;

}

void TerminalView::announce(const string &message) const {
    cout << "\n\n";
    cout << "======================================================" << endl;
    cout << message << endl;
    cout << "======================================================" << endl;
    cout << "\n\n";
}

void TerminalView::announceInvalidMove() const {
    announce("          INVALID MOVE, TRY AGAIN ! ! !           ");
}

void TerminalView::announceNoWinner() const {
    announce("  THERE IS NO WINNER FOR NOW, THE GAME CONTINUE !  ");
}

void TerminalView::playNextTurn() const {
    do {
        announce("  PRESS [ENTER] TO PLAY THE NEXT TURN !   ");
    } while (cin.get() != '\n');
    system("clear");
}

void TerminalView::playNextRound() const {
    do {
        announce("  PRESS [ENTER] TO PLAY THE NEXT ROUND !   ");
    } while (cin.get() != '\n');
    system("clear");
}

int TerminalView::announceCardEffect(int n, bool isAI) const {
    switch (n) {
        case 1:
            announce("           NEXT PLAYER TURN  !        ");
            break;
        case 2:
            announce("      NEXT PLAYER TURN IS SKIPPED   ! ");
            break;
        case 3:
            announce("      NEXT PLAYER DRAW TWO CARDS    ! ");
            break;
        case 4:
            announce("      NEXT PLAYER DRAW FOUR CARDS   ! ");
            break;
        case 5:
            announce("        GAME SENS IS REVERSED       ! ");
            break;
        case 6:

            if(isAI)
                return rand() % 2 == 0 ? 0 : 4;
            else {
                string question = "YOU CAN CHOOSE THE NEXT COLOR BY TYPING [INDEX] ! \n 0 --------> RED \n 1 --------> BLACK \n\n";
                return askForIntegerAnswer(question, 0, 1) == 0 ? 0 : 4;
            }
        case 7:
            if(isAI)
                return rand() % 4;
            else {
                string question = "YOU CAN CHOOSE THE NEXT COLOR BY TYPING [INDEX] ! \n 0 --------> RED \n 1 --------> BLUE \n 2 --------> GREEN \n 3 --------> YELLOW\n\n";
                return askForIntegerAnswer(question, 0, 3);
            }
        default:
            break;
    }
    return -1;
}


tuple<int,int,int> TerminalView::askForCut(Player *p, CardBundle &table) const {
    cout << "===========================================\n";
    cout << "       THE TABLE LOOKS LIKE  \n" << endl;
    cout << table << "\n\n";

    cout << *p << endl;
    cout << "==================================================================\n\n";
    cout << "|                                                                |" << endl;
    cout << "|  CHOOSE THE CARD YOU WANNA PLAY BY TYPING [INDEX] AND [ENTER]  |\n";
    cout << "|                                                                |" << endl;
    cout << "==================================================================\n\n";

    string chooseCard =
            "==================================================================\n\n"
            "|                                                                |\n"
            "|  CHOOSE THE CARD YOU WANNA PLAY BY TYPING [INDEX] AND [ENTER]  |\n"
            "|                                                                |\n"
            "==================================================================\n\n";

    int cardPlayed = askForIntegerAnswer(chooseCard, 0, p->getHandSize() - 1);

    string questionTake1 =
            "===========================================================================\n\n"
            "|                                                                         |\n"
            "|  CHOOSE THE FIRST CARD YOU WANT TO TAKE BY TYPING [INDEX] AND [ENTER]   |\n"
            "|                                                                         |\n"
            "===========================================================================\n\n";

    int take1 = askForIntegerAnswer(questionTake1, 0, table.size() - 1 );
    int take2 = -1;

    if(p->getCardFromPos(cardPlayed) == table.getCardFromPos(take1))
        return std::make_tuple(cardPlayed, take1, take2);

    string questionTake2 =
            "===========================================================================\n\n"
            "|                                                                         |\n"
            "|  CHOOSE THE SECOND CARD YOU WANT TO TAKE BY TYPING [INDEX] AND [ENTER]  |\n"
            "|                                                                         |\n"
            "===========================================================================\n\n";

    while (true) {
        take2 = askForIntegerAnswer(questionTake2, 0, table.size() - 1);

        if (take2 == take1) {
            cout << "Please choose a different card from the first take : different position than -> " << take1 << endl;
            continue;
        }else
            return std::make_tuple(cardPlayed, take1, take2);
    }
}

bool TerminalView::wantToCut(Player *p, CardBundle &cb) const {

    if(p->isAIPlayer()){
        int ans = rand() % 2;
        if (ans == 0)
            return true;
        else
            return false;
    }

    cout << "===========================================\n";
    cout << "       THE TABLE LOOKS LIKE  \n" << endl;
    cout << cb << "\n\n";
    cout << *p << endl;

    cin.sync();
    char ans;
    cout << "====================================\n";
    cout << "|     Do you want to cut [y/n] ?   |\n";
    cout << "====================================\n";
    cin >> ans;
    cin.sync();
    return (ans == 'Y' || ans == 'y');
}


void TerminalView::showCard(const Card &card, const string &message) const {
    cout << message << card << endl;
}

void TerminalView::showCardPlayed(const Card &card) const{
    showCard(card, "YOU PLAYED THE CARD ");
}

void TerminalView::showFirstCard(const Card &card) const {
    showCard(card, "THE FIRST CARD RETURNED ON THE TABLE IS " );
}

void TerminalView::showCutPlayed(Player *p, CardBundle &table, std::tuple<int,int,int> cut) const {
    cout << "===========================================\n";
    cout << "       -     HERE IS YOUR CUT    -         " << endl;
    cout << "       YOU PLAYED " << p->getCardFromPos(std::get<0>(cut)) << endl;
    cout << "       YOU TOOK " << table.getCardFromPos(std::get<1>(cut)) << endl;

    if(std::get<2>(cut) != -1)
        cout << "AND " << table.getCardFromPos(std::get<2>(cut)) << endl;
    cout << "===========================================\n";
}