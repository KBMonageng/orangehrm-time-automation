# OrangeHRM Time Automation

Team Seven's Java automation framework for the OrangeHRM Time module, using Playwright, TestNG, Maven, Apache POI, and Extent Reports.

The project covers clock-in/out, attendance records, timesheet editing and submission, and an approval workflow.

- [Project README](<CapStone Team 7/team_seven/README.md>): setup, test data, configuration, and current test limitations.
- [Framework review](FRAMEWORK_REVIEW.md): source findings and results from the review run.

To run the suite, open a terminal in `CapStone Team 7/team_seven` and run `mvn test`. The suite uses the shared OrangeHRM demo. The latest recorded full run had 3 passed, 1 failed, and 1 skipped scenario; see the review for details.

Generated reports and screenshots stay in the local `target` directory and are excluded from this repository.
