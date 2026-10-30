"""Publish CI evidence without replacing the exit status of verification steps."""
import glob
import os
from pathlib import Path
import xml.etree.ElementTree as ET


def render():
    labels = {"success": "Passed", "failure": "Failed", "skipped": "Skipped", "cancelled": "Cancelled"}
    rows = ["## Build & Deployment Verification", "", "| Check | Result |", "| --- | --- |"]
    for name, variable in [("Nullness compiler checks", "NULLNESS_RESULT"),
                           ("Build and tests", "BUILD_RESULT"),
                           ("Container verification", "CONTAINER_RESULT")]:
        rows.append(f"| {name} | {labels.get(os.getenv(variable), 'Not run')} |")

    reports = glob.glob("build/test-results/test/TEST-*.xml")
    counts = dict.fromkeys(["tests", "failures", "errors", "skipped"], 0)
    for report in reports:
        suite = ET.parse(report).getroot()
        for key in counts:
            counts[key] += int(suite.get(key, 0))
    rows.extend(["", "### Tests", ""])
    if reports:
        passed = counts["tests"] - counts["failures"] - counts["errors"] - counts["skipped"]
        rows.append(f"{counts['tests']} total · {passed} passed · "
                    f"{counts['failures'] + counts['errors']} failed · {counts['skipped']} skipped")
    else:
        rows.append("No JUnit results were produced.")

    evidence = Path("build/ci/container-results.txt")
    rows.extend(["", "### Container and JVM AOT Cache", "", "| Check / Measurement | Result |", "| --- | --- |"])
    if evidence.exists():
        for line in evidence.read_text().splitlines():
            key, value = line.split("=", 1)
            rows.append(f"| {key} | {value.replace('|', '&#124;')} |")
    else:
        rows.append("| Container checks | Not run |")
    rows.extend(["", "Unlisted container checks were not reached. Timings are CI observations, not performance guarantees.", ""])
    server = os.getenv("GITHUB_SERVER_URL", "https://github.com")
    repo = os.getenv("GITHUB_REPOSITORY", "")
    run = os.getenv("GITHUB_RUN_ID", "")
    if repo and run:
        rows.append(f"[Workflow logs and test report]({server}/{repo}/actions/runs/{run})")
    return "\n".join(rows) + "\n"


if __name__ == "__main__":
    summary = render()
    destination = os.getenv("GITHUB_STEP_SUMMARY")
    if destination:
        with open(destination, "a", encoding="utf-8") as output:
            output.write(summary)
    else:
        print(summary)
