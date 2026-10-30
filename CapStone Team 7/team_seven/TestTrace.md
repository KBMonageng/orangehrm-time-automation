# Team Seven Test Trace Document

| Test ID | Scenario | Complexity | Page Object | Test Class | Data Sheet | Group |
|---|---|---|---|---|---|---|
| T7-TIME-001 | Clock In/Out - Validate logs | Simple | ClockInOutPage.java | ClockInOutTest.java | ClockInOut | smoke, regression |
| T7-TIME-002 | View Attendance - Validate entries | Simple | ViewAttendancePage.java | ViewAttendanceTest.java | ViewAttendance | smoke, regression |
| T7-TIME-003 | Edit Timesheet - Validate edits | Medium | EditTimesheetPage.java | EditTimesheetTest.java | EditTimesheet | regression |
| T7-TIME-004 | Submit Timesheet - Validate submission | Medium | SubmitTimesheetPage.java | SubmitTimesheetTest.java | SubmitTimesheet | regression |
| T7-TIME-005 | Approval Workflow - Validate approval process | Complex | ApprovalWorkflowPage.java | ApprovalWorkflowTest.java | ApprovalWorkflow | regression |

## Notes

- Login is handled in BaseTest before each test method.
- Login data is stored in Excel and is not hardcoded in Java classes.
- Locators are stored in locators.properties.
- Test data is stored in LoginData.xlsx.
- Screenshots are captured automatically when a test fails.
- Extent Report is generated after execution.
