import { Component, Input } from 'angular2/core';
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";
import { NgFor } from 'angular2/common';

declare var _:any;

@Component({
    selector: 'div.alert-console',
    template: `<div *ngIf="type == 'detailed'">
    <div *ngFor="#alert of alerts" class="alert alert-{{alert.type}} alert-dismissible" role="alert">
        <button type="button" class="close" (click)="clearAlert(alert)" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <strong>{{alert.type}}:</strong>&nbsp;<span [textContent]="alert.message"></span>
    </div>
</div>
<div *ngIf="type == 'labels'">
    <a *ngFor="#alert of alerts" href="#/log" style="float: left;"><span class="label label-{{alert.type}}">{{alert.type}}</span></a>
</div>`,
    directives: [NgFor]
})
export class AlertConsole {

    @Input() type: string;

    private alerts: Alert[] = [];
    private addSubscription: any;
    private clearSubscription: any;

    constructor(private _alertService: AlertService) {
        this.type = "detailed";
    }

    ngOnInit() {
        this.addSubscription = this._alertService.alertAdd$.subscribe(
            alert => this.newAlert(alert));

        this.clearSubscription = this._alertService.alertClear$.subscribe(
            toClear => this.alerts = this.alerts.filter(alert => alert != toClear));
    }

    newAlert(alert: Alert) {
        this.alerts.push(alert);

        if (alert.autoClear) {
            _.delay(() => {
                this.alerts = this.alerts.filter(existing => existing != alert);
            }, 2000);
        }
    }

    clearAlert(toClear: Alert) {
        this._alertService.clear(toClear);
    }
}