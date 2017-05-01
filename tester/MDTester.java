package modulemetricdistance.tester;

import modulemetricdistance.distancecalculationalgo.MetricDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yliu12 on 2017/4/29.
 */
public class MDTester {

    public static void main(String[] args) {
        String directory;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please type in file name");
        String s = null;
        try{
             s = br.readLine();

        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        if(s == null){
            return;
        }
        directory = s.trim();
        System.out.println("the Input directory is :   " + directory);


        File file = new File(directory);
        File[] files = file.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isFile()){
                fileNames.add(directory+"/"+files[i].getName());
            }
        }

        System.out.println("Find "+fileNames.size()+" files");


        for(String testFIle : fileNames  ){

            MetricDistance MD = new MetricDistance(testFIle);

            String sss = MD.calculateMetricDistance();

        }

        //MetricDistance MD = new MetricDistance();
        //MD.calculateMetricDistance();

    }



}
