import java.lang.*;
import java.util.*;
import java.io.*;
import java.math.*;

public class Interpreter{
	public static String BASE_PROMPT = "amari $ ";
	public static String ERROR_ADD = "error $ ";
	public static HashMap<String, Object> userDefined = new HashMap<>();

	public static Set<String> BASIC_MATH_OPERATIONS = Set.of("+", "-", "*", "/");

	public static boolean isNumeric(String s){
		try{
			Double d = Double.parseDouble(s);
			return true;
		}
		catch (Exception e){
			return false;
		}
	}

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
		//arr myArray1 + myArray2

		String op = splitString[2];

		if (BASIC_MATH_OPERATIONS.contains(op)){
			//we want to push numeric values
			//that is, non-array values
			//to the end of our expression
			//so that we can process everything
			//in the format <array + number>
			//or even just the regular <array + array>

			if (isNumeric(splitString[1])){
				//here, we get the first two arguments
				//which will basically be something like
				//"arr someNumber"
				//and then we get the right side of the expression
				//and then we switch the first and right hand sides
				String base = splitString[0];
				String first = splitString[1];
				String[] right_hand_side = Arrays.copyOfRange(splitString, 3, splitString.length);
				String[] modifiedInput = new String[splitString.length];
				modifiedInput[0] = base;
				for (int i = 0; i < right_hand_side.length; i++){
					modifiedInput[i + 1] = right_hand_side[i];
				}
				modifiedInput[splitString.length - 2] = op;
				modifiedInput[splitString.length - 1] = first;
				splitString = modifiedInput;
			}

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
			Object parsedSecondArg = parseUserInput(secondArg, false);
			if (parsedSecondArg instanceof ndArrayBase){
				ndArrayBase arr2 = (ndArrayBase) parsedSecondArg;
				return arr1.mathOperation(arr2, op);
			}
			else{
				return null;
			}
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
		else{
			System.out.println(o);
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

		//check if input is numeric type
		if (splitString.length == 1 && isNumeric(baseCommand)){
			int[] shape = {1};
			ArrayList<Comparable> data = new ArrayList<Comparable>();
			data.add(new BigDecimal(baseCommand));
			return new ndArrayBase(data, shape);
		}

		//check if user is creating a new array
		if (baseCommand.equals("arr")){
			ndArrayBase array = arrayCommand(splitString);
			return array;
		}
		else if (baseCommand.equals("ma")){
			return null;
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
			//here we see if the user's input is actually a
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
				if (output != null){
					printObject(output);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}