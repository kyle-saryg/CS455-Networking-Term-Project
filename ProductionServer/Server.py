import socket
import json

HOST = ""  # Listen on all available network interfaces
PORT_NUMBER = 8000
MAX_NUMBER_OF_PLAYERS = 4

def main():
    quiz = None

    # Opening quiz stored in JSON format
    with open('Quiz.json') as file:
        quiz = json.load(file)

    questionObjects = createQuestionObjects(quiz)

    # Used to read data from every client
    readers = []
    # Used to send data to every client
    writers = []
    #Tracks score from each user
    scores = [0] * MAX_NUMBER_OF_PLAYERS

    try:
        # Creating socket connection
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
            server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            server_socket.bind((HOST, PORT_NUMBER))
            server_socket.listen()
            print(f"Server started and listening on port {PORT_NUMBER}")

            connection_ctr = 0
            while connection_ctr < MAX_NUMBER_OF_PLAYERS:
                client_socket, address = server_socket.accept()
                print(f"Client connected from {address[0]}")

                # Creating reader and writer from client
                reader = client_socket.makefile("r")
                writer = client_socket.makefile("w")

                readers.append(reader)
                writers.append(writer)

                connection_ctr += 1

            print(f"RECEIVED {MAX_NUMBER_OF_PLAYERS} CONNECTIONS")

            for questionCtr, questionObj in enumerate(questionObjects):
                isLastQuestion = False
                if questionCtr == (len(questionObjects) - 1):
                    print("Last Question")
                    isLastQuestion = True
                jsonData = {
                    "Question": questionObjects[0].question,
                    "Selections": questionObjects[0].options,
                    "Player1Score": scores[0],
                    "Player2Score": scores[1],
                    "Player3Score": scores[2],
                    "Player4Score": scores[3],
                    "isLastQuestion": isLastQuestion
                }
                jsonString = json.dumps(jsonData)
                sendToAllClients(writers, jsonString)

                # 'readers' and 'scores' are indexed the same
                # 'reader[0]' correspondes to player in 'scores[0]'
                for player, reader in enumerate(readers):
                    response = json.loads(receiveFromClient(reader))
                    print(response)
                    # Player has the correct answer
                    if response["choice"] == questionObj.answer:
                        scores[player] += response["time"]
            
            print(scores)

            server_socket.close()

    except Exception as e:
        print(e)


class QuestionOptionsAnswer:
    def __init__(self, questionOptionAnswer):
        self.question = questionOptionAnswer[0]
        self.options = questionOptionAnswer[1]
        self.answer = questionOptionAnswer[2]

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