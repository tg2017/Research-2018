package Main;

import Giles.ANN.AdvancedNeuralNetwork;
import Giles.util.CSVReader;
import Giles.util.NewProcessor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ChangingSampleSizeMain {

    private static List<String> outputNames = new ArrayList<>();
    private static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static boolean allDoubles;
    private static List<List<Double>> expecteds = new ArrayList<>();

    private static AdvancedNeuralNetwork network;

    private static CSVReader reader = new CSVReader("C:\\Users\\super\\Documents\\All Files\\School\\Research\\Research-2018\\CSVs\\Profiles1.csv");
    private static CSVReader outReader = new CSVReader("C:\\Users\\super\\Documents\\All Files\\School\\Research\\Research-2018\\CSVs\\Expected-Outputs.txt");
    private static List<List<Double>> examples = new ArrayList<>(NewProcessor.readAndStore(reader));
    private static List<List<String>> expectedsStrings = new ArrayList<>(NewProcessor.readAndStoreString(outReader));
    private static boolean proceed = true;


    public static void main(String[] args) {
        resume();
    }

    public static void finish() {
        network.saveWB("C:\\Users\\super\\Documents\\All Files\\School\\Research\\Research-2018\\Network Saves\\SampleSize" + (examples.size()) + "Save.csv");
        examples.remove((examples.size() - 1));
        expecteds.remove((expecteds.size() - 1));
        if(examples.size() != 0) {
            resume();
        }
    }

    private static void resume() {
        //This chunk gives the output neurons alphabetical names, beginning with A
        int alphabetCounter = -1;
        //Names all output neurons in full sets of 26
        for (int i = 0; i < examples.size() / alphabet.length; i++) {
            if (alphabetCounter == -1) {
                Collections.addAll(outputNames, alphabet);
            } else {
                for (String letter : alphabet) {
                    outputNames.add(alphabet[alphabetCounter] + letter);
                }
            }
            alphabetCounter++;
        }

        //Names remaining output neurons
        for (int k = 0; k < (examples.size() % alphabet.length); k++) {
            if (alphabetCounter == -1) {
                outputNames.add(alphabet[k]);
            } else {
                outputNames.add(alphabet[alphabetCounter] + alphabet[k]);
            }
        }


        for (List<String> expectedsStringList : expectedsStrings) {
            for (String expectedString : expectedsStringList) {
                if (!NewProcessor.isDouble(expectedString)) {
                    allDoubles = false;
                    break;
                }
            }
        }
        if (allDoubles) {
            List<Double> tempList;
            for (List<String> currentList : expectedsStrings) {
                tempList = new ArrayList<>();
                for (String currentString : currentList) {
                    tempList.add(NewProcessor.convertToDouble(currentString));
                }
                expecteds.add(new ArrayList<>(tempList));
            }
        } else if (expectedsStrings.size() == 1) {
            List<Double> tempList;
            for (int i = 0; i < expectedsStrings.get(0).size(); i++) { //Each element of Strings list (each letter)
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
            JOptionPane.showMessageDialog(null, "The format of the expected output values provided is not valid.\nPlease provide expected outputLayerSize as names or as doubles.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        network = new AdvancedNeuralNetwork(examples.get(0).size(), 24, expecteds.get(0).size());


        network.setCSVFile("C:\\Users\\super\\Documents\\All Files\\School\\Research\\Research-2018\\Reports\\SampleSize" + (examples.size()) + "Report.csv");
        System.out.println(examples.size());
        network.train(examples, expecteds, .4, AdvancedNeuralNetwork.ACCURACY_LINEAR, 100, false, true);

    }
}