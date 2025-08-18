package org.implorestudios.playerhunt.language;

public abstract class Replacer {
    protected final String placeholder;
    
    protected Replacer(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public abstract String getReplacement();
    
    public String replace(String text) {
        return text.replace(placeholder, getReplacement());
    }
}