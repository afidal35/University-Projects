//
// Created by Fidalgo Alex & Feaux De Lacroix Martin on 24/12/2020.
//

#include "War.h"
#include "TerminalView.h"
#include "Controller.h"

int main(){
    auto c = new Controller<War,TerminalView>();
    c->run();
}