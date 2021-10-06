#!/usr/bin/env python3
from ev3dev2.motor import Motor, OUTPUT_B, OUTPUT_D, SpeedRPM, SpeedPercent, MoveTank
from ev3dev2.sensor.lego import ColorSensor
from ev3dev2.sensor import INPUT_1
from time import time, sleep
import os
import sys
import time
import math

class ColorHandler:

    def __init__(self, auto_scan : bool, cl : ColorSensor, nb_mesure : int = 100, time : float = 2):
        self.colors = {}
        self.next_id = 0
        self.auto_scan = auto_scan
        self.cl = cl
        self.nb_mesure = nb_mesure
        self.time = time
        os.system('setfont Lat15-Terminus24x12')

    def scan(self) -> int:

        mesures = []

        sleep_time = self.time / self.nb_mesure

        for i in range(self.nb_mesure):
            mesures.append(self.cl.rgb)
            sleep(sleep_time)

        mean = [0, 0, 0]
        square_mean = [0, 0, 0]
        ecart_type = [0, 0, 0]
        ecart_type_range = [(0, 0), (0, 0), (0, 0)]

        for i in range (0, 3):
            mean[i] = sum(tup[i] for tup in mesures)
            square_mean[i] = sum((tup[i]**2) for tup in mesures)

        for i in range (0, 3):
            mean[i] /= self.nb_mesure     
            square_mean[i] /= self.nb_mesure   

        for i in range (0, 3):
            ecart_type[i] = ((square_mean[i] - (mean[i]**2))**(1/2))

        self.colors[self.next_id] = (mean, ecart_type)
        self.next_id += 1

        return self.next_id - 1

    def get_mean(self, color : int) -> "list[int]":
        return self.colors[color][0]

    def check_color(self, rgb) -> int:
        for m, erg in self.colors.items():   
            if self.in_range(rgb[0], erg[0][0], erg[1][0]) and \
                self.in_range(rgb[1], erg[0][1], erg[1][1]) and \
                 self.in_range(rgb[2], erg[0][2], erg[1][2]):
                return m
        
        if self.auto_scan:
            return self.scan()

        return -1


    def in_range(self, value : int, expected : float, ecart : float) -> bool:
        return abs(value - expected) < ecart * 3 + 5 #look 5 above the frontier

    def dist(self, color : int, rgb : "list[int]") -> int:
        mean = self.colors[color][0]

        return math.sqrt((mean[0] - rgb[0]) ** 2 + (mean[1] - rgb[1]) ** 2 + (mean[2] - rgb[2]) ** 2)

    def nearest(self, rgb : "list[int]", colors : "list[int]" = None) -> int:
        if not colors:
            colors = self.colors.keys()

        color = -1
        dist = 1000
        for i in colors:
            if self.dist(i, rgb) < dist:
                dist = self.dist(i, rgb)
                color = i

        return color


if __name__ == "__main__":
    cl = ColorSensor(INPUT_1)
    color = ColorHandler(True, cl)
    
    print("test recognition couleur")

    sleep(2)

    for i in range(1, 26):
        print("mesure : ", i)
        print("result : ", color.check_color(cl.rgb))
        sleep(3)