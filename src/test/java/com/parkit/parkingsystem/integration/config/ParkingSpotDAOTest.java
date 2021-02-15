package com.parkit.parkingsystem.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAOTest{
    private static DataBaseTestConfig dataBaseConfigTest = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseConfigTest;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();

    }

    @Test
    public void getNextAvailableSpotTestForBike() throws SQLException{
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
        System.out.println(result);
        assertEquals(result,4);
    }

    @Test
    public void updateParkingTestIfTrue() throws SQLException{

        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        boolean isTrue =  parkingSpotDAO.updateParking(parkingSpot);
        assertEquals(isTrue,Boolean.TRUE);
    }

    @Test
    public void updateParkingTestIfFalse() throws SQLException{

        ParkingSpot parkingSpot = new ParkingSpot(0,ParkingType.CAR,false);
        boolean isFalse =  parkingSpotDAO.updateParking(parkingSpot);
        assertEquals(isFalse,Boolean.FALSE);

    }


}
