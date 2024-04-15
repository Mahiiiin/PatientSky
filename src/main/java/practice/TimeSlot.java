package practice;

import java.time.LocalDateTime;

public class TimeSlot {
    private String id;//": "2dfb4ae6-6715-4e19-b0d5-a14e5b62ef93"
    private String calendar_id;//": "48644c7a-975e-11e5-a090-c8e0eb18c1e9",
              //type_id": "452935de-975e-11e5-ae1a-c8e0eb18c1e9",
    private LocalDateTime start;//": "2019-04-23T08:00:00",
    private LocalDateTime end;//": "2019-04-23T08:15:00",
    private boolean public_bookable;//": true,
    private boolean out_of_office;//": false

    public TimeSlot() {
    }

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCalendar_id() {
        return calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public boolean isPublic_bookable() {
        return public_bookable;
    }

    public void setPublic_bookable(boolean public_bookable) {
        this.public_bookable = public_bookable;
    }

    public boolean isOut_of_office() {
        return out_of_office;
    }

    public void setOut_of_office(boolean out_of_office) {
        this.out_of_office = out_of_office;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
