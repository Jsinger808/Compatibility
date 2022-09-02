<<<<<<< HEAD
# Compatibility Predictor

## Steps to Run
- Download files off of GitHub and run application on an IDE (preferably IntelliJ)
- Start Postman and make a POST request to "http://localhost:8080/api/v1/compatibility/candidate"

## Logic Behind It
- CompatibilityController takes in user's JSON file
- CompatibilityController deparses JSON file and calls CandidateService method "evaluateCandidates"
- evaluateCandidates pulls info (name, attributes) from each applicant in the list
- evaluateCandidate's exception handling throws custom exception if scores are incorrect (less than 0 or greater than 10) or null
- evaluateCandidate's logic multiplies each score with its attribute's respective weighted value, which is stored in the weightedAttributes.properties file
- evaluateCandidate adds each applicant to a temporary list before setting the list into the response's DTO (scoredApplicantListDTO)
- CompatibilityController returns scoredApplicantListDTO to user

## Ways to Improve
- Set up an H2 in-memory DB for storing applicants
- If DB exists, create APIs to update and delete (assuming applicants can reapply and that their attributes have improved/degraded since)
- Figure out how to throw custom exceptions for invalid parameters while still allowing teams object in JSON input file

=======
>>>>>>> 740a75ab4834b213dda522542c1db965ad58c1b9

