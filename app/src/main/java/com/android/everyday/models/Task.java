package com.android.everyday.models;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Task {

    // Priority constants
    public static final String[] PRIORITY_LABELS = {"Trivial", "Normal", "Urgent"};
    public static final String[] REPEAT_LABELS = {"minutes", "hours", "days", "weeks", "months", "years"};
    public static final int TRIVIAL = 0;
    public static final int NORMAL = 1;
    public static final int URGENT = 2;
    // Repeat constants
    public static final int MINUTES = 0;
    public static final int HOURS = 1;
    public static final int DAYS = 2;
    public static final int WEEKS = 3;
    public static final int MONTHS = 4;
    public static final int YEARS = 5;

    // Extra intent flags
    public static final String EXTRA_TASK = "com.android.everyday.models.TASK";
    public static final String EXTRA_TASK_ID = "com.android.everyday.models.TASK_ID";

    /**************************************************************************
     * Private fields                                                         *
     **************************************************************************/

    private int id,repeatType,repeatInterval;
    private String name, gID, notes;
    private boolean isCompleted,hasDateDue,hasFinalDateDue,isRepeating;
    private long dateCreated,dateModified,dateDue;
    private Calendar dateCreatedCal, dateModifiedCal, dateDueCal;

    /**************************************************************************
     * Constructors                                                           *
     **************************************************************************/

    /**
     * Default constructor. Creates an empty task.
     */
    public Task() {
    }

    /**
     * Constructor, all fields
     *
     * @param id
     * @param name
     * @param isCompleted
     * @param hasDateDue
     * @param hasFinalDateDue
     * @param isRepeating
     * @param repeatType
     * @param repeatInterval
     * @param dateCreated
     * @param dateModified
     * @param dateDue
     * @param gID
     * @param notes
     */
    public Task(int id,
                String name,
                boolean isCompleted,
                boolean hasDateDue,
                boolean hasFinalDateDue,
                boolean isRepeating,
                int repeatType,
                int repeatInterval,
                long dateCreated,
                long dateModified,
                long dateDue,
                String gID,
                String notes) {
        this.id = id;
        this.name = name;
        this.isCompleted = isCompleted;
        this.hasDateDue = hasDateDue;
        this.hasFinalDateDue = hasFinalDateDue;
        this.isRepeating = isRepeating;
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.dateDue = dateDue;
        this.gID = gID;
        this.notes = notes;

        updateDateCreatedCal();
        updateDateModifiedCal();
        updateDateDueCal();
    }

    /**************************************************************************
     * Class methods                                                          *
     **************************************************************************/

    private void updateDateCreatedCal() {
        if (dateCreatedCal == null)
            dateCreatedCal = new GregorianCalendar();

        dateCreatedCal.setTimeInMillis(dateCreated);
    }

    private void updateDateModifiedCal() {
        if (dateModifiedCal == null)
            dateModifiedCal = new GregorianCalendar();

        dateModifiedCal.setTimeInMillis(dateModified);
    }

    private void updateDateDueCal() {
        if (!hasDateDue) {
            dateDueCal = null;
            return;
        }

        if (dateDueCal == null)
            dateDueCal = new GregorianCalendar();

        dateDueCal.setTimeInMillis(dateDue);
    }

    public boolean isPastDue() {
        return !(!hasDateDue || isCompleted) && dateDue - System.currentTimeMillis() < 0;
    }

    /**************************************************************************
     * Overridden parent methods                                              *
     **************************************************************************/
    /**
     * Compares this object to another. To return true, the compared object must
     * have the same class and identical IDs.
     *
     * @param o the object to be compared with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;

        return ((Task) o).id == this.id;
    }

    /**
     * Returns a string representation of the class.
     *
     * @return a string representation of the class
     */
    @Override
    public String toString() {
        return name;
    }

    /**************************************************************************
     * Getters and setters                                                    *
     **************************************************************************/

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean is_completed) {
        this.isCompleted = is_completed;
    }

    public void toggleIsCompleted() {
        isCompleted = !isCompleted;
    }

    public boolean hasDateDue() {
        return hasDateDue;
    }

    public void setHasDateDue(boolean hasDateDue) {
        this.hasDateDue = hasDateDue;
    }

    public boolean hasFinalDateDue() {
        return hasFinalDateDue;
    }

    public void setHasFinalDateDue(boolean hasFinalDateDue) {
        this.hasFinalDateDue = hasFinalDateDue;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(boolean isRepeating) {
        this.isRepeating = isRepeating;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        if (repeatType >= 0 && repeatType <= 5) {
            this.isRepeating = true;
            this.repeatType = repeatType;
        }
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public Calendar getDateCreatedCal() {
        return dateCreatedCal;
    }

    public void setDateCreated(long date_created) {
        this.dateCreated = date_created;
        updateDateCreatedCal();
    }

    public long getDateModified() {
        return dateModified;
    }

    public Calendar getDateModifiedCal() {
        return dateModifiedCal;
    }

    public void setDateModified(long date_modified) {
        this.dateModified = date_modified;
        updateDateModifiedCal();
    }

    public long getDateDue() {
        return dateDue;
    }

    public Calendar getDateDueCal() {
        return dateDueCal;
    }

    public void setDateDue(long date_due) {
        this.hasDateDue = true;
        this.dateDue = date_due;
        updateDateDueCal();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getgID() {
        return gID;
    }

    public void setgID(String gID) {
        this.gID = gID;
    }
}
