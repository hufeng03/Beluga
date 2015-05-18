import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Feng on 2015-05-10.
 */
public class Test {

    public static void main(String[] args) {
        String str = "test/assets/hufeng.jpg";
        Pattern pattern = Pattern.compile("^/assets");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String result = matcher.replaceAll("");
            System.out.println(result);
        } else {
            System.out.println("no matches");
        }
    }
}
