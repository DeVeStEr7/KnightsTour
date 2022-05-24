package StackGame;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.undo.CannotUndoException;


public class Driver {

    final static int rowL = 5;//number of rows for chess board
    final static int colL = 5;//number of cols for chess board
    static Stack<Location> stack = new Stack<Location>(); //store moves in order (backtrack capability)

    //list of exhausted locations for each location.  Must use method convertLocToIndex to find a Locations proper index
    static ArrayList<ArrayList<Location>> exhausted = new ArrayList<ArrayList<Location>>(rowL*colL);
    static int board[][] = new int[rowL][colL];//2d array used to store the order of moves
    static boolean visited[][] = new boolean[rowL][colL];//2d array used to store what locations have been used
    static Location startLoc;

    static long startTime;
    static long endTime;
    static int numOfLoops = 0;
    static int numOfIfs = 0;

    public static void main(String[] args) {

        System.out.println("START");
        initExhausted();
        ArrayList<Location> currentPossible;
        obtainStartLoc();
        //System.out.println("Start Loc is " + startLoc);

        startTime = System.currentTimeMillis();
        stack.push(startLoc);
        visited[startLoc.row][startLoc.col] = true;
        Location prevLoc = startLoc;
        while(stack.size() != rowL *colL && stack.size() != 0)
        {
            numOfLoops++;
            //printPossibleMoveLocations(prevLoc);
            currentPossible = getPossibleMoves(prevLoc);
            Location currentLoc = getNextMove(prevLoc,currentPossible);
            if(currentLoc == null) {
                numOfIfs++;
                clearExhausted(prevLoc);
                //printExhausedList(prevLoc);
                stack.pop();
                board[prevLoc.getRow()][prevLoc.getCol()] = 0;
                if(stack.size() > 0) {
                    numOfIfs++;
                    prevLoc = stack.peek();
                }
                //System.out.println(prevLoc);
            }
            else if(inExhausted(prevLoc,currentLoc)) {
                numOfIfs++;
                clearExhausted(prevLoc);
                //printExhausedList(prevLoc);
                stack.pop();
                prevLoc = stack.peek();
                //System.out.println(prevLoc);
            }
            else {
                addToExhausted(prevLoc,currentLoc);
                stack.push(currentLoc);
                //just added
                board[currentLoc.getRow()][currentLoc.getCol()] = stack.size();
                //printExhausedList(prevLoc);
                prevLoc = currentLoc;
            }
            //printBoard();
        }
        endTime = System.currentTimeMillis();
        System.out.println("Total Time (ms): " + (endTime-startTime));
        System.out.println("Total loops: " + numOfLoops);
        System.out.println("Total Ifs: " + numOfIfs);
        if(stack.size() == 0) {
            System.out.print("It's Impossible for this point");
        }

        else {
            printBoard();
        }
    }

    /*
     * Printed out the exhausted list for a given Location
     */
    public static void printExhausedList(Location loc)
    {
        System.out.print("The exhausted list for " + loc + " is: ");
        ArrayList currentLoc = exhausted.get(convertLocToIndex(loc));
        for(int i = 0; i < currentLoc.size(); i++) {
            System.out.print(currentLoc.get(i) + " ");
        }
        System.out.println();

    }

    /*
     * Prints out the possible move locations for a given Location
     */
    public static void printPossibleMoveLocations(Location loc)
    {
        System.out.print("The possible moves for " + loc + " :");
        ArrayList<Location> possibleMoves = getPossibleMoves(loc);
        for(int i = 0; i < possibleMoves.size(); i++) {
            System.out.print(possibleMoves.get(i) + " ");
        }
        System.out.println();
    }

