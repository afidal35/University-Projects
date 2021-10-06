#!/usr/bin/env python3
from os import error
from ev3dev2.motor import LargeMotor, OUTPUT_B, OUTPUT_A, SpeedPercent, MoveTank
from ev3dev2.sensor.lego import ColorSensor
from ev3dev2.sensor import INPUT_1
from ev3dev2.button import Button
from time import time, sleep
import json

from ColorHandler import ColorHandler
from PID import PID
from ButtonHandler import ButtonHandler

class LineFollower:
    def __init__(self, speed : int, offset : int) -> None:
        self.speed = speed
        self.offset = offset

        # Connecting left and right motor
        self.left_motor = LargeMotor(OUTPUT_B)
        self.right_motor = LargeMotor(OUTPUT_A)
        #Connecting color sensor
        self.cl = ColorSensor(INPUT_1)
        self.color_handler = ColorHandler(False, self.cl)

        self.pid = PID()

        #declare color usable
        self.background = -1
        self.line = None
        self.depart = None
        self.intersection = None

    def dist_to_background(self, color : int) -> int:
        if self.background > -1:
            mean = self.color_handler.get_mean(self.background)
        else:
            mean = [255, 255, 255] #default background is white

        return self.color_handler.dist(color, mean)

    def init_sensor(self) -> None:
        self.cl.calibrate_white()

    def scan_line(self) -> None:
        self.line = self.color_handler.scan()
        self.distance_max = self.dist_to_background(self.line)
    
    def scan_depart(self) -> None:
        self.depart = self.color_handler.scan()
        self.distance_max_depart = self.dist_to_background(self.depart)

    def scan_intersection(self) -> None:
        self.intersection = self.color_handler.scan()
        self.distance_max_intersection = self.dist_to_background(self.intersection)

    def scan_background(self) -> None:
        self.background = self.color_handler.scan()

        #need to recalculate all distance to background
        if self.line:
            self.distance_max = self.dist_to_background(self.line)
        if self.depart:
            self.distance_max_depart = self.dist_to_background(self.depart)
        if self.intersection:
            self.distance_max_intersection = self.dist_to_background(self.intersection)

    def calculate_speed(self, adjustment : float, offset : int, min_int : float = 0, max_int : float = 100) -> SpeedPercent:
        return SpeedPercent(min(max(self.speed + adjustment + offset, min_int), max_int))

    def follow_line(self) -> None:
        colors = {}

        if self.line != None:
            colors[self.line] = self.distance_max
        if self.depart != None:
            colors[self.depart] = self.distance_max_depart
        if self.intersection != None:
            colors[self.intersection] = self.distance_max_intersection

        pred_color = -1
        color_amount = 0
        bg_amount = 0

        while True:
            read = self.cl.rgb

            color = self.color_handler.nearest(read, colors.keys())

            if self.color_handler.check_color(read) == color:
                error = 0
            else:
                dist = (self.color_handler.dist(color, read) / colors[color]) * 100
                
                error = dist 

            #because we always want to go a bit more to the left
            pid = (self.pid.calculate_pid(error))

            #color amount is unriable, lighter color are way more likely to get pick
            if color == pred_color or error > 10:
                color_amount += 1
            else:
                color_amount = 0
                pred_color = color

            if error > 60:
                bg_amount += 1
            else:
                bg_amount = 0

            speed_left = self.calculate_speed(pid, -self.offset)
            speed_right = self.calculate_speed(-pid, self.offset)
            if bg_amount > 40:
                variance_left = (bg_amount / 60) * 2.5
                variance_right = (bg_amount / 60) * 6

                speed_left = self.calculate_speed(pid, -self.offset, max_int = 100 - variance_left)
                speed_right = self.calculate_speed(-pid, self.offset, min_int = variance_right)

            self.left_motor.on(speed_left)
            self.right_motor.on(speed_right)

            sleep(0.002)

        self.left_motor.stop()
        self.right_motor.stop()
        sleep(50)

    def print_dist(self):
        read = self.cl.rgb
        dist = (self.color_handler.dist(self.line, read) / self.distance_max) * 100
        print(dist)


if __name__ == "__main__":
    lf = LineFollower(40, 25)
    log = []
    btn = ButtonHandler()

    btn.add_option("init captor - 1", lf.init_sensor)
    btn.add_option("scan line - 2", lf.scan_line)
    btn.add_option("scan depart - o", lf.scan_depart)
    btn.add_option("scan intersection - o", lf.scan_intersection)
    btn.add_option("scan background - o", lf.scan_background)
    btn.add_option("start - 3", lf.follow_line)

    btn.draw()
    btn.handle_buttons()

    """
    print("init")
    sleep(5)
    lf.init_sensor()

    print("scan line")
    sleep(5)
    lf.scan_line()

    print("scan depart")
    sleep(5)
    lf.scan_depart()

    print("scan intersection")
    sleep(5)
    lf.scan_intersection()

    print("go")
    sleep(5)
    lf.follow_line()
    """

    with open("result.json", "w") as write_file:
        json.dump(log, write_file)