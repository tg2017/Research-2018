package Giles.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewProcessor extends DataProcessor {
    public static double sigmoid(double x){
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }

    public static double dSigmoid(double x){
        return sigmoid(x) * (1-sigmoid(x));
    }

    //Returns the derivative of a value that has already had the sigmoid function applied
    public static double deriveSigmoid(double sigmoidValue){return sigmoidValue * (1-sigmoidValue);}

    //Reads in data from csv file and stores them in Lists
    public static List<List<Double>> readAndStore(CSVReader reader){
        List<List<Double>> theList = new ArrayList<>();

        //Read in values from csv, separated by each new line
        reader.setSplitString("new line");

        //Get values from CSVReader
        String[] initialArray = reader.getValues();

        List<List> tempValues = new ArrayList<>();

        //Split values by comma and add to tempValues
        for (String initialArrayElement : initialArray) {
            List parsedArray = Arrays.asList(initialArrayElement.split("\\s*,\\s*"));
            tempValues.add(parsedArray);
        }

        //Remove blank ("") values from arrays and store new arrays in finalValues
        List<List> finalValues = new ArrayList<>();
        for (List tempElement : tempValues) {
            finalValues.add(NewProcessor.removeBlanks(tempElement));
        }

        List<Double> currentData = new ArrayList<>();

        for (List currentDataStrings : finalValues) {
            currentData = new ArrayList<>();
            for (Object currentDataAsString : currentDataStrings) {
                currentData.add(NewProcessor.convertToDouble(currentDataAsString));
            }
            theList.add(currentData);
        }
        return theList;
    }
}
