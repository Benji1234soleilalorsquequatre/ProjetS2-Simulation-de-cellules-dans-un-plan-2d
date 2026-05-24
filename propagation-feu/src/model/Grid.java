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
}