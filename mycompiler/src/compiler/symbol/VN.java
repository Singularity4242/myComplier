package compiler.symbol;

import java.util.ArrayList;
import java.util.List;

//非终结符
public class VN implements Symbol {
    public List<Symbol> symbolList = new ArrayList<>();
    public String name;
    public VN(String name) {
        this.name = name;
    }
    public void addSymbol(Symbol symbol) { //添加符号
        symbolList.add(symbol);
    }
}
