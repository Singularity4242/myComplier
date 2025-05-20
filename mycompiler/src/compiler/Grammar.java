package compiler;

import compiler.basic.Syn;
import compiler.basic.Token;
import compiler.exception.GrammarAnalysisException;
import compiler.symbol.*;

import java.util.*;
public class Grammar {
    ListIterator<Token> tokenIterator;
    Set<String> variablesSet = new HashSet<>();//已声明的变量
    Token nowToken = null;
    private VT wantVT(Syn syn) throws GrammarAnalysisException {//syn是期望的token
        nowToken = tokenIterator.next();//下一个token
        if (nowToken.syn == syn.syn) {//是否是期望的类型
            return new VT(nowToken);  //终结符
        } else {
            throw new GrammarAnalysisException(nowToken);//错误
        }
    }

    //操作比较，现在这个token是不是 > < >= <= == !=
    private VT wantCmp() throws GrammarAnalysisException {
        nowToken = tokenIterator.next();
        if (nowToken.syn == Syn.LG.syn ||
                nowToken.syn == Syn.LE.syn ||
                nowToken.syn == Syn.LT.syn ||
                nowToken.syn == Syn.ME.syn ||
                nowToken.syn == Syn.EQ.syn) {
            return new VT(nowToken);
        } else {
            throw new GrammarAnalysisException(nowToken);
        }
    }

    //非终结，程序起点
    public VN getProgram(List<Token> tokens) throws GrammarAnalysisException {
        tokenIterator = tokens.listIterator();
        return program();
    }

    //基本结构是否符合
    private VN program() throws GrammarAnalysisException {
        VN p = new VN("程序");  //创一个非终结符表示整个程序
        p.addSymbol(wantVT(Syn.INT));  // int
        p.addSymbol(wantVT(Syn.MAIN));  //main
        p.addSymbol(wantVT(Syn.L_PAREN));  //(
        p.addSymbol(wantVT(Syn.R_PAREN));  //)
        p.addSymbol(block());//语句块
        return p;
    }
    private VN block() throws GrammarAnalysisException {
        VN k = new VN("语句块");  //一个非终结符 表示语句块
        k.addSymbol(wantVT(Syn.L_BRACKET)); //{
        k.addSymbol(statementString());//语句串
        k.addSymbol(wantVT(Syn.R_BRACKET));  //}
        return k;
    }
    //语句串
    private VN statementString() throws GrammarAnalysisException {
        VN ss = new VN("语句串");
        VN s;
        while ((s = statement()) != null) {//单个语句分析
            ss.addSymbol(s);
        }
        return ss;
    }

    //解析单个语句，根据当前token,识别语句结构
    private VN statement() throws GrammarAnalysisException {
        nowToken = tokenIterator.next();
        if (nowToken.syn == Syn.INT.syn) {
            VN fs = new VN("赋值语句");
            fs.addSymbol(new VT(nowToken));
            fs.addSymbol(wantVT(Syn.ID));
            if (variablesSet.contains(nowToken.value)) {//已有变量
                throw new GrammarAnalysisException("重复定义变量 : " + nowToken.value);
            }
            variablesSet.add(nowToken.value);
            fs.addSymbol(wantVT(Syn.ASSIGN));
            fs.addSymbol(expression());
            fs.addSymbol(wantVT(Syn.SEMICOLON));
            return fs;
        } else if (nowToken.syn == Syn.ID.syn) { //赋值
            if (!variablesSet.contains(nowToken.value)) {
                throw new GrammarAnalysisException("未定义变量 : " + nowToken.value);
            }
            VN fs = new VN("赋值语句");
            fs.addSymbol(new VT(nowToken));
            fs.addSymbol(wantVT(Syn.ASSIGN));
            fs.addSymbol(expression());
            fs.addSymbol(wantVT(Syn.SEMICOLON));
            return fs;
        } else if (nowToken.syn == Syn.IF.syn) {//条件
            VN fs = new VN("条件语句");
            fs.addSymbol(new VT(nowToken));
            fs.addSymbol(wantVT(Syn.L_PAREN));
            fs.addSymbol(boolStatement());
            fs.addSymbol(wantVT(Syn.R_PAREN));
            fs.addSymbol(block());
            nowToken = tokenIterator.next();
            if (nowToken.syn == Syn.ELSE.syn) {
                tokenIterator.previous();
                fs.addSymbol(elseS());
                return fs;
            }
            tokenIterator.previous();
            return fs;
        } else if (nowToken.syn == Syn.WHILE.syn) {
            VN fs = new VN("循环语句");
            fs.addSymbol(new VT(nowToken));
            fs.addSymbol(wantVT(Syn.L_PAREN));
            fs.addSymbol(boolStatement());
            fs.addSymbol(wantVT(Syn.R_PAREN));
            fs.addSymbol(block());
            return fs;
        }
        tokenIterator.previous();
        return null;
    }

    private VN elseS() throws GrammarAnalysisException {
        VN e = new VN("分支语句");
        e.addSymbol(wantVT(Syn.ELSE));
        nowToken = tokenIterator.next();
        if (nowToken.syn == Syn.IF.syn) {
            tokenIterator.previous();
            e.addSymbol(statement());
        } else {
            tokenIterator.previous();
            e.addSymbol(block());
        }
        return e;
    }

    private VN expression() throws GrammarAnalysisException {
        VN e = new VN("表达式");
        e.addSymbol(item());
        while (true) {
            nowToken = tokenIterator.next();
            if (nowToken.syn == Syn.PLUS.syn) {
                e.addSymbol(new VT(nowToken));
                e.addSymbol(item());
            } else if (nowToken.syn == Syn.MINUS.syn) {
                e.addSymbol(new VT(nowToken));
                e.addSymbol(item());
            } else {
                tokenIterator.previous();
                break;
            }
        }
        return e;
    }

    private VN boolStatement() throws GrammarAnalysisException {
        VN b = new VN("布尔语句");
        b.addSymbol(expression());
        b.addSymbol(wantCmp());
        b.addSymbol(expression());
        return b;
    }

    private VN item() throws GrammarAnalysisException {
        VN i = new VN("项");
        i.addSymbol(factor());
        while (true) {
            nowToken = tokenIterator.next();
            if (nowToken.syn == Syn.DIVIDE.syn) {
                i.addSymbol(new VT(nowToken));
                i.addSymbol(factor());
            } else if (nowToken.syn == Syn.TIMES.syn) {
                i.addSymbol(new VT(nowToken));
                i.addSymbol(factor());
            } else {
                tokenIterator.previous();
                break;
            }
        }
        return i;
    }

    private VN factor() throws GrammarAnalysisException {
        VN f = new VN("因子");
        nowToken = tokenIterator.next();
        if (nowToken.syn == Syn.L_PAREN.syn) {
            f.addSymbol(new VT(nowToken));
            f.addSymbol(expression());
            f.addSymbol(wantVT(Syn.R_PAREN));
        } else if (nowToken.syn == Syn.ID.syn) {
            if (!variablesSet.contains(nowToken.value)) {
                throw new GrammarAnalysisException("未声明的变量 : " + nowToken.value);
            }
            f.addSymbol(new VT(nowToken));
        } else {
            tokenIterator.previous();
            f.addSymbol(wantVT(Syn.NUM));
        }
        return f;
    }


}
