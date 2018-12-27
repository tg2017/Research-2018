package Giles.ANN;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

    public class Test {

        public static void main(String[] args) {
            new Test();
        }

        public Test() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }

                    JFrame frame = new JFrame("Testing");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.add(new NetworkPicture(60, 75, 25));
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
        }

        public class NetworkPicture extends JPanel {

            private List<Ellipse2D> inputCircles = new ArrayList<>();
            private List<Ellipse2D> hiddenCircles = new ArrayList<>();
            private List<Ellipse2D> outputCircles = new ArrayList<>();

            private boolean[] overflow = new boolean[3];
            private int[] initialSizes = new int[3];

            public NetworkPicture(int inputNum, int hiddenNum, int outputNum) {
                final int MAXSIZE = 50; //MUST BE AN EVEN NUMBER (otherwise overflowed layers will not appear centered)
                final double DIAMETER = 20;
                final double SPACE = 10;

                initialSizes[0] = inputNum;
                initialSizes[1] = hiddenNum;
                initialSizes[2] = outputNum;

                //Accommodate for overflows (the size of a layer being too large)
                overflow[0] = inputNum > MAXSIZE;
                overflow[1] = hiddenNum > MAXSIZE;
                overflow[2] = outputNum > MAXSIZE;
                if(overflow[0]){
                    inputNum = MAXSIZE;
                }
                if(overflow[1]){
                    hiddenNum = MAXSIZE;
                }
                if(overflow[2]){
                    outputNum = MAXSIZE;
                }

                //Find the length of the largest layer (for centering purposes)
                int largestLayerSize = 0;
                if(inputNum > largestLayerSize){
                    largestLayerSize = inputNum;
                }
                if(hiddenNum > largestLayerSize){
                    largestLayerSize = hiddenNum;
                }
                if(outputNum > largestLayerSize){
                    largestLayerSize = outputNum;
                }

                //Find the midpoint of the largest layer and use that to set the start point of each layer (for centering purposes)
                double midpoint = ((double)largestLayerSize / 2.0) * DIAMETER + ((double)largestLayerSize / 2.0 - .5) * SPACE;
                double inputXStart = midpoint - (((double)inputNum / 2.0) * DIAMETER + ((double)inputNum / 2.0 - .5) * SPACE);
                double hiddenXStart = midpoint - (((double)hiddenNum / 2.0) * DIAMETER + ((double)hiddenNum / 2.0 - .5) * SPACE);
                double outputXStart = midpoint - (((double)outputNum / 2.0) * DIAMETER + ((double)outputNum / 2.0 - .5) * SPACE);

                //Create the circles in each layer
                for(int i = 0; i < inputNum; i++){
                    inputCircles.add(new Ellipse2D.Double(inputXStart + i*DIAMETER+ i*SPACE, 10, DIAMETER, DIAMETER));
                }
                for(int h = 0; h < hiddenNum; h++){
                    hiddenCircles.add(new Ellipse2D.Double(hiddenXStart + h*DIAMETER+ h*SPACE, 410, DIAMETER, DIAMETER));
                }
                for(int o = 0; o < outputNum; o++){
                    outputCircles.add(new Ellipse2D.Double(outputXStart + o*DIAMETER + o*SPACE, 810, DIAMETER, DIAMETER));
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

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20000, 2000);
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

                //g2d.draw(new Ellipse2D.Double(0,0,20,20));
                /*double from = angleBetween(circle1, circle2);
                double to = angleBetween(circle2, circle1);

                Point2D pointFrom = getPointOnCircle(circle1, from);
                Point2D pointTo = getPointOnCircle(circle2, to);

                g2d.setColor(Color.RED);
                Line2D line = new Line2D.Double(pointFrom, pointTo);
                g2d.draw(line);*/
                g2d.dispose();
            }

        }

    }
