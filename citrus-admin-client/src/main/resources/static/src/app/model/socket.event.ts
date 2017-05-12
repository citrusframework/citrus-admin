import {Moment} from "moment";
export class SocketEvent {

    static Types = {
        PROCESS_START: "PROCESS_START",
        TEST_START: "TEST_START",
        TEST_ACTION_FINISH: "TEST_ACTION_FINISH",
        TEST_FAILED: "TEST_FAILED",
        PROCESS_FAILED: "PROCESS_FAILED",
        PROCESS_SUCCESS: "PROCESS_SUCCESS",
        LOG_MESSAGE: "LOG_MESSAGE"
    };

    constructor() { }

    public timestamp:number;
    public msg: string;
    public type: string;
    public processId: string;
}