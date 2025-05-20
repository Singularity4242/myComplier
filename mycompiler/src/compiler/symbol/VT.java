package compiler.symbol;

import compiler.basic.Token;

//终结符
public class VT implements Symbol {
    public String value;
    public int syn;
    public VT(Token token) {
        this.syn = token.syn;
        this.value = token.value;
    }
}
