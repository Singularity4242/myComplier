package compiler.basic;

//标识码表
public enum Syn {
    //关键词
    MAIN(1, "main"),
    INT(2, "int"),
    //CHAR(3,"char"),
    IF(4, "if"),
    ELSE(5,"else"),
    BREAK(6, "break"),
    WHILE(7, "while"),
    PRINTF(8,"printf"),
    //ID 和 NUM
    ID(10, "ID"),
    NUM(20, "NUM"),
    //符号
    ASSIGN(21, "="),
    PLUS(22, "+"),
    MINUS(23, "-"),
    TIMES(24, "*"),
    DIVIDE(25, "/"),
    L_PAREN(26, "("),
    R_PAREN(27, ")"),
    //L_BRACKET1(28, "["),
    //R_BRACKET1(29, "]"),
    L_BRACKET(30, "{"),
    R_BRACKET(31, "}"),
    COMMA(32, ","),
    COLON(33, ":"),
    SEMICOLON(34, ";"),

    //比较符
    LG(35, ">"),
    LT(36, "<"),
    ME(37, ">="),
    LE(38, "<="),
    EQ(39, "=="),
    NE(40, "!="),
    END(1000, "\0"),
    ERROR(-1, "ERROR");


    public int syn;
    public String value;

    Syn(int syn, String value) {
        this.syn = syn;
        this.value = value;
    }

}
