import Giles.util.NewProcessor;

import java.util.ArrayList;
import java.util.List;


//This class represents a neural network with one input layer, one hidden layer,
// and one output layer, each with a user-defined number of neurons.
public class NeuralNetwork {

    private List<List<Neuron>> network;
    private List<Neuron> hiddenLayer;
    private List<Neuron> outputLayer;


    public NeuralNetwork(int numInputs, int numHiddens, int numOutputs){
        network = new ArrayList<>();
        hiddenLayer = new ArrayList<>(); //Stores all the neurons in the hidden layer
        Neuron currentNeuron;
        for(int i = 0; i < numHiddens; i++) { //Cycles through each neuron of hidden layer
            currentNeuron = new Neuron();
            for (int j = 0; j < numInputs; j++) { //Cycles through each neuron of input layer
                currentNeuron.addWeight(Math.random()); //Weights
            }
            currentNeuron.setBias(Math.random()); //Add a bias for each hidden layer neuron
            hiddenLayer.add(currentNeuron);
        }

        outputLayer = new ArrayList<>();
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
        System.out.println(network.toString());
    }

    //Finds the raw value of the neuron's activation
    public double getNeuronValue(Neuron neuron, List<Double> inputs){
        double value = 0;

        for(int i=0; i < neuron.size(); i++){
            value += inputs.get(i) * neuron.getWeight(i);
        }

        return value;
    }

    //Applies sigmoid function to neuron value
    public double getNeuronActivation(Neuron neuron, List<Double> inputs){
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

    public void backProp(List<Double> expectedOutcomes){
        List<Neuron> currentLayer;
        Neuron currentNeuron;
        List<Double> errors;
        for(int i = network.size() - 1; i > 0; i++){ //If in any layer before the output layer...
            currentLayer = new ArrayList<>(network.get(i));
            errors = new ArrayList<>();
            if(i != network.size()-1){
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

    /*private void learn(){ //Updates the values of weights and biases to reflect changes demanded by training example
        List<Double> inputs = new ArrayList<>();
        for(int i = 0; i < network.size(); i++){ //Cycle through layers
            inputs = new ArrayList<>()

        }

    }*/

}
