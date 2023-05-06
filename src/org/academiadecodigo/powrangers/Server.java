package org.academiadecodigo.powrangers;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
     private String[][] game = new String[10][10];
    private Integer[] coordinatesX = new Integer[10];
    private Integer[] coordinatesY = new Integer[10];


    public static void main(String[] args) throws IOException, InterruptedException {

        int portNumber = (int)Math.ceil(Math.random()*9999);

        System.out.println("This is our SpeedBattleShip game!");
        System.out.println("To connect use PorNumber:" + portNumber + ";\n" +
                "to play first choose a Capital Letter (A-J);\n" +
                "then choose a number (1-10).\n" +
                "First to sunk 3 enemy boats wins the game"); // loop through all arguments and print it to the user
        for (String str: args) {
            System.out.println(str);
        }

        ArrayList<Socket> listOfPlayers = new ArrayList<>();
        ArrayList<Server> listOfServer = new ArrayList<>();



        ServerSocket serverSocket = new ServerSocket(portNumber);

        String name;

        int count = 0;


            while (count <= 1){

                System.out.println("Waiting for connection...");
                Socket SCsocket = serverSocket.accept();
                listOfPlayers.add(SCsocket);
                count += 1;
                name = "Player " + count;
                Server server = new Server();
                listOfServer.add(server);

                PrintWriter printWriter = new PrintWriter(SCsocket.getOutputStream(), true);

                printWriter.println("\n              |￣￣￣￣￣￣￣￣￣￣￣￣￣￣|\n" +
                        "                Welcome to Speed Battleship\n" +
                        "              |＿＿＿＿＿＿＿＿＿＿＿＿＿＿|\n" +
                        "                         \\ (•◡•) /\n" +
                        "                          \\     /");

                printWriter.println("\n    " + Colors.ANSI_BACKGROUND_BLUE + " Welcome to BattleShip, the best game in the world!" + Colors.ANSI_RESET + "\n");
                printWriter.println("\n" + "                   " + Colors.ANSI_BACKGROUND_BLUE + "< YOU ARE " + name + " >" + Colors.ANSI_RESET + "\n");
                printWriter.println(Colors.ANSI_CYAN + "                    *This is your map*" + Colors.ANSI_RESET + "\n");

               // server.menu(SCsocket);
                // Show the Grid!
                server.showGrid(SCsocket);

                printWriter.println("\n" + "                    <_You have 3 boats_>" + "\n");

                printWriter.println("               Choose your TARGET_COORDINATES \n");

                System.out.println(name + " Connected " + SCsocket.getInetAddress().getHostAddress());

                Thread thread = new Thread(new ServerThread(SCsocket, server.getGame(), listOfPlayers, server.coordinatesX, server.coordinatesY,listOfServer, server));
                thread.start();

            }

        }

    public void menu(Socket SCsocket){
        Prompt prompt = null;

        try {
            prompt = new Prompt(SCsocket.getInputStream(), new PrintStream(SCsocket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean state = true;
        while (state) {
            String[] options = {"PLAY", "SEE PLAYERS"};
            MenuInputScanner scanner = new MenuInputScanner(options);
            scanner.setMessage("\n              |￣￣￣￣￣￣￣￣￣￣￣￣￣￣|\n" +
                    "                Welcome to Speed Battleship\n" +
                    "              |＿＿＿＿＿＿＿＿＿＿＿＿＿＿|\n" +
                    "                         \\ (•◡•) /\n" +
                    "                          \\     /");
            scanner.setMessage("  PRESS TO START!!  ");

            int userInput = prompt.getUserInput(scanner);
            if (userInput==1){
                state=false;
            }else {
                System.out.println("WRONG ANSWER");
            }
        }
    }


    public void showGrid(Socket socket) throws IOException {

            PrintWriter printWriter1 = new PrintWriter(socket.getOutputStream(), true);
            int n = 0;

            String value ="";
            printWriter1.println("           \uD83C\uDD30 " + "  \uD83C\uDD31 " + "  \uD83C\uDD32 " + "  \uD83C\uDD33 " + "  \uD83C\uDD34 " + "  \uD83C\uDD35 " + "  \uD83C\uDD36 " + "  \uD83C\uDD37 " + "  \uD83C\uDD38 " + "  \uD83C\uDD39 ");

            for (int i = 0; i < game.length; i++) {
                for (int j = 0; j < game.length; j++) {

                    game[j][i]=" \uD83C\uDF0A "; //water
                    value+=game[j][i];
                }
                value="";
            }
            for (int i = 0; i < coordinatesY.length; i++) {

                int x = (int)Math.ceil(Math.random() * 9);

                int y = (int)Math.ceil(Math.random() * 9);

                coordinatesX[i] = x;
                coordinatesY[i] = y;

                game[x][y] = " \uD83D\uDEA3 "; // boat
            }

            for (int i = 0; i < game.length; i++) {
                for (int j = 0; j < game.length; j++) {
                    value+=game[j][i];

                }
                n++;
                if (n==10){
                    printWriter1.println( "       " + n +" " + value);

                }else {
                    printWriter1.println( "       " + n+"  " + value);

                }
                value="";
            }



        }

    public String[][] getGame() {
        return game;
    }

    public int getCoordinatesX(int index) {
        return coordinatesX[index];

    }

    public int getCoordinatesXSize() {
        return coordinatesX.length;
    }

    public int getCoordinatesY(int index) {
        return coordinatesY[index];
    }

    public int getCoordinatesYSize() {
        return coordinatesY.length;
    }
}




