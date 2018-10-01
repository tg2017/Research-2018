import Giles.util.CSVReader;
import Giles.util.NewProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static List<List<Double>> examples = new ArrayList<>();
    private static List<List<Double>> expecteds = new ArrayList<>();

    private static CSVReader inReader = new CSVReader("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\frequencies.txt");
    private static CSVReader outReader = new CSVReader("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\outputs.txt");

    public static void main(String[] args) {
        readAndStore(inReader, examples);
        readAndStore(outReader, expecteds);

        System.out.println("Examples: " + examples);
        System.out.println("Expected Outcomes: " + expecteds);

        NeuralNetwork network = new NeuralNetwork(examples.get(0).size(), 20, expecteds.get(0).size());
        System.out.println();
        network.print();

        /*for (List<Double> example : examples) {
            System.out.println("Outputs: " + network.forwardProp(example));
        }*/

        network.train(examples, expecteds, .3, 50000, "C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\CSVTest.csv");



    }

    //Reads in data from csv file and stores them in Lists
    private static void readAndStore(CSVReader reader, List<List<Double>> theList){
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
    }
}