    /*
     * prints out the board (numbers correspond to moves)
     */
    public static void printBoard()
    {
        //Location newestNode = stack.peek();
        for(int i = 0; i < rowL; i++) {
            for(int j = 0; j < colL; j++) {
                /*
                if (board[i][j] > stack.size()){
                    board[i][j] = 0;
                }


                if(convertLocToIndex(newestNode) == convertLocToIndex(new Location(i,j))) {
                    board[i][j] = stack.size();
                }
                */
                System.out.print("  " + board[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }
    /*
     * prints out true/false for what spaces have been visited
     */
    public static void printVisited()
    {
        for(int i = 0; i < visited.length; i++) {
            for(int j = 0; j < visited[i].length; j++) {
                if(board[i][j] > 0)
                    System.out.print("  " + true + "  ");
                else
                    System.out.print("  " + false + "  ");
            }
            System.out.println();
        }
    }

    /*
     * clear out the exhausted list for a given Location
     * This needs to be done everytime a Location is removed from the Stack
     */
    public static void clearExhausted(Location loc)
    {
        ArrayList currentList = exhausted.get(convertLocToIndex(loc));
        while(currentList.size() != 0) {
            numOfLoops++;
            currentList.remove(0);
        }
    }

    /*
     * set up the exhausted list with empty exhausted lists.
     */
    public static void initExhausted()
    {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                exhausted.add(new ArrayList<Location>());
            }
        }
    }

    /*
     * is this dest Location exhausted from the source Location
     */
    public static boolean inExhausted(Location source, Location dest)
    {
        ArrayList<Location> exhaustedLoc =  exhausted.get(convertLocToIndex(source));
        for(int i = 0; i < exhaustedLoc.size(); i++) {
            numOfLoops++;
            if(exhaustedLoc.get(i).getRow() == dest.getRow() && exhaustedLoc.get(i).getCol() == dest.getCol()) {
                //added
                numOfIfs++;
                board[dest.getRow()][dest.getCol()] = 0;
                return true;
            }
        }
        return false;
    }

    /*
     * returns the next valid move for a given Location on a given ArrayList of possible moves
     */
    public static Location getNextMove(Location loc, ArrayList<Location> list) {
        if (list.size() == 0) {
            numOfIfs++;
            return null;
        }
        Location nextMove = new Location();
        int counter = 0;
        nextMove = list.get(counter); //0 in the list
        while(inExhausted(loc, nextMove)){
            numOfLoops++;
            counter++;
            if(counter >= list.size()) {
                numOfIfs++;
                return null;
            }
            nextMove = list.get(counter); //1 in the list
        }
        return nextMove;
    }

    /*
     * converts a (row,col) to an array index for use in the exhausted list
     */
    public static int convertLocToIndex(Location loc)
    {
        return (loc.getRow()*rowL) + loc.getCol();
    }

    /*
     * adds a dest Location in the exhausted list for the source Location
     */
    public static void addToExhausted(Location source, Location dest)
    {
        exhausted.get(convertLocToIndex(source)).add(dest);;
    }

    /*
     * is this Location a valid one
     */
    public static boolean isValid(Location loc)
    {
        return board[loc.getRow()][loc.getCol()] == 0;
    }

    /*
     * returns another Location for the knight to move in.  If no more possible move
     * locations exist from Location loc, then return null
     */
    public static ArrayList<Location> getPossibleMoves(Location loc)
    {
        ArrayList<Location> possibleMoves = new ArrayList<Location>();
        if(loc.getRow() < rowL-2 && loc.getCol() < colL-1){                 //if we are in 1st case 2
            numOfIfs++;
            if(isValid(new Location(loc.getRow()+2,loc.getCol()+1))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() + 2, loc.getCol() + 1));
            }
        }
        if(loc.getRow() < rowL-1 && loc.getCol() < colL-2){                 //if we are in 2nd case 4
            numOfIfs++;
            if(isValid(new Location(loc.getRow()+1,loc.getCol()+2))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow()+1,loc.getCol()+2));
            }
        }
        if(loc.getRow() < rowL-2 && loc.getCol() > 0){                 //if we are in 3rd case 3
            numOfIfs++;
            if(isValid(new Location(loc.getRow()+2,loc.getCol()-1))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() + 2, loc.getCol() - 1));
            }
        }
        if(loc.getRow() < rowL-1 && loc.getCol() > 1){                 //if we are in 4th case 5
            numOfIfs++;
            if(isValid(new Location(loc.getRow()+1,loc.getCol()-2))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() + 1, loc.getCol() - 2));
            }
        }
        if(loc.getRow() > 0 && loc.getCol() < colL-2){                 //if we are in 5th case 6
            numOfIfs++;
            if(isValid(new Location(loc.getRow()-1,loc.getCol()+2))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() - 1, loc.getCol() + 2));
            }
        }
        if(loc.getRow() > 0 && loc.getCol() > 1){                 //if we are in 6th case 7
            numOfIfs++;
            if(isValid(new Location(loc.getRow()-1,loc.getCol()-2))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() - 1, loc.getCol() - 2));
            }
        }
        if(loc.getRow() > 1 && loc.getCol() > 0){                 //if we are in 7th case 8
            numOfIfs++;
            if(isValid(new Location(loc.getRow()-2,loc.getCol()-1))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() - 2, loc.getCol() - 1));
            }
        }
        if(loc.getRow() > 1 && loc.getCol() < colL-1){                 //if we are in 8th case 9
            numOfIfs++;
            if(isValid(new Location(loc.getRow()-2,loc.getCol()+1))) {
                numOfIfs++;
                possibleMoves.add(new Location(loc.getRow() - 2, loc.getCol() + 1));
            }
        }
        return possibleMoves;
    }


    public static Location getPossibleMovesImproved(Location loc)
    {
        ArrayList<Location> possibleMovesImproved = new ArrayList<Location>(getPossibleMoves(loc));
        Location leastLoc = possibleMovesImproved.get(0);
        for(int i = 0; i < possibleMovesImproved.size(); i++) {
            if(getPossibleMoves(leastLoc).size() > getPossibleMoves(possibleMovesImproved.get(i)).size()) {
                leastLoc = possibleMovesImproved.get(i);
            }
        }

        return leastLoc;
    }
    /*
     * prompt for input and read in the start Location
     */
    public static void obtainStartLoc()
    {
        Scanner input = new Scanner(System.in);
        int row;
        int col;
        System.out.println("What location would you like (row,col)");
        String answer = input.next();
        row = Integer.parseInt(answer.substring(1,answer.indexOf(",")));
        col = Integer.parseInt(answer.substring(answer.indexOf(",")+1, answer.length()-1));
        startLoc = new Location(row, col);
        board[row][col] = 1;
        //System.out.println(startLoc.toString());
    }

}

