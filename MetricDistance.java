package modulemetricdistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Created by yliu12 on 2017/4/25.
 * CS692-Final-DistanceMetrics
 */



/*
To DO

    1. WHat's the Largest Record/module Number?


    2. What should happen if There is only one Record


 */



public class MetricDistance {
    private String fileDirectory;
    private int fieldTotal;

    private List<String> linesFromFIle = new ArrayList<>();
    private Map<String, int[]> moduleMap = new HashMap<>();

    private List<String> rawModNames = new ArrayList<>();



    public static void main(String[] args) {
        MetricDistance MD = new MetricDistance();
        MD.calculateMetricDistance();

    }

    private void calculateMetricDistance() {
        try {
            readConsoleInput();
            fileDirectoryFormatCheck(fileDirectory);
            readInputFile(fileDirectory);
            fileDataFormatCheck(linesFromFIle);
            Map<String,int[]> resultmap = calculateResults();
            printResult(resultmap);

        } catch (Exception e) {
            MD_error(e.getMessage(), MD_errortype.Exception, 9, e);
        }


    }



    private void readConsoleInput() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please type in file name");
        String s = br.readLine();
        fileDirectory = s.trim();
        System.out.println("the Input file is :   " + fileDirectory);


    }

    private void fileDirectoryFormatCheck(String fileDirectory) {
        if (!fileDirectory.matches(".*.txt"))
            MD_error("FileNameNotCorrect", MD_errortype.FileNameNotCorrect, 9, null);
        //throw Exception e;
    }


    private void readInputFile(String fileDirectory) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
        System.out.println("+++++++++++++++++++++++++++++++++++FileData Starts+++++++++++++++++++++++++++++++");

        try {

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            //linesFromFIle.add(line);
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                linesFromFIle.add(line);
                System.out.println(line);
                line = br.readLine();
            }

            System.out.println("+++++++++++++++++++++++++++++++++++FileData Ends+++++++++++++++++++++++++++++++");

        } catch (IOException e) {

            MD_error(e.getMessage(), MD_errortype.IOException, 9, e);
        } catch (Exception e) {

            MD_error(e.getMessage(), MD_errortype.Exception, 9, e);
        } finally {
            br.close();
        }

    }


    private String fileDataFormatCheck(List<String> linesFromFIle) {
        if (null == linesFromFIle || linesFromFIle.isEmpty()) {
            MD_error("The Source file is empty", MD_errortype.EmptySourceFile, 9, null);
            return null;
        }
        if (linesFromFIle.size() < 3) {
            MD_error("The Source file does not have enough record", MD_errortype.SourceFileTooShort, 9, null);
            return null;
        }

        String firstLine = linesFromFIle.get(0);
        linesFromFIle.remove(0);

        firstLineCheck(firstLine);
        bodyCheckandMergeSameMods(linesFromFIle);

        if(moduleMap.size() ==1){
            MD_error("All Module are the same", MD_errortype.AllModuleAreSame, 9, null);
        }
        return null;
    }

    private void firstLineCheck(String firstLine) {
        if (!firstLine.matches("\\d{1,32},\\d{1,32}")) {
            MD_error("Illegal First Line In File", MD_errortype.IllegalFistLineInFile, 9, null);
        }
        String[] firstLineItems = firstLine.split(",");

        if (!firstLineItems[0].equals(linesFromFIle.size() + "")) {
            MD_error("Wrong Recod Number In File", MD_errortype.WrongRecordNumberInFile, 9, null);
        }
        fieldTotal = Integer.parseInt(firstLineItems[1]);
    }

    private void bodyCheckandMergeSameMods(List<String> linesFromFIle) {
        for (String record : linesFromFIle) {
            String[] fields = record.split(",");
            String moduleName = fields[0];





            if (fields.length != fieldTotal + 1) {
                MD_error("Error in number of fields, expected " + fieldTotal + " but " + (fields.length - 1) + " in " + moduleName, MD_errortype.WrongFieldNumberInFile, 9, null);
            }


            if (!moduleName.matches("Mod\\d{1,32}")) {
                MD_error("Illegal Module Name " + moduleName, MD_errortype.IllegalModuleName, 9, null);
            }
            if (rawModNames.contains(moduleName)) {
                MD_error("Duplicate Module Name " + moduleName, MD_errortype.DuplicateModuleName, 9, null);
            }
            rawModNames.add(moduleName);

            int[] onlyFields = new int[fieldTotal];

            for (int i = 1; i < fields.length; i++) {
                String field = fields[i].trim();
                if (!field.matches("\\d{1,32}")) {
                    MD_error(" Illegal Field in " + moduleName, MD_errortype.IllegalField, 9, null);
                }
                onlyFields[i - 1] = Integer.parseInt(field);
            }


            String nameOfEquivilantMod = null;
            for (Map.Entry<String, int[]> mapRecord : moduleMap.entrySet()) {

                if(Arrays.equals(mapRecord.getValue(),onlyFields)){
                    nameOfEquivilantMod = mapRecord.getKey();
                    break;
                }
            }
            if(null != nameOfEquivilantMod){
                moduleMap.put( nameOfEquivilantMod+moduleName, moduleMap.remove( nameOfEquivilantMod ) );
                continue;

            }

            moduleMap.put(moduleName, onlyFields);
        }

    }

    private  Map<String,int[]> calculateResults() {

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
                        ||(minDistanceObj.getNumOfDifferentFields()==dObj.getNumOfDifferentFields()&&minDistanceObj.getDistanceTotal()>dObj.getDistanceTotal())){

                    minDistanceObj = dObj;
                }



            }
            resultMap.remove(minDistanceObj.getModName()+"->"+currentMod);
            resultMap.put(currentMod+"->"+minDistanceObj.getModName(),minDistanceObj.getDistanceArray().clone());



        }
        return resultMap;
    }

    private DistanceResultObject calculateFieldsDistance(int[] currentFields, int[] otherFields) {

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


    private void printResult(Map<String, int[]> resultmap) {
        List<String> toDeleteKeys = new ArrayList<>();


        for(String toDeleteKey : toDeleteKeys){
            resultmap.remove(toDeleteKey);
        }

        System.out.println("digraph G {");
        for (Map.Entry<String, int[]> resultLine : resultmap.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(resultLine.getKey());
            sb.append("[label=\"");
            int[] distanceArray = resultLine.getValue();
            for (int i=0;i<distanceArray.length;i++){
                if (distanceArray[i] == 0){
                    continue;
                }
                sb.append("m"+(i+1)+" ("+distanceArray[i]+"),");

            }
            sb.setLength(sb.length()-1);
            sb.append("\"];");
            System.out.println(sb);
        }
        System.out.println("}");

    }





//==================================================================================Error Handling ======================================================================================================================
    private void MD_error(String errorMessage, Enum MD_errortype, int severity, Exception e) {
        System.out.println("==========================================ERROR==================================================");
        System.out.println(errorMessage);
        System.out.println("Error Type" + MD_errortype);
        System.out.println("severity: " + severity);
        if (null != e) {
            e.printStackTrace();
        }
        System.out.println("=============================================ERROR===============================================");

        System.exit(1);

    }

    enum MD_errortype {
        IOException,
        Exception,
        FileNameNotCorrect,
        EmptySourceFile,
        SourceFileTooShort,
        IllegalFistLineInFile,
        WrongRecordNumberInFile,
        WrongFieldNumberInFile,
        IllegalModuleName,
        DuplicateModuleName,
        IllegalField,
        AllModuleAreSame
    }

}
