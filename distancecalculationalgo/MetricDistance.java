package modulemetricdistance.distancecalculationalgo;

import java.io.*;
import java.util.*;


/**
 * Created by yliu12 on 2017/4/25.
 * CS692-Final-DistanceMetrics
 */


public class MetricDistance {
    private String fileDirectory;
    private int fieldTotal;

    private List<String> linesFromFIle = new ArrayList<>();
    private Map<String, int[]> moduleMap = new HashMap<>();

    private List<String> rawModNames = new ArrayList<>();
    private boolean proceedFlag = true;

public MetricDistance (String fileDirectory){
    this.fileDirectory = fileDirectory;

    }
    public MetricDistance ( ){

    }

    public static void main(String[] args) {
        MetricDistance MD = new MetricDistance();
        MD.calculateMetricDistance();

    }




    public String calculateMetricDistance() throws MDException{
        try {
            if (fileDirectory == null){
                readConsoleInput();
            }

            System.out.println(fileDirectory);
            fileDirectoryFormatCheck(fileDirectory);
            readInputFile(fileDirectory);
            fileDataFormatCheck(linesFromFIle);
            if(!proceedFlag){
                return null;
            }
            Map<String,int[]> resultMap = calculateResults();
            printResult(resultMap);

        } catch (Exception e) {
           e.printStackTrace();
            // MD_error(e.getMessage(), MD_errorType.Exception, 9, e);
        }
        return "";

    }



    private void readConsoleInput() throws IOException, MDException {


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please type in file name");
        String s = br.readLine();
        fileDirectory = s.trim();
        System.out.println("the Input file is :   " + fileDirectory);


    }

    private void fileDirectoryFormatCheck(String fileDirectory)throws  MDException {
        if (!fileDirectory.matches(".*.txt"))
            MD_error("FileNameNotCorrect", MD_errorType.FileDirectoryException, 8);
        //throw Exception e;
    }


