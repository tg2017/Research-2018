package Main;

import GUI.src.Settings;
import Giles.ANN.AdvancedNeuralNetwork;
import Giles.util.CSVReader;
import Giles.util.NewProcessor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static List<List<Double>> examples;
    private static List<List<String>> expectedsStrings;
    private static List<List<Double>> expecteds = new ArrayList<>();
    private static List<String> outputNames = new ArrayList<>();//{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI"};
    private static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static CSVReader inReader;
    private static CSVReader outReader;

    public static final int NUMBERS_NAMES = 0, LETTERS_NAMES = 1, USER_NAMES = 2;
    public static final int CONSTANT = AdvancedNeuralNetwork.CONSTANT;
    public static final int ITERATION_LINEAR = AdvancedNeuralNetwork.ITERATION_LINEAR;
    public static final int ITERATION_QUADRATIC = AdvancedNeuralNetwork.ITERATION_QUADRATIC;
    public static final int ACCURACY_LINEAR = AdvancedNeuralNetwork.ACCURACY_LINEAR;
    public static final int ACCURACY_QUADRATIC = AdvancedNeuralNetwork.ACCURACY_QUADRATIC;

    //Variables that are changes in settings are initialized with defaults
    private static double learningRate = .4;
    private static int iterations = 150000;
    private static int numberOfHiddens = 24;
    private static boolean useDynamic = false;
    private static boolean printMonitor = true;
    private static boolean createPicture = true;
    private static boolean saveReport = false;
    private static boolean saveState = false;
    private static boolean loadState = false;
    private static String inputsFilename;
    private static String outputsFilename;
    private static String reportFilename;
    private static String userNamesFilename;
    private static String saveNetworkFilename;
    private static String loadNetworkFilename;
    private static int outputNamesType = LETTERS_NAMES;
    private static int dynamicType = CONSTANT;

    private static Settings settingsGUI = new Settings();
    private static boolean allDoubles;

    private static AdvancedNeuralNetwork network;


    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Settings().setVisible(true);
            }
        });
    }

    public static void startProgram(){


        inReader = new CSVReader(inputsFilename);
        outReader = new CSVReader(outputsFilename);

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

        //Name the output neurons
        if(outputNamesType == LETTERS_NAMES) {
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
        } else if(outputNamesType == NUMBERS_NAMES){
            //This chunk gives the output neurons numerical names, beginning with 1
            for(int i = 0; i < examples.size(); i++){
                outputNames.add(i, ((Integer)(i+1)).toString());
            }
        } else if(outputNamesType == USER_NAMES) {
            outputNames = new ArrayList(NewProcessor.readAndStoreString(new CSVReader(userNamesFilename)));
        }



        for(List<String> expectedsStringList : expectedsStrings){
            for(String expectedString : expectedsStringList){
                if(!NewProcessor.isDouble(expectedString)){
                    allDoubles = false;
                    break;
                }
            }
        }
        if(allDoubles){
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
            JOptionPane.showMessageDialog(null, "The format of the expected output values provided is not valid.\nPlease provide expected outputLayerSize as names or as doubles.");
        }

        if(printMonitor) {
            System.out.println("Examples: " + examples);
            System.out.println("Expected Outcomes: " + expecteds);
        }

        if(loadState){
            network = new AdvancedNeuralNetwork(loadNetworkFilename);
        } else {
            network = new AdvancedNeuralNetwork(examples.get(0).size(), numberOfHiddens, expecteds.get(0).size());
        }

        System.out.println();
        network.print();

        /*for (List<Double> example : examples) {
            System.out.println("Outputs: " + network.forwardProp(example));
        }*/

        if(saveReport) {
            network.setCSVFile(reportFilename);
        }
        if(createPicture) {
            network.createPicture();
        }
        network.train(examples, expecteds, learningRate, dynamicType, iterations, printMonitor);
        System.out.println(network.test(examples, outputNames).toString());
        if(createPicture) {
            network.createPicture();
        }
        if(saveState) {
            network.saveWB(saveNetworkFilename);
        }
    }




    //Getters and Setters
    public static void setLearningRate(double rate){
        learningRate = rate;
    }
    public static double getLearningRate(){
        return learningRate;
    }

    public static int getIterations() {
        return iterations;
    }
    public static void setIterations(int iterations) {
        Main.iterations = iterations;
    }

    public static int getNumberOfHiddens() {
        return numberOfHiddens;
    }
    public static void setNumberOfHiddens(int numberOfHiddens) {
        Main.numberOfHiddens = numberOfHiddens;
    }

    public static boolean isUseDynamic() {
        return useDynamic;
    }
    public static void setUseDynamic(boolean useDynamic) {
        Main.useDynamic = useDynamic;
    }

    public static boolean isPrintMonitor() {
        return printMonitor;
    }
    public static void setPrintMonitor(boolean printMonitor) {
        Main.printMonitor = printMonitor;
    }

    public static boolean isCreatePicture() {
        return createPicture;
    }
    public static void setCreatePicture(boolean createPicture) {
        Main.createPicture = createPicture;
    }

    public static boolean isSaveReport() {
        return saveReport;
    }
    public static void setSaveReport(boolean saveReport) {
        Main.saveReport = saveReport;
    }

    public static boolean isSaveState() {
        return saveState;
    }
    public static void setSaveState(boolean saveState) {
        Main.saveState = saveState;
    }

    public static boolean isLoadState() {
        return loadState;
    }
    public static void setLoadState(boolean loadState) {
        Main.loadState = loadState;
    }

    public static String getInputsFilename() {
        return inputsFilename;
    }
    public static void setInputsFilename(String inputsFilename) {
        Main.inputsFilename = inputsFilename;
    }

    public static String getOutputsFilename() {
        return outputsFilename;
    }
    public static void setOutputsFilename(String outputsFilename) {
        Main.outputsFilename = outputsFilename;
    }

    public static String getReportFilename() {
        return reportFilename;
    }
    public static void setReportFilename(String reportFilename) {
        Main.reportFilename = reportFilename;
    }

    public static String getUserNamesFilename() {
        return userNamesFilename;
    }
    public static void setUserNamesFilename(String userNamesFilename) {
        Main.userNamesFilename = userNamesFilename;
    }

    public static String getSaveNetworkFilename() {
        return saveNetworkFilename;
    }
    public static void setSaveNetworkFilename(String saveNetworkFilename) {
        Main.saveNetworkFilename = saveNetworkFilename;
    }

    public static String getLoadNetworkFilename() {
        return loadNetworkFilename;
    }
    public static void setLoadNetworkFilename(String loadNetworkFilename) {
        Main.loadNetworkFilename = loadNetworkFilename;
    }

    public static int getOutputNamesType() {
        return outputNamesType;
    }
    public static void setOutputNamesType(int outputNamesType) {
        Main.outputNamesType = outputNamesType;
    }

    public static int getDynamicType() {
        return dynamicType;
    }
    public static void setDynamicType(int dynamicType) {
        Main.dynamicType = dynamicType;
    }
}
