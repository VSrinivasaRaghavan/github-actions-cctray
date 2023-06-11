# github-actions-cctray

Create a fine grained access token for your repo in github. 
The token must contain read access to read all workflow statuses.

export the token in the following placeholder as environment variable.

export GITHUB_FGPAT={your token here}

Run the either of these commands to start the application that translate github actions 
API response to a form CCMenu can understand.

./batect run

[or]

./gradlew bootRun

//Batect is a build tool that quickly containerizes any application.

The application starts in localhost:8000 from container and localhost:8080 without container, so point the CCMenu accordingly.

The API http://localhost:[port]/workflows returns all latest workflow runs, of which you can filter and add only workflows you are 

interested from CCMenu itself.

Sample response from API : 

<?xml version="1.0" ?>
<Projects>
  <Project name=".github/workflows/ci.yml" activity="Sleeping" lastBuildStatus="Failure" lastBuildTime="2023-06-11T22:16:17.000+05:30" webUrl="https://github.com/VSrinivasaRaghavan/resume-factory/actions/runs/5236505918"></Project>
  <Project name="Manual workflow" activity="Sleeping" lastBuildStatus="Success" lastBuildTime="2023-06-11T21:19:22.000+05:30" webUrl="https://github.com/VSrinivasaRaghavan/resume-factory/actions/runs/5236280544"></Project>
  <Project name="Deploy static content to Pages" activity="Sleeping" lastBuildStatus="Failure" lastBuildTime="2023-06-11T21:16:36.000+05:30" webUrl="https://github.com/VSrinivasaRaghavan/resume-factory/actions/runs/5236271002"></Project>
</Projects>
