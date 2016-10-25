import { Component, Input } from '@angular/core';
import {Alert} from "../model/alert";
import {AlertService} from "../service/alert.service";

declare var jQuery:any;
declare var _:any;

@Component({
    selector: 'div.alert-dialog',
    template: `<div class="dialog" dialog-id="alert-dialog" dialog-close="no">
    <h3>Sorry! Something happend while processing your request</h3>
    <div class="alert alert-{{alert?.type}}" role="alert">
        <strong><i class="fa {{alert?.type}}"></i>&nbsp;{{alert?.type}}:</strong>&nbsp;<span [textContent]="alert?.message"></span>&nbsp;<a *ngIf="alert?.link" class="alert-link" href="{{alert?.link.url}}">{{alert?.link.name}}</a>
    </div>
</div>`
})
export class AlertDialog {

    @Input("errors-only") errorsOnly: boolean = true;

    private alert: Alert;
    private addSubscription: any;

    constructor(private _alertService: AlertService) {
    }

    ngOnInit() {
        this.addSubscription = this._alertService.alertAdd$.subscribe(
            alert => {
                if (this.errorsOnly) {
                    if (alert.type == 'danger') {
                        this.showAlert(alert);
                    }
                } else {
                    this.showAlert(alert);
                }
            });
    }

    showAlert(alert: Alert) {
        this.alert = alert;

        jQuery('#alert-dialog').modal();

        if (alert.autoClear) {
            var delay = alert.type == 'warning' ? 10000 : 2500;

            _.delay(() => {
                this.closeAlert();
            }, delay);
        }
    }

    closeAlert() {
        jQuery('#alert-dialog').modal('hide');
        this.alert = undefined;
    }
}