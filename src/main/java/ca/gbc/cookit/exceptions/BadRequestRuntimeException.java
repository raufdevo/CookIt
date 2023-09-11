package ca.gbc.cookit.exceptions;

public class BadRequestRuntimeException extends RuntimeException{
    private final String messageCode;

    public BadRequestRuntimeException( String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
