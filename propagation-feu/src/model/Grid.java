/**
 * The Grid class represents a grid structure with cells initialized to a safe state and specific
 * parameters.
 */
package model;
public class Grid{
    private Cell[][] forest;
    private int height;
    private int width;
    public Grid(int height,int width){
        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                this.forest[i][j] = new Cell(State.SAFE,40, 0);
            }
        }
    }
    /** 
     * @return int
     */
    public int getHeight(){
        return this.height;
    }
    /** 
     * @return int
     */
    public int getWidth(){
        return this.width;
    }
    public void displayGrid(){
        for(int i=0;i<height;i++){
            // The line `for(int j=0;j<width;j++){` is a nested loop that iterates over the columns of
            // the grid. It is part of the `displayGrid` method in the `Grid` class. This loop is
            // responsible for iterating over each column in a row of the grid and printing out the
            // corresponding state symbol for each cell in that row.
            for(int j=0;j<width;j++){
                State currentState=this.forest[i][j].getCurrentState();
                String a="a";
                if(currentState==State.SAFE){
                    a="O";
                }
                if(currentState==State.DANGER){
                    a="!";
                }
                if(currentState==State.FIRE){
                    a="X";
                }
                if(currentState==State.DEAD){
                    a=" ";
                }
                System.out.print(a);


            }
            System.out.println();
        }
    }
}