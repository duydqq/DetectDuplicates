import java.io.*;
import java.util.*;
import java.util.concurrent.SynchronousQueue;


class App {

    private static final String LOGNAME = "duplicates.txt";
    private static final String LISTNAME = "hashes.txt";
    private static final String HASH_ALGORITHM = "MD5";
    private static final int BUFFER_SIZE = 2048;

    private static boolean auto = false;
    private static boolean help = false;
    private static boolean list = false;
    private static boolean benchmark = false;

    private static String startDirectory = ".";

    private static long bytesProcessed = 0;
    private static long filesProcessed = 0;

    public static void main(String[] args) throws Exception {
        long beforeTotal = System.currentTimeMillis();
        for (String arg : args) {
            switch (arg) {
                case "-auto":
                    auto = true;
                    break;
                case "-help":
                    help = true;
                    break;
                case "-list":
                    list = true;
                    break;
                case "-bench":
                    benchmark = true;
                    break;
                default:
                    startDirectory = arg;
                    break;
            }
        }
        if (help) {
            printHelp();
            System.exit(0);
        }
        long beforeMD5 = System.currentTimeMillis();
        Map<String, List<File>> map = getHashes(new File(startDirectory));
        long deltaMD5 = System.currentTimeMillis()-beforeMD5;

        if (list) {
            listHashes(map);
        } else if (auto) deleteCollisions(map);
        else listCollisions(map);

        long deltaTotal = System.currentTimeMillis()-beforeTotal;
        if(benchmark) printBenchmark(deltaMD5, deltaTotal);
    }

    private static void printBenchmark(long deltaMD5, long deltaTotal) {
        System.out.println("Processed:");
        System.out.println(HASH_ALGORITHM+"'s of "+formatSize(bytesProcessed)+" of Data in "+formatTime(deltaMD5)+". ("+round2decimal((bytesProcessed/1000000D)/(deltaMD5/1000D))+" MB/second)");
        System.out.println(filesProcessed+" files in "+formatTime(deltaTotal)+" total.");
    }

    private static double round2decimal(double in){
        return (double)Math.round(in*100D)/100D;
    }
    //http://stackoverflow.com/a/3758880
    private static String formatSize(long bytes){
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1)+"";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static String formatTime(long milliseconds){
        if(milliseconds<1000)
            return milliseconds+" milliseconds";
        if(milliseconds<(60*1000))
            return milliseconds/1000+" seconds";
        if(milliseconds<(60*1000*60))
            return milliseconds/(60*1000)+" minutes";
        return milliseconds/(60*60*1000)+" hours";
    }
    private static void listHashes(Map<String, List<File>> map) {
        try {
            int files = 0;
            PrintWriter printer = new PrintWriter(LISTNAME, "UTF-8");


            for (String elem : map.keySet()) {
                for (File f : map.get(elem)) {
                    files++;
                    printer.println(elem + "  " + f.getName());

                }
                printer.flush();
            }
            printer.close();
            System.out.println("Done. Wrote "+files+" "+HASH_ALGORITHM+"'s into File "+LISTNAME);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private static void deleteCollisions(Map<String, List<File>> map) {
        int collisions = 0;
        for (String elem : map.keySet()) {
            List<File> files = map.get(elem);
            if (files.size() > 1)
                for (int i = 1; i < files.size(); i++) {
                    collisions++;
                    files.get(i).delete();
                }
        }
        System.out.println("Done. " + collisions + " files were deleted.");
    }

    private static void listCollisions(Map<String, List<File>> map) {

        int collisions = 0;

        try {
            PrintWriter printer = null;

            for (String elem : map.keySet()) {
                List<File> files = map.get(elem);
                if (files.size() > 1) {
                    if (printer == null) printer = new PrintWriter(LOGNAME, "UTF-8");
                    collisions++;
                    printer.println("Duplicate " + collisions + " (" + HASH_ALGORITHM + "=" + elem + ")");
                    for (File f : map.get(elem)) {
                        printer.println(f.getName());
                    }
                    printer.println();
                    printer.flush();
                }
            }
            if (printer != null) printer.close();
            System.out.println("Done. " + collisions + " Collisions were detected.");
            if (collisions > 0) {
                System.out.println("Check file " + LOGNAME + " for details.");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    private static void printHelp() {
        System.out.println("This program calculates the " + HASH_ALGORITHM + " checksums of all files in a directory\n" +
                        "and checks if there are identical files.\n" +
                        "If there are duplicates, the file "+LOGNAME+" will be created with details.\n\n"+
                        "Usage: java -jar DetectDuplicates.jar [(optional) Path to a Directory] [OPTION]\n\n" +
                        "The first optional parameter is a directory path to check for identical files. \n" +
                        "If omitted, the directory of the jar-file will be checked.\n" +
                        "Possible Options: \n" +
                        "-auto                 - automatically deletes duplicates\n"+
                        "-list                 - lists the " + HASH_ALGORITHM + " hashes in file "+LISTNAME+" and exits\n"+
                        "-bench                - prints some benchmark data after completion\n" +
                        "-help                 - prints this message and exits\n");

    }

    private static Map<String, List<File>> getHashes(File directory) {

        Map<String, List<File>> hashMap = new HashMap<>();

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (file.getName().equals(LOGNAME))continue;
            if (file.getName().equals(LISTNAME))continue;
            bytesProcessed+=file.length();
            filesProcessed++;

            String hash = HashUtils.fileToHash(file, HASH_ALGORITHM, BUFFER_SIZE);
            if (!hashMap.containsKey(hash)) {
                List<File> list = new ArrayList<>();
                list.add(file);
                hashMap.put(hash, list);
            } else {
                hashMap.get(hash).add(file);
            }
        }
        return hashMap;
    }

}
