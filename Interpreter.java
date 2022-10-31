import java.lang.*;
import java.util.*;
import java.io.*;

public class Interpreter{
	public static String BASE_PROMPT = "amari $ ";
	public static String ERROR_ADD = "error $ ";

	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		HashMap<String, Object> userDefined = new HashMap<>();

		while (true){
			//user prompt
			System.out.print(BASE_PROMPT);
			String userInput = scan.nextLine();

			//create split string to process user input
			String[] splitString = userInput.split(" ");

			//get the base command
			String baseCommand = splitString[0];

			//check if user is creating a new array
			if (baseCommand.equals("arr")){
				//get the variable name, the shape, and the raw data
				String arrayName = splitString[1];
				String[] shapeStringArray = splitString[2].split(",");
				String[] valueStringArray = splitString[3].split(",");

				int dimensionality = shapeStringArray.length;

				//set up the arraylists for the shape and values
				int[] shape = new int[dimensionality];
				ArrayList<Comparable> values = new ArrayList<Comparable>();

				//fill the shape arraylist
				for (int shapeInd = 0; shapeInd < dimensionality; shapeInd++){
					shape[shapeInd] = Integer.parseInt(shapeStringArray[shapeInd]);
				}

				//fill the value arraylist
				for (String val : valueStringArray){
					//try integer conversion
					try{
						values.add(Integer.parseInt(val));
						continue;
					}
					catch (Exception e){

					}

					try{
						values.add(Double.parseDouble(val));
						continue;
					}
					catch (Exception e){
						values.add(val);
					}
				}

				try{
					ndArrayBase array = new ndArrayBase(values, shape);
					userDefined.put(arrayName, array);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			else{
				//here we see if the user's input is actualy a
				//defined variable
				try{
					Object result = userDefined.get(baseCommand);
					if (result instanceof ndArrayBase){
						ndArrayBase arr = (ndArrayBase) result;
						arr.printSelf();
					}
					if (result == null){
						System.out.println(BASE_PROMPT + ERROR_ADD + "Symbol <" + baseCommand + "> not found!");
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}