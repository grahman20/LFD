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
 * Discretization is the process of converting numerical values into categorical values. We propose a new data-driven discretization technique called low frequency discretizer (LFD) that does not require any user input. LFD uses low frequency values as cut points and thus reduces the information loss due to discretization. It uses all other categorical attributes and any numerical attribute that has already been categorized. It considers that the influence of an attribute in discretization of another attribute depends on the strength of their relationship. 
 * 
 * <h2>Reference</h2>
 * 
 * Rahman, M. G. and Islam, M. Z. (2016): Discretization of Continuous Attributes Through Low Frequency Numerical Values and Attribute Interdependency. Expert Systems with Applications, 45, 410-423. http://dx.doi.org/10.1016/j.eswa.2015.10.005.
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
