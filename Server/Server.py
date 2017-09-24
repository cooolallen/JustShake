# -*- coding: utf-8 -*-
"""
Created on Fri Sep 22 22:42:13 2017

@author: Rajith
"""

from flask import Flask, request
import json, csv
import Logger

songDir = '../call/src/goblue.txt'
    
app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
l = Logger.Logger()
players =
choreography = {}
timeList = []
with open(songDir,'rt') as file:
    reader = csv.reader(file, delimiter="\t")
    for row in reader:
        data = row[0].strip()
        choreography.update({ data : "shake" })
        timeList.append(float(data))

@app.route('/',methods=["GET"])
def postInfo():
    if request.method is "GET":
        return "Hello World"
    json = request.get_json(silent=True)
    if 'data' in json:
        '''print(json)'''
        username = json['userID']
        data = json['data']
        timestamp = float(json['time'])
        l.updateScores(username, data, timestamp)
        return "Action recieved"
    elif 'userID' in json:
        l.playerJoin(json['userID'])
        if len(l.players) == 3:
            l.state = Logger.GameState.Playing
        return "User added"
 
@app.route('/gamestatus/',methods=["GET"])
def gameStatus():
    '''
    Check whether the game is running.
    
    :param: none
    :returns: string describing the status of the game
    '''        
    if l.state is Logger.GameState.Waiting:
        return "GAME WAIT"
    if l.state is Logger.GameState.Playing:
        return "GAME PLAYING"
    return "GAME OVER"

@app.route('/action/',methods=["POST"])
def addAction():
    '''print(request.get_json(silent=True))'''
    json = request.get_json(silent=True)
    username = json["userID"]
    if "time" not in json:
        l.playerJoin(json['userID'])
        if len(l.players) == 3:
            l.state = Logger.GameState.Playing
        return ""
    timestamp = float(json["time"])
    diff = abs(timestamp - min(timeList, key=lambda x:abs(x-timestamp)))
    if diff < 0.1:
        l.updateScore(username,100)
    elif diff < 0.5:
        l.updateScore(username,20)
    return "Action Recieved"

@app.route("/score/", methods=["GET"])
def getScoreData():
    '''
    Show current scores
    :params: None
    :returns: JSON representation of players and their scores
    '''
    if l is None:
        return ""
    return json.dumps(l.players);

# For debugging
@app.route("/init/",methods=["GET"])
def startLogger():  
    l.state = Logger.GameState.Playing
    return "Initialization Successful"
# For debugging
@app.route('/terminate/',methods=["GET"])
def stopLogger():
    l.state = Logger.GameState.Done
    return "Termination Sucessful"
# For debugging    
@app.route('/data/<string:username>/<string:data>/<string:timestamp>')
def specifyAction(username,data,timestamp):
    l.updateScores(username,data,timestamp)
    return "Recieved"
# For debugging
@app.route("/join/<string:username>/", methods=["GET"])
def playerJoin(username):
    if l is None:
        return "Create a new game"
    l.playerJoin(username)
    if len(l.players) == 3:
        l.state = Logger.GameState.Playing
    return "Joined"