package ca.gbc.cookit.exception;



public class BadRequestRuntimeException extends RuntimeException {

    private final String messageCode;

    public BadRequestRuntimeException(String messageCode) {

        this.messageCode = messageCode;
    }

    public String getMessageCode() {

        return messageCode;
    }
}
