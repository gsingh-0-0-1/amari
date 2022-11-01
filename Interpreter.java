import java.lang.*;
import java.util.*;
import java.io.*;

public class Interpreter{
	public static String BASE_PROMPT = "amari $ ";
	public static String ERROR_ADD = "error $ ";
	public static HashMap<String, Object> userDefined = new HashMap<>();

	public static ndArrayBase declareNewArray(String[] splitString){
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
			return array;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static ndArrayBase arrayCommand(String[] splitString){
		//in general, this command should be formatted as such:
		//arr <arrayname> <keyword> <arraytwo>
		//for instance:
		//arr myArray1 add myArray2

		if (splitString[2].equals("+")){
			ndArrayBase arr1 = (ndArrayBase) userDefined.get(splitString[1]);

			//we can also have a command that looks like this:
			//arr myArray1 add arr myArray2 add myArray3
			//which should add myArray2 and myArray3
			//and then add the result to myArray1
			//and so we need to construct a string from the given
			//String array (without the first three elements, which
			//are the keyword 'arr', the name of the first array,
			//and the keyword 'add'
			String[] secondArgArray = new String[splitString.length - 3];
			for (int i = 0; i < splitString.length - 3; i++){
				secondArgArray[i] = splitString[i + 3];
			}
			String secondArg = String.join(" ", secondArgArray);
			return arr1.mathAdd((ndArrayBase) parseUserInput(secondArg, false));
		}
		else{
			ndArrayBase array = declareNewArray(splitString);
			return array;
		}
	}

	public static void printObject(Object o){
		if (o instanceof ndArrayBase){
			ndArrayBase arr = (ndArrayBase) o;
			arr.printSelf();
		}
	}

	public static void printUserVariable(String symbol){
		try{
			Object result = userDefined.get(symbol);
			if (result != null){
				printObject(result);
			}
			else{
				System.out.println(BASE_PROMPT + ERROR_ADD + "Symbol <" + symbol + "> not found!");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static Object parseUserInput(String userInput, boolean mainThread){
		//create split string to process user input
		String[] splitString = userInput.split(" ");

		//get the base command
		String baseCommand = splitString[0];


		//check if user is creating a new array
		if (baseCommand.equals("arr")){
			ndArrayBase array = arrayCommand(splitString);
			return array;
		}
		else if (baseCommand.equals("exit")){
			System.exit(0);
			return null;
		}
		else if (splitString.length >= 3 && splitString[1].equals("=")){
			//in this case, we have input that looks like:
			//<someVarName> = <someExpression>
			//so we can define a variable conveniently like this
			//by using the .put function for our hashmap
			//and then applying parseUserInput to the right side
			//of the expression
			String[] rightSide = new String[splitString.length - 2];
			for (int i = 0; i < splitString.length - 2; i++){
				rightSide[i] = splitString[i + 2];
			}
			String rightSideExpression = String.join(" ", rightSide);
			userDefined.put(splitString[0], parseUserInput(rightSideExpression, false));
			return userDefined.get(splitString[0]);
		}
		else{
			//here we see if the user's input is actualy a
			//defined variable
			if (mainThread){
				printUserVariable(baseCommand);
				return null;
			}
			else{
				return userDefined.get(baseCommand);
			}
		}
	}

	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);

		while (true){
			//user prompt
			try{
				System.out.print(BASE_PROMPT);
				String userInput = scan.nextLine();
				Object output = parseUserInput(userInput, true);
				printObject(output);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}