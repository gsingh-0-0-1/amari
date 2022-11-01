import java.lang.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class ndArrayBase extends ArrayList{
	public int dimensionality;
	public int[] shape;
	public ArrayList<Comparable> data;

	public String getShape(){
		String[] stringShape = new String[this.shape.length];
		for (int i = 0; i < this.shape.length; i++){
			stringShape[i] = Integer.toString(this.shape[i]);
		}
		return String.join(",", stringShape);
	}

	public void printSelf(){
		//we can recursively reduce the dimensionality of the given array
		//that we're trying to print until it becomes 1 dimensional
		//then we can print just the row
		if (this.shape.length > 1){
			//create the shape of the subarray, which will be the shape of
			//this array but stripped of the first axis
			int[] newShape = new int[this.shape.length - 1];
			for (int i = 1; i < this.shape.length; i++){
				newShape[i - 1] = this.shape[i];
			}

			//find the number of elements in the subarray
			int newShapeProduct = 1;
			for (int axisLen : newShape){
				newShapeProduct = newShapeProduct * axisLen;
			}

			//get the values for the subarray
			for (int subArrayInd = 0; subArrayInd < this.shape[0]; subArrayInd++){
				ArrayList<Comparable> values = new ArrayList<Comparable>();

				//this loop just indexes the current array
				//to fetch the desired values for the subarray
				//so, if we have an initial array of shape 4,3,3
				//for every subArrayInd from 0 to 3, we'll get 9
				//values which we will use to fill an array of shape 3,3
				//that will happen four times as determined by the outer
				//loop here that iterates over subArrayInd
				int startValue = subArrayInd * newShapeProduct;
				int endValue = (subArrayInd + 1) * newShapeProduct;
				for (int ind = startValue; ind < endValue; ind++){
					values.add(this.data.get(ind));
				}

				ndArrayBase subArray = new ndArrayBase(values, newShape);
				subArray.printSelf();

				for (int newlineInd = 0; newlineInd < this.shape.length - 2; newlineInd++){
					System.out.print("\n");
				}
			}

		}
		else{
			for (Comparable val : this.data){
				System.out.print(val);
				System.out.print(" ");
			}
			System.out.print("\n");
		}
	}

	public ndArrayBase mathAdd(ndArrayBase array){
		//first we need to test if the shapes of the two arrays are the same
		if (array.shape.length != this.shape.length){
			System.out.printf("Dimensionalities %d and %d do not match!\n", this.shape.length, array.shape.length);
			return null;
		}
		else{
			//check if the shapes actually match
			//if they are unequal at any point, return null
			//after printing out the shapes
			for (int i = 0; i < this.shape.length; i++){
				if (array.shape[i] != this.shape[i]){
					System.out.println("Shapes " + this.getShape() + " and " + array.getShape() + "do not match!");
					return null;
				}
			}

			//now that we've passed all our checks, we can add together the
			//values in the array
			ArrayList<Comparable> newValues = new ArrayList<Comparable>();
			for (int ind = 0; ind < this.data.size(); ind++){
				BigDecimal val1 = new BigDecimal(this.data.get(ind).toString());
				BigDecimal val2 = new BigDecimal(array.data.get(ind).toString());
				newValues.add(val1.add(val2));
			}
			ndArrayBase finalArray = new ndArrayBase(newValues, this.shape);
			return finalArray;
		}
	}

	public ndArrayBase(ArrayList<Comparable> c, int[] shape){
		this.data = c;
		int nValues = c.size();
		int shapeProduct = 1;

		for (int i : shape){
			shapeProduct *= i;
		}

		if (shapeProduct != nValues){
			String message = String.format("Array size <%d> is not compatible with shape <", nValues);

			for (int i = 0; i < shape.length; i++){
				message = message + Integer.toString(shape[i]);

				if (i != shape.length - 1){
					message = message + ", ";
				}
				else{
					message = message + ">!";
				}
			}

			System.out.println(message);
		}
		else{
			this.shape = new int[shape.length];
			for (int shapeInd = 0; shapeInd < shape.length; shapeInd++){
				this.shape[shapeInd] = shape[shapeInd];
			}
		}
	}
}