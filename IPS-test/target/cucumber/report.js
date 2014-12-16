$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("policies.feature");
formatter.feature({
  "line": 2,
  "name": "Security - policy",
  "description": "",
  "id": "security---policy",
  "keyword": "Feature",
  "tags": [
    {
      "line": 1,
      "name": "@ALL_TESTS"
    },
    {
      "line": 1,
      "name": "@WIP"
    },
    {
      "line": 1,
      "name": "@Regression"
    }
  ]
});
formatter.scenario({
  "line": 5,
  "name": "Get valid response when getting all policies for account",
  "description": "",
  "id": "security---policy;get-valid-response-when-getting-all-policies-for-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 7,
  "name": "I have a valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 8,
  "name": "I GET the policies",
  "keyword": "When "
});
formatter.step({
  "line": 9,
  "name": "I receive a response that contains the expected list of policies",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 2085281835,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_GET_the_policies()"
});
formatter.result({
  "duration": 2214585803,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_that_contains_the_expected_list_of_policies()"
});
formatter.result({
  "duration": 2659795,
  "status": "passed"
});
formatter.scenario({
  "line": 11,
  "name": "Get invalid response when getting all policies for account",
  "description": "",
  "id": "security---policy;get-invalid-response-when-getting-all-policies-for-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 13,
  "name": "I have an invalid account",
  "keyword": "Given "
});
formatter.step({
  "line": 14,
  "name": "I GET the policies",
  "keyword": "When "
});
formatter.step({
  "line": 15,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "invalid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 4196675,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_GET_the_policies()"
});
formatter.result({
  "duration": 16894658,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 503677,
  "status": "passed"
});
formatter.scenario({
  "line": 17,
  "name": "Get valid response when getting a single policy from an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-getting-a-single-policy-from-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 19,
  "name": "I have a valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 20,
  "name": "I GET a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 21,
  "name": "I receive a response that contains the expected policy",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 184289154,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "GET",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 8
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 7410705,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_receive_a_response_that_contains_the_expected_policy()"
});
formatter.result({
  "duration": 42955,
  "status": "passed"
});
formatter.scenario({
  "line": 23,
  "name": "Get invalid response when getting a single policy for an account",
  "description": "",
  "id": "security---policy;get-invalid-response-when-getting-a-single-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 25,
  "name": "I have an invalid account",
  "keyword": "Given "
});
formatter.step({
  "line": 26,
  "name": "I GET a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 27,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "invalid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 6273725,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "GET",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 8
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 9198473,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 52078,
  "status": "passed"
});
formatter.scenario({
  "line": 29,
  "name": "Get valid response when creating a policy for an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-creating-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 31,
  "name": "I have a valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 32,
  "name": "I POST a policy",
  "keyword": "When "
});
formatter.step({
  "line": 33,
  "name": "I receive a response that contains a uuid for the created policy",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 158265968,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_POST_a_policy()"
});
formatter.result({
  "duration": 53966048,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_receive_a_response_that_contains_a_uuid_for_the_created_policy()"
});
formatter.result({
  "duration": 123163,
  "status": "passed"
});
formatter.scenario({
  "line": 35,
  "name": "Get invalid response when creating a policy for an account",
  "description": "",
  "id": "security---policy;get-invalid-response-when-creating-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 37,
  "name": "I have an invalid account",
  "keyword": "Given "
});
formatter.step({
  "line": 38,
  "name": "I POST a policy",
  "keyword": "When "
});
formatter.step({
  "line": 39,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "invalid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 4890799,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_POST_a_policy()"
});
formatter.result({
  "duration": 13891603,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 60821,
  "status": "passed"
});
formatter.scenario({
  "line": 41,
  "name": "Get valid response when updating a policy for an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-updating-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 43,
  "name": "I have an valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 44,
  "name": "I PUT a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 45,
  "name": "I receive a response that does not contain an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 143673779,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "PUT",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 8
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 11055425,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_receive_a_response_that_does_not_contain_an_error_message()"
});
formatter.result({
  "duration": 50558,
  "status": "passed"
});
formatter.scenario({
  "line": 47,
  "name": "Get invalid response when updating a policy for an account",
  "description": "",
  "id": "security---policy;get-invalid-response-when-updating-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 49,
  "name": "I have an invalid account",
  "keyword": "Given "
});
formatter.step({
  "line": 50,
  "name": "I PUT a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 51,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "invalid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 4933374,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "PUT",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 8
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 7722415,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 59681,
  "status": "passed"
});
formatter.scenario({
  "line": 53,
  "name": "Get valid response when updating a policy for an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-updating-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 55,
  "name": "I have a valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 56,
  "name": "I PUT an invalid policy",
  "keyword": "When "
});
formatter.step({
  "line": 57,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 179391893,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "PUT",
      "offset": 2
    },
    {
      "val": "invalid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 14982966,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 57020,
  "status": "passed"
});
formatter.scenario({
  "line": 59,
  "name": "Get valid response when deleting a policy for an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-deleting-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 61,
  "name": "I have an valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 62,
  "name": "I DELETE a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 63,
  "name": "I receive a response that does not contain an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 148586245,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "DELETE",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 11
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 16303171,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.I_receive_a_response_that_does_not_contain_an_error_message()"
});
formatter.result({
  "duration": 47896,
  "status": "passed"
});
formatter.scenario({
  "line": 65,
  "name": "Get invalid response when deleting a policy for an account",
  "description": "",
  "id": "security---policy;get-invalid-response-when-deleting-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 67,
  "name": "I have an invalid account",
  "keyword": "Given "
});
formatter.step({
  "line": 68,
  "name": "I DELETE a valid policy",
  "keyword": "When "
});
formatter.step({
  "line": 69,
  "name": "I receive a response with an error message",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "invalid",
      "offset": 10
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 3813881,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "DELETE",
      "offset": 2
    },
    {
      "val": "valid",
      "offset": 11
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 8448850,
  "status": "passed"
});
formatter.match({
  "location": "PoliciesSteps.i_receive_a_response_with_an_error_message()"
});
formatter.result({
  "duration": 50938,
  "status": "passed"
});
formatter.scenario({
  "line": 71,
  "name": "Get valid response when deleting a policy for an account",
  "description": "",
  "id": "security---policy;get-valid-response-when-deleting-a-policy-for-an-account",
  "type": "scenario",
  "keyword": "Scenario"
});
formatter.step({
  "line": 73,
  "name": "I have a valid account",
  "keyword": "Given "
});
formatter.step({
  "line": 74,
  "name": "I DELETE an invalid policy",
  "keyword": "When "
});
formatter.step({
  "line": 75,
  "name": "I receive a response with error message Policy test-id-2 cannot be found for account: TCCD",
  "keyword": "Then "
});
formatter.match({
  "arguments": [
    {
      "val": "valid",
      "offset": 9
    }
  ],
  "location": "PoliciesSteps.I_have_validity_account(String)"
});
formatter.result({
  "duration": 146301642,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "DELETE",
      "offset": 2
    },
    {
      "val": "invalid",
      "offset": 12
    }
  ],
  "location": "PoliciesSteps.I_METHOD_validity_policy(String,String)"
});
formatter.result({
  "duration": 8059212,
  "status": "passed"
});
formatter.match({
  "arguments": [
    {
      "val": "Policy test-id-2 cannot be found for account: TCCD",
      "offset": 40
    }
  ],
  "location": "PoliciesSteps.I_receive_a_response_with_error_message(String)"
});
formatter.result({
  "duration": 84769,
  "status": "passed"
});
});