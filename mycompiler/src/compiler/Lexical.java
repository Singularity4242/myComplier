package compiler;

import compiler.basic.Syn;
import compiler.basic.Token;
import compiler.exception.WordAnalysisException;

import java.util.ArrayList;
import java.util.List;
/*
  根据字母表，可以得知
  一共有九个状态，
  一共有6种输入类型 EOF(0)  letter(1)   num(2)    symbol(3)    ">,<"(4)   "="(5)  "分隔符"(6)  "!"(7)
*/
public class Lexical {
    private int gnRow = 0;
    private int gnCol = 0;
    private int gnLocate = 0;//指针，跟踪当前位置
    private char[] code;
    private StringBuilder sb;
    private List<Token> result = new ArrayList<>();
    private boolean isError = false;
    private String errorString = "";
    private static final char[] legalSymbol = {'(', ')', '[', ']', '{', '}', ',', ';', '+', '-', '*', '/'};
    public Lexical(char[] code) {
        this.code = code;
    }

    private void start() {
        while (true) {
            sb = new StringBuilder();
            char ch = code[gnLocate];
            sb.append(ch);
            gnLocate++;
            gnCol++;
            if (ch == '\r') {
                gnCol--;
            } else if (ch == '\t') {
                gnCol += 3;
            } else if (ch == '\n') {
                gnCol = 0;
                gnRow++;
            } else if (ch == '\0') {
                result.add(new Token(Syn.END));
                break;
            } else if (ch == '!') {
                ch = code[gnLocate];
                if (ch == '=') {
                    Token token = new Token(Syn.NE);   //!=
                    result.add(token);
                    gnLocate++;
                    gnCol++;
                } else {
                    error("符号'!'使用错误,只能用作!=");
                    return;
                }
            } else if (isCmp(ch)) {  //是><=
                char nextCh = code[gnLocate];
                if (nextCh == '=') {  //接=
                    sb.append(nextCh);
                    gnLocate++;
                    gnCol++;
                    if (ch == '>') result.add(new Token(Syn.ME));//  >=
                    if (ch == '<') result.add(new Token(Syn.LE));//  <=
                    if (ch == '=') result.add(new Token(Syn.EQ));//  ==
                } else {
                    if ((isLegalSymbol(nextCh) && ch != '(') || isCmp(nextCh) || nextCh == '!') { //
                        cannotNext(ch, nextCh);  //错误，不能跟着
                        return;
                    } else {
                        if (ch == '>') result.add(new Token(Syn.LG));
                        if (ch == '<') result.add(new Token(Syn.LT));
                        if (ch == '=') result.add(new Token(Syn.ASSIGN));
                    }
                }
            } else if (isLetter(ch)) {    //遇到字母字符时词法进入分支，从当前位置读取字符，直到不再是字母或数字
                while (true) {
                    ch = code[gnLocate];
                    if (isLetter(ch) || isDigit(ch)) {
                        sb.append(ch);
                        gnLocate++;
                        gnCol++;
                    } else {
                        String id = sb.toString();  //标识符转换成字符串
                        Token token = getToken(id);
                        if (token.syn == -1) {//无法获取token 则视为普通标识符
                            token = new Token(10, id);
                        }
                        result.add(token);
                        break;
                    }
                }
            } else if (isDigit(ch)) {
                while (true) {
                    ch = code[gnLocate];
                    if (isDigit(ch)) {
                        sb.append(ch);
                        gnLocate++;
                        gnCol++;
                    } else if (isLetter(ch)) {
                        cannotNext(code[gnLocate - 1], ch);
                        return;
                    } else {
                        String num = sb.toString();
                        result.add(new Token(20, num));
                        break;
                    }
                }
            } else if (isLegalSymbol(ch)) {//双符号检验
                if (isLegalSymbol(code[gnLocate]) && !canNext(ch, code[gnLocate])) {
                    cannotNext(ch, code[gnLocate]);
                    return;
                }
                String doubleCmp = sb.toString();
                Token token = getToken(doubleCmp);
                result.add(token);
            } else if (ch != ' ') {
                error("未知符号 " + ch);
                return;
            }
        }

    }

    private void cannotNext(char l, char r) {
        error("'" + r + "'" + " can‘t follow " + "'" + l + "'");
    }
    public static boolean isCmp(char ch) {
        return ch == '>' || ch == '<' || ch == '=';
    }
    private void error(String s) {
        errorString = s;
        isError = true;
    }
    public static boolean isLegalSymbol(char ch) {
        for (char s : legalSymbol) {
            if (ch == s) {
                return true;
            }
        }
        return false;
    }
    public List<Token> getResult() throws WordAnalysisException {
        result = new ArrayList<>();
        gnRow = 0;
        gnCol = 0;
        gnLocate = 0;
        start();
        if (isError) {
            throw new WordAnalysisException(gnRow, gnCol, errorString);
        }
        return result;
    }
    public static boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public static Token getToken(String value) {
        for (Syn syn : Syn.values()) {
            if (syn.value.equals(value)) {
                return new Token(syn.syn, value);
            }
        }
        return new Token(-1, "ERROR");
    }

    //能跟着的
    public static boolean canNext(char l, char r) {
        if ((l == '[' && r == ']') || (l == '(' && r == ')') || (l == '{' && r == '}')) return true;
        if (l == ')' && (r == '{' || r == '+' || r == '-' || r == '*' || r == '/' || r == ')' || r == ';')) return true;
        return r == '(' && (l == '+' || l == '-' || l == '*' || l == '/' || l == '(');
    }
}
