package Giles.ANN;

import GUI.src.ProgressBar;
import Giles.util.CSVReader;
import Giles.util.CSVWriter;
import Giles.util.NewProcessor;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedNeuralNetwork extends NeuralNetwork {

    private boolean csvReady = false;
    private CSVWriter csvWriter;

    private NumberFormat format = NumberFormat.getNumberInstance();

    public static final int CONSTANT = 0;
    public static final int ITERATION_LINEAR = 1;
    public static final int ITERATION_QUADRATIC = 2;
    public static final int ACCURACY_LINEAR = 3;
    public static final int ACCURACY_QUADRATIC = 4;


    public AdvancedNeuralNetwork(int numInputs, int numHiddens, int numOutputs) {
        super(numInputs, numHiddens, numOutputs);
        format.setMaximumFractionDigits(9);
        format.setMinimumFractionDigits(9);
    }

    //Allows the user to initialize a network using a csv of saved weights/biases
    public AdvancedNeuralNetwork(String weightsBiasesFilename) {
        super(0, 0, 0);
        loadWB(weightsBiasesFilename);
    }

    //Trains the network by running backprop using the given examples, <numberOfIterations> number of times, and changes the learning rate using the specified algorithm
    public void train(List<List<Double>> examples, List<List<Double>> expectedOutcomes, double learningRate, int dynamicLearningRate, int numberOfIterations, boolean printMonitor){

        double totalError;
        int numberCorrect = 0;
        double percentCorrect = 0;

        ProgressBar progressBar = new ProgressBar();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(true);
            }
        });
        progressBar.setMaximum(numberOfIterations);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);

        //For writing data to CSV File
        List<String> currentSet = new ArrayList<>();

        List<Double> currentOutputs;
        List<Double> currentExpecteds;

        double dynamicRate = learningRate;
        for(int iteration = 0; iteration < numberOfIterations; iteration++){ //Repeat for each iteration

            //Update progress bar
            progressBar.setValue(iteration);

            totalError = 0;
            numberCorrect = 0;
            for(List<Double> currentExample : examples){ //Cycle through training examples
                currentOutputs = new ArrayList<>(forwardProp(currentExample)); //Get outputLayerSize based on inputLayerSize of current example
                currentExpecteds = new ArrayList<>(expectedOutcomes.get(examples.indexOf(currentExample)));

                //printExampleSummary(currentExample, currentExpecteds);

                if(currentExpecteds.get(getPrediction(currentExample)) == 1){
                    numberCorrect++;
                }
                //System.out.println(currentExpecteds);

                //Calculate error (to see if improving over time)
                for(int i = 0; i < currentExpecteds.size(); i++){
                    totalError += (Math.pow((currentExpecteds.get(i) - currentOutputs.get(i)), 2));
                    //System.out.println(Math.pow((currentExpecteds.get(i) - currentOutputs.get(i)), 2));
                }
                backProp(currentExpecteds);
                learn(dynamicRate, currentExample);
            }

            percentCorrect = ((double)numberCorrect / (double)(examples.size())) * 100.0;

            if(printMonitor)
                System.out.println("Iteration: " + (iteration+1) + "\t\tLearning Rate: " + format.format(dynamicRate) + "\t\tTotal Error: " + format.format(totalError) + "\t\tNumber Correct: " + numberCorrect + "\t\tPercent Correct: " + format.format(percentCorrect) + "%");

            //Update progress bar text
            progressBar.setText(iteration, percentCorrect, numberCorrect, totalError, learningRate);

            //For printing data to CSV
            if(csvReady) {
                if (iteration == 0) {
                    currentSet = new ArrayList<>();
                    currentSet.add("Iteration");
                    currentSet.add("Learning Rate");
                    currentSet.add("Total Error");
                    currentSet.add("Number Correct");
                    currentSet.add("Percent Correct");
                    csvWriter.addSet(currentSet);
                }

                currentSet = new ArrayList<>();
                currentSet.add(((Integer) (iteration + 1)).toString());
                currentSet.add(((Double) (dynamicRate)).toString());
                currentSet.add(((Double) (totalError)).toString());
                currentSet.add(((Integer) (numberCorrect)).toString());
                currentSet.add(((Double)(percentCorrect)).toString() + "%");
                csvWriter.addSet(currentSet);
            }

            //Change learning rate
            switch(dynamicLearningRate){
                case CONSTANT:
                    dynamicRate = learningRate; break;
                case ITERATION_LINEAR:
                    dynamicRate = ((learningRate * -1)/numberOfIterations) * iteration + learningRate; break;
                case ITERATION_QUADRATIC:
                    dynamicRate = ((learningRate * -1) / Math.pow(numberOfIterations, 2)) * (Math.pow(iteration, 2) - Math.pow(numberOfIterations, 2)); break;
                case ACCURACY_LINEAR:
                    dynamicRate = ((learningRate * -1)/expectedOutcomes.size()) * numberCorrect + learningRate; break;
                case ACCURACY_QUADRATIC:
                    dynamicRate = ((learningRate * -1) / Math.pow(expectedOutcomes.size(), 2)) * (Math.pow(numberCorrect, 2) - Math.pow(expectedOutcomes.size(), 2)); break;
                default:
                    dynamicRate = learningRate; break;
            }
        }

        if(csvReady)
