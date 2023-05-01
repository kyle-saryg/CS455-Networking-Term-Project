import json
import socket
import sys
import threading
from copy import deepcopy
from random import shuffle
import select
from time import sleep

HOST = ""  # Listen on all available network interfaces
PORT_NUMBER = 8000
exitEvent = threading.Event()

def main():
    quiz = None

    # Executed without specified player count
    if len(sys.argv) == 1:
        maxPlayerCount = 2
        print(f"PLAYING WITH {maxPlayerCount} CLIENTS PER QUIZ")
    # Specified player count
    elif len(sys.argv) == 2:
        try:
            maxPlayerCount = int(sys.argv[1])
        except Exception:
            print("USAGE (Within <> is optional):\npython3 Server.py <Specified Player Count>")
            exit()
    else:
        print("USAGE (Within <> is optional):\npython3 Server.py <Specified Player Count>")
        exit()

    print("\nTo stop server: q + enter")
    print("Will wait for all running quizes to end\n")
    # Argument (maxPlayerCount,) is simply a tuple with out item
    # (maxPlayerCount) does not work, not a tuple, ignores parentheses
    daemonThread = threading.Thread(target=startDaemon, args=(maxPlayerCount,))
    daemonThread.start()

    while sys.stdin.readline() != 'q\n':
        sleep(1)

    exitEvent.set()
    print("ENDING SERVER")
    # joins on spawned daemon thread (closes socket before join)
    # All other quiz threads will fail when socket closes
    daemonThread.join()


class QuestionOptionsAnswer:
    def __init__(self, questionOptionAnswer):
        self.question = questionOptionAnswer[0]
        self.options = questionOptionAnswer[1]
        self.answer = questionOptionAnswer[2]

def startDaemon(playerCount):
    # Opening quiz stored in JSON format
    with open('Quiz.json') as file:
        quiz = json.load(file)

    """
    Creating proprietary question objects
    Each object holds
    '.question' <String>
    '.options' [<String>]
    '.answer' <Int>
    """
    questionObjects = createQuestionObjects(quiz)

    # Used to read data from every client
    readers = []
    # Used to send data to every client
    writers = []
    #Tracks score from each user
    scores = [0] * playerCount

    try:
        # Creating socket connection
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            server_socket.bind((HOST, PORT_NUMBER))
            server_socket.listen()
            server_socket.setblocking(False)
            print(f"Server started and listening on port {PORT_NUMBER}")

            connection_ctr = 0
            # If the user enters 'Q' the Server will exit
            while not exitEvent.is_set():

                # Socket is set to non-blocking mode
                connectionPresent, _, _, = select.select([server_socket], [], [], 0)
                # Attempted connection is made
                if connectionPresent:
                    client_socket, address = server_socket.accept()
                    print(f"Client connected from {address[0]}")

                    # Creating reader and writer from client
                    reader = client_socket.makefile("r")
                    writer = client_socket.makefile("w")

                    readers.append(reader)
                    writers.append(writer)

                    connection_ctr += 1

                    # Without secondary condition would attempt to create a quiz room with zero connections
                    if connection_ctr % playerCount == 0 and connection_ctr > 0:
                        print(f"RECEIVED {playerCount} CONNECTIONS")
                        shuffle(questionObjects)
                        quizThread = threading.Thread(target=playQuiz, args=(questionObjects, deepcopy(scores), readers, writers))
                        quizThread.start()
                        readers = []
                        writers = []
                        scores = [0] * playerCount
                    
                    # so the computer can rest lol
                    sleep(1)

            server_socket.close()
            return
    except Exception as e:
        print(e)

# Plays the game, used as a target for 'threading.Thread()'
def playQuiz(questionObjects, scores, readers, writers):
    signalStartQuiz(writers, scores)
    sleep(5)
    startQuiz(questionObjects, scores, readers, writers)
    sleep(5)
    endQuiz(writers, scores)

    # Creates a list of tuples (<Reader>, <Writer>), and closes them respectively
    for readerWriter in zip(readers, writers):
        readerWriter[0].close()
        readerWriter[1].close()

    print(scores)

# You'll be surprised what this function does...
# Signals the to the client, the game will start
def signalStartQuiz(writers, scores):
    jsonData = {
        "Question": "Start Game",
        "Selections": [],
        "Scores": scores,
        "id": -1
    }
    jsonString = json.dumps(jsonData)
    sendToAllClients(writers, jsonString)

# Starts the Quiz
def startQuiz(questionObjects, scores, readers, writers):
    # Starting the quiz
    for questionCtr, questionObj in enumerate(questionObjects):

        for i, writer in enumerate(writers):
            jsonData = {
                "Question": questionObjects[questionCtr].question,
                "Selections": questionObjects[questionCtr].options,
                "Scores": scores,
                "id": i
            }
            jsonString = json.dumps(jsonData)
            sendToClient(writer, jsonString)

        # 'readers' and 'scores' are indexed the same
        # 'reader[0]' correspondes to player in 'scores[0]'
        for player, reader in enumerate(readers):
            response = json.loads(receiveFromClient(reader))
            print(response)
            # Player has the correct answer
            if response["choice"] == questionObj.answer:
                scores[player] += response["time"]
            print(scores)
        sleep(3)

# Signals the to the client, the game will end
def endQuiz(writers, scores):
    jsonData = {
        "Question": "End Game",
        "Selections": [],
        "Scores": scores,
        "id": -1
    }
    jsonString = json.dumps(jsonData)

    sendToAllClients(writers, jsonString)

# Takes in the JSON formatted quiz, creates a list of 'QuestionOptionsAnswer' objects
def createQuestionObjects(quiz):
    questions = []
    options = []
    answer = []
    for item in quiz["Quiz"]:
        questions.append(item["Question"])
        options.append(item["Options"])
        answer.append(item["Answer"])
    
    questionObjects = []
    questionOptionsAnswer = zip(questions,options, answer)
    for foo in questionOptionsAnswer:
        questionObjects.append(QuestionOptionsAnswer(foo))
    
    return questionObjects

def sendToAllClients(writers, data):
    # Adding custom delimiter
    data = data + "#"

    for writer in writers:
        writer.write(data)
        writer.flush()

def sendToClient(writer, data):
    # Adding custom delimiter
    data = data + "#"
    writer.write(data)
    writer.flush()

def receiveFromClient(reader):
    # Reading one character at a time, using StringBuilder
    output = ""

    try:
        while True:
            tmp = reader.read(1)
            if tmp == "#":
                break
            output += tmp

    except Exception as e:
        print(e)

    return output


if __name__ == "__main__":
    main()