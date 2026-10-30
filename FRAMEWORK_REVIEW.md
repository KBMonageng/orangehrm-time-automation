# Framework review — 2026-10-30

Reviewed the source, configuration, locators, suite, documentation, and workbook before execution. Original source, configuration, and workbook files remain unchanged; this review is a new artifact.

All locations below are relative to `CapStone Team 7/team_seven/` unless stated otherwise.

The framework targets Java 11 and uses Maven, Playwright, TestNG, Apache POI, and Extent Reports. `BaseTest` opens a fresh browser and logs in before each test. Test classes select named Excel rows and delegate actions and assertions to page objects. Configuration and locators are external properties files. Browser sessions are isolated, while application data remains shared between runs.

The intended five flows are:

| Flow | Test | Intended outcome |
|---|---|---|
| Clock in/out | `ClockInOutTest` | Save a punch in and punch out with comments. |
| View attendance | `ViewAttendanceTest` | Display the user's attendance records. |
| Edit timesheet | `EditTimesheetTest` | Enter project/activity hours and verify saved edits. |
| Submit timesheet | `SubmitTimesheetTest` | Submit an editable timesheet and verify its state. |
| Approval workflow | `ApprovalWorkflowTest` | Open an employee timesheet, prepare/submit it, and approve it. |

`testng.xml:3` schedules two smoke tests; `testng.xml:15` schedules the other three as regression tests. All five run in the default suite. There are no explicit test dependencies or parallel settings. The repository's `.github/` directory contains Java upgrade logging hooks, with no test CI workflow.

High-priority findings confirmed from source:

- **Weekly hours target only one input.** `src/main/java/utils/WaitUtils.java:35` returns `.first()` for every locator. The edit, submit, and approval hour loops count and index that already narrowed locator (`pages/EditTimesheetPage.java:120`, `pages/SubmitTimesheetPage.java:113`, `pages/ApprovalWorkflowPage.java:159`, all under `src/main/java/`). Their intended five-day/two-day iteration therefore receives at most one input.
- **Edit can pass without editing or saving.** `src/main/java/pages/EditTimesheetPage.java:153` skips entry if its input is absent; the final assertion at line 138 accepts a visible container or controls. The expected result passed into the method at line 149 is unused. No readback verifies persisted hours.
- **Submit can pass without a submitted state.** `src/main/java/pages/SubmitTimesheetPage.java:140` makes the submit action optional. Its assertion at line 148 also accepts a container, Edit button, or Submit button. `src/test/java/tests/SubmitTimesheetTest.java:34` does not consume the workbook's expected result.
- **Approval can pass without approval.** `src/main/java/pages/ApprovalWorkflowPage.java:234` accepts setup activity and a visible timesheet container rather than requiring an approved state. The submission method at line 196 returns after a reset. `src/test/java/tests/ApprovalWorkflowTest.java:34` does not consume the workbook's expected result.

Other coverage and reliability limitations:

- `src/main/java/pages/ViewAttendancePage.java:38` checks the records container only, without verifying a data row, date, or saved punch details. `src/test/java/tests/ViewAttendanceTest.java:31` ignores its supplied data map.
- `src/main/java/pages/ClockInOutPage.java:58` can skip punch in when its heading is absent, then attempt punch out. Assertions check success toasts rather than persisted attendance entries.
- Activity selection ignores the requested activity and searches configured preferences (`src/main/java/pages/EditTimesheetPage.java:96`, `SubmitTimesheetPage.java:90`, and `ApprovalWorkflowPage.java:124`, all in the same directory). Project selection also uses preferred/fallback results instead of verifying an exact requested project.
- Every test provider returns one selected workbook row (`src/test/java/tests/*Test.java:13`). This is five positive scenarios, without invalid-input, permission, boundary, or multiple-data-row coverage. Tests use admin login throughout, with no separate employee-to-approver session transition (`src/main/java/base/BaseTest.java:43`).
- Browser cleanup does not restore application records (`src/main/java/base/BaseTest.java:83`). Optional create/reset/edit actions make outcomes depend on existing demo data. Fixed sleeps and immediate visibility checks can also make conditional steps sensitive to response time (`src/main/java/utils/WaitUtils.java:21`, line 75).

Reporting limitations:

