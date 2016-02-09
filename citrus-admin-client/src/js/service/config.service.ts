import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ConfigService {

    constructor (private http: Http) {}

    private _beansUrl = 'beans';

    getBeans(type: string) {
        return this.http.get(this._beansUrl + '/' + type)
                        .map(res => res.json())
                        .catch(this.handleError);
    }

    createBean(bean: any) {
        return this.http.post(this._beansUrl, JSON.stringify(bean));
    }

    updateBean(bean: any) {
        return this.http.put(this._beansUrl + '/' + bean.id, JSON.stringify(bean));
    }

    deleteBean(id: string) {
        return this.http.delete(this._beansUrl + '/' + id)
                .catch(this.handleError);
    }

    getEndpointType(type: string) {
        return this.http.get(this._beansUrl + '/type/' + type)
            .map(res => res.json())
            .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}