package Giles.ANN;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class NetworkPicture extends JPanel {

    protected List<Ellipse2D> inputCircles = new ArrayList<>();
    protected List<Ellipse2D> hiddenCircles = new ArrayList<>();
    protected List<Ellipse2D> outputCircles = new ArrayList<>();

    protected boolean[] overflow = new boolean[3];
    protected int[] initialSizes = new int[3];

    //Constants, mostly for size
    private final int MAXSIZE = 50;
    private final double SPACE = 5;
    private final double VERTICALSPACE = 350;
    private final int WIDTH = 1500;

    private double midpoint, inputXStart, hiddenXStart, outputXStart;
    private int largestLayerSize = 0;
    private double diameter = 20;


    public NetworkPicture(int inputNum, int hiddenNum, int outputNum) {

        initialSizes[0] = inputNum;
        initialSizes[1] = hiddenNum;
        initialSizes[2] = outputNum;

        //Accommodate for overflows (the size of a layer being too large)
        overflow[0] = inputNum > MAXSIZE;
        overflow[1] = hiddenNum > MAXSIZE;
        overflow[2] = outputNum > MAXSIZE;
        /*if(overflow[0]){
            inputNum = MAXSIZE;
        }
        if(overflow[1]){
            hiddenNum = MAXSIZE;
        }
        if(overflow[2]){
            outputNum = MAXSIZE;
        }*/

        findStartPoints(inputNum, hiddenNum, outputNum);

        //Create the circles in each layer
        for(int i = 0; i < inputNum; i++){
            inputCircles.add(new Ellipse2D.Double(inputXStart + i* diameter + i*SPACE, 10, diameter, diameter));
        }
        for(int h = 0; h < hiddenNum; h++){
            hiddenCircles.add(new Ellipse2D.Double(hiddenXStart + h* diameter + h*SPACE, 10 + VERTICALSPACE, diameter, diameter));
        }
        for(int o = 0; o < outputNum; o++){
            outputCircles.add(new Ellipse2D.Double(outputXStart + o* diameter + o*SPACE, 150 + 2 * VERTICALSPACE, diameter, diameter));
        }
    }

    protected Point2D center(Rectangle2D bounds) {
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    protected double angleBetween(Shape from, Shape to) {
        return angleBetween(center(from.getBounds2D()), center(to.getBounds2D()));
    }

    protected double angleBetween(Point2D from, Point2D to) {
        double x = from.getX();
        double y = from.getY();

        // This is the difference between the anchor point
        // and the mouse.  Its important that this is done
        // within the local coordinate space of the component,
        // this means either the MouseMotionListener needs to
        // be registered to the component itself (preferably)
        // or the mouse coordinates need to be converted into
        // local coordinate space
        double deltaX = to.getX() - x;
        double deltaY = to.getY() - y;

        // Calculate the angle...
        // This is our "0" or start angle..
        double rotation = -Math.atan2(deltaX, deltaY);
        rotation = Math.toRadians(Math.toDegrees(rotation) + 180);

        return rotation;
    }

    protected Point2D getPointOnCircle(Shape shape, double radians) {
        Rectangle2D bounds = shape.getBounds();
//          Point2D point = new Point2D.Double(bounds.getX(), bounds.getY());
        Point2D point = center(bounds);
        return getPointOnCircle(point, radians, Math.max(bounds.getWidth(), bounds.getHeight()) / 2d);
    }

    protected Point2D getPointOnCircle(Point2D center, double radians, double radius) {

        double x = center.getX();
        double y = center.getY();

        radians = radians - Math.toRadians(90.0); // 0 becomes th?e top
        // Calculate the outter point of the line
        double xPosy = Math.round((float) (x + Math.cos(radians) * radius));
        double yPosy = Math.round((float) (y + Math.sin(radians) * radius));

        return new Point2D.Double(xPosy, yPosy);

    }

    //Find the x value at which each layer needs to be started in order for all layers to be centered
    protected void findStartPoints(int inputNum, int hiddenNum, int outputNum){
        //Find the length of the largest layer (for centering purposes)
        if(inputNum > largestLayerSize){
            largestLayerSize = inputNum;
        }
        if(hiddenNum > largestLayerSize){
            largestLayerSize = hiddenNum;
        }
        if(outputNum > largestLayerSize){
            largestLayerSize = outputNum;
        }

        diameter = ((double)WIDTH / (double)largestLayerSize) - SPACE;

        //Find the midpoint of the largest layer and use that to set the start point of each layer (for centering purposes)
        midpoint = ((double)largestLayerSize / 2.0) * diameter + ((double)largestLayerSize / 2.0 - .5) * SPACE;
        inputXStart = midpoint - (((double)inputNum / 2.0) * diameter + ((double)inputNum / 2.0 - .5) * SPACE);
        hiddenXStart = midpoint - (((double)hiddenNum / 2.0) * diameter + ((double)hiddenNum / 2.0 - .5) * SPACE);
        outputXStart = midpoint - (((double)outputNum / 2.0) * diameter + ((double)outputNum / 2.0 - .5) * SPACE);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, 1000);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        for (Ellipse2D inputCircle : inputCircles) {
            g2d.draw(inputCircle);
        }
        for (Ellipse2D hiddenCircle : hiddenCircles) {
            g2d.draw(hiddenCircle);
        }
        for (Ellipse2D outputCircle : outputCircles) {
            g2d.draw(outputCircle);
        }

        Point2D startPoint;
        Point2D endPoint;

        //Draw lines between input neurons and hidden neurons
        for(Ellipse2D inputCircle : inputCircles){
            for(Ellipse2D hiddenCircle : hiddenCircles){

                //Determine points to draw lines from
                startPoint = getPointOnCircle(inputCircle, angleBetween(inputCircle, hiddenCircle));
                endPoint = getPointOnCircle(hiddenCircle, angleBetween(hiddenCircle, inputCircle));

                //Draw the line
                g2d.draw(new Line2D.Double(startPoint, endPoint));
            }
        }
        for(Ellipse2D hiddenCircle : hiddenCircles){
            for(Ellipse2D outputCircle : outputCircles){

                //Determine points to draw lines from
                startPoint = getPointOnCircle(hiddenCircle, angleBetween(hiddenCircle, outputCircle));
                endPoint = getPointOnCircle(outputCircle, angleBetween(outputCircle, hiddenCircle));

                //Draw the line
                g2d.draw(new Line2D.Double(startPoint, endPoint));
            }
        }
        g2d.dispose();
    }

}
