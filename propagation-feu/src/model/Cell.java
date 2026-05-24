package model;
public class Cell{
    private State currentState;
    private State nextState;
    private int humidity;
    private int heat;
    public Cell(State currentState, int humidity,int heat){
        this.currentState=currentState;
        this.nextState= currentState;
        this.humidity=humidity;
        this.heat=heat;

    }
   
    public State getCurrentState(){
        return this.currentState;
    }
    public State getNextState(){
        return this.nextState;
    }
    public int getHumidity(){
        return this.humidity;
    }
    public int getHeat(){
        return this.heat;
    }
    @Override
    public String toString(){
        return this.getCurrentState() +" "+ this.getHumidity() + " "+ this.getHeat();
    }
}