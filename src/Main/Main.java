package Main;

import GUI.src.Settings;
import Giles.ANN.AdvancedNeuralNetwork;
import Giles.util.CSVReader;
import Giles.util.NewProcessor;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    /** Variable Declarations **/
    private static List<List<Double>> examples;
    private static List<List<Double>> expecteds = new ArrayList<>();
    private static List<String> outputNames = new ArrayList<>();
    private static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static String savedFiles;

    //Constants
    public static final int NUMBERS_NAMES = 0, LETTERS_NAMES = 1, USER_NAMES = 2;
    public static final int CONSTANT = AdvancedNeuralNetwork.CONSTANT;
    public static final int ITERATION_LINEAR = AdvancedNeuralNetwork.ITERATION_LINEAR;
    public static final int ITERATION_QUADRATIC = AdvancedNeuralNetwork.ITERATION_QUADRATIC;
    public static final int ACCURACY_LINEAR = AdvancedNeuralNetwork.ACCURACY_LINEAR;
    public static final int ACCURACY_QUADRATIC = AdvancedNeuralNetwork.ACCURACY_QUADRATIC;

    //Variables that are changed in settings are initialized with defaults
    private static double learningRate = .4;
    private static int iterations = 150000;
    private static int numberOfHiddens = 24;
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

    private static boolean allDoubles;

    private static AdvancedNeuralNetwork network;
    /**End of Variable Declarations**/

    //Starts the GUI and saves the filenames
    public static void main(String[] args) {
        savedFiles = System.getProperty("user.dir") + "\\filenames.txt";
        CSVReader filenamesReader = new CSVReader(savedFiles);
        filenamesReader.setSplitString("new line");
        String[] filenames = filenamesReader.getValues();

        //<editor-fold defaultstate="collapsed" desc=" Appearance-setting code">
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

        /* Create and display the GUI */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Settings settingsGUI = new Settings();
                if(!filenames[0].equals("empty") && !filenames[1].equals("empty")) {
                    settingsGUI.setInputsFilename(filenames[0]);
                    settingsGUI.setOutputsFilename(filenames[1]);
                }
                settingsGUI.setVisible(true);
            }
        });
    }

    //Runs the neural network training process and, at the end, gets network predictions
    public static void startProgram(){
        //Save the filenames for use in future runthroughs
        changeFilenames(new String[]{inputsFilename, outputsFilename});

        CSVReader inReader = new CSVReader(inputsFilename);
        CSVReader outReader = new CSVReader(outputsFilename);

        examples = new ArrayList<>(NewProcessor.readAndStore(inReader));
        List<List<String>> expectedsStrings = new ArrayList<>(NewProcessor.readAndStoreString(outReader));

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
            JOptionPane.showMessageDialog(null, "The format of the expected output values provided is not valid.\nPlease provide expected outputLayerSize as names or as doubles.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if(printMonitor) {
            System.out.println("Examples: " + examples);
            System.out.println("Expected Outcomes: " + expecteds);
        }

        //Create the network object
        if(loadState){
            network = new AdvancedNeuralNetwork(loadNetworkFilename);
        } else {
            network = new AdvancedNeuralNetwork(examples.get(0).size(), numberOfHiddens, expecteds.get(0).size());
        }

        if(printMonitor) {
            System.out.println();
            network.print();
        }

        /*for (List<Double> example : examples) {
            System.out.println("Outputs: " + network.forwardProp(example));
        }*/

        if(saveReport) {
            network.setCSVFile(reportFilename);
        }
        if(createPicture) {
            network.createPicture();
        }

        //Train
        network.train(examples, expecteds, learningRate, dynamicType, iterations, printMonitor);
        System.out.println(network.test(examples, outputNames).toString());
        if(createPicture) {
            network.createPicture();
        }
        if(saveState) {
            network.saveWB(saveNetworkFilename);
        }

        //Show final prediction results
        for(List<Double> example : examples){
            network.printExampleSummary(example, expecteds.get(examples.indexOf(example)), outputNames);
        }
    }

    //Writes to savedFiles file
    public static void changeFilenames(String[] newFileNames){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(savedFiles, false))) {

            String newFilenames= "";//Will store new filenames as single String, with "\r\n"'s

            //Copy values of newFileNames into one string, newFilenames, with "\r\n"'s
            for(int i = 0; i < newFileNames.length; i++) {
                if (i == 0) {
                    newFilenames += newFileNames[i];
                } else {
                    newFilenames += "\r\n" + newFileNames[i];
                }
            }

            //Write data to file
            bw.write(newFilenames);

            //Display confirmation message
            System.out.println("\n\nSuccessfully changed filename");
            //JOptionPane.showMessageDialog(null, "Successfully changed filename");


        } catch (IOException e) {
            //Print error message if exception is caught
            e.printStackTrace();
            System.out.println("Error changing filename in file: " + savedFiles);
            JOptionPane.showMessageDialog(null, "Error changing data in file: " + savedFiles);
        }

    }

    //Setters
    public static void setLearningRate(double rate){
        learningRate = rate;
    }
    public static void setIterations(int iterations) {
        Main.iterations = iterations;
    }
    public static void setNumberOfHiddens(int numberOfHiddens) {
        Main.numberOfHiddens = numberOfHiddens;
    }
    public static void setPrintMonitor(boolean printMonitor) {
        Main.printMonitor = printMonitor;
    }
    public static void setCreatePicture(boolean createPicture) {
        Main.createPicture = createPicture;
    }
    public static void setSaveReport(boolean saveReport) {
        Main.saveReport = saveReport;
    }
    public static void setSaveState(boolean saveState) {
        Main.saveState = saveState;
    }
    public static void setLoadState(boolean loadState) {
        Main.loadState = loadState;
    }
    public static void setInputsFilename(String inputsFilename) {
        Main.inputsFilename = inputsFilename;
    }
    public static void setOutputsFilename(String outputsFilename) {
        Main.outputsFilename = outputsFilename;
    }
    public static void setReportFilename(String reportFilename) {
        Main.reportFilename = reportFilename;
    }
    public static void setUserNamesFilename(String userNamesFilename) {
        Main.userNamesFilename = userNamesFilename;
    }
    public static void setSaveNetworkFilename(String saveNetworkFilename) {
        Main.saveNetworkFilename = saveNetworkFilename;
    }
    public static void setLoadNetworkFilename(String loadNetworkFilename) {
        Main.loadNetworkFilename = loadNetworkFilename;
    }
    public static void setOutputNamesType(int outputNamesType) {
        Main.outputNamesType = outputNamesType;
    }
    public static void setDynamicType(int dynamicType) {
        Main.dynamicType = dynamicType;
    }
}
