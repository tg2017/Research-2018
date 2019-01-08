package Giles.ANN;

import java.util.ArrayList;
import java.util.List;

public class Neuron {
    private List<Double> weights;
    private double bias;
    private double activation;
    private double error;

    //Initializes as empty neuron
    public Neuron() {
        this.weights = new ArrayList<>();
    }

    //Initializes neuron using specified values
    public Neuron(List<Double> weights, double bias) {
        this.weights = new ArrayList<>(weights);
        this.bias = bias;
    }

    public void addWeight(double weight) {
        weights.add(weight);
    }

    public void setWeight(int index, double weight) {
        weights.set(index, weight);
    }

    public void setWeights(List<Double> newWeights){
        weights = new ArrayList<>(newWeights);
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    public double getWeight(int index) {
        return weights.get(index);
    }

    //Size- number of weights associated with neuron
    public int size() {
        return weights.size();
    }

    public String toString() {
        StringBuilder output = new StringBuilder("**Weights: {");
        for (int i = 0; i < weights.size(); i++) {
            if (i != 0) {
                output.append(", ");
            }
            output.append(weights.get(i));
        }
        output.append("} | Bias: ");
        output.append(bias);
        output.append("**");

        return output.toString();
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public double getActivation() {
        return activation;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
}
