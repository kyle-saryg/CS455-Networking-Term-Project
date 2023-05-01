CS455 - Intro To Networking
Final Project - Reaction time quiz game

Brian Hong
Drake Farmer
Kyle Sarygin
Ramiz Tanous

___      _     _   _   _   __  _  _ ___
 |  |_| |_    |_| |_| | |   | |_ |   |
 |  | | |_    |   | \ |_| |_| |_ |_  |

The reaction time quiz game is a simple quiz game which awards players based on their speed of correct responses. Players compete head to head in room sizes of 2 or more to see who knows the most trivia the
fastest. Players can start a room with their friends, once the game starts each player is presented with a question and four possible answers. After each player has responded or has timed out by waiting too
long, points are awarded for each correct answer based on how quickly each player responded. Once the desired number of questions have been answered, all players are met with a total scores screen to decide
once and for all who the trivia master is.

THE SERVER:
	The server is multithreaded, which allows for multiple concurrent games to be played at once. The room size can be changed to allow any number of players to participate. Nearly all logic is handled
	on the server, from point calculation to round timing. The clients are only responsible for getting user input and sending it back to the server. This reduces inconsistency between clients and makes
	overall code logic easier as it is mostly located all in one place.

THE CLIENTS:
	The clients communicate with the server when connecting, and are alerted by the server when the game starts. Once the game has started each client waits for data from the server, it then presents the
	user with the question and options and returns data of the selection immediately once the user has made a selection or if they have waited too long and ran out of time.

Server Workload Split:
	Ramiz: System design, flow charting
	Kyle: Generating server code, working out bugs

Client Workload Split:
	

___      _    _  _   _   _
 |  |_| |_   |  | | | \ |_
 |  | | |_   |_ |_| |_/ |_

Server Code: Located top level folder titled 'ProductionServer'
Android Executable: Located at top level directory titled '


 _      _             _   ___      _    _   _         _
|_| |  |_| \ / | |\| |     |  |_| |_   |   |_| |\|/| |_
|   |_ | |  |  | | \ |_|   |  | | |_   |_| | | | | | |_

* User will need to start multiple emulators to statisfy the number of client connections
* Hardcoded server to 'localhost' if user wants to run the server over a network would need to change ip address to the server hosted device WITHIN THE CLIENT

1. Start python server 
	* "python3 Server.py <OPTIONAL ARGUMENT: Int>"
	* The optional argument will specify the number of client connections before starting the quiz
		* Default value of 2
2. Start Android emulator(s)
	* Server will not start the quiz until specified number of clients have connected
