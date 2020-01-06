package org.finnpic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class FinnPicSpec {
    @Test
    public void createReturnsFinnpicIfGivenValidPic() {
        FinnPic validPic1 = FinnPic.create("070377-281V");
        assertEquals("070377-281V", validPic1.getValue());
        assertEquals(FinnPic.Gender.MALE, validPic1.getGender());
        assertEquals(LocalDate.of(1977, 3, 7), validPic1.getBirthDate());
        assertEquals(7, validPic1.getBirthDay());
        assertEquals(3, validPic1.getBirthMonth());
        assertEquals(1977, validPic1.getBirthYear());
    }

    @Test
    public void createThrowsIllegalArgumentExceptionIfConstructedFromAnInvalidPic() {
        try {
            FinnPic.create("070377-281X");
            fail("An exception should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid PIC: '070377-281X'. The control character ('X') is wrong: it should be 'V'.", e.getMessage());
        }
    }

    @Test
    public void isValidReturnsTrueForValidPic() {
        assertTrue(FinnPic.isValid("070377-281V"));
    }

    @Test
    public void isValidReturnsFalseForInvalidPic() {
        assertFalse(FinnPic.isValid("070377-281X"));
    }
}
