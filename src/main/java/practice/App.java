package practice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App{

    Map<String, CalendarInfo> calendarIds;

    public static void main( String[] args )
    {
        App app = new App();
        try {
            app.loadCalendarData();
            app.init();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void init() throws IOException {
        findAvailableTime(Arrays.asList("48644c7a-975e-11e5-a090-c8e0eb18c1e9","48cadf26-975e-11e5-b9c2-c8e0eb18c1e9","452dccfc-975e-11e5-bfa5-c8e0eb18c1e9"), 30,
                new Interval(
                        LocalDateTime.of(2019,4,23,8,15,0),
                        LocalDateTime.of(2019,4,23, 15,45,0)
                ),
                ""
        );
    }

    private void findAvailableTime(List<String> calendarIds, int duration, Interval interval, String timeSlotType) {
        List<List<TimeSlot>> availabletimeslotList = new ArrayList<>();

        //1. Find available time for each Calendar Id

        for (String eachCalenderId : calendarIds) {
            CalendarInfo calendarInfo = this.calendarIds.get(eachCalenderId);
            List<Appointment> appointments = calendarInfo.getAppointments();
            List<TimeSlot> timeslots = calendarInfo.getTimeslots();
            List<TimeSlot> availableTime = findAvailableTime(timeslots, appointments, interval);
            availabletimeslotList.add(availableTime);
        }


        //2. Find common available time across calendar Ids
        List<TimeSlot> primaryTimeSlot = availabletimeslotList.get(0);

        primaryTimeSlot = createChunks(primaryTimeSlot);

        for (int i = 1; i < availabletimeslotList.size() ; i++) {

            List<TimeSlot> secondaryTimeSlots = availabletimeslotList.get(i);

            secondaryTimeSlots = createChunks(secondaryTimeSlots);

            findCommonTime(primaryTimeSlot, secondaryTimeSlots);
        }

        System.out.println("Combined common time between calendarIds : "+primaryTimeSlot);
        //3. Check for Duration in available timeslots.

        List<TimeSlot> splitTimeSlotAsPerDuration = splitTimeSlotAsPerDuration(primaryTimeSlot, duration);

        System.out.println("TimeSlots Available between all calendarIds : "+splitTimeSlotAsPerDuration);
    }


    private List<TimeSlot> createChunks(List<TimeSlot> timeslotList) {

        List<TimeSlot> combinedTimeSlot = new ArrayList<>();

        TimeSlot startTime = timeslotList.get(0);

        for (int i = 0; i < timeslotList.size()-1; i++) {

            TimeSlot timeSlot1 = timeslotList.get(i);
            TimeSlot timeSlot2 = timeslotList.get(i + 1);

            System.out.println("Comparing and Combining : "+timeSlot1+" :: "+timeSlot2);

            if (!timeSlot1.getEnd().equals(timeSlot2.getStart())){
                // Create a combined timeSlot when the break is found in endTime and startTime of consecutive timeslots.
                combinedTimeSlot.add(new TimeSlot(startTime.getStart(), timeSlot1.getEnd()));

                // Use the combined timeslot after the break as StartTime for a next combined slot.
                startTime = timeslotList.get(i+1);
            }

        }

        combinedTimeSlot.add(new TimeSlot(startTime.getStart(), timeslotList.get(timeslotList.size()-1).getEnd()));

        return combinedTimeSlot;
    }

    private List<TimeSlot> splitTimeSlotAsPerDuration(List<TimeSlot> combinedTimeSlot, int duration) {

        List<TimeSlot> splitedTimeSlots = new ArrayList<>();

        for (TimeSlot timeSlot : combinedTimeSlot) {

            long minutes = ChronoUnit.MINUTES.between(timeSlot.getStart(), timeSlot.getEnd());

            if(minutes> duration) {
                LocalDateTime startTime = timeSlot.getStart();
                LocalDateTime endTime = null;
                do {
                    endTime = startTime.plusMinutes(duration);
                    splitedTimeSlots.add(new TimeSlot(startTime, endTime));
                    startTime = endTime;
                }while (endTime.isBefore(timeSlot.getEnd()));
                //Create a split till endTime of each Split is not crossing the endTime of combinedTimeslot.

            } else {
                splitedTimeSlots.add(new TimeSlot(timeSlot.getStart(), timeSlot.getEnd()));
            }
        }

        return splitedTimeSlots;
    }

    private void findCommonTime(List<TimeSlot> primaryTimeSlot, List<TimeSlot> timeSlots) {
        Iterator<TimeSlot> iterator = primaryTimeSlot.iterator();
        while (iterator.hasNext()) {
            TimeSlot timeSlot = iterator.next();
            boolean ifTimeExistsWithinRange = false;

            for (TimeSlot slot : timeSlots) {
                ifTimeExistsWithinRange = findIfTimeExistsWithinRange(timeSlot, slot);
                if(ifTimeExistsWithinRange)
                    break;
//                System.out.println(ifTimeExistsWithinRange);
            }

            if(!ifTimeExistsWithinRange){
                System.out.println("Removing the timeframe: "+timeSlot+" from primary timeslot list");
                iterator.remove();
            }

        }
    }

    private boolean findIfTimeExistsWithinRange(TimeSlot timeSlot, TimeSlot slot) {

        LocalDateTime start1 = timeSlot.getStart();
        LocalDateTime end1 = timeSlot.getEnd();
        long timePeriod1 = ChronoUnit.MINUTES.between(start1, end1);

        LocalDateTime start2 = slot.getStart();
        LocalDateTime end2 = slot.getEnd();
        long timePeriod2 = ChronoUnit.MINUTES.between(start2, end2);

        System.out.println("Comparing timePeriod1 : "+timeSlot+ " :: timePeriod2 : "+slot);

        if(timePeriod1 > timePeriod2){
            // Check timePeriod2 contains in timePeriod1
            return ( start2.isAfter(start1) || start2.isEqual(start1) ) &&  (end2.isBefore(end1) || end2.isEqual(end1) );

        } else {
            // Check timePeriod1 contains in timePeriod2
            return ( start1.isAfter(start2) || start1.isEqual(start2) ) &&  (end1.isBefore(end2) || end1.isEqual(end2) );
        }
    }

    /*
            Assumption : Interval startTime/endTime is exactly matching with timeslot startTime/endTime
     */
    private List<TimeSlot> findAvailableTime(List<TimeSlot> timeslots, List<Appointment> appointments, Interval interval) {
        Iterator<TimeSlot> iterator = timeslots.iterator();
        while(iterator.hasNext()){
            TimeSlot timeslot = iterator.next();
            if((timeslot.getEnd().isBefore(interval.getStart()) ||timeslot.getEnd().isEqual(interval.getStart()))
                || (timeslot.getStart().isAfter(interval.getEnd()) ||timeslot.getStart().isEqual(interval.getEnd()))){
                iterator.remove();
                continue;
            }
            for (Appointment appointment : appointments) {
                if (timeslot.getStart().equals(appointment.getStart())
                        && timeslot.getEnd().equals(appointment.getEnd())) {
                    //Remove the timeSlots which already have appointment
                    iterator.remove();
                    break;
                }
            }
        }
        return timeslots;
    }


    void loadCalendarData() throws IOException {
        InputStream resourceAsStream = App.class.getClassLoader().getResourceAsStream("John.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.registerModule(new JavaTimeModule());
        calendarIds = objectMapper.readValue(resourceAsStream,
                new TypeReference<Map<String, CalendarInfo>>() {});

    }

}
