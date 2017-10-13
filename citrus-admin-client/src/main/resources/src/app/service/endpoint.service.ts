import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {JsonHeader, toJson} from "./common";

@Injectable()
export class EndpointService {

    constructor (private http: Http) {}

    private serviceUrl = 'api/endpoints';

    getEndpoints() {
        return this.http.get(this.serviceUrl)
                        .map(toJson())
                        .catch(this.handleError);
    }

    createEndpoint(bean: any) {
        return this.http.post(this.serviceUrl, JSON.stringify(bean), JsonHeader)
                .catch(this.handleError);
    }

    updateEndpoint(bean: any) {
        return this.http.put(`${this.serviceUrl}/${bean.id}`, JSON.stringify(bean), JsonHeader)
                .catch(this.handleError);
    }

    deleteEndpoint(id: string) {
        return this.http.delete(`${this.serviceUrl}/${id}`)
                .catch(this.handleError);
    }

    getEndpointTypes() {
        return this.http.get(`${this.serviceUrl}/types`)
            .map(toJson())
            .catch(this.handleError);
    }

    getEndpointType(type: string) {
        return this.http.get(`${this.serviceUrl}/type/${type}`)
                .map(toJson())
                .catch(this.handleError);
    }

    private handleError (error: Response) {
        return Observable.throw(error.json() || 'Server error');
    }

}