package io.github.uchagani.stagehand.exeptions;

public class MissingPageObjectAnnotation extends RuntimeException {
    public MissingPageObjectAnnotation(String message) {
        super(message);
    }
}
