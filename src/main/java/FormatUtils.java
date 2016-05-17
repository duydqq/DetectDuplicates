
public class FormatUtils {


    public static double round2decimal(double input, int places){
        double quota = Math.pow(10D,places);
        return (double)Math.round(input*quota)/quota;
    }
    //http://stackoverflow.com/a/3758880
    public static String formatSize(long bytes){
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1)+"";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String formatTime(long milliseconds){
        if(milliseconds<1000)
            return milliseconds+" milliseconds";
        if(milliseconds<(60*1000))
            return milliseconds/1000+" seconds";
        if(milliseconds<(60*1000*60))
            return milliseconds/(60*1000)+" minutes";
        return milliseconds/(60*60*1000)+" hours";
    }
}
