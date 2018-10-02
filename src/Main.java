import Giles.ANN.FancyNeuralNetwork;
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
        examples = new ArrayList<>(NewProcessor.readAndStore(inReader));
        expecteds = new ArrayList<>(NewProcessor.readAndStore(outReader));

        System.out.println("Examples: " + examples);
        System.out.println("Expected Outcomes: " + expecteds);

        //FancyNeuralNetwork network = new FancyNeuralNetwork(examples.get(0).size(), 25, expecteds.get(0).size());
        FancyNeuralNetwork network = new FancyNeuralNetwork("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\WBTest.csv");
        System.out.println();
        network.print();

        /*for (List<Double> example : examples) {
            System.out.println("Outputs: " + network.forwardProp(example));
        }*/

        network.setCSVFile("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\CSVTest.csv");
        network.train(examples, expecteds, .15, 50000);
        network.saveWB("C:\\Users\\super\\IdeaProjects\\NeuralNetwork4\\src\\WBTest.csv");
    }
}
