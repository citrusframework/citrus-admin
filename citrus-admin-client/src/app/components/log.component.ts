import {Component} from 'angular2/core';
import {NgFor} from "angular2/common";
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";

@Component({
    templateUrl: 'app/components/log.html',
    directives: [NgFor]
})
export class LogComponent {

    private alerts: Alert[] = [];

    constructor(private _alertService: AlertService) {}

    ngOnInit() {
        this.alerts = this._alertService.getAlerts();
    }

    clearAlert(toClear: Alert) {
        this._alertService.clear(toClear);
        this.alerts = this.alerts.filter(alert => alert != toClear);
    }
}