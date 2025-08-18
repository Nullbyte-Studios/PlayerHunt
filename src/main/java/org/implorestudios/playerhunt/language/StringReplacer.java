package org.implorestudios.playerhunt.language;

public class StringReplacer extends Replacer {
    private final String replacement;
    
    public StringReplacer(String placeholder, String replacement) {
        super(placeholder);
        this.replacement = replacement != null ? replacement : "";
    }
    
    @Override
    public String getReplacement() {
        return replacement;
    }
}