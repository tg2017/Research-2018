package Giles.ANN;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNetworkPicture extends NetworkPicture{
    private FancyNeuralNetwork network;
    private List<List<Neuron>> neurons;

    public AdvancedNetworkPicture(FancyNeuralNetwork neuralNetwork){
        super(neuralNetwork.inputLayerSize, neuralNetwork.hiddenLayerSize, neuralNetwork.outputLayerSize);
        network = neuralNetwork;
        neurons = new ArrayList<>(network.getNeurons());

    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.draw(inputCircles.get(1));

        //Draw circles, with colors ranging from true red to true green based on neuron bias

        for (int i = 0; i < inputCircles.size(); i++) {
            g2d.setColor(Color.BLACK);
            //new Color((int)(255 * (1 - neurons.get(0).get(i).getActivation())), (int)(255 * neurons.get(0).get(i).getActivation()), 0));
            g2d.draw(inputCircles.get(i));
        }


        for (int h = 0; h < hiddenCircles.size(); h++) {
            g2d.setColor(new Color((int)(255 * (1 - neurons.get(0).get(h).getBias())), (int)(255 * (1 - neurons.get(0).get(h).getBias())), 255));
            g2d.fill(hiddenCircles.get(h));g2d.setColor(Color.BLACK);
            g2d.draw(hiddenCircles.get(h));

        }

        for (int o = 0; o < outputCircles.size(); o++) {
            g2d.setColor(new Color((int)(255 *(1 - neurons.get(1).get(o).getBias())), (int)(255 *(1 - neurons.get(1).get(o).getBias())), 255));
            g2d.fill(outputCircles.get(o));
            g2d.setColor(Color.BLACK);
            g2d.draw(outputCircles.get(o));
        }

        Point2D startPoint;
        Point2D endPoint;

        //Draw lines between input neurons and hidden neurons, with colors ranging from true red to true green based on weight value
        for(int i = 0; i < inputCircles.size(); i++){
            for(int h = 0; h < hiddenCircles.size(); h++){

                //Determine points to draw lines from
                startPoint = getPointOnCircle(inputCircles.get(i), angleBetween(inputCircles.get(i), hiddenCircles.get(h)));
                endPoint = getPointOnCircle(hiddenCircles.get(h), angleBetween(hiddenCircles.get(h), inputCircles.get(i)));

                //Set the color
                g2d.setColor(new Color((int)(255 * (1 - neurons.get(0).get(h).getWeight(i))), (int)(255 * (1 - neurons.get(0).get(h).getWeight(i))), 255));

                //Draw the line
                g2d.draw(new Line2D.Double(startPoint, endPoint));
            }
        }
        for(int h = 0; h < hiddenCircles.size(); h++){
            for(int o = 0; o < outputCircles.size(); o++){

                //Determine points to draw lines from
                startPoint = getPointOnCircle(hiddenCircles.get(h), angleBetween(hiddenCircles.get(h), outputCircles.get(o)));
                endPoint = getPointOnCircle(outputCircles.get(o), angleBetween(outputCircles.get(o), hiddenCircles.get(h)));

                //Set the color
                g2d.setColor(new Color((int)(255 * (1 - neurons.get(1).get(o).getWeight(h))), (int)(255 * (1 - neurons.get(1).get(o).getWeight(h))), 255));

                //Draw the line
                g2d.draw(new Line2D.Double(startPoint, endPoint));
            }
        }
        g2d.dispose();
    }
}