/*
   1 2 3 4 _               1  2  3  4  5           2 3 4 3 2
   3 4 1 2 _               6  7  8  9  10          3 4 6 4 3
   2 1 23 4 _              11 12 13 14 15          4 6 8 6 4
   4 3 4 _ _               16 17 18 19 20          3 4 6 4 3
   _ _ _ _ _               21 22 23 24 25          2 3 4 3 2

   possible moves :    2:i+2 j+1,   indexSpots of 1,2,3,4      6,7,8,9     11,12,13,14
                       3:i+2 j-1,   indexSpots of 2,3,4,5      7,8,9,10    12,13,14,15
                       4:i+1 j+2,   indexSpots of 1,2,3,       6,7,8       11,12,13        16,17,18
                       5:i+1 j-2,   indexSpots of 3,4,5        8,9,10      13,14,15        18,19,20
                       6:i-1 j+2,   indexSpots of 6,7,8        11,12,13    16,17,18        21,22,23
                       7:i-1 j-2,   indexSpots of 8,9,10       13,14,15    18,19,20        23,24,25
                       8:i-2 j-1,   indexSpots of 12,13,14,15  17,18,19,20 22,23,24,25
                       9:i-2 j+1    indexSpots of 11,12,13,14  16,17,18,19 21,22,23,24

   _ 8 _ 9 _
   7 _ _ _ 6
   _ _ 1 _ _
   5 _ _ _ 4
   _ 3 _ 2 _
*/
