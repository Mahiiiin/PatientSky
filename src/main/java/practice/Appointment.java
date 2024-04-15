package practice;

import java.time.LocalDateTime;

public class Appointment {
    private String id; //": "2faea810-8e14-4d45-b075-4d78528183f3",
    private String calendar_id;//": "452dccfc-975e-11e5-bfa5-c8e0eb18c1e9",
    private LocalDateTime start;//": "2019-04-23T08:00:00",
    private LocalDateTime end;//": "2019-04-23T08:30:00",
    private String time_slot_type_id;//": "4529821e-975e-11e5-bbaf-c8e0eb18c1e9",

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

    public String getTime_slot_type_id() {
        return time_slot_type_id;
    }

    public void setTime_slot_type_id(String time_slot_type_id) {
        this.time_slot_type_id = time_slot_type_id;
    }


    //               "patient_id": "1cfeee58-9751-11e5-9c8d-c8e0eb18c1e9",
//               "patient_comment": null,
//               "note": null,
//               "type_id": null,
//               "state": 0,
//               "out_of_office_location": "",
//               "out_of_office": false,
//               "completed": true,
//               "is_scheduled": false
}
