import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Double> inputs = new ArrayList<>(Arrays.asList(3.0, 5.0));
        List<Double> expecteds = new ArrayList<>(Arrays.asList())
        NeuralNetwork newNetwork = new NeuralNetwork(2, 1, 2);
        newNetwork.print();
        System.out.println("Outputs: " + newNetwork.forwardProp(inputs));


    }
}
