package com.udacity.catpoint.security;


import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;
import com.udacity.catpoint.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Unit test for simple Security module.
 */
@ExtendWith(MockitoExtension.class)
public class SecurityTest {
    @Mock
    private FakeImageService fakeImageService;
    @Mock
    private SecurityRepository securityRepository;
    @Mock
    private StatusListener statusListener;
    private SecurityService securityService;

    private Sensor activatedSensor;
    private Sensor deactivatedSensor;

    private final String randomString = String.valueOf(UUID.randomUUID());

    @BeforeEach
    void beforeEachTest() {
        activatedSensor = new Sensor(randomString, SensorType.DOOR, true);
        deactivatedSensor = new Sensor(randomString, SensorType.DOOR, false);
        securityService = new SecurityService(securityRepository, fakeImageService);
    }

    //Test for case 1
    //If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_AWAY", "ARMED_HOME"})
    void changeAlarmStatusToPending_whenSystemIsArmedAndSensorActivated(ArmingStatus armingStatus) {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        when(securityRepository.getArmingStatus()).thenReturn(armingStatus);

        securityService.changeSensorActivationStatus(deactivatedSensor, true);

        verify(securityRepository, times(2)).getAlarmStatus();
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.PENDING_ALARM);
        verify(securityRepository, times(1)).updateSensor(deactivatedSensor);
    }


    //Test for case 2
    //If alarm is armed and a sensor becomes activated and the system is already pending alarm,
    // set the alarm status to alarm.
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_AWAY", "ARMED_HOME"})
    void changeStatusToAlarm_whenSystemIsArmedAndSensorActivatedAndStatusIsPending(ArmingStatus armingStatus) {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepository.getArmingStatus()).thenReturn(armingStatus);

        securityService.changeSensorActivationStatus(deactivatedSensor, true);
        ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);

        verify(securityRepository, atMostOnce()).setAlarmStatus(captor.capture());
        assertEquals(captor.getValue(), AlarmStatus.ALARM);
        verify(securityRepository, times(2)).getAlarmStatus();
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
        verify(securityRepository, times(1)).updateSensor(deactivatedSensor);
    }

    //Test for case 3
    //If pending alarm and all sensors are inactive, return to no alarm state.
    @Test
    void returnToNoAlarmState_whenPendingAlarmAndAllSensorsIsInactive() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(activatedSensor, false);
        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);

        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
        verify(securityRepository, times(1)).updateSensor(activatedSensor);
        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.NO_ALARM);
    }

    //Test for case 4
    //If alarm is active, change in sensor state should not affect the alarm state.
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ChangeSensorStateShouldNotAffectTheAlarmState_whenAlarmIsActive(Boolean status) {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(activatedSensor, status);

        verify(securityRepository, never()).setAlarmStatus(any(AlarmStatus.class));
        verify(securityRepository, times(1)).getAlarmStatus();
    }

    //Test for case 5
    //If a sensor is activated while already active and the system is in pending state,
    //change it to alarm state.
    @Test
    void changeStatusToAlarm_whenSensorIsActivatedAndSystemIsPending() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(activatedSensor, true);
        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);

        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());
        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.ALARM);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    //Test for case 6
    //If a sensor is deactivated while already inactive, make no changes to the alarm state.
    @ParameterizedTest
    @EnumSource(value = AlarmStatus.class, names = {"PENDING_ALARM", "NO_ALARM", "ALARM"})
    void alarmStateNoChanges_whenSensorDeactivated(AlarmStatus alarmStatus) {
        when(securityRepository.getAlarmStatus()).thenReturn(alarmStatus);

        securityService.changeSensorActivationStatus(deactivatedSensor, false);

        verify(securityRepository, never()).setAlarmStatus(any(AlarmStatus.class));
        verify(securityRepository, times(1)).updateSensor(deactivatedSensor);
    }

    //Test for case 7
    //If the image service identifies an image containing a cat while the system is armed-home,
    // put the system into alarm status.
    @Test
    void changeStateToAlarm_whenImageContainCatAndSystemIsArmHome() {
        when(fakeImageService.imageContainsCat(any(BufferedImage.class), eq(50.0f))).thenReturn(true);
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        securityService.processImage(mock(BufferedImage.class));

        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());

        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.ALARM);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    //Test for case 8
    //If the image service identifies an image that does not contain a cat,
    //change the status to no alarm as long as the sensors are not active.
    @Test
    void changeStatusToNoAlarmAsLongTheSensorsAreNotActive_whenImageNotContainCat() {
        when(fakeImageService.imageContainsCat(any(BufferedImage.class), eq(50.0f))).thenReturn(false);

        securityService.processImage(mock(BufferedImage.class));
        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());

        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.NO_ALARM);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //Test for case 9
    //If the system is disarmed, set the status to no alarm.
    @Test
    void setStatusToNoAlarm_whenSystemIsDisarmed() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());

        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.NO_ALARM);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //For test case 10
    //If the system is armed, reset all sensors to inactive.
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_AWAY", "ARMED_HOME"})
    void resetAllSensorsToInactive_whenSystemIsArmed(ArmingStatus armingStatus) {
        Set<Sensor> sensors = addAndGetSampleSensor( true);

        when(securityRepository.getSensors()).thenReturn(sensors);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.setArmingStatus(armingStatus);

        for (Sensor s : securityService.getSensors()) {
            assertEquals(s.getActive(), false);
        }
    }

    //Test for case 11
    //If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
    @Test
    void setAlarmStatusToAlarm_whenSystemIsArmedHomeAndCameraShowsACat() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(fakeImageService.imageContainsCat(any(BufferedImage.class), eq(50.0f))).thenReturn(true);

        securityService.processImage(mock(BufferedImage.class));
        ArgumentCaptor<AlarmStatus> alarmStatusArgumentCaptor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMostOnce()).setAlarmStatus(alarmStatusArgumentCaptor.capture());

        assertEquals(alarmStatusArgumentCaptor.getValue(), AlarmStatus.ALARM);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void assertNotThrowError_whenAddAndRemoveStatusListener() {
        assertDoesNotThrow(() -> {
            securityService.removeStatusListener(statusListener);
            securityService.addStatusListener(statusListener);
        });
    }

    @Test
    void addAndRemoveSensor_whenSensorIsActivated(){
        assertDoesNotThrow(() -> {
            securityService.addSensor(activatedSensor);
            securityService.removeSensor(activatedSensor);
        });

        verify(securityRepository, times(1)).addSensor(activatedSensor);
        verify(securityRepository, times(1)).removeSensor(activatedSensor);
    }

    @Test
    void getAlarmStatus_andAssertGetSuccess(){
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        assertEquals(securityService.getAlarmStatus(), AlarmStatus.ALARM);

        verify(securityRepository, atMostOnce()).getAlarmStatus();
        verify(securityRepository, times(1)).getAlarmStatus();
    }

    private Set<Sensor> addAndGetSampleSensor(boolean status) {
        Set<Sensor> sensors = new HashSet<>();
        int counter = 0;
        while (counter < 5) {
            sensors.add(new Sensor(randomString, SensorType.DOOR, status));
            counter++;
        }
        return sensors;
    }
}
