package ch.eskaton.asn4j.runtime.exceptions;

public class UnexpectedTagException extends DecodingException {

    private static final long serialVersionUID = 1L;

    public UnexpectedTagException() {
        super("Unexpected tag found");
    }

    public UnexpectedTagException(String message) {
        super(message);
    }

}
