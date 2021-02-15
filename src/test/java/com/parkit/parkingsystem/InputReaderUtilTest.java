package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.InputReaderUtil;

public class InputReaderUtilTest {

    private static InputReaderUtil inputReaderUtil;
    private static InputStream inputStream;
    private static Scanner scan;
    private static String input, vehicleRegNumber;

    @BeforeAll
    private static void setUp() {
        inputReaderUtil = new InputReaderUtil();
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Valid user selection")
    public void givenReadSelection_whenEntryIsValidAndMiniEntryAllow_thenReturnNumberEntry() {
        // 1,2 & 3 are valid entry
        input = "1";
        inputStream = new ByteArrayInputStream(
                (input).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThat(1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Invalid user selection (negative number)")
    public void givenReadSelection_whenEntryIsNegativeNumber_thenReturnMinusOne() {
        // 1,2 & 3 are valid entry
        input = "-1";
        inputStream = new ByteArrayInputStream(
                (input).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Invalid user selection (symbol)")
    public void givenReadSelection_whenEntryIsSymbol_thenReturnMinusOne() {
        // 1,2 & 3 are valid entry
        input = "-";
        inputStream = new ByteArrayInputStream(
                (input).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }



    @Test
    @Tag("ReadSelection")
    @DisplayName("Invalid user selection (letter)")
    public void givenReadSelection_whenEntryIsNotValidWithLetter_thenReturnMinusOne() {
        // Bad entry - letter
        input = "p";
        inputStream = new ByteArrayInputStream(
                (input).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }

    @Test
    @Tag("ReadSelection")
    @DisplayName("Invalid user selections - letter number and symbols")
    public void givenReadSelection_whenEntryIsNotValidWithLetterNumberAndSymbols_thenReturnMinusOne() {
        // Bad entry - letter
        input = "-15p*";
        inputStream = new ByteArrayInputStream(
                (input).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThat(-1).isEqualTo(inputReaderUtil.readSelection());
    }


    @Test
    @Tag("ReadVehicleRegistrationNumber")
    @DisplayName("Exception - Invalid vehicle registration number - space")
    public void givenReadVehicleRegistrationNumber_whenInvalidEntryWithSpace_thenReturnIllegalArgumentException() {
        vehicleRegNumber = " ";
        inputStream = new ByteArrayInputStream(
                (vehicleRegNumber).getBytes(Charset.forName("UTF-8")));
        scan = new Scanner(inputStream);
        inputReaderUtil.setScan(scan);
        assertThatIllegalArgumentException().isThrownBy(() -> {
            inputReaderUtil.readVehicleRegistrationNumber();
        });
    }
}
