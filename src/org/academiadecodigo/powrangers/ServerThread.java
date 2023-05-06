package org.academiadecodigo.powrangers;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerRangeInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringSetInputScanner;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ServerThread implements Runnable {


    ArrayList<Socket> listOfPlayers;
    ArrayList<Server> listOfServer;
    Server server;
    Socket clientSocket;

    String[][] game;
    Integer[] coordinatesX;
    Integer[] coordinatesY;
    static int count;
    String name;
    int victoryCounter = 3;

    PrintWriter printWriter;

    ServerThread(Socket clientSocket, String[][] game, ArrayList<Socket> listOfPlayers, Integer[] coordinatesX, Integer[] coordinatesY, ArrayList<Server> listOfServer, Server server) throws IOException {
        this.server = server;
        this.listOfServer = listOfServer;
        this.listOfPlayers = listOfPlayers;
        this.coordinatesX = coordinatesX;
        this.coordinatesY = coordinatesY;
        this.game = game;
        this.clientSocket = clientSocket;

        printWriter = new PrintWriter(clientSocket.getOutputStream(), true);


        count += 1;
        name = "Player " + count + " - ";
    }

    int[] tryCoordinates = new int[2];

    @Override
    public void run() {

        makeGrid();

        while (clientSocket.isConnected()) {

            pickCoordinates();
            checkBoat(tryCoordinates[0], tryCoordinates[1]);
            refreshGrid();
            checkVictory();

        }
    }

    public void pickCoordinates() {
        Prompt prompt = null;
        int x = 0;
        int y;

        //int [] tryCoordinates = new int[2];

        try {
            prompt = new Prompt(clientSocket.getInputStream(), new PrintStream(clientSocket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Set<String> options = new HashSet<>();
        options.add("A");
        options.add("B");
        options.add("C");
        options.add("D");
        options.add("E");
        options.add("F");
        options.add("G");
        options.add("H");
        options.add("I");
        options.add("J");

        String[] finalArray = options.toArray(new String[0]);


        StringSetInputScanner askforCoordenate = new StringSetInputScanner(options);
        askforCoordenate.setMessage("\n                   INSERT A CAPITAL LETTER \n\n");

        askforCoordenate.setError("\n                      NOT A VALID LETTER");

        String resposta = prompt.getUserInput(askforCoordenate);
        printWriter.println("\n\n **************************************************************\n\n");

        printWriter.println("");
        IntegerRangeInputScanner secondcoordinatte = new IntegerRangeInputScanner(1, 10);
        secondcoordinatte.setMessage("\n                      INSERT A NUMBER \n\n");
        secondcoordinatte.setError("\n                     NOT A VALID NUMBER");

        int num = prompt.getUserInput(secondcoordinatte);
        printWriter.println("\n\n **************************************************************\n\n");

        tryCoordinates[1] = num - 1;

        for (int i = 0; i < finalArray.length; i++) {
            if (finalArray[i].equals(resposta)) {
                tryCoordinates[0] = i;
                break;
            }
        }
        //return tryCoordinates;
    }

    public void makeGrid() {
        String value = "";
        int n = 0;

        printWriter.println("           \uD83C\uDD30 " + "  \uD83C\uDD31 " + "  \uD83C\uDD32 " + "  \uD83C\uDD33 " + "  \uD83C\uDD34 " + "  \uD83C\uDD35 " + "  \uD83C\uDD36 " + "  \uD83C\uDD37 " + "  \uD83C\uDD38 " + "  \uD83C\uDD39 ");

        for (int i = 0; i < game.length; i++) {
            for (int j = 0; j < game.length; j++) {
                game[j][i] = " \uD83C\uDF0A ";
                value += game[j][i];
            }
            n++;
            if (n == 10) {
                printWriter.println("       " + n + " " + value);

            } else {
                printWriter.println("       " + n + "  " + value);

            }
            value = "";

        }
    }

    public void checkBoat(int x, int y) {
        System.out.println(game.length);
        System.out.println(x);
        Server finalServer = null;
        for (int i = 0; i < listOfServer.size(); i++) {
            if (listOfServer.get(i) != server) {
                finalServer = listOfServer.get(i);
            }
        }
        for (int i = 0; i < finalServer.getCoordinatesXSize(); i++) {
            if ((finalServer.getCoordinatesX(i) == x) && (finalServer.getCoordinatesY(i) == y)) {
                game[x][y] = Colors.SKULL;
                enemmySocket();
                victoryCounter--;
                System.out.println("boom dia");
                break;

            } /*else if (x == 0 || x == (game.length - 1) || y == 0 || y == (game.length - 1)) {

                if (((finalServer.getCoordinatesX(i) == (x + 1)) || (finalServer.getCoordinatesX(i) == (x - 1)) && (finalServer.getCoordinatesY(i) == (y))) ||
                        ((finalServer.getCoordinatesX(i) == (x + 1)) || (finalServer.getCoordinatesX(i) == (x - 1)) && (finalServer.getCoordinatesY(i) == (y - 1))) ||
                        ((finalServer.getCoordinatesY(i) == (y + 1)) || (finalServer.getCoordinatesY(i) == (y - 1)) && (finalServer.getCoordinatesX(i) == (x)))) {
                    System.out.println("tas la");

                    if (x == 0) {
                        game[x + 1][y] = Colors.FLAG;
                        game[x + 1][y - 1] = Colors.FLAG;
                        game[x + 1][y + 1] = Colors.FLAG;
                        game[x][y + 1] = Colors.FLAG;
                        game[x][y - 1] = Colors.FLAG;
                        game[x][y] = Colors.BOMB;
                        break;
                    }
                    if (x == (game.length - 1)) {
                        game[x - 1][y] = Colors.FLAG;
                        game[x - 1][y - 1] = Colors.FLAG;
                        game[x - 1][y + 1] = Colors.FLAG;
                        game[x][y + 1] = Colors.FLAG;
                        game[x][y - 1] = Colors.FLAG;
                        game[x][y] = Colors.BOMB;
                        break;
                    }
                    if (y == 0) {
                        game[x - 1][y] = Colors.FLAG;
                        game[x - 1][y + 1] = Colors.FLAG;
                        game[x + 1][y] = Colors.FLAG;
                        game[x + 1][y + 1] = Colors.FLAG;
                        game[x][y + 1] = Colors.FLAG;
                        game[x][y] = Colors.BOMB;
                        break;
                    }
                    if (y == (game.length - 1)) {
                        game[x - 1][y] = Colors.FLAG;
                        game[x - 1][y - 1] = Colors.FLAG;
                        game[x + 1][y] = Colors.FLAG;
                        game[x + 1][y - 1] = Colors.FLAG;
                        game[x][y - 1] = Colors.FLAG;
                        game[x][y] = Colors.BOMB;
                        break;
                    }

                    game[x][y] = Colors.BOMB;
                    break;
                }
            }
            */


            if (((finalServer.getCoordinatesX(i) == (x + 1)) || (finalServer.getCoordinatesX(i) == (x - 1)) && (finalServer.getCoordinatesY(i) == (y))) ||
                    ((finalServer.getCoordinatesX(i) == (x + 1)) || (finalServer.getCoordinatesX(i) == (x - 1)) && (finalServer.getCoordinatesY(i) == (y - 1))) ||
                    ((finalServer.getCoordinatesY(i) == (y + 1)) || (finalServer.getCoordinatesY(i) == (y - 1)) && (finalServer.getCoordinatesX(i) == (x)))) {

                game[x - 1][y] = Colors.FLAG;
                game[x - 1][y - 1] = Colors.FLAG;
                game[x - 1][y + 1] = Colors.FLAG;
                game[x + 1][y] = Colors.FLAG;
                game[x + 1][y - 1] = Colors.FLAG;
                game[x + 1][y + 1] = Colors.FLAG;
                game[x][y + 1] = Colors.FLAG;
                game[x][y - 1] = Colors.FLAG;
                game[x][y] = Colors.BOMB;

                break;
            }
        }
        System.out.println("fim de dia");
        game[x][y] = Colors.BOMB;
    }


    public void refreshGrid() {
        String value = "";
        int n = 0;

        printWriter.println("           \uD83C\uDD30 " + "  \uD83C\uDD31 " + "  \uD83C\uDD32 " + "  \uD83C\uDD33 " + "  \uD83C\uDD34 " + "  \uD83C\uDD35 " + "  \uD83C\uDD36 " + "  \uD83C\uDD37 " + "  \uD83C\uDD38 " + "  \uD83C\uDD39 ");

        for (int i = 0; i < game.length; i++) {
            for (int j = 0; j < game.length; j++) {
                value += game[j][i];
            }
            n++;
            if (n == 10) {
                printWriter.println("       " + n + " " + value);

            } else {
                printWriter.println("       " + n + "  " + value);

            }
            value = "";

        }

    }

    public void checkVictory() {
        if (victoryCounter == 0) {
            printWriter.println("\n                      " + name + "WON \n");
            printWriter.println("                       ___________\n" +
                    "                      '._==_==_=_.'\n" +
                    "                      .-\\:      /-.\n" +
                    "                     | (|:.     |) |\n" +
                    "                      '-|:.     |-'\n" +
                    "                        \\::.    /\n" +
                    "                         '::. .'\n" +
                    "                           ) (\n" +
                    "                         _.' '._\n" +
                    "                        `\"\"\"\"\"\"\"` \n");

            printWriter.println("__  ______  __  __   _       ______  _   ________________________\n" +
                    "\\ \\/ / __ \\/ / / /  | |     / / __ \\/ | / / / / / / / / / / / / /\n" +
                    " \\  / / / / / / /   | | /| / / / / /  |/ / / / / / / / / / / / / \n" +
                    " / / /_/ / /_/ /    | |/ |/ / /_/ / /|  /_/_/_/_/_/_/_/_/_/_/_/  \n" +
                    "/_/\\____/\\____/     |__/|__/\\____/_/ |_(_|_|_|_|_|_|_|_|_|_|_)   \n" +
                    "                                                                 \n");


            try {
                closeAllSockets();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void closeAllSockets() throws IOException {
        for (int i = 0; i < listOfPlayers.size(); i++) {
            listOfPlayers.get(i).close();
        }
    }

    public void enemmySocket() {
        for (int i = 0; i < listOfPlayers.size(); i++) {
            if (listOfPlayers.get(i) != clientSocket) {
                try {
                    PrintWriter printOnEnemy = new PrintWriter(listOfPlayers.get(i).getOutputStream(), true);
                    printOnEnemy.println("__  ______  __  __   __    ____  ___________   ___       ____  ____  ___  ______\n" +
                            "\\ \\/ / __ \\/ / / /  / /   / __ \\/ ___/_  __/  /   |     / __ )/ __ \\/   |/_  __/\n" +
                            " \\  / / / / / / /  / /   / / / /\\__ \\ / /    / /| |    / __  / / / / /| | / /   \n" +
                            " / / /_/ / /_/ /  / /___/ /_/ /___/ // /    / ___ |   / /_/ / /_/ / ___ |/ /    \n" +
                            "/_/\\____/\\____/  /_____/\\____//____//_/    /_/  |_|  /_____/\\____/_/  |_/_/     \n" +
                            "                                                                                ");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


}

