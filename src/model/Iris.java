package model;
/**
 * Model class for Irises.
 * 
 * The relevant attributes/features are:
 * 	- sepalLength;
 * 	- sepalWidth;
 * 	- length //Length of the flower itself;
 * 	- width //Width of the flower itself;
 * 
 * The target field is the type field. This is the value
 * that should be determined by the classification algorithm.
 * @author Basit
 *
 */
public class Iris {
	private double sepalLength;
	private double sepalWidth;
	private double length;
	private double width;
	private IrisType type;
	
	public Iris(){
		this(0.0, 0.0, 0.0, 0.0, null);
	}
	
	public Iris(double sLength,double sWidth,double length, double width, String type){
		sepalLength=sLength;
		sepalWidth=sWidth;
		this.length=length;
		this.width=width;
		
		if(type==null||type==""){
			this.type=null;
			return;
		}
		
		switch(type){
		case "Iris-setosa":
			this.type=IrisType.IRIS_SETOSA;
			break;
		case "Iris-versicolor":
			this.type=IrisType.IRIS_VERSICOLOUR;
			break;
		case "Iris-virginica":
			this.type=IrisType.IRIS_VIRGINICA;
			break;
		}
	}
	
	public double getSepalLength() {
		return sepalLength;
	}
	
	public void setSepalLength(double sepalLength) {
		this.sepalLength = sepalLength;
	}
	
	public double getSepalWidth() {
		return sepalWidth;
	}
	
	public void setSepalWidth(double sepalWidth) {
		this.sepalWidth = sepalWidth;
	}
	
	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public double getWidth() {
		return width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	public IrisType getType() {
		return type;
	}
	
	public void setType(IrisType type) {
		this.type = type;
	}
}
