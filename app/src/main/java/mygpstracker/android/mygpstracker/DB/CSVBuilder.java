package mygpstracker.android.mygpstracker.DB;

/***
 * this class will help build a csv format String
 */

public class CSVBuilder {

    private StringBuilder stringBuilder = new StringBuilder();
    private char separator;
    private char escapeChar;
    private String lineEnd;
    private char quoteChar;

    public static final char DEFAULT_SEPARATOR = ',';
    public static final char NO_QUOTE_CHARACTER = '\u0000';
    public static final char NO_ESCAPE_CHARACTER = '\u0000';
    public static final String DEFAULT_LINE_END = "\n";
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    public static final char DEFAULT_ESCAPE_CHARACTER = '"';

    /**
     * Default constructor using the default parameters
     */
    public CSVBuilder() {
        this(DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    /**
     * Customize constructor using the given parameters
     * @param separator
     * @param quoteChar
     * @param escapeChar
     * @param lineEnd
     */
    public CSVBuilder(char separator, char quoteChar, char escapeChar, String lineEnd) {
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
        this.lineEnd = lineEnd;
    }

    /**
     * Add the Strings in the array to the file as a new record
     * @param nextLine
     */
    public void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quoteChar != NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == quoteChar) {
                    sb.append(escapeChar).append(nextChar);
                } else if (escapeChar != NO_ESCAPE_CHARACTER && nextChar == escapeChar) {
                    sb.append(escapeChar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }
            if (quoteChar != NO_QUOTE_CHARACTER)
                sb.append(quoteChar);
        }

        sb.append(lineEnd);
        stringBuilder.append(sb.toString());

    }

    @Override
    public String toString(){
        return stringBuilder.toString();
    }


    /**
     * Resets the builder to create a new CSV.
     */
    public void resetBuilder(){
        stringBuilder = new StringBuilder();
    }


    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public char getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    public String getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }
}
