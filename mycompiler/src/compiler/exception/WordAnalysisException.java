package compiler.exception;

public class WordAnalysisException extends Exception {
    public final int row;
    public final int col;
    public final String errorMessage;

    public WordAnalysisException(int row, int col, String message) {
        super(message);
        this.row = row;
        this.col = col;
        this.errorMessage = message;
    }

    @Override
    public String toString() {
        return errorMessage + "\n" + "在" + row + "行,第" + col + "列";
    }
}
