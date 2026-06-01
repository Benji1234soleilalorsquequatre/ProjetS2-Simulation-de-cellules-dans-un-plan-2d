package model;
public class Wind {
    private int windSpeed;
    private int windX;
    private int windY;
    public Wind(int windSpeed,int windX,int windY){
        this.windSpeed=windSpeed;
        this.windX=windX;
        this.windY=windY;
    }
    /** 
     * @return int
     */
    public int getWindSpeed(){
        return this.windSpeed;
    }
    /** 
     * @return int
     */
    public int getWindX(){
        return this.windX;
    }
    /** 
     * @return int
     */
    public int getWindY(){
        return this.windY;
    }

}
