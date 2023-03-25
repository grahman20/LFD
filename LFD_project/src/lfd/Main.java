/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lfd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * FIMUS imputes numerical and categorical missing values by using a data set’s 
 * existing patterns including co-appearances of attribute values, correlations 
 * among the attributes and similarity of values belonging to an attribute. 
 * 
 * <h2>Reference</h2>
 * 
 * Rahman, M. G. and Islam, M. Z. (2014): FIMUS: A Framework for Imputing Missing Values Using Co-Appearance, Correlation and Similarity Analysis, Knowledge-Based Systems, 56, 311 - 327, ISSN 0950-7051, DOI information: http://dx.doi.org/10.1016/j.knosys.2013.12.005
 *  
 * @author Md Geaur Rahman <https://csusap.csu.edu.au/~grahman/>
 */
public class Main {
        /** command line reader */
    BufferedReader stdIn;
        /** class name, used in logging errors */
    static String className = lfd.Main.class.getName();
    
    public Main()
    {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main terminal=new Main();
        String fileAttrInfo = terminal.inputFileName("Please enter the name of the file containing the 2 line attribute information.(example: c:\\data\\attrinfo.txt?)");
        String fileDataFileIn= terminal.inputFileName("Please enter the name of the data file having numerical attributes: (example: c:\\data\\data.txt?)");
        String fileOutput = terminal.inputFileName("Please enter the name of the output file: (example: c:\\data\\out.txt?)");
        //call LFD              
        LFD fimus=new LFD();
        fimus.runLFD(fileAttrInfo, fileDataFileIn, fileOutput);
        System.out.println("\nData discretization by LFD is done. The completed data set is written to: \n"+fileOutput);
    }
      

    /**
     * Given a message to display to the user, ask user to enter a file name.
     *
     * @param message message to user prompting for filename
     * @return filename entered by user
     */
    private String inputFileName(String message)
    {
        String fileName = "";
        try
        {
            System.out.println(message);
            fileName = stdIn.readLine();
        }
        catch (IOException ex)
        {
            Logger.getLogger(className).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }

}
