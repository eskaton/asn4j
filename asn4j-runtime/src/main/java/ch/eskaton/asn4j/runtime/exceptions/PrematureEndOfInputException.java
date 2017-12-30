package ch.eskaton.asn4j.runtime.exceptions;

public class PrematureEndOfInputException extends DecodingException {

    private static final long serialVersionUID = 1L;

    public PrematureEndOfInputException() {
        super("Premature end of input");
    }

}
