import { Component, Input } from '@angular/core';
import {Alert} from "../../model/alert";
import {AlertService} from "../../service/alert.service";
import * as _ from 'lodash';
import {Router} from "@angular/router";

@Component({
    selector: 'div.alert-console',
    template: `
        <div *ngIf="type == 'detailed'">
            <div *ngFor="let alert of alerts" class="alert alert-{{alert.type}} alert-dismissible" role="alert">
                <button type="button" class="close" (click)="clearAlert(alert)" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <strong><i class="fa {{alert.type}}"></i>&nbsp;{{alert.type}}:</strong>&nbsp;<span [textContent]="alert.message"></span>&nbsp;<a *ngIf="alert.link" class="alert-link" href="{{alert.link.url}}">{{alert.link.name}}</a>
            </div>
        </div>
        <div *ngIf="type == 'labels'">
            <a name="log-label" *ngFor="let alert of alerts" class="clickable" (click)="openLog()" style="float: left;"><span class="label label-{{alert.type}}"><i class="fa {{alert.type}}"></i>&nbsp;{{alert.type}}</span></a>
        </div>`
})
export class AlertConsole {

    @Input() type: string;

    private alerts: Alert[] = [];
    private addSubscription: any;
    private clearSubscription: any;

    constructor(private _alertService: AlertService,
                private router: Router) {
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
            var delay = alert.type == 'warning' ? 10000 : 2500;

            _.delay(() => {
                this.alerts = this.alerts.filter(existing => existing != alert);
            }, delay);
        }
    }

    clearAlert(toClear: Alert) {
        this._alertService.clear(toClear);
    }

    openLog() {
        this.router.navigate(['/log']);
    }
}