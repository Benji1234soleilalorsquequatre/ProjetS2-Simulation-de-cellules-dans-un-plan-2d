package model;
public class Main{
    /** 
     * @param args
     */
    /**
     * The main function in Java with an empty body.
     * 
     * @param args The `args` parameter in the `main` method is an array of strings that allows you to
     * pass command-line arguments to your Java program when it is executed. You can access these
     * arguments within your program to customize its behavior based on the input provided at runtime.
     */
    public static void main(String [] args){
        Grid foret=new Grid(5,5);
        foret.displayGrid();
        
    }
}