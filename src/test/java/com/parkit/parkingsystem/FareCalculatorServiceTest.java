package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class    FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }


    /**
     * class test to check the calculation of far to any type of vehicle.
     */
    @Nested
    @Tag("CalculateFar")
    @DisplayName("CalculateFar for any type of  vehicle")
    class CalculateFar {

        @Test
        public void calculateFareCar() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareBike() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
        }

        @Test
        public void calculateFareUnkownType() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
        }

        @Test
        public void calculateFareBikeWithFutureInTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
        }

        @Test
        public void calculateFareBikeWithLessThanOneHourParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithLessThanOneHourParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        public void calculateFareCarWithMoreThanADayParkingTime() {
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);
            assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        }

        @Test
        void calculateFareCarWithOutTimeLessThanInTime() {
            // ARRANGE
            Date inTime = new Date();


            // parking fare per hour
            Date outTime = new Date();
            outTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


            // ACT ...
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);

            // ASSERT
            assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }


    /**
     * Test class to verify if any type of vehicle with a parking time under 30 minutes has a park for
     * free.<br>
     * <strong>Test cases:</strong>
     * <ul>
     * <li>parking time [0,29, 30] minutes</li>
     * <li>type of vehicle: [Bike,Car,Other]</li>
     * </ul>
     * <strong>Test case set =</strong> {[0,Bike], [29,Car],[30,Bike],[30,Car],[29,Other],[30,Other]}
     */
    @Nested
    @Tag("CalculateFarUnder30Min")
    @DisplayName("Calculate Far for parking time under 30min")


    class CalculateFarUnder30Min {
        @Test
        void calculateFareCarWith29MinutesParkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);

            // ASSERT
            assertEquals(0, ticket.getPrice());
        }

        @Test
        void calculateFareBikeWith0MinuteParkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis());
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);

            // ASSERT
            assertEquals(0, ticket.getPrice());
        }

        @Test
        void calculateFareBikeWith30MinuteParkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
            Date outTime = new Date();
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            fareCalculatorService.calculateFare(ticket);

            // ASSERT
            assertEquals(0, ticket.getPrice());
        }
    }


    /**
     * Test class to verify that a recurring user has a discount of 5% to his parking price.<br>
     * <strong>Test cases:</strong>
     * <ul>
     * <li>parking time [FirstTime, RecurringUser]</li>
     * <li>type of vehicle: [Bike,Car,Other]</li>
     * </ul>
     * <strong>Test case set =</strong> {[RecurringUser,Bike],
     * [RecurringUser,Car],[FirsTime,Bike],[FirsTime,Car],[FirstTime,Other],[Recurring,Other]}
     * @author tlili
     */
    @Nested
    @Tag("CalculateFarWithDiscount")
    @DisplayName("calculate far for recurring users")
    class CalculateFarWithFivePourcentsDiscount {
        @Test
        void calculateFarRecurringCarOneHourParkingTime() {
            //ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


            //ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(true);
            fareCalculatorService.calculateFare(ticket);

            //ASSERT
            assertThat(ticket.getPrice())
                    .isEqualTo(Fare.roundedFare(0.95 * Fare.CAR_RATE_PER_HOUR));
        }

        @Test
        void calculateFarRecurringBikeOneHourParkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);


            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(true);
            fareCalculatorService.calculateFare(ticket);

            // ASSERT
            assertThat(ticket.getPrice())
                    .isEqualTo(Fare.roundedFare(0.95 * Fare.BIKE_RATE_PER_HOUR));
        }

        @Test
        void calculateFarCarFirstPArkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);


            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(false);
            fareCalculatorService.calculateFare(ticket);

            // ASSERT
            assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
        }

        @Test
        void calculateFarBikeFirstPArkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);


            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(false);
            fareCalculatorService.calculateFare(ticket);


            // ASSERT
            assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
        }

        @Test
        void calculateFarUnknowTypeUserFirstPArkingTime() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, null, false);


            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(false);

            // ASSERT
            assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
        }

        @Test
        void calculateFarUnknowTypeUserRecurring() {
            // ARRANGE
            Date inTime = new Date();
            inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
            Date outTime = new Date();

            ParkingSpot parkingSpot = new ParkingSpot(1, null, false);


            // ACT
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setIsRecurringUser(true);

            // ASSERT
            assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
        }
    }
}


