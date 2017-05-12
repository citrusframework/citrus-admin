import {Injectable} from "@angular/core";
import {parseBody, StompConnection, StompConnectionService} from "./stomp-connection.service";
import {SocketEvent} from "../model/socket.event";
import {TestResult} from "../model/tests";
import * as moment from "moment";
import {log} from "../util/redux.util";

const asSocketEvent = (rb:any) => {
    const se = new SocketEvent();
    se.type = rb.type;
    se.msg = rb.type;
    se.processId = rb.processId;
    se.timestamp = moment.now();
    return se;
};

const asTestResult = (rb:any) => rb as TestResult;

@Injectable()
export class LoggingService {
    private stomp:StompConnection;
    constructor(
       private stompConnection:StompConnectionService
    ) {
        this.stomp = stompConnection.getConnection('/api/logging')
    }

    private getConnectedTopicObservable(topic:string) {
        return this.stomp.connect().switchMap(c => c.subscribeToTopic(topic))
    }

    get testEvents() {
        return this.getConnectedTopicObservable('/topic/test-events').map(parseBody(asSocketEvent))
    }

    get logOutput() {
        return this.getConnectedTopicObservable('/topic/log-output').map(parseBody(asSocketEvent))
    }

    get messages() {
        return this.getConnectedTopicObservable('/topic/messages').map(parseBody(asSocketEvent))
    }

    get results() {
        return this.getConnectedTopicObservable('/topic/results').map(parseBody(asTestResult))
    }
}