//            currentSet = new ArrayList<>();
//            currentSet.add("Percent Correct:");
//            currentSet.add(((Double)percentCorrect).toString());
//            csvWriter.addSet(currentSet);
            csvWriter.write();
    }

    //Trains the network by running backprop using the given examples, <numberOfIterations> number of times, and changes the learning rate using the specified algorithm, using a progress bar
    public void train(List<List<Double>> examples, List<List<Double>> expectedOutcomes, double learningRate, int dynamicLearningRate, int numberOfIterations, boolean printMonitor, boolean useProgressBar){

        ProgressBar progressBar = new ProgressBar();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(true);
            }
        });
        progressBar.setMaximum(numberOfIterations);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);

        //In order for the progress bar to work, everything has to be in a SwingWorker class
        class Process extends SwingWorker<Void, Void>{
            private double totalError;
            private int numberCorrect = 0;
            private double percentCorrect = 0;

            //For writing data to CSV File
            private List<String> currentSet = new ArrayList<>();

            private List<Double> currentOutputs;
            private List<Double> currentExpecteds;

            private double dynamicRate = learningRate;

            @Override
            public Void doInBackground() {
                for(int iteration = 0; iteration < numberOfIterations; iteration++){ //Repeat for each iteration

                    //Update progress bar
                    progressBar.setValue(iteration);

                    totalError = 0;
                    numberCorrect = 0;
                    for(List<Double> currentExample : examples){ //Cycle through training examples
                        currentOutputs = new ArrayList<>(forwardProp(currentExample)); //Get outputLayerSize based on inputLayerSize of current example
                        currentExpecteds = new ArrayList<>(expectedOutcomes.get(examples.indexOf(currentExample)));

                        //printExampleSummary(currentExample, currentExpecteds);

                        if(currentExpecteds.get(getPrediction(currentExample)) == 1){
                            numberCorrect++;
                        }
                        //System.out.println(currentExpecteds);

                        //Calculate error (to see if improving over time)
                        for(int i = 0; i < currentExpecteds.size(); i++){
                            totalError += (Math.pow((currentExpecteds.get(i) - currentOutputs.get(i)), 2));
                            //System.out.println(Math.pow((currentExpecteds.get(i) - currentOutputs.get(i)), 2));
                        }
                        backProp(currentExpecteds);
                        learn(dynamicRate, currentExample);
                    }

                    percentCorrect = ((double)numberCorrect / (double)(examples.size())) * 100.0;

                    if(printMonitor)
                        System.out.println("Iteration: " + (iteration+1) + "\t\tLearning Rate: " + format.format(dynamicRate) + "\t\tTotal Error: " + format.format(totalError) + "\t\tNumber Correct: " + numberCorrect + "\t\tPercent Correct: " + format.format(percentCorrect) + "%");

                    //Update progress bar text
                    progressBar.setText(iteration, percentCorrect, numberCorrect, totalError, dynamicRate);

                    //For printing data to CSV
                    if(csvReady) {
                        if (iteration == 0) {
                            currentSet = new ArrayList<>();
                            currentSet.add("Iteration");
                            currentSet.add("Learning Rate");
                            currentSet.add("Total Error");
                            currentSet.add("Number Correct");
                            currentSet.add("Percent Correct");
                            csvWriter.addSet(currentSet);
                        }

                        currentSet = new ArrayList<>();
                        currentSet.add(((Integer) (iteration + 1)).toString());
                        currentSet.add(((Double) (dynamicRate)).toString());
                        currentSet.add(((Double) (totalError)).toString());
                        currentSet.add(((Integer) (numberCorrect)).toString());
                        currentSet.add(((Double)(percentCorrect)).toString() + "%");
                        csvWriter.addSet(currentSet);
                    }

                    //Change learning rate
                    switch(dynamicLearningRate){
                        case CONSTANT:
                            dynamicRate = learningRate; break;
                        case ITERATION_LINEAR:
                            dynamicRate = ((learningRate * -1)/numberOfIterations) * iteration + learningRate; break;
                        case ITERATION_QUADRATIC:
                            dynamicRate = ((learningRate * -1) / Math.pow(numberOfIterations, 2)) * (Math.pow(iteration, 2) - Math.pow(numberOfIterations, 2)); break;
                        case ACCURACY_LINEAR:
                            dynamicRate = ((learningRate * -1)/expectedOutcomes.size()) * numberCorrect + learningRate; break;
                        case ACCURACY_QUADRATIC:
                            dynamicRate = ((learningRate * -1) / Math.pow(expectedOutcomes.size(), 2)) * (Math.pow(numberCorrect, 2) - Math.pow(expectedOutcomes.size(), 2)); break;
                        default:
                            dynamicRate = learningRate; break;
                    }
                }
                return null;
            }

            @Override
            public void done(){
                if(csvReady)
//            currentSet = new ArrayList<>();
//            currentSet.add("Percent Correct:");
//            currentSet.add(((Double)percentCorrect).toString());
//            csvWriter.addSet(currentSet);
                    csvWriter.write();
            }

        }

        //Runs the process
        Process training = new Process();
        training.execute();
    }



    public void train(List<List<Double>> examples, List<List<Double>> expectedOutcomes, double learningRate, int numberOfIterations){
        train(examples, expectedOutcomes, learningRate, CONSTANT, numberOfIterations, true);
    }

    public void setCSVFile(String csvFilename){
        csvWriter = new CSVWriter(csvFilename);
        csvReady = true;
    }

    //"WB" refers to "weights and biases" - these pertain to the writing/saving/loading of weights and biases to/from a csv file
    public void saveWB(String wbFilename){
        CSVWriter wbWriter;
        wbWriter = new CSVWriter(wbFilename);
        List<List<String>> weights = new ArrayList<>();
        List<String> biases = new ArrayList<>();

        //First set is number of input neurons, followed by number of hidden neurons, followed by number of output neurons
        wbWriter.addSet(new ArrayList<>(Arrays.asList(inputLayerSize.toString(), hiddenLayerSize.toString(), outputLayerSize.toString())));

        for (List<Neuron> layer : network) { //Cycle through layers
            for (Neuron neuron : layer) { //Cycle through neurons in layer
                List<String> temp = new ArrayList<>(); //Stores the list of weights for each neuron until it is stored in "weights"
                for(int k = 0; k < neuron.size(); k++){ //Cycle through neuron weights
                    temp.add(((Double)neuron.getWeight(k)).toString());
                }
                weights.add(new ArrayList<>(temp));
                biases.add(((Double)(neuron.getBias())).toString());
            }
        }
        for (List<String> neuronWeights : weights) {
            wbWriter.addSet(neuronWeights);
        }
        wbWriter.addSet(biases);
        wbWriter.write();
    }
    private void loadWB(String wbFilename){
        CSVReader wbReader;

        //Read in weights and biases from file - each neuron's weights are stored in a list, and all the biases are stored in one list at the end of the list of lists
        wbReader = new CSVReader(wbFilename);
        List<List<Double>> wb;
        wb = new ArrayList<>(NewProcessor.readAndStore(wbReader));

        //Retrieve number of neurons in each layer from first List in "wb", and initialize the network using these values
        inputLayerSize = wb.get(0).get(0).intValue();
        hiddenLayerSize = wb.get(0).get(1).intValue();
        outputLayerSize = wb.get(0).get(2).intValue();
        init(inputLayerSize, hiddenLayerSize, outputLayerSize);

        //Load in weights
        //Note: Lists stored in "wb" correspond to neurons (each Neuron has its own List of weights), and last List is the list of all the biases
        int currentNeuronList = 1; //Start from 2nd list, bc 1st list is used to store info about number of neurons in each layer
        int biasCounter = 0;
        for(List<Neuron> layer : network) { //Cycle through layers
            for (Neuron neuron : layer) { //Cycle through neurons
                neuron.setWeights(wb.get(currentNeuronList));
                neuron.setBias(wb.get(wb.size() - 1).get(biasCounter));
                currentNeuronList++;
                biasCounter++;
            }
        }
    }

    //Initializes the network with random weights and biases
    private void init(int numInputs, int numHiddens, int numOutputs){
        network = new ArrayList<>();
        List<Neuron> hiddenLayer = new ArrayList<>();
        Neuron currentNeuron;
        for(int i = 0; i < numHiddens; i++) { //Cycle through each neuron of hidden layer
            currentNeuron = new Neuron();
            for (int j = 0; j < numInputs; j++) { //Cycle through each neuron of input layer
                currentNeuron.addWeight(Math.random()); //Weights
            }
            currentNeuron.setBias(Math.random()); //Add a bias for each hidden layer neuron
            hiddenLayer.add(currentNeuron);
        }

        List<Neuron> outputLayer = new ArrayList<>();
        for(int i = 0; i < numOutputs; i++) { //Cycle through each neuron of output layer
            currentNeuron = new Neuron();
            for (int j = 0; j < numHiddens; j++) { //Cycle through each neuron of hidden layer
                currentNeuron.addWeight(Math.random()); //Weights
            }
            currentNeuron.setBias(Math.random()); //Add a bias for each output layer neuron
            outputLayer.add(currentNeuron);
        }

        network.add(hiddenLayer);
        network.add(outputLayer);
    }

    //Returns prediction as a name, based on the names supplied in outputNames
    public String getPrediction(List<Double> inputs, String[] outputNames){
        return outputNames[super.getPrediction(inputs)];
    }

    public void printExampleSummary(List<Double> example, List<Double> expecteds, List<String> outputNames) {
        System.out.println("\n\tOutput: " + outputNames.get(getPrediction(example)) + " | " + forwardProp(example));
        System.out.println("\tExpected: " + outputNames.get(expecteds.indexOf(1.0)) + " | " + expecteds);
        System.out.println("\tCorrect: " + (expecteds.get(getPrediction(example)) == 1));
    }

