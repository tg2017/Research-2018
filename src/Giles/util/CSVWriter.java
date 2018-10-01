package Giles.util;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    private String filename;
    private StringBuilder output = new StringBuilder();

    public CSVWriter(String filename){
        this.filename = filename;
    }

    public void addSet(List<String> set){
        for(String element : set){
            if(set.indexOf(element) != 0) {
                output.append(",");
            }
            output.append(element);
        }
        output.append("\n");
    }

    public void write(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false))) {

            //Replace '\n' escape sequence with '\r\n', which writes to a new line IN FILE
            String finalOutput = output.toString().replaceAll("\n", "\r\n");

            //Write data to file
            bw.write(finalOutput);

            //Display "Successfully Written" message
            System.out.println("\n\nSuccessfully wrote data to file: " + filename);
            JOptionPane.showMessageDialog(null, "Successfully wrote data to file: " + filename);

        } catch (IOException e) {
            //Print error message if exception is caught
            e.printStackTrace();
            System.out.println("Error writing to file: " + filename);
            JOptionPane.showMessageDialog(null, "Error writing to file: " + filename);
        }
    }
}
