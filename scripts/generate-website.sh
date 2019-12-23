#!/bin/sh
# Run this script from the root folder of the project! i.e. DO NOT run it from scripts/ directory!

echo "Building a website for FinnPic project..."

# Build a test report, a coverage report, and API docs.
echo "Running sbt..."
sbt clean coverage test coverageReport doc

# Clean the old versions of generated content from Hugo.
echo "Cleaning up Hugo's static/ dir..."
rm -rf hugo/static/api-docs
rm -rf hugo/static/coverage-report
rm -rf hugo/static/test-report
mkdir hugo/static/api-docs
mkdir hugo/static/coverage-report
mkdir hugo/static/test-report

# Copy the generated stuff into Hugo folder
echo "Copying the stuff generated by sbt to Hugo's static/ dir..."
cp -R target/scala-2.12/api/* hugo/static/api-docs/
cp -R target/scala-2.12/scoverage-report/* hugo/static/coverage-report/
cp -R target/test-reports/* hugo/static/test-report/

# Run the Hugo build
echo "Running the Hugo build..."
cd hugo || exit 2
hugo

echo ""
echo "The Hugo site has been generated. Now just git commit & push the project (on master branch) and the site will be available on Netlify."