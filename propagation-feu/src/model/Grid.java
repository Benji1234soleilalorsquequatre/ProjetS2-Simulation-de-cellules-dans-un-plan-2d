/**
 * The Grid class represents a grid structure with cells initialized to a safe state and specific
 * parameters.
 */
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
    /**
     * The `getCell` function returns the cell at the specified row and column coordinates within a
     * forest grid if the coordinates are within the grid boundaries.
     * 
     * @param row The `row` parameter represents the row index of the cell in a 2D grid or matrix. It
     * is used to specify the vertical position of the cell within the grid.
     * @param col The `col` parameter in the `getCell` method represents the column index of the cell
     * that you want to retrieve from the `forest` array. It is used to specify the column position of
     * the cell within the 2D array.
     * @return The method is returning a Cell object located at the specified row and column in the
     * forest array. If the row and column values are within the valid range, it returns the Cell
     * object at that position. If the row and column values are outside the valid range, it returns
     * null.
     */
    public Cell getCell(int row,int col){
        if(row>=0 && row<height && col>=0 && col<width){
            return this.forest[row][col];
        }
        return null;

    }
    // The `setCellState` method in the `Grid` class is responsible for updating the state of a
    // specific cell in the grid at the given row and column indices with a new state provided as
    // `newState`. Here's a breakdown of what the method does:
    public void setCellState(int row,int col, State newState){
         if(row>=0 && row<height && col>=0 && col<width){
            this.forest[row][col]=new Cell(newState, 40,0);
         }

    }
}   