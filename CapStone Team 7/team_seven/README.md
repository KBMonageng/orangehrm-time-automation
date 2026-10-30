# Team Seven: OrangeHRM Time Automation

This is our capstone automation project for OrangeHRM's Time module. Our work focuses on attendance, timesheets, and the approval workflow.

The framework uses Java, Playwright, and TestNG. Each scenario has its own page object and test class, with Excel supplying the test data. Maven handles the build, and Extent Reports generates the HTML report.

## What's in the suite

| Intended scenario | Test class | Runs under |
|---|---|---|
| Clock in and out | `ClockInOutTest` | Smoke Tests |
| View attendance records | `ViewAttendanceTest` | Smoke Tests |
| Edit a timesheet | `EditTimesheetTest` | Regression Tests |
| Submit a timesheet | `SubmitTimesheetTest` | Regression Tests |
| Approve an employee's timesheet | `ApprovalWorkflowTest` | Regression Tests |

`testng.xml` runs these five scenarios. Before each test, `BaseTest` opens a fresh browser and logs in as admin so the test can reach the Time module. Login is shared setup for our team.

Clock-in/out and attendance have both `smoke` and `regression` tags in Java. The default XML suite lists them only under Smoke Tests, so each runs once. The suite has no explicit test dependencies or parallel settings.

The scenario names describe what the tests aim to do. Their current assertions have some gaps, which are listed under [Current limitations](#current-limitations).

The scenario IDs and their matching classes are listed in [TestTrace.md](TestTrace.md).

## Running the tests

Make sure a JDK 11 or later and Maven are available in your terminal. The project compiles for Java 11. Playwright also needs the browser files for the selected browser; allow network access for dependencies, browser downloads, and the demo site.

Open a terminal in `CapStone Team 7/team_seven`, where `pom.xml` is, and run:

```sh
mvn clean test
```

Run the command from that folder because the framework reads configuration and test data using relative paths.

By default, the tests open a visible Chromium window and connect to the OrangeHRM demo. Clock-in/out and the timesheet flows can update records there. Browser cleanup closes the session without restoring those records, so existing demo data can affect later runs.

`mvn clean test` removes the previous `target` directory, including reports, screenshots, and archived runs stored there. Use `mvn test` to skip that cleanup, but copy any reports you want to keep before running again because the current report files are overwritten.

## Configuration and test data

Most day-to-day changes can be made in these files:

| File | What to change there |
|---|---|
| `config.properties` | Application URL, browser, headless mode, timeouts, report paths, and preferred projects and activities. |
| `locators.properties` | Named selectors used by the page objects. Some selectors also appear directly in the page classes. |
| `testng.xml` | The classes and groups included in the suite. |
| `src/test/resources/testdata/LoginData.xlsx` | Login details and the inputs for each scenario. Apache POI reads this workbook. |

The workbook has six sheets: `LoginData`, `ClockInOut`, `ViewAttendance`, `EditTimesheet`, `SubmitTimesheet`, and `ApprovalWorkflow`. Each test looks up its named row in the matching sheet.

Each data provider returns one selected row, identified by `testCaseName`. Adding rows to Excel does not add test runs automatically. Keep headers such as `projectName` and `expectedResult` unchanged because the tests use exact keys to read those values. Test-case name matching is case-insensitive.

`login.excel.path` controls the login workbook path only. The five scenario providers use `src/test/resources/testdata/LoginData.xlsx` directly in their Java code.

Some workbook values are not used by the current checks. Attendance loads its row without using its values. The timesheet pages use `projectName` as search text, then choose from configured preferences or a fallback. They select activities from `preferred.activities` or a keyboard fallback and ignore the supplied `activityName`. The workbook's `expectedResult` is checked for login and clock-in/out, but not for attendance, editing, submission, or approval.

To run without a visible browser window, set `headless=true` in `config.properties`. Browser options are `chromium`, `firefox`, and `webkit`. These settings are read from the file; the configuration reader does not apply Maven `-D` overrides.

If you change the application host, update `app.url`, `punchInUrl`, and `punchOutUrl` together. Punch-in/out navigation uses those separate absolute URLs.

## Finding your way around

```text
team_seven/
|-- pom.xml
|-- testng.xml
|-- config.properties
|-- locators.properties
|-- README.md
|-- TestTrace.md
`-- src/
    |-- main/java/
    |   |-- base/       Browser setup, login, and test cleanup
    |   |-- pages/      Page objects for login and the Time scenarios
    |   `-- utils/      Data readers, waits, logging, screenshots, and reports
    `-- test/
        |-- java/tests/       The five scenario test classes
        `-- resources/testdata/
            `-- LoginData.xlsx
```

The test classes load their data and call scenario methods on the page objects. Most navigation, workflow steps, and assertions currently live in those page objects. `BaseTest` handles login and test lifecycle hooks, while the utility classes handle data, waits, logging, screenshots, and reports.

## Reports and screenshots

With the current configuration, check these locations inside `team_seven` after a run:

| Output | Location |
|---|---|
| Extent report | `target/reports/TeamSeven_Time_ExtentReport.html` |
| TestNG report | `target/surefire-reports/index.html` |
| Failure screenshots | `target/screenshots/` |

For browser startup or login failures, check the Maven output and Surefire reports as well. Setup failures can show as skipped tests in the Extent report without a screenshot.

The generated Extent report currently uses an incorrect relative path for screenshot attachments. Open the PNG files in `target/screenshots/` directly if the images do not appear in the report.

## Current limitations

- Clock-in/out checks save messages without reading back the attendance entries. Punch-in can be skipped if its heading is absent, allowing the flow to punch out against an existing record.
- Attendance checks that the records container is visible. It does not verify individual rows, dates, or punch details.
- Editing, submission, and approval can pass on general page or control visibility. A pass does not always prove that hours were saved or that the timesheet reached Submitted or Approved.
- The hours-entry loops receive only the first matching input from `WaitUtils.find()`. They currently fill at most one day rather than the intended set of days.
- Approval conditionally resets, creates, edits, saves, submits, or approves the selected employee's timesheet using the admin session. If Reset is used, the code skips submission. It does not test a separate employee login followed by an approver login.
- Fixed sleeps and immediate visibility checks can skip actions while the page is still loading. Employee and project selection also use suggestions without confirming an exact identity.

During the review on 30 October 2026, compilation passed. The full suite recorded 3 passed, 1 failed, and 1 skipped scenario. A separate retry of Edit Timesheet and Approval Workflow recorded 1 failed and 1 skipped. These counts exclude failed setup methods, which Surefire also includes in its totals.

The saved full-suite results are in `target/review-runs/full-suite-2026-10-30/`, and the retry results are in `target/review-runs/diagnostic-retry-2026-10-30/`. The standard report paths above contain the later retry. See [the framework review](../../FRAMEWORK_REVIEW.md) for the failure details and source locations.
