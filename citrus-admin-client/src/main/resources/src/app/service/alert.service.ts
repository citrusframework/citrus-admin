import {Observable} from 'rxjs/Observable';
import 'rxjs/Rx';
import {Observer} from 'rxjs/Observer';
import {Alert} from "../model/alert";

export class AlertService {
    private _alerts: Alert[] = [];

    alertAdd$: Observable<Alert>;
    private _addObserver: Observer<Alert>;

    alertClear$: Observable<Alert>;
    private _clearObserver: Observer<Alert>;

    constructor() {
        this.alertAdd$ = new Observable<Alert>(observer =>
            this._addObserver = observer).share();

        this.alertClear$ = new Observable<Alert>(observer =>
            this._clearObserver = observer).share();
    }

    add(alert: Alert) {
        this._alerts.push(alert);
        this._addObserver.next(alert);
    }

    clear(toClear: Alert) {
        this._alerts = this._alerts.filter(alert => alert != toClear);
        this._clearObserver.next(toClear);
    }

    getAlerts() {
        return this._alerts;
    }
}