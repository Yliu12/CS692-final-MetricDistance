package modulemetricdistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yliu12 on 2017/4/25.
 */
public class MetricDistance {
    private String fileDirectory;
    List<String> LinesFromFIle = new ArrayList<>();

    public static void main(String[] args){
        MetricDistance MD = new MetricDistance();
        MD.calculateMetricDistance();

    }

    private void calculateMetricDistance (){
        try{
            readConsoleInput();
            fileDirectoryFormatCheck(fileDirectory);
            readInputFile(fileDirectory);

        }
        catch (Exception e){
            MD_error(e.getMessage(), MD_errortype.Exception ,9,e);
        }


    }


    private void readConsoleInput() throws IOException {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please type in file name");
            String s = br.readLine();
            fileDirectory = s.trim();
            System.out.println("the Input file is :   "+fileDirectory);



    }
    private void fileDirectoryFormatCheck(String fileDirectory) {
        if(!fileDirectory.matches(".*.txt"))
        MD_error("FileNameNotCorrect", MD_errortype.FileNameNotCorrect,9,null);
        //throw Exception e;
    }



    private void readInputFile(String fileDirectory) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
        try {

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                LinesFromFIle.add(line);
            }
            String everything = sb.toString();
        } catch (IOException e) {

            MD_error(e.getMessage(), MD_errortype.IOException,9,e);
        }
        catch (Exception e) {

            MD_error(e.getMessage(), MD_errortype.Exception,9,e);
        }
        finally {
            br.close();
        }

    }

    private void MD_error(String errorMessage,Enum MD_errortype, int severity,Exception e) {
        System.out.println("==========================================ERROR==================================================");
        System.out.println(errorMessage);
        System.out.println("Error Type" + MD_errortype);
        System.out.println("severity: "+severity);
        if(null!= e){
            e.printStackTrace();
        }
        System.out.println("=============================================ERROR===============================================");

        System.exit(1);
    }

    enum MD_errortype {
        IOException, Exception, FileNameNotCorrect,
    }
///"(\\d{1,32},){3}"

}
