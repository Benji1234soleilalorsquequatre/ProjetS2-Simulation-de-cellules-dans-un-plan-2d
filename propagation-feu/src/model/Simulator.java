package model;

public class Simulator {
    private Grid currentGrid;
    private Wind wind;
    private int counter;
    public Simulator(Grid currentGrid, Wind wind){
        this.currentGrid=currentGrid;
        this.wind=wind;
        this.counter=0;
    }
    public void nextStep(){
        int h=currentGrid.getHeight();
        int w=currentGrid.getWidth();
        Grid nextGrid=new Grid(h,w);
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                Cell currentCell= currentGrid.getCell(i,j);
                State currentState= currentCell.getCurrentState();
                State futureState= currentState;
                nextGrid.setCellState( i, j,futureState);
            }
        }
        this.currentGrid=nextGrid;
        this.counter++;
    }



}
