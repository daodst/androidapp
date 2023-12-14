



package net.gsantner.opoc.format.markdown;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


@SuppressWarnings({"WeakerAccess", "CaughtExceptionImmediatelyRethrown", "SameParameterValue", "unused", "SpellCheckingInspection", "RepeatedSpace", "SingleCharAlternation", "Convert2Lambda"})
public class SimpleMarkdownParser {
    
    
    
    public interface SmpFilter {
        String filter(String text);
    }

    public final static SmpFilter FILTER_ANDROID_TEXTVIEW = new SmpFilter() {
        @Override
        public String filter(String text) {
            
            

            
            while (text.contains("\n\n#")) {
                text = text.replace("\n\n#", "\n#");
            }

            return text
                    .replaceAll("(?s)<!--.*?-->", "")  
                    .replace("\n\n", "\n<br/>\n") 
                    .replace("~°", "&nbsp;&nbsp;") 
                    .replaceAll("(?m)^### (.*)$", "<br/><big><b><font color='#000000'>$1</font></b></big><br/>") 
                    .replaceAll("(?m)^## (.*)$", "<br/><big><big><b><font color='#000000'>$1</font></b></big></big><br/><br/>") 
                    .replaceAll("(?m)^# (.*)$", "<br/><big><big><big><b><font color='#000000'>$1</font></b></big></big></big><br/><br/>") 
                    .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") 
                    .replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") 
                    .replaceAll("<(http|https):\\/\\/(.*)>", "<a href='$1://$2'>$1://$2</a>") 
                    .replaceAll("(?m)^([-*] )(.*)$", "<font color='#000001'>&#8226;</font> $2<br/>") 
                    .replaceAll("(?m)^  (-|\\*) ([^<]*)$", "&nbsp;&nbsp;<font color='#000001'>&#8226;</font> $2<br/>") 
                    .replaceAll("`([^<]*)`", "<font face='monospace'>$1</font>") 
                    .replace("\\*", "●") 
                    .replaceAll("(?m)\\*\\*(.*)\\*\\*", "<b>$1</b>") 
                    .replaceAll("(?m)\\*(.*)\\*", "<i>$1</i>") 
                    .replace("●", "*") 
                    .replaceAll("(?m)  $", "<br/>") 
                    ;
        }
    };

    public final static SmpFilter FILTER_WEB = new SmpFilter() {
        @Override
        public String filter(String text) {
            
            while (text.contains("\n\n#")) {
                text = text.replace("\n\n#", "\n#");
            }

            text = text
                    .replaceAll("(?s)<!--.*?-->", "")  
                    .replace("\n\n", "\n<br/>\n") 
                    .replaceAll("~°", "&nbsp;&nbsp;") 
                    .replaceAll("(?m)^### (.*)$", "<h3>$1</h3>") 
                    .replaceAll("(?m)^## (.*)$", "<h2>$1</h2>") 
                    .replaceAll("(?m)^# (.*)$", "<h1>$1</h1>") 
                    .replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\\'$2\\' alt='$1' />") 
                    .replaceAll("<(http|https):\\/\\/(.*)>", "<a href='$1://$2'>$1://$2</a>") 
                    .replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\\'$2\\'>$1</a>") 
                    .replaceAll("(?m)^[-*] (.*)$", "<font color='#000001'>&#8226;</font> $1  ") 
                    .replaceAll("(?m)^  [-*] (.*)$", "&nbsp;&nbsp;<font color='#000001'>&#8226;</font> $1  ") 
                    .replaceAll("`([^<]*)`", "<code>$1</code>") 
                    .replace("\\*", "●") 
                    .replaceAll("(?m)\\*\\*(.*)\\*\\*", "<b>$1</b>") 
                    .replaceAll("(?m)\\*(.*)\\*", "<i>$1</i>") 
                    .replace("●", "*") 
                    .replaceAll("(?m)  $", "<br/>") 
            ;
            return text;
        }
    };

