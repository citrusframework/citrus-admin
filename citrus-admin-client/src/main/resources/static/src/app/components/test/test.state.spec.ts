import {reduce, TestStateInit, TestStateActions, TestState, TestMap} from "./test.state";
import {IdMap} from "../../util/redux.util";
import {Test} from "../../model/tests";

describe('TesState.reducer', () => {

    const StateWithTests:TestState = {
        ...TestStateInit,
        tests: <TestMap>{
            "ListBooks_Ok_2_IT": {
                name: 'ListBooks_Ok_2_IT',
                type: 'XML',
                packageName: 'com.consol.citrus.samples.bookstore',
                className: 'ListBooks_Ok_2_IT',
                methodName: 'ListBooks_Ok_2_IT',
                relativePath: 'com/consol/citrus/samples/bookstore/ListBooks_Ok_2_IT.xml'
            },
            "SchemaValidation_Error_1_IT": {
                name: 'SchemaValidation_Error_1_IT',
                type: 'XML',
                packageName: 'com.consol.citrus.samples.bookstore',
                className: 'SchemaValidation_Error_1_IT',
                methodName: 'SchemaValidation_Error_1_IT',
                relativePath: 'com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml'
            }
        },
        packages: {
            'com.consol.citrus.samples.bookstore': {
                name: 'com.consol.citrus.samples.bookstore',
                tests: [

                    {
                        name: 'ListBooks_Ok_2_IT',
                        type: 'XML',
                        packageName: 'com.consol.citrus.samples.bookstore',
                        className: 'ListBooks_Ok_2_IT',
                        methodName: 'ListBooks_Ok_2_IT',
                        relativePath: 'com/consol/citrus/samples/bookstore/ListBooks_Ok_2_IT.xml'
                    },
                    {
                        name: 'SchemaValidation_Error_1_IT',
                        type: 'XML',
                        packageName: 'com.consol.citrus.samples.bookstore',
                        className: 'SchemaValidation_Error_1_IT',
                        methodName: 'SchemaValidation_Error_1_IT',
                        relativePath: 'com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml'
                    }
                ]
            }
        },
        testNames: [
            'ListBooks_Ok_2_IT',
            'SchemaValidation_Error_1_IT'
        ]
    }

    describe(`Action:${TestStateActions.PACKAGES.SUCCESS}`, () => {
        it('Should map Packageresponse', () => {
            const s = reduce(TestStateInit, {
                type: TestStateActions.PACKAGES.SUCCESS,
                payload: [
                    {
                        "name": "com.consol.citrus.samples.bookstore",
                        "tests": [{
                            "name": "ListBooks_Ok_2_IT",
                            "type": "XML",
                            "packageName": "com.consol.citrus.samples.bookstore",
                            "className": "ListBooks_Ok_2_IT",
                            "methodName": "ListBooks_Ok_2_IT",
                            "relativePath": "com/consol/citrus/samples/bookstore/ListBooks_Ok_2_IT.xml"
                        }, {
                            "name": "SchemaValidation_Error_1_IT",
                            "type": "XML",
                            "packageName": "com.consol.citrus.samples.bookstore",
                            "className": "SchemaValidation_Error_1_IT",
                            "methodName": "SchemaValidation_Error_1_IT",
                            "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                        }]
                    },
                    {
                        "name": "com.consol.citrus.samples.bookstore2",
                        "tests": [
                            {
                                "name": "ListBooks_Ok_1_IT",
                                "type": "XML",
                                "packageName": "com.consol.citrus.samples.bookstore",
                                "className": "ListBooks_Ok_1_IT",
                                "methodName": "ListBooks_Ok_1_IT",
                                "relativePath": "com/consol/citrus/samples/bookstore/ListBooks_Ok_1_IT.xml"
                            }
                        ]
                    }]
            });
            expect(s.packages["com.consol.citrus.samples.bookstore"]).toBeDefined();
            expect(s.packages["com.consol.citrus.samples.bookstore2"]).toBeDefined();
            expect(Object.keys(s.tests).length).toBe(3);
            expect(s.tests["SchemaValidation_Error_1_IT"]).toEqual({
                "name": "SchemaValidation_Error_1_IT",
                "type": "XML",
                "packageName": "com.consol.citrus.samples.bookstore",
                "className": "SchemaValidation_Error_1_IT",
                "methodName": "SchemaValidation_Error_1_IT",
                "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
            })
        })
    })


    describe(`Action:${TestStateActions.ADD_TAB}`, () => {

        const StateWithTestsAndOpenTests = {
            ...StateWithTests,
            openTests: ['SchemaValidation_Error_1_IT']
        }

        it('Should add a tab', () => {
            const s: TestState = reduce(StateWithTests as any, {
                type: TestStateActions.ADD_TAB,
                payload: {
                    "name": "SchemaValidation_Error_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            });
            expect(s.openTabs.length).toBe(1);
            expect(s.openTabs.indexOf("SchemaValidation_Error_1_IT")).toBe(0)
            expect(s.selectedTest).toBe("SchemaValidation_Error_1_IT")

        })

        it('Should add a tab and an ignore but select an existsing tab', () => {
            const _s: TestState = reduce(StateWithTestsAndOpenTests as any, {
                type: TestStateActions.ADD_TAB,
                payload: {
                    "name": "SchemaValidation_Error_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            });
            const s = reduce(_s as any, {
                type: TestStateActions.ADD_TAB,
                payload: {
                    "name": "GetBookDetails_Ok_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "GetBookDetails_Ok_1_IT",
                    "methodName": "GetBookDetails_Ok_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/GetBookDetails_Ok_1_IT.xml"
                }
            });
            expect(s.openTabs.length).toBe(2);
            expect(s.openTabs.indexOf("SchemaValidation_Error_1_IT")).toBeGreaterThan(-1)
            expect(s.openTabs.indexOf("GetBookDetails_Ok_1_IT")).toBeGreaterThan(-1)
            expect(s.selectedTest).toBe("GetBookDetails_Ok_1_IT");
        })
    })

    describe(`Action: ${TestStateActions.REMOVE_TAB}`, () => {

        const fourOpenTabs = ['SchemaValidation_Error_1_IT','AddBook_Ok_1_IT','AddBook_Ok_1_IT','GetBookDetails_Ok_1_IT']
        const oneOpenTab = ['GetBookDetails_Ok_1_IT'];

        it('should remove an existing tab', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: fourOpenTabs,
                selectedTest:'GetBookDetails_Ok_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.REMOVE_TAB,
                payload: {
                    "name": "GetBookDetails_Ok_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "GetBookDetails_Ok_1_IT",
                    "methodName": "GetBookDetails_Ok_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/GetBookDetails_Ok_1_IT.xml"
                }
            })
            expect(s.openTabs.length).toBe(3);
            expect(s.selectedTest).toBe('AddBook_Ok_1_IT');

        })

        it('should remove single tab and remove selected tab', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: oneOpenTab,
                selectedTest:'GetBookDetails_Ok_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.REMOVE_TAB,
                payload: {
                    "name": "GetBookDetails_Ok_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "GetBookDetails_Ok_1_IT",
                    "methodName": "GetBookDetails_Ok_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/GetBookDetails_Ok_1_IT.xml"
                }
            })
            expect(s.openTabs.length).toBe(0);
            expect(s.selectedTest).toBe('');
        })

        it('should remove the first tab and select next one', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: fourOpenTabs,
                selectedTest:'SchemaValidation_Error_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.REMOVE_TAB,
                payload: {
                    "name": "SchemaValidation_Error_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            })
            expect(s.openTabs.length).toBe(3);
            expect(s.selectedTest).toBe('AddBook_Ok_1_IT');
        })

    })

    describe(`Action:${TestStateActions.SELECT_TAB}`, () => {
        const fourOpenTabs = ['SchemaValidation_Error_1_IT','AddBook_Ok_1_IT','AddBook_Ok_1_IT','GetBookDetails_Ok_1_IT']

        it('should select the tab for the test', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: fourOpenTabs,
                selectedTest: 'SchemaValidation_Error_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.SELECT_TAB,
                payload: {
                    "name": "GetBookDetails_Ok_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "GetBookDetails_Ok_1_IT",
                    "methodName": "GetBookDetails_Ok_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/GetBookDetails_Ok_1_IT.xml"
                }
            })
            expect(s.selectedTest).toBe('GetBookDetails_Ok_1_IT');
        })

        it('should not change state if tab is already selected', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: fourOpenTabs,
                selectedTest: 'SchemaValidation_Error_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.SELECT_TAB,
                payload:   {
                    "name": "SchemaValidation_Error_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            })
            expect(s).toBe(StateWithTestsAndOpenTests)
        })

        it('should not change state if tab is not opened', () => {
            const StateWithTestsAndOpenTests:TestState = {
                ...StateWithTests,
                openTabs: fourOpenTabs,
                selectedTest: 'SchemaValidation_Error_1_IT'
            }
            const s = reduce(StateWithTestsAndOpenTests, {
                type: TestStateActions.SELECT_TAB,
                payload:   {
                    "name": "Grumpy_non_exisiting_Test",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            })
            expect(s).toBe(StateWithTestsAndOpenTests)
        })
    })

    describe(`Action:${TestStateActions.DETAIL.SUCCESS}`, () => {
        it('should add details', () => {
            const s = reduce(StateWithTests, {
                type: TestStateActions.DETAIL.SUCCESS,
                payload: {
                    "name": "SchemaValidation_Error_1_IT",
                    "type": "XML",
                    "packageName": "com.consol.citrus.samples.bookstore",
                    "className": "SchemaValidation_Error_1_IT",
                    "methodName": "SchemaValidation_Error_1_IT",
                    "groups": null,
                    "file": "/Users/timkeiner/Projects/consol/TA/citrus-samples/sample-bookstore/src/test/resources/com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT",
                    "lastModified": 1267050863000,
                    "author": "Christoph Deppisch",
                    "description": "This test gets schema validation errors in SOAP fault response from server as the request is not\n valid ('year'=>'03.Okt.2008' not a valid number).",
                    "variables": {
                        "isbn": "978-0596517335",
                        "faultCode": "{http://www.consol.com/citrus/samples/errorcodes}CITRUS:999"
                    },
                    "parameters": {},
                    "actions": [
                        {
                            "type": "assert-fault",
                            "modelType": "com.consol.citrus.model.testcase.ws.AssertFaultModel",
                            "properties": [
                                {
                                    "id": "description",
                                    "fieldName": "description",
                                    "displayName": "Description",
                                    "value": null,
                                    "optionKey": null,
                                    "options": null,
                                    "required": false
                                }
                            ],
                            "actions": [
                                {
                                    "type": "send",
                                    "modelType": "com.consol.citrus.model.testcase.core.SendModel",
                                    "properties": [
                                        {
                                            "id": "endpoint",
                                            "fieldName": "endpoint",
                                            "displayName": "Endpoint",
                                            "value": "bookStoreClient",
                                            "optionKey": null,
                                            "options": null,
                                            "required": false
                                        },
                                        {
                                            "id": "message.data",
                                            "fieldName": "message.data",
                                            "displayName": "Message Data",
                                            "value": "\n                                \n                                    <bkr:AddBookRequestMessage xmlns:bkr=\"http://www.consol.com/schemas/bookstore\">\n                                        <bkr:book>\n                                            <bkr:title>Maven: The Definitive Guide</bkr:title>\n                                            <bkr:author>Mike Loukides, Sonatype</bkr:author>\n                                            <bkr:isbn>${isbn}</bkr:isbn>\n                                            <bkr:year>03.Okt.2008</bkr:year>\n                                        </bkr:book>\n                                    </bkr:AddBookRequestMessage>\n                                \n                            ",
                                            "optionKey": null,
                                            "options": null,
                                            "required": false
                                        },
                                        {
                                            "id": "fork",
                                            "fieldName": "fork",
                                            "displayName": "Fork",
                                            "value": "false",
                                            "optionKey": null,
                                            "options": [
                                                "true",
                                                "false"
                                            ],
                                            "required": false
                                        },
                                        {
                                            "id": "actor",
                                            "fieldName": "actor",
                                            "displayName": "TestActor",
                                            "value": null,
                                            "optionKey": null,
                                            "options": null,
                                            "required": false
                                        }
                                    ],
                                    "actions": []
                                }
                            ]
                        }
                    ],
                    "relativePath": "com/consol/citrus/samples/bookstore/SchemaValidation_Error_1_IT.xml"
                }
            })

            expect(s.details['SchemaValidation_Error_1_IT']).toBeDefined();
        })
    })
})
