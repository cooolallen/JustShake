# -*- coding: utf-8 -*-
"""
Created on Sun Sep 24 05:26:22 2017

@author: Rajith
"""

from enum import Enum
from time import time

class Singleton(type):
    _instances = {}
    def __call__(cls,*args,**kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args,**kwargs)
        return cls._instances[cls]

class GameState(Enum):
    Waiting = 0
    Playing = 1
    Done = 2

class Logger(metaclass=Singleton):
    
    def __init__(self):
        self.startTime = time()     
        self.players = {}  
        self.state = GameState.Waiting;
    
    def setState(gameState):
        if gameState in GameState.__members__:
            self.state = gameState
        
    def playerJoin(self,userID):
        # Initalize score to zero
        self.players.update({userID : 0})
    
    def updateScores(self,userID,points):
        # TODO: Timestamp Logic
        self.players[userID] += points
        
    def report(self):
        return self.players