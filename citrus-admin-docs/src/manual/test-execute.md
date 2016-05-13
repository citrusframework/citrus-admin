## Execute tests

Test execution is a very powerful feature as it enables you to execute your tests within a browser environment almost everywhere. Just hit the *Run* button and Citrus will start a new background process that executes the
test case immediately. At the moment we do only support Maven test execution. This means that a new Maven process is launched in background executing the test.

![Execution](screenshots/test-execute.png)

As you can see the test log output is forwarded to your browser. Also the test progress and result (success or failure) is tracked by the administration UI. In near future you may
be able to review all messages that were exchanged during the test run in the message view panel.