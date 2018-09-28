package Giles.util;

public class NewProcessor extends DataProcessor {
    public static double sigmoid(double x){
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }

    public static double dSigmoid(double x){
        return sigmoid(x) * (1-sigmoid(x));
    }

    //Returns the derivative of a value that has already had the sigmoid function applied
    public static double deriveSigmoid(double sigmoidValue){return sigmoidValue * (1-sigmoidValue);}
}
