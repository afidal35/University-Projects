#!/usr/bin/env python3
class PID:
    def __init__(self, kp : float = 0.5, ki : float = 0.005, kd : float = 4) -> None:
        self.kp = kp
        self.ki = ki
        self.kd = kd
        
        self.last_error = 0
        self.target = 0
        self.integral = 0

        self.error = 0

    def calculate_pid(self, error : int) -> int:
        p_gain = error * self.kp

        if error < 10:
            self.integral = 0
        else:
            self.integral += error

        i_gain = self.integral * self.ki

        d_gain = (error - self.last_error) * self.kd

        self.last_error = error

        return p_gain + i_gain + d_gain