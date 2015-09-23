package corpus;

import java.util.HashMap;


public class Symbol {
    private static HashMap<String, Symbol> symbolTable = new HashMap<String, Symbol>();
    private static int numSymbols;
    private String value;
    private int id;
 
    private Symbol(String value) {
        this.value = value;
        id = numSymbols++;
    }
 
    public static Symbol getSymbol(String key) {
        // String value = key.toUpperCase();
    	String value = key.toLowerCase();
    	// Swan: 21st September 2015
        Symbol s;
        if (symbolTable.containsKey(value)) {
            s = symbolTable.get(value);
        } else {
            s = new Symbol(value);
            symbolTable.put(value, s);
        }
        return s;
    }
 
    public String getValue() {
        return value;
    }
 
    public int getId() {
        return id;
    }
 
    public boolean equals(Symbol s) {
        return id == s.id;
    }
 
    public String toString() {
        return value;
    }
}