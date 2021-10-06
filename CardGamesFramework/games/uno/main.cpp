//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 24/12/2020.
//

#include "Uno.h"
#include "TerminalView.h"
#include "Controller.h"

int main(){
    auto c = new Controller<Uno,TerminalView>();
    c->run();
}