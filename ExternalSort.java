import com.sun.deploy.util.ArrayUtil;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

//1 7 5 8 2 1 5 5 6 8 1 4 3 9 6 4
public class ExternalSort{
    public static Path callSort(Path tape1, int runsize) throws IOException {
        File t1 = tape1.toFile();
        File t2 = new File("t2.txt");
        File t3 = new File("t3.txt");
        File t4 = new File("t4.txt");
        int[] data = new int[runsize];
        Scanner read = new Scanner(t1);
        int numCount = 0;
        while(read.hasNextInt()){
            int num = read.nextInt();
            numCount++;
        }
        read.close();
        read = new Scanner(t1);
        int numRuns = ((numCount/runsize) + (numCount % runsize == 0 ? 0:1));
        boolean swap = true;
        ArrayList<Integer> list3 = new ArrayList<Integer>();
        ArrayList<Integer> list4 = new ArrayList<>();
        for(int j=0; j<numRuns; j++){
            for(int i=0; i<runsize; i++){
                if(read.hasNextInt()) {
                    data[i] = read.nextInt();
                }
            }
            Arrays.sort(data);
            if(swap){
                for(int a:data){
                    list3.add(a);
                }
                FileWriter out = new FileWriter(t3, true);
                out.write(printArray(data));
                out.close();
            }
            else{
                for(int a:data){
                    list4.add(a);
                }
                FileWriter out = new FileWriter(t4, true);
                out.write(printArray(data));
                out.close();
            }
            data = new int[runsize];
            swap = !swap;
        }
        read.close();
        System.out.println("LIST3: " +list3.toString());
        System.out.println("LIST4: " +list4.toString());
        int[] merged = mergeArrays(list3, list4, runsize);
        numRuns = (merged.length / (runsize*2)) + (merged.length % (runsize*2) != 0 ? 1:0);
        FileWriter out1 = new FileWriter(t1, false);
        FileWriter out2 = new FileWriter(t2, false);
        StringBuilder str = new StringBuilder();
        swap = true;

        int index = 0;
        int runCounter =0;
        while(index < merged.length){
            str.append(merged[index]);
            str.append(" ");
            index++;
            runCounter++;
            if(runCounter == runsize*2){
                if(swap){
                    out1.write(str.toString());
                }else{
                    out2.write(str.toString());
                }
                str = new StringBuilder();
                swap = !swap;
            }
        }
        if(swap){
            out1.write(str.toString());
        }else{
            out2.write(str.toString());
        }
        out1.close();
        out2.close();

        list3 = readTape(t1);
        list4 = readTape(t2);
        merged = mergeArraysFINAL(list3, list4);
        Path fin = Paths.get("output.txt");
        out1 = new FileWriter(fin.toFile());
        str = new StringBuilder();
        for(int a:merged){
            if(a != 0){
                str.append(a).append(" ");
            }
        }
        out1.write(str.toString());
        out1.close();

        flush(t1);
        flush(t2);
        flush(t3);
        flush(t4);
        return fin;
    }
    private static String printArray(int[] array){
        StringBuilder arr = new StringBuilder();
        for (int anArray : array) {
            if (anArray != 0) {
                arr.append(anArray);
                arr.append(" ");
            }
        }
        return arr.toString();
    }
    private static int[] mergeArrays(ArrayList<Integer> a1, ArrayList<Integer> a2, int runsize){
        int[] toReturn = new int[a1.size()+a2.size()];
        ArrayList<int[]> sliceda1 = sliceArray(a1, runsize);
        ArrayList<int[]> sliceda2 = sliceArray(a2, runsize);
        int index1=0, index2=0, index3=0;
        int slicecount1 = sliceda1.size();
        int slicecount2 = sliceda2.size();
        System.out.println("Slice1 " + slicecount1 + "    Slice2: " + slicecount2 );
        int slicecounter =0;
        int[] currenta1 = sliceda1.get(slicecounter);
        int[] currenta2 = sliceda2.get(slicecounter);
        while(index3 < toReturn.length){
            System.out.print("index1 : " +index1 + "   index2 : " + index2 + "\t");
            if(index1 < runsize && index2 < runsize) {
                System.out.println("COMPARING " + currenta1[index1] + " AND " + currenta2[index2]);
            }
            if(index1 >= runsize && index2 < runsize){
                toReturn[index3] = currenta2[index2];
                index2++;
                index3++;
            }
            else if(index1 < runsize && index2 >= runsize){
                toReturn[index3] = currenta1[index1];
                index1++;
                index3++;
            }
            else if(index1 < runsize && index2 < runsize){
                if(currenta1[index1] <= currenta2[index2]){
                    toReturn[index3] = currenta1[index1];
                    index1++;
                    index3++;
                }
                else{
                    toReturn[index3] = currenta2[index2];
                    index2++;
                    index3++;
                }
            }
            else if(index1 == runsize && index2 == runsize){
                System.out.println("SLICECOUNTER: " + slicecounter + "  SLICEA1: " + slicecount1 + " SLICEA2: " + slicecount2);
                slicecounter++;
                if(slicecounter < slicecount1) {
                    currenta1 = sliceda1.get(slicecounter);
                }else{
                    currenta1 = new int[runsize];
                    for(int a:currenta1){
                        currenta1[a] = 999999;
                    }
                }
                if(slicecounter < slicecount2) {
                    currenta2 = sliceda2.get(slicecounter);
                }else{
                    currenta2 = new int[runsize];
                    for(int b:currenta2){
                        currenta2[b] = 99999;
                    }
                }
                index1=0;
                index2=0;
                System.out.println("CURRENTa1: " + Arrays.toString(currenta1));
                System.out.println("CURRENTa2: " + Arrays.toString(currenta2));
            }
            System.out.println("Array: " + Arrays.toString(toReturn));
        }
        return toReturn;
    }
    private static int[] mergeArraysFINAL(ArrayList<Integer> a1, ArrayList<Integer> a2){
        System.out.println("FinalA1: " + a1 + "\nFinalA2: " + a2);
        int[] toReturn = new int[a1.size()+a2.size()];
        int index1=0, index2=0, index3=0;
        int num1, num2;
        while(index3 < toReturn.length) {
            if(index1 >= a1.size()){
                num1 = 99999999;
            }else num1 = a1.get(index1);
            if(index2 >= a2.size()){
                num2 = 99999999;
            }else num2 = a2.get(index2);
            if (num1 <= num2) {
                toReturn[index3] = num1;
                index1++;
                index3++;
            } else {
                toReturn[index3] = num2;
                index2++;
                index3++;
            }
            System.out.println("FINAL Array: " + Arrays.toString(toReturn));
        }
        return toReturn;
    }
    private static ArrayList<int[]> sliceArray(ArrayList<Integer> arr, int runsize){
        ArrayList<int[]> ret = new ArrayList<>();
        int numSlices = ((arr.size() / runsize) + (arr.size() % runsize != 0 ? 1:0));
        System.out.println("NUMSLICES: " + numSlices);
        for(int i=0; i<numSlices; i++){
            ret.add(new int[runsize]);
            for(int j=0; j<runsize; j++){
                ret.get(i)[j] = arr.get((runsize*i) + j);
            }
        }
        return ret;
    }
    private static ArrayList<Integer> readTape(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        ArrayList<Integer> ret = new ArrayList<>();
        while(reader.hasNextInt()){
            ret.add(Integer.parseInt(reader.next()));
        }
        return ret;
    }
    private static void flush(File t) throws IOException {
        FileWriter flusher = new FileWriter(t);
        flusher.write("");
        flusher.close();
    }
    public static void main(String args[]) throws IOException {
        Path output;
        if(args.length != 0){
            output = callSort(Paths.get(args[0]),2);
        }else{
            output = callSort(Paths.get("t1.txt"),4);
        }
        System.out.println("Path: " + output.toRealPath());
        Scanner read = new Scanner(output.toFile());
        System.out.print("Output: ");
        while(read.hasNextLine()){
            String str = read.nextLine();
            System.out.println(str);
        }
    }
}