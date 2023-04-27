import socket
import json

HOST = ""  # Listen on all available network interfaces
PORT_NUMBER = 8000
MAX_NUMBER_OF_PLAYERS = 2

def main():
    # Used to read data from every client
    readers = []
    # Used to send data to every client
    writers = []

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

            # Reading data from clients
            for i in range(MAX_NUMBER_OF_PLAYERS):
                print(receive_from_client(readers[i]))

            # Sending data to clients
            for i in range(MAX_NUMBER_OF_PLAYERS):
                send_to_client(writers[i], f"WHOLE LOTTA LIGMA {i}")



            server_socket.close()

    except Exception as e:
        print(e)

def send_to_client(writer, data):
    data = data + "#"
    writer.write(data)
    writer.flush()

def receive_from_client(reader):
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