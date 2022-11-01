import java.lang.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class ndArrayBase extends ArrayList{
	public int dimensionality;
	public int[] shape;
	public boolean scalar;
	public ArrayList<Comparable> data;

	public Comparable get(int index){
		if (!scalar){
			return this.data.get(index);
		}
		else{
			return this.data.get(0);
		}
	}

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
					values.add(this.get(ind));
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

	public BigDecimal convertToBD(Comparable n){
		return new BigDecimal(n.toString());
	}

	public ndArrayBase mathOperation(Object o, String operator){
		if (o instanceof ndArrayBase){
			ndArrayBase array = (ndArrayBase) o;
			//first we need to test if the shapes of the two arrays are the same
			//but one-dimensional arrays are okay, since scalar operations can work
			//so long as we do them element-wise
			int a1dim = this.shape.length;
			int a2dim = array.shape.length;
			if (a1dim != a2dim && !(array.scalar || this.scalar)){
				System.out.printf("Dimensionalities %d and %d do not match!\n", this.shape.length, array.shape.length);
				return null;
			}
			else{
				//check if the shapes actually match
				//if they are unequal at any point, return null
				//after printing out the shapes
				//but again, if one is a scalar, we can ignore
				//these checks
				if (!(array.scalar || this.scalar)){
					for (int i = 0; i < this.shape.length; i++){
						if (array.shape[i] != this.shape[i]){
							System.out.println("Shapes " + this.getShape() + " and " + array.getShape() + "do not match!");
							return null;
						}
					}
				}

				//now that we've passed all our checks, we can add together the
				//values in the array
				//in the case that one of the arrays is a scalar,
				//we need to properly choose the shape for the resulting
				//array
				int[] resultShape;
				if (array.scalar || this.scalar){
					if (array.scalar){
						resultShape = this.shape;
					}
					else{
						resultShape = array.shape;
					}
				}
				else{
					//if neither is a scalar, then by default
					//we know that the shapes match at this point
					//since we've checked for that
					//which means we can assign resultShape to the shape
					//of either of the two arrays
					resultShape = this.shape;
					//resultShape = array.shape would work as well
				}

				int nResultValues = 1;
				for (int i : resultShape){
					nResultValues = nResultValues * i;
				}

				ArrayList<Comparable> newValues = new ArrayList<Comparable>();
				for (int ind = 0; ind < nResultValues; ind++){
					BigDecimal val1 = convertToBD(this.get(ind));
					BigDecimal val2 = convertToBD(array.get(ind));
					BigDecimal newValue = val1.add(val2);
					if (operator.equals("+")){
						newValue = val1.add(val2);
					}
					else if (operator.equals("*")){
						newValue = val1.multiply(val2);
					}
					else if (operator.equals("-")){
						newValue = val1.subtract(val2);
					}
					else if (operator.equals("/")){
						try{
							newValue = val1.divide(val2);
						}
						catch (ArithmeticException e){
							//in case there isn't an exact solution to the division
							//for instance, in the case of 1 / 3
							newValue = val1.divide(val2, 5, RoundingMode.HALF_UP);
						}
					}
					newValues.add(newValue);
				}
				ndArrayBase finalArray = new ndArrayBase(newValues, resultShape);
				return finalArray;
			}
		}
		else if (o instanceof BigDecimal){
			BigDecimal n = (BigDecimal)o;
			ArrayList<Comparable> newValues = new ArrayList<Comparable>();
			for (int ind = 0; ind < this.data.size(); ind++){
				newValues.add(convertToBD(this.get(ind)).add(n));
			}
			ndArrayBase finalArray = new ndArrayBase(newValues, this.shape);
			return finalArray;
		}
		else{
			return null;
		}
	}

	public ndArrayBase(ArrayList<Comparable> c, int[] shape){
		this.data = c;
		int nValues = c.size();
		if (nValues == 1 || (shape.length == 1 && shape[0] == 1)){
			this.scalar = true;
		}
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