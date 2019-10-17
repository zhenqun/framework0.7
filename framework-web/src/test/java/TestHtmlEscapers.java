import com.google.common.html.HtmlEscapers;

/**
 * @author toquery
 * @version 1
 */
public class TestHtmlEscapers {
    public static void main(String[] args) {
        String str = "<script>alert('test')</script>\n" +
                "echo foo > file &\n" +
                "<a>你好</a>\n";
        String out = HtmlEscapers.htmlEscaper().escape(str);
        System.out.println(out);
    }
}
