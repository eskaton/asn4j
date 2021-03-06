package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

class ASN1ObjectIdentifierTest {

    @Test
    void testInvalidObjectIdentifiers() {
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(0));
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(3));
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(-1));
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(0, 40));
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(1, 40));
        testInvalidIdentifier(() -> ASN1ObjectIdentifier.from(1, 39, -1));
    }

    @Test
    void testValidObjectIdentifiers() {
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(0, 1));
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(1, 1));
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(2, 1));
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(0, 39));
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(1, 39));
        assertDoesNotThrow(() -> ASN1ObjectIdentifier.from(2, 40));
    }

    private void testInvalidIdentifier(Supplier<ASN1ObjectIdentifier> supplier) {
        try {
            supplier.get();
            fail("Expected a ValidationException");
        } catch (ValidationException e) {
        }
    }

}
