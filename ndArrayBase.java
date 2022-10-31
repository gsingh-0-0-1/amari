import java.lang.*;
import java.util.*;
import java.io.*;

public class ndArrayBase extends ArrayList{
	public int dimensionality;
	public int[] shape;
	public ArrayList<Comparable> data;

	public void printSelf(){
		if (this.shape.length > 2){
			System.out.println("Array dimensionality is greater than 2 and is thus difficult to print!");
			System.out.println("Please manually index or print a sub-array.");
		}
		if (this.shape.length == 1){
			for (Comparable val : this.data){
				System.out.print(val);
				System.out.print(" ");
			}
		}
		else{
			for (int i = 0; i < this.shape[0]; i++){
				for (int j = 0; j < this.shape[1]; j++){
					System.out.print(this.data.get(i * this.shape[1] + j));
					System.out.print(" ");
				}
				System.out.printf("\n");
			}
		}
	}

	public ndArrayBase(ArrayList<Comparable> c, int[] shape) throws sizeMismatchException{
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

			throw new sizeMismatchException(message);
		}
		else{
			this.shape = new int[shape.length];
			for (int shapeInd = 0; shapeInd < shape.length; shapeInd++){
				this.shape[shapeInd] = shape[shapeInd];
			}
		}
	}
}