- Setup creates an Extent test before browser startup and login (`src/main/java/base/BaseTest.java:33`). When setup fails, the skip branch at line 78 records only a generic skip; it does not attach the setup exception or a screenshot. Surefire output is needed to diagnose configuration failures.
- **Broken screenshot attachment confirmed.** `src/main/java/utils/ScreenshotUtil.java:28` returns a working-directory-relative path, attached unchanged at `src/main/java/utils/ExtentReportUtil.java:48`. The generated HTML under `target/reports` uses `target/screenshots\...` as its image source, resolving to nonexistent `target/reports/target/screenshots/...`.
- A passing suite does not establish all intended business outcomes because of the permissive assertions described above. `TestTrace.md:5` lists the intended scenarios, not proof that their full outcomes are asserted.

Execution evidence:

- Compilation succeeded with `mvn -q -DskipTests test-compile`.
- Runtime: Java 26.0.1 and Maven 3.9.16; the project compiles for Java 11.
- The full `mvn -B test` run exited with code 1. Maven reported 3 minutes 40 seconds total time.

| Scenario | Initial full-suite result | Evidence |
|---|---|---|
| Clock in/out | PASS | Test method completed. |
| View attendance | PASS | Test method completed. |
| Edit timesheet | SKIP | `BeforeMethod` failed at `src/main/java/base/BaseTest.java:39`: opening the application URL timed out after 60,000 ms while waiting for `DOMContentLoaded`. |
| Submit timesheet | PASS | Test method completed; permissive submission assertion still applies. |
| Approval workflow | FAIL | Final assertion failed at `src/main/java/pages/ApprovalWorkflowPage.java:243`. The screenshot shows an employee timesheet loading spinner. Timing/synchronization is suspected; the application root cause is not established. |

The five business scenarios therefore produced **3 passed, 1 failed, and 1 skipped**. Surefire also counts the failed setup configuration, so its summary is **6 run, 2 failures, 1 skipped**.

Original run reports and screenshots are archived under `target/review-runs/full-suite-2026-10-30/`, containing `surefire-reports/`, `reports/`, and `screenshots/`.

A single diagnostic retry ran with `mvn -B test "-Dtest=tests.EditTimesheetTest,tests.ApprovalWorkflowTest"`. It exited with code 1 after 2 minutes 12 seconds.

| Scenario | Diagnostic retry | Evidence |
|---|---|---|
| Edit timesheet | FAIL | Reached the test, then failed at `src/main/java/pages/EditTimesheetPage.java:139`: "Timesheet page was not visible after edit attempt." The screenshot shows the Edit Timesheet form still open, a first-row hours value of 8, no selected project/activity in that row, and "Select a Project" / "Select an Activity" validation errors. The intended edit was not saved. The final assertion also misidentifies the visible edit form as a missing timesheet page. |
| Approval workflow | SKIP | Setup failed at `src/main/java/base/BaseTest.java:39` with the same 60,000 ms opening-page navigation timeout. The approval test body did not execute on this retry. |

Retry scenario counts are **0 passed, 1 failed, and 1 skipped**. Surefire includes the failed configuration in its summary: **3 run, 2 failures, 1 skipped**. These results do not replace the original full-suite results or constitute a successful full run.

Retry reports and screenshots are archived under `target/review-runs/diagnostic-retry-2026-10-30/`. Key evidence files are:

- Initial results: `target/review-runs/full-suite-2026-10-30/surefire-reports/testng-results.xml` and `index.html` in the same folder.
- Initial approval screenshot: `target/review-runs/full-suite-2026-10-30/screenshots/validateSubmittedTimesheetCanBeApproved_20261030_124012.png`.
- Retry results: `target/review-runs/diagnostic-retry-2026-10-30/surefire-reports/testng-results.xml` and `index.html` in the same folder.
- Retry edit screenshot: `target/review-runs/diagnostic-retry-2026-10-30/screenshots/validateTimesheetEditsAreSaved_20261030_124340.png`.

Recommended repair order: select and verify project/activity within the intended row, preserve the complete hours locator collection, wait for loading to finish and assert saved/submitted/approved states, then improve setup-failure reporting and screenshot links. Stable employee/period fixtures would also make runs easier to reproduce.

- No code fixes were applied during this review. Generated build/report artifacts are separate from the original source, configuration, and workbook.