    private void readInputFile(String fileDirectory) throws IOException, MDException {

        File f =  new File(fileDirectory);
        if(f.length()>10000000){
            MD_error("File Too Large, Max 10MB", MD_errorType.IOException, 9);

        }


        try (BufferedReader br = new BufferedReader(new FileReader(fileDirectory))) {


            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            //linesFromFIle.add(line);
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                linesFromFIle.add(line);

                line = br.readLine();
            }


            System.out.println("+++++++++++++++++++++++++++++++++++FileData Starts+++++++++++++++++++++++++++++++");
            System.out.println(sb.toString());
            System.out.println("+++++++++++++++++++++++++++++++++++FileData Ends+++++++++++++++++++++++++++++++");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String fileDataFormatCheck(List<String> linesFromFIle)throws  MDException {
        if (null == linesFromFIle || linesFromFIle.isEmpty()) {
            MD_error("The Source file is empty", MD_errorType.FileFormatException, 9);
            return null;
        }
        if (linesFromFIle.size() < 3) {
            MD_error("The Source file does not have enough record", MD_errorType.FileFormatException, 9);
            return null;
        }

        String firstLine = linesFromFIle.get(0);
        linesFromFIle.remove(0);

        firstLineCheck(firstLine);
        bodyCheckandMergeSameMods(linesFromFIle);

        if(moduleMap.size() ==1){

            /* TO Be Tested
                Delete THis Error and make it an output

             */
            proceedFlag = false;
            StringBuilder sb = new StringBuilder();

            sb.append("digraph G {");
            for (Map.Entry<String, int[]> resultLine : moduleMap.entrySet()) {
                sb.append(resultLine.getKey());
                sb.append("[label=\"");
                int[] distanceArray = resultLine.getValue();
                for (int i=0;i<distanceArray.length;i++){
                    if (distanceArray[i] == 0) {
                        continue;
                    }
                    sb.append("m").append(i+1).append(" (").append(distanceArray[i]).append("),");

                }
                sb.setLength(sb.length()-1);
                sb.append("\"];");

            }

            sb.append("}");
            System.out.println(sb);
        }
        return null;
    }

    private void firstLineCheck(String firstLine) throws  MDException {
        if (!firstLine.matches("\\d{1,32},\\d{1,32}")) {
            MD_error("Illegal First Line In File", MD_errorType.FileFormatException, 9);
        }
        String[] firstLineItems = firstLine.split(",");

        if (!firstLineItems[0].equals(linesFromFIle.size() + "")) {
            MD_error("First line module number and actual module number not match", MD_errorType.FileFormatException, 7);
        }
        fieldTotal = Integer.parseInt(firstLineItems[1]);
    }

    private void bodyCheckandMergeSameMods(List<String> linesFromFIle) throws  MDException {
        for (String record : linesFromFIle) {
            String[] fields = record.split(",");
            String moduleName = fields[0];





            if (fields.length != fieldTotal + 1) {
                MD_error("Error in number of fields, expected " + fieldTotal + " but " + (fields.length - 1) + " in " +
                        moduleName, MD_errorType.FileFormatException, 9);
            }


            if (!moduleName.matches("Mod\\d+")) {
                MD_error("Illegal Module Name in "+  moduleName, MD_errorType.FileFormatException, 9);
            }
            if (rawModNames.contains(moduleName)) {
                MD_error("Duplicate Module Name " + moduleName, MD_errorType.FileFormatException, 9);
            }
            rawModNames.add(moduleName);

            int[] onlyFields = new int[fieldTotal];

            for (int i = 1; i < fields.length; i++) {
                String field = fields[i].trim();
                if (!field.matches("\\d{1,32}")) {
                    MD_error(" Illegal Field in " + moduleName, MD_errorType.IllegalField, 9);
                }
                onlyFields[i - 1] = Integer.parseInt(field);
            }


            String nameOfEquivalentMod = null;
            for (Map.Entry<String, int[]> mapRecord : moduleMap.entrySet()) {

                if(Arrays.equals(mapRecord.getValue(),onlyFields)){
                    nameOfEquivalentMod = mapRecord.getKey();
                    break;
                }
            }
            if(null != nameOfEquivalentMod){
                moduleMap.put( nameOfEquivalentMod+moduleName, moduleMap.remove( nameOfEquivalentMod ) );
                continue;

            }

            moduleMap.put(moduleName, onlyFields);
        }

    }

    private  Map<String,int[]> calculateResults() throws  MDException{

        Map<String,int[]> resultMap = new HashMap<>();


        for (Map.Entry<String, int[]> currentRecord : moduleMap.entrySet()) {
            DistanceResultObject minDistanceObj = null;
            String currentMod = currentRecord.getKey();
            int[] currentFields = currentRecord.getValue();


            for (Map.Entry<String, int[]> otherRecord : moduleMap.entrySet()) {

                String otherMod = otherRecord.getKey();
                int[] otherFields = otherRecord.getValue();

                if (currentMod.equals(otherMod)) {
                    continue;
                }


                DistanceResultObject dObj = calculateFieldsDistance(currentFields, otherFields);
                dObj.setModName(otherMod);

                if(minDistanceObj == null
                        ||minDistanceObj.getNumOfDifferentFields()>dObj.getNumOfDifferentFields()
                        ||(minDistanceObj.getNumOfDifferentFields()==dObj.getNumOfDifferentFields()
                            &&minDistanceObj.getDistanceTotal()>dObj.getDistanceTotal())){

                    minDistanceObj = dObj;
                }



            }
            if(minDistanceObj == null){
                throw new MDException();
            }

            resultMap.remove(minDistanceObj.getModName()+"->"+currentMod);
            resultMap.put(currentMod+"->"+minDistanceObj.getModName(),minDistanceObj.getDistanceArray().clone());



        }
        return resultMap;
    }

    private DistanceResultObject calculateFieldsDistance(int[] currentFields, int[] otherFields) throws MDException{

        DistanceResultObject currentDistance = new DistanceResultObject();
        int[] distanceArray = new int[currentFields.length];
        int distanceTotal = 0;
        int numOfDifferentFields = 0;


        for(int i=0;i<currentFields.length;i++){

           int thisFieldsDistance = Math.abs(currentFields[i]-otherFields[i]);
            distanceArray[i] = thisFieldsDistance;
            distanceTotal +=thisFieldsDistance;
            if(thisFieldsDistance!=0){
                numOfDifferentFields ++;
            }

        }


        currentDistance.setDistanceArray(distanceArray);
        currentDistance.setDistanceTotal(distanceTotal);
        currentDistance.setNumOfDifferentFields(numOfDifferentFields);
        return currentDistance;
    }


    private String printResult(Map<String, int[]> resultmap) throws MDException, IOException {

        /*        List<String> toDeleteKeys = new ArrayList<>();



        for(String toDeleteKey : toDeleteKeys){
            resultmap.remove(toDeleteKey);
        }*/








        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");

        for (Map.Entry<String, int[]> resultLine : resultmap.entrySet()) {

            sb.append(resultLine.getKey());
            sb.append("[label=\"");
            int[] distanceArray = resultLine.getValue();
            for (int i=0;i<distanceArray.length;i++){
                if (distanceArray[i] == 0){
                    continue;
                }
                sb.append("m").append((i+1)).append(" (").append(distanceArray[i]).append("),");

            }
            sb.setLength(sb.length()-1);
            sb.append("\"];\n");

        }
        sb.append("}\n");
        System.out.println(sb);

        String filename = fileDirectory.replace(".","-result.");
        while(true){


            File writeName = new File(filename);

            if(writeName.createNewFile()){
                BufferedWriter out = new BufferedWriter(new FileWriter(writeName));
                out.write(sb.toString());
                out.flush();
                out.close();

                break;
            }
            filename = filename.replace(".","-new.");
        }

        return sb.toString();
    }





//==================================================================================Error Handling ======================================================================================================================
    private void MD_error(String errorMessage, Enum MD_errortype, int severity) throws  MDException{

      StringBuilder sb = new StringBuilder();
        sb.append("==========================================ERROR==================================================\n");


        sb.append(errorMessage).append("\n").append("Error Type" ).append( MD_errortype).append("\n").append("severity: " ).append(severity).append("\n");

        sb.append("==========================================ERROR==================================================\n");

        System.err.println(sb);


        try{
            String fileName = fileDirectory.replace(".","-error.");


            while(true){
                File writeName = new File(fileName);
                if(writeName.createNewFile()){
                    BufferedWriter out = new BufferedWriter(new FileWriter(writeName));
                    out.write(sb.toString());
                    out.flush();
                    out.close();
                    break;
                }
                fileName = fileDirectory.replace(".","-new.");
            }
        }catch (IOException ioE){
                    ioE.printStackTrace();
        }



        throw new MDException();
    }

    enum MD_errorType {
        IOException,
        FileDirectoryException,
        FileFormatException,
        IllegalField,
    }

}
