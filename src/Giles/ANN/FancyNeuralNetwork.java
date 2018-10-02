package Giles.ANN;

import Giles.util.CSVReader;
import Giles.util.CSVWriter;
import Giles.util.NewProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FancyNeuralNetwork extends NeuralNetwork {

    private boolean csvReady = false;
    private CSVWriter csvWriter;

    //"WB" refers to "weights and biases" - these pertain to the writing/saving/loading of weights and biases to/from a csv file
    private CSVWriter wbWriter;
    private CSVReader wbReader;
    Integer inputs, hiddens, outputs;


    public FancyNeuralNetwork(int numInputs, int numHiddens, int numOutputs) {
        super(numInputs, numHiddens, numOutputs);
        inputs = numInputs;
        hiddens = numHiddens;
        outputs = numOutputs;
    }

    //Allows the user to initialize a network using a csv of saved weights/biases
    public FancyNeuralNetwork(String weightsBiasesFilename) {
        super(0, 0, 0);
        loadWB(weightsBiasesFilename);
    }

    public void train(List<List<Double>> examples, List<List<Double>> expectedOutcomes, double learningRate, int numberOfIterations){
        double totalError;
        int numberCorrect;

        //For writing data to CSV File
        List<String> currentSet;

        List<Double> currentOutputs;
        List<Double> currentExpecteds;
        for(int iteration = 0; iteration < numberOfIterations; iteration++){ //Repeat for each iteration
            totalError = 0;
            numberCorrect = 0;
            for(List<Double> currentExample : examples){ //Cycle through training examples
                currentOutputs = new ArrayList<>(forwardProp(currentExample)); //Get outputs based on inputs of current example
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
                learn(learningRate, currentExample);
            }
            System.out.println("Iteration: " + (iteration+1) + "\t\tTotal Error: " + totalError + "\t\tNumber Correct: " + numberCorrect);

            //For printing data to CSV
            if(csvReady) {
                if (iteration == 0) {
                    currentSet = new ArrayList<>();
                    currentSet.add("Iteration");
                    currentSet.add("Total Error");
                    currentSet.add("Number Correct");
                    csvWriter.addSet(currentSet);
                }

                currentSet = new ArrayList<>();
                currentSet.add(((Integer) (iteration + 1)).toString());
                currentSet.add(((Double) (totalError)).toString());
                currentSet.add(((Integer) (numberCorrect)).toString());
                csvWriter.addSet(currentSet);
            }
        }
        if(csvReady)
            csvWriter.write();
    }

    public void setCSVFile(String csvFilename){
        csvWriter = new CSVWriter(csvFilename);
        csvReady = true;
    }

    public void saveWB(String wbFilename){
        wbWriter = new CSVWriter(wbFilename);
        List<List<String>> weights = new ArrayList<>();
        List<String> biases = new ArrayList<>();

        //First set is number of input neurons, followed by number of hidden neurons, followed by number of output neurons
        wbWriter.addSet(new ArrayList<>(Arrays.asList(inputs.toString(), hiddens.toString(), outputs.toString())));

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
        //Read in weights and biases from file - each neuron's weights are stored in a list, and all the biases are stored in one list at the end of the list of lists
        wbReader = new CSVReader(wbFilename);
        List<List<Double>> wb = new ArrayList<>();
        wb = new ArrayList<>(NewProcessor.readAndStore(wbReader));

        //Retrieve number of neurons in each layer from first List in "wb", and initialize the network using these values
        inputs = wb.get(0).get(0).intValue();
        hiddens = wb.get(0).get(1).intValue();
        outputs = wb.get(0).get(2).intValue();
        init(inputs, hiddens, outputs);

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

    private void init(int numInputs, int numHiddens, int numOutputs){
        network = new ArrayList<>();
        List<Neuron> hiddenLayer = new ArrayList<>();
        Neuron currentNeuron;
        for(int i = 0; i < numHiddens; i++) { //Cycles through each neuron of hidden layer
            currentNeuron = new Neuron();
            for (int j = 0; j < numInputs; j++) { //Cycles through each neuron of input layer
                currentNeuron.addWeight(Math.random()); //Weights
            }
            currentNeuron.setBias(Math.random()); //Add a bias for each hidden layer neuron
            hiddenLayer.add(currentNeuron);
        }

        List<Neuron> outputLayer = new ArrayList<>();
        for(int i = 0; i < numOutputs; i++) { //Cycles through each neuron of output layer
            currentNeuron = new Neuron();
            for (int j = 0; j < numHiddens; j++) { //Cycles through each neuron of hidden layer
                currentNeuron.addWeight(Math.random()); //Weights
            }
            currentNeuron.setBias(Math.random()); //Add a bias for each output layer neuron
            outputLayer.add(currentNeuron);
        }

        network.add(hiddenLayer);
        network.add(outputLayer);
    }



}
