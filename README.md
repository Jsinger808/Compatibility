# Compatibility Predictor

## Steps to Run
- Download files off of GitHub and run application on an IDE (preferably IntelliJ)
- Start Postman and make a POST request to "http://localhost:8080/api/v1/compatibility/candidate"

## Assumptions
- Higher the score the better
- Team members want/are compatible with applicants who value the same attributes they do
- Regardless of team chemistry, applicants who score 10 in everything get assigned the highest score: 1.
- Regardless of team chemistry, applicants who score 0 in everything get assigned the lowest score: 0.


## Features
- Two custom exceptions
- Input validation
- Scores in range [0, 1]
- Two integration tests (one valid and one that throws a custom exception)
- Non-hard coded integers 

## Logic Behind It
- Spring Boot rejects JSON payloads with invalid parameters (missing or bad data types) by throwing custom InvalidParameters exception
- CompatibilityController takes in user's JSON file
- CompatibilityController deparses JSON file and calls CandidateService method "evaluateCandidates" passing in TeamMemberApplicantDTO
- evaluateCandidates throws custom InvalidScore Exception if scores are incorrect (less than 0 or greater than 10) or null
- evaluateCandidates adds total attribute scores from all team members
- evaluateCandidates uses a HashMap and Priority Queue to assign top two attribute scores with a positive percentage multiplier and the bottom two attribute scores
with a negative one
- In case of ties, prioritize attributes that were first added to HashMap (Intelligence, Endurance, Strength, SpicyFoodTolerance)
- evaluateCandidates pulls info (name, attributes) from each applicant in the list
- evaluateCandidates throws InvalidScore exception if scores are incorrect (less than 0 or greater than 10) or null
- evaluateCandidates multiplies each applicant's attributes with its percentage multiplier, stored in priorityValues.properties, and creates a weighted overall score
- evaluateCandidate adds each applicant and their weighted score to a temporary list before setting it in the response DTO (scoredApplicantListDTO)
- CompatibilityController stringifies response DTO and returns JSON payload to user

## Ways to Improve
- Set up an H2 in-memory DB for storing applicants
- If DB exists, create APIs to update and delete (assuming applicants can reapply and that their attributes have improved/degraded since)
- Remove hard coded Strings using a Constants or properties file
