# Synopsis
This server communicates with the browser client and phone client in order to facilitate gameplay. In addition to syncing phone movements with the video, the server also keeps track of score and sends teh information to who asks.

# Technologies Used
The server is written in Python and is primarily powered by Flask, a server library for Python. Other libraries are used as well for parsing JSON and CSV files.
# How to Execute
## In Windows
Open a command prompt to the directory containing Server.py and Logger.py. Type the following to run the server
``` SET FLASK_APP=Server.py
python -m flask run --host=0.0.0.0```

