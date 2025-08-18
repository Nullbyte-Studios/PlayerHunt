package org.implorestudios.playerhunt.language;

public class IntegerReplacer extends Replacer {
    private final int value;
    
    public IntegerReplacer(String placeholder, int value) {
        super(placeholder);
        this.value = value;
    }
    
    @Override
    public String getReplacement() {
        return String.valueOf(value);
    }
}