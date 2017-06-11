import {Component, Input, OnInit} from '@angular/core';
import {TestDetail, TestResult} from "../../../../model/tests";
import {TestService} from "../../../../service/test.service";
import {SocketEvent} from "../../../../model/socket.event";
import {Message} from "../../../../model/message";
import {Alert} from "../../../../model/alert";
import {AlertService} from "../../../../service/alert.service";
import * as _ from 'lodash';
import * as moment from 'moment';
import {LoggingService} from "../../../../service/logging.service";

@Component({
    selector: "test-messages",
    templateUrl: 'test-message-list.html'
})
export class TestMessageListComponent implements OnInit {
    @Input() detail: TestDetail;

    constructor(private loggingService: LoggingService,
                private _alertService: AlertService) {
    }

    ngOnInit() {
        this.loggingService.messages
            .subscribe(this.handleMessage);
    }

    handleMessage(message: any) {
        console.log('Handle mesage', message);
        this.detail.messages.push(new Message(_.uniqueId(), message.type, message.msg, moment().toISOString()));
    }

    notifyError(error: any) {
        this._alertService.add(new Alert("danger", error, false));
    }
}