#!/usr/bin/env python3

from ev3dev2.button import Button
from ev3dev2.console import Console

from time import sleep
import sys

class ButtonHandler:
    def __init__(self) -> None:
        self.button = Button()
        self.console = Console()
        self.console.set_font("Lat15-Fixed18.psf.gz", False)
        self.options = []
        self.index = 0

    def add_option(self, text : str, command) -> None:
        self.options.append((text, command))

    def draw(self) -> None:
        self.console.reset_console()
        mid_col = self.console.columns // 2

        for i in range(len(self.options)):
            print("")
            self.console.text_at(self.options[i][0], column = 1, row = i+1, inverse = (i == self.index))
        print("")
    
    def apply(self) -> None:
        self.options[self.index][1]()

        print("done")
        sleep(1.5)

    def up(self) -> None:
        self.index -= 1

        if self.index < 0:
            self.index = len(self.options) - self.index

    def down(self) -> None:
        self.index += 1

        if self.index >= len(self.options):
            self.index -= len(self.options)

    def handle_buttons(self) -> bool:
        while True:
            if self.button.any():                
                if self.button.backspace:
                    print("Leave")
                    return True
                elif self.button.enter:
                    self.button.wait_for_released('enter')
                    self.apply()
                elif self.button.up:
                    self.up()
                elif self.button.down:
                    self.down()
                
                self.draw()
                sleep(0.2)




        