package Giles.ANN;

import Giles.util.NewProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNetworkPicture extends NetworkPicture {
    private List<List<Neuron>> neurons;

    public AdvancedNetworkPicture(NeuralNetwork neuralNetwork){
        super(neuralNetwork.inputLayerSize, neuralNetwork.hiddenLayerSize, neuralNetwork.outputLayerSize);
        neurons = new ArrayList<>(neuralNetwork.getNeurons());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.draw(inputCircles.get(1));

        //Draw circles, with colors ranging from white to true blue based on neuron bias
        for (Ellipse2D inputCircle : inputCircles) {
            g2d.setColor(Color.BLACK); //Note: Input neurons have no bias
            g2d.draw(inputCircle);
        }

        for (int h = 0; h < hiddenCircles.size(); h++) {
            g2d.setColor(new Color((int)(255 * (1 - NewProcessor.sigmoid(neurons.get(0).get(h).getBias()))), (int)(255 * (1 - NewProcessor.sigmoid(neurons.get(0).get(h).getBias()))), 255));
            g2d.fill(hiddenCircles.get(h));
            g2d.setColor(Color.BLACK);
            g2d.draw(hiddenCircles.get(h));
        }

        for (int o = 0; o < outputCircles.size(); o++) {
            g2d.setColor(new Color((int)(255 *(1 - NewProcessor.sigmoid(neurons.get(1).get(o).getBias()))), (int)(255 *(1 - NewProcessor.sigmoid(neurons.get(1).get(o).getBias()))), 255));
            g2d.fill(outputCircles.get(o));
            g2d.setColor(Color.BLACK);
            g2d.draw(outputCircles.get(o));
        }

        Point2D startPoint;
        Point2D endPoint;

        //Draw lines between input neurons and hidden neurons, with colors ranging from white to true blue based on weight value
        for(int i = 0; i < inputCircles.size(); i++){
            for(int h = 0; h < hiddenCircles.size(); h++){

                //Determine points to draw lines from
                startPoint = getPointOnCircle(inputCircles.get(i), angleBetween(inputCircles.get(i), hiddenCircles.get(h)));
                endPoint = getPointOnCircle(hiddenCircles.get(h), angleBetween(hiddenCircles.get(h), inputCircles.get(i)));

                //Set the color
                g2d.setColor(new Color((int)(255 * (1 - NewProcessor.sigmoid(neurons.get(0).get(h).getWeight(i)))), (int)(255 * (1 - NewProcessor.sigmoid(neurons.get(0).get(h).getWeight(i)))), 255));

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
                g2d.setColor(new Color((int)(255 * (1 - NewProcessor.sigmoid(neurons.get(1).get(o).getWeight(h)))), (int)(255 * (1 - NewProcessor.sigmoid(neurons.get(1).get(o).getWeight(h)))), 255));

                //Draw the line
                g2d.draw(new Line2D.Double(startPoint, endPoint));
            }
        }
        g2d.dispose();
    }
}
