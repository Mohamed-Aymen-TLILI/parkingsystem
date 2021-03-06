package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * class of integration tests to check the use of Database.
 */

 @ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
    private static final String TICKET_NUMBER = "ABCDEF";

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(TICKET_NUMBER);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {

        // GIVEN
        int previousSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        //WHEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        // THEN:
        // check that a ticket is actualy saved in DB
        Ticket ticket = ticketDAO.getTicket(TICKET_NUMBER);
        assertNotNull(ticket);
        assertFalse(ticket.getParkingSpot().isAvailable());

        // check that Parking table is updated with availability
        int newSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertNotEquals(previousSlot, newSlot);
    }

    @Test
    private void testParkingLotExit() {
        testParkingLotExit(false);
    }

    public void testParkingLotExit(boolean recurring) {
        // GIVEN:
        testParkingACar();
        Ticket ticket = ticketDAO.getTicket(TICKET_NUMBER);
        // WHEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        long outTime = ticket.getInTime().getTime() + 60 * 60 * 1000;
        parkingService.processExitingVehicle(new Date(outTime));

        // check that the fare generated and out time are populated correctly in the database
        ticket = ticketDAO.getTicket(TICKET_NUMBER);
        // THEN
        assertNotNull(ticket);
        assertEquals(outTime, ticket.getOutTime().getTime());
        assertEquals(Fare.CAR_RATE_PER_HOUR * (recurring ? Fare.RECURRING_USER : 1.0), ticket.getPrice());
    }

    @Test
    public void testRecurringDiscount() {
        testParkingLotExit();
        testParkingLotExit(true);
        testParkingLotExit(true);
    }
}