//    public void printExampleSummary(List<Double> example, List<Double> expecteds, String[] outputNames) {
//        System.out.println("\n\tOutput: " + getPrediction(example) + " | " + forwardProp(example));
//        System.out.println("\tExpected: " + outputNames[expecteds.indexOf(1.0)] + " | " + expecteds);
//        System.out.println("\tCorrect: " + (expecteds.get(getPrediction(example)) == 1));
//    }

    //Returns the results of the network's predictions of the output names of the supplied inputs
    public List<String> test(List<List<Double>> inputs, String[] outputNames){
        List<String> theList = new ArrayList<>();
        for(List<Double> input : inputs){
            theList.add(getPrediction(input, outputNames));
        }
        return theList;
    }
    public List<String> test(List<List<Double>> inputs, List<String> outputNames){
        String[] temp = new String[outputNames.size()];
        for(String str : outputNames){
            temp[outputNames.indexOf(str)] = str;
        }
        return test(inputs, temp);
    }

    //Create a representative image of the network
    public void createPicture(){
        JFrame frame = new JFrame("Neural Network Image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new AdvancedNetworkPicture(this));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public void createSimplePicture(){
        JFrame frame = new JFrame("Neural Network Image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new NetworkPicture(inputLayerSize, hiddenLayerSize, outputLayerSize));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }//This option has no color to indicate weights and biases - just black
}
