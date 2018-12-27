import Giles.ANN.FancyNeuralNetwork;
import Giles.util.CSVReader;
import Giles.util.NewProcessor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private static List<List<Double>> examples;
    private static List<List<String>> expectedsStrings;
    private static List<List<Double>> expecteds = new ArrayList<>();
    private static List<String> outputNames = new ArrayList<>();//{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI"};
    private static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static CSVReader inReader = new CSVReader("C:\\Users\\super\\Desktop\\Profiles.csv");
    private static CSVReader outReader = new CSVReader("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\outputtest.txt");

    public static void main(String[] args) {
        examples = new ArrayList<>(NewProcessor.readAndStore(inReader));
        expectedsStrings = new ArrayList<>(NewProcessor.readAndStoreString(outReader));


        //Change all example values to activations
        for(List<Double> example : examples){
            for(Double value : example){
                example.set(example.indexOf(value),
                        //NewProcessor.sigmoid(
                         value
                        // )
                        );
            }
        }


        //This chunk gives the outputs alphabetical names, beginning with A
        int alphabetCounter = -1;
        //Names all outputs in full sets of 26
        for(int i = 0; i < examples.size() / alphabet.length; i++){
            if(alphabetCounter == -1){
                Collections.addAll(outputNames, alphabet);
            } else {
                for (String letter : alphabet) {
                    outputNames.add(alphabet[alphabetCounter] + letter);
                }
            }
            alphabetCounter++;
        }

        //Names remaining outputs
        for(int k = 0; k < (examples.size() % alphabet.length); k++){
            if(alphabetCounter == -1){
                outputNames.add(alphabet[k]);
            } else {
                outputNames.add(alphabet[alphabetCounter] + alphabet[k]);
            }
        }

        //Sets the "expecteds" values based on the output names, if the expected values provided are in the form of names
        if(NewProcessor.isDouble(expectedsStrings.get(0).get(0))){
            List<Double> tempList;
            for (List<String> currentList : expectedsStrings) {
                tempList = new ArrayList<>();
                for (String currentString : currentList) {
                    tempList.add(NewProcessor.convertToDouble(currentString));
                }
                expecteds.add(new ArrayList<>(tempList));
            }
        } else if(expectedsStrings.size() == 1) {
            List<Double> tempList;
            for(int i = 0; i < expectedsStrings.get(0).size(); i++){ //Each element of Strings list (each letter)
                tempList = new ArrayList<>();
                for (String outputName : outputNames) { //Each element of output names
                    if (expectedsStrings.get(0).get(i).equals(outputName)) {
                        tempList.add(1.0);
                    } else {
                        tempList.add(0.0);
                    }
                }
                expecteds.add(new ArrayList<>(tempList));
            }
        } else {
            JOptionPane.showMessageDialog(null, "The format of the expected output values provided is not valid.\nPlease provide expected outputs as names or as doubles.");
        }

        System.out.println("Examples: " + examples);
        System.out.println("Expected Outcomes: " + expecteds);

        FancyNeuralNetwork network = new FancyNeuralNetwork(examples.get(0).size(), 20, expecteds.get(0).size());
        //FancyNeuralNetwork network = new FancyNeuralNetwork("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\WBTest.csv");
        System.out.println();
        network.print(); 

        /*for (List<Double> example : examples) {
            System.out.println("Outputs: " + network.forwardProp(example));
        }*/


        network.setCSVFile("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\CSVTest.csv");
        network.train(examples, expecteds, .15, 250000);
        //network.saveWB("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\WBTest.csv");
    }
}
