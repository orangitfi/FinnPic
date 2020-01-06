package org.finnpic;

import java.time.LocalDate;

import static org.finnpic.FinnPic.Gender.FEMALE;
import static org.finnpic.FinnPic.Gender.MALE;

public class FinnPic {
    private final Pic pic;

    public static FinnPic create(String input) {
        return new FinnPic(Pic$.MODULE$.fromStringUnsafe(input));
    }

    private FinnPic(Pic pic) {
        this.pic = pic;
    }

    public String getValue() {
        return pic.value();
    }

    public LocalDate getBirthDate() {
        return pic.birthDate();
    }

    public int getBirthDay() {
        return pic.birthDay();
    }

    public int getBirthMonth() {
        return pic.birthMonth();
    }

    public int getBirthYear() {
        return pic.birthYear();
    }

    public enum Gender {
        MALE, FEMALE
    }


    public Gender getGender() {
        org.finnpic.Gender gender = pic.gender();
        if (gender == Male$.MODULE$) {
            return MALE;
        } else if (gender == Female$.MODULE$) {
            return FEMALE;
        } else {
            throw new IllegalArgumentException("No gender for " + gender + ".");
        }
    }
}
