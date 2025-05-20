package compiler.exception;

import compiler.basic.Token;

public class GrammarAnalysisException extends Exception{
    public GrammarAnalysisException(Token token){
        super("【语法错误】："+token.value);
    }
    public GrammarAnalysisException(String s){
        super(s);
    }
}
