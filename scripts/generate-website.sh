#!/bin/sh
# Run this script from the root folder of the project! i.e. DO NOT run it from scripts/ directory!

# Build a test report, a coverage report, and API docs.
sbt clean coverage test coverageReport doc