    public final static SmpFilter FILTER_CHANGELOG = new SmpFilter() {
        @Override
        public String filter(String text) {
            text = text
                    .replace("New:", "<font color='#276230'>New:</font>")
                    .replace("New features:", "<font color='#276230'>New:</font>")
                    .replace("Added:", "<font color='#276230'>Added:</font>")
                    .replace("Add:", "<font color='#276230'>Add:</font>")
                    .replace("Fixed:", "<font color='#005688'>Fixed:</font>")
                    .replace("Fix:", "<font color='#005688'>Fix:</font>")
                    .replace("Removed:", "<font color='#C13524'>Removed:</font>")
                    .replace("Updated:", "<font color='#555555'>Updated:</font>")
                    .replace("Improved:", "<font color='#555555'>Improved:</font>")
                    .replace("Modified:", "<font color='#555555'>Modified:</font>")
                    .replace("Mod:", "<font color='#555555'>Mod:</font>")
            ;
            return text;
        }
    };
    public final static SmpFilter FILTER_H_TO_SUP = new SmpFilter() {
        @Override
        public String filter(String text) {
            text = text
                    .replace("<h1>", "<sup><sup><sup>")
                    .replace("</h1>", "</sup></sup></sup>")
                    .replace("<h2>", "<sup><sup>")
                    .replace("</h2>", "</sup></sup>")
                    .replace("<h3>", "<sup>")
                    .replace("</h3>", "</sup>")
            ;
            return text;
        }
    };
    public final static SmpFilter FILTER_NONE = new SmpFilter() {
        @Override
        public String filter(String text) {
            return text;
        }
    };

    
    
    
    private static SimpleMarkdownParser __instance;

    public static SimpleMarkdownParser get() {
        if (__instance == null) {
            __instance = new SimpleMarkdownParser();
        }
        return __instance;
    }

    
    
    
    private SmpFilter _defaultSmpFilter;
    private String _html;

    public SimpleMarkdownParser() {
        setDefaultSmpFilter(FILTER_WEB);
    }

    
    
    
    public SimpleMarkdownParser setDefaultSmpFilter(SmpFilter defaultSmpFilter) {
        _defaultSmpFilter = defaultSmpFilter;
        return this;
    }

    public SimpleMarkdownParser parse(String filepath, SmpFilter... smpFilters) throws IOException {
        return parse(new FileInputStream(filepath), "", smpFilters);
    }

    public SimpleMarkdownParser parse(InputStream inputStream, String lineMdPrefix, SmpFilter... smpFilters) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(lineMdPrefix);
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException rethrow) {
            _html = "";
            throw rethrow;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        _html = parse(sb.toString(), "", smpFilters).getHtml();
        return this;
    }

    public SimpleMarkdownParser parse(String markdown, String lineMdPrefix, SmpFilter... smpFilters) throws IOException {
        _html = markdown;
        if (smpFilters.length == 0) {
            smpFilters = new SmpFilter[]{_defaultSmpFilter};
        }
        for (SmpFilter smpFilter : smpFilters) {
            _html = smpFilter.filter(_html).trim();
        }
        return this;
    }

    public String getHtml() {
        return _html;
    }

    public SimpleMarkdownParser setHtml(String html) {
        _html = html;
        return this;
    }

    public SimpleMarkdownParser removeMultiNewlines() {
        _html = _html.replace("\n", "").replaceAll("(<br/>){3,}", "<br/><br/>");
        return this;
    }

    public SimpleMarkdownParser replaceBulletCharacter(String replacment) {
        _html = _html.replace("&#8226;", replacment);
        return this;
    }

    public SimpleMarkdownParser replaceColor(String hexColor, int newIntColor) {
        _html = _html.replace(hexColor, String.format("#%06X", 0xFFFFFF & newIntColor));
        return this;
    }

    @Override
    public String toString() {
        return _html != null ? _html : "";
    }
}
