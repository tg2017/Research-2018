import Giles.util.NewProcessor;

import java.util.ArrayList;
import java.util.List;


//This class represents a neural network with one input layer, one hidden layer,
// and one output layer, each with a user-defined number of neurons.
public class NeuralNetwork {

    private List<List<Neuron>> network;


    public NeuralNetwork(int numInputs, int numHiddens, int numOutputs){
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

    public void print(){
        System.out.println("Note: Each set of brackets is a layer, each set of stars is a neuron");
        for (List<Neuron> layer : network) {
            System.out.println(layer);
        }
    }

    //Finds the raw value of the neuron's activation
    private double getNeuronValue(Neuron neuron, List<Double> inputs){
        double value = 0;

        for(int i=0; i < neuron.size(); i++){
            value += inputs.get(i) * neuron.getWeight(i);
        }

        return value;
    }

    //Applies sigmoid function to neuron value
    private double getNeuronActivation(Neuron neuron, List<Double> inputs){
        return NewProcessor.sigmoid(getNeuronValue(neuron, inputs));
    }

    //Returns activations of output layer after "inputs" data was entered into the input layer and forward propagated
    public List<Double> forwardProp(List<Double> theInputs){
        List<Double> inputs = new ArrayList<>(theInputs);
        List<Double> outputs = new ArrayList<>();
        List<Double> newInputs; //Used as a temp to store activations of layer until the next layer is ready to use them as inputs
        Neuron currentNeuron;
        for(int i = 0; i < network.size(); i++) {
            newInputs = new ArrayList<>(); //Reset newInputs
            for (int j = 0; j < network.get(i).size(); j++) { //Cycles through each neuron in the current layer
                currentNeuron = network.get(i).get(j);
                currentNeuron.setActivation(getNeuronActivation(currentNeuron, inputs)); //Sets the activation variable of the neuron
                newInputs.add(currentNeuron.getActivation());
            }
            inputs = new ArrayList<>(newInputs); //Change the inputs to be passed into the next layer into the activations of the previous layer
        }

        for(int i = 0; i < network.get(network.size() - 1).size(); i++){ //Cycle through output layer
            currentNeuron = network.get(network.size() - 1).get(i);
            outputs.add(currentNeuron.getActivation());
        }
        return outputs;
    }

    private void backProp(List<Double> expectedOutcomes){
        List<Neuron> currentLayer;
        Neuron currentNeuron;
        List<Double> errors;
        for(int i = network.size() - 1; i >= 0; i--){  //Cycle through all layers
            currentLayer = new ArrayList<>(network.get(i));
            errors = new ArrayList<>(); //Reset errors list for use in next layer
            if(i != network.size()-1){ //If in any layer before the output layer...
                for(int j = 0; j < currentLayer.size(); j++){ //Cycle through neurons in layer
                    double currentError = 0;
                    for(int k = 0; k < network.get(i+1).size(); k++){ //Cycle through neurons in next layer
                        currentNeuron = network.get(i+1).get(k);
                        currentError += currentNeuron.getWeight(j) * currentNeuron.getError();
                    }
                    errors.add(currentError);
                }
            } else {
                for(int j = 0; j < currentLayer.size(); j++) { //Cycle through neurons in current (output) layer
                    currentNeuron = currentLayer.get(j);
                    errors.add(expectedOutcomes.get(j) - currentNeuron.getActivation());
                }
            }
            for(int j = 0; j < currentLayer.size(); j++) { //Cycle through neurons in current layer
                currentNeuron = currentLayer.get(j);
                currentNeuron.setError(errors.get(j) * NewProcessor.deriveSigmoid(currentNeuron.getActivation()));
            }
        }
    }

    //Updates the weights and biases of the neurons in the network based on input data
    private void learn(double rate, List<Double> theInputs){ //Updates the values of weights and biases to reflect changes demanded by training example
        List<Double> inputs;
        List<Neuron> currentLayer;
        for(int i = 0; i < network.size(); i++){ //Cycle through layers
            currentLayer = network.get(i);
            inputs = new ArrayList<>(theInputs);
            if(i != 0){ //If not in first hidden layer...
                inputs = new ArrayList<>();
                for(int j = 0; j < network.get(i - 1).size(); j++){
                    inputs.add(j, network.get(i-1).get(j).getActivation()); //...set the inputs to the activations of the neurons in the prev. layer
                }
            }
            //System.out.println(inputs);
            for (Neuron currentNeuron : currentLayer) { //Cycle through neurons in layer
                for (int k = 0; k < inputs.size(); k++) { //Cycle through weights of currentNeuron (should be same size as inputs)
                    currentNeuron.setWeight(k,
                            currentNeuron.getWeight(k) +
                                    inputs.get(k) *
                                            currentNeuron.getError() * rate); //Update weight value (weight = weight + input * error * learning rate)
                }
                currentNeuron.setBias(currentNeuron.getBias() + currentNeuron.getError() * rate); //Update bias
            }
        }
    }

    //Trains the ANN using the given set of examples
    public void train(List<List<Double>> examples, List<List<Double>> expectedOutcomes, double learningRate, int numberOfIterations){
        double totalError;
        int numberCorrect;

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
        }
    }

    //Returns the index of the output neuron with the highest activation after the specified example was forward propagated
    public int getPrediction(List<Double> inputs){
        forwardProp(inputs);
        List<Neuron> outputLayer = network.get(network.size()-1);
        int indexOfMax = -1;
        double currentMax = -9999999;
        for(int i = 0; i < outputLayer.size(); i++){
            if(outputLayer.get(i).getActivation() > currentMax){
                currentMax = outputLayer.get(i).getActivation();
                indexOfMax = i;
            }
        }
        return indexOfMax;

    }

    public void printExampleSummary(List<Double> example, List<Double> expecteds){
        System.out.println("\n\tOutputs: " + forwardProp(example) + " | " + getPrediction(example));
        System.out.println("\tExpected: " + expecteds);
        System.out.println("\tCorrect: " + (expecteds.get(getPrediction(example)) == 1));

    }
